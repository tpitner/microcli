package cz.utulnykoutek.microcli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MicroCLITest {

  @Test
  void parsesOptionsAndParametersByStableIds() {
    var cli = new MicroCLI("test")
        .add(
            new Flag("verbose", "v", "verbose", "Produces verbose output"),
            new IntOption("itemCount", "c", "count", "COUNT", 2, "Number of items"))
        .add(new StringPositional("name", "NAME", "default", "Name to greet"));

    cli.parse(new String[]{"-v", "--count", "5", "Ada"});

    assertTrue(cli.getFlag("verbose"));
    assertTrue(cli.isParsed("itemCount"));
    assertTrue(cli.isValid("itemCount"));
    assertEquals(5, cli.getInt("itemCount"));
    assertEquals("Ada", cli.getString("name"));
  }

  @Test
  void acceptsShortAndLongOptionNamesForTheSameId() {
    var shortCli = new MicroCLI("test")
        .add(new IntOption("itemCount", "c", "count", "COUNT", 2, "Number of items"));
    shortCli.parse(new String[]{"-c", "4"});
    assertEquals(4, shortCli.getInt("itemCount"));

    var longCli = new MicroCLI("test")
        .add(new IntOption("itemCount", "c", "count", "COUNT", 2, "Number of items"));
    longCli.parse(new String[]{"--count", "6"});
    assertEquals(6, longCli.getInt("itemCount"));
  }

  @Test
  void returnsDefaultsForMissingOrInvalidValues() {
    var cli = new MicroCLI("test")
        .add(new IntOption("itemCount", "c", "count", "COUNT", 2, "Number of items"))
        .add(new StringPositional("name", "NAME", "default", "Name to greet"));

    cli.parse(new String[]{"--count", "not-a-number"});

    assertTrue(cli.isParsed("itemCount"));
    assertFalse(cli.isValid("itemCount"));
    assertEquals(2, cli.getInt("itemCount"));

    assertFalse(cli.isParsed("name"));
    assertFalse(cli.isValid("name"));
    assertEquals("default", cli.getString("name"));
  }

  @Test
  void parsesPositionalParametersWithoutOptions() {
    var cli = new MicroCLI("test")
        .add(
            new StringPositional("name", "NAME", "default", "Name to greet"),
            new IntPositional("age", "AGE", 0, "Age"));

    cli.parse(new String[]{"Ada", "42"});

    assertEquals("Ada", cli.getString("name"));
    assertEquals(42, cli.getInt("age"));
  }

  @Test
  void rejectsDuplicateIdsAcrossOptionsAndParameters() {
    var cli = new MicroCLI("test").add(new Flag("same", "v", "verbose", "Verbose"));

    assertThrows(IllegalArgumentException.class,
        () -> cli.add(new StringPositional("same", "NAME", "value", "Name")));
    assertThrows(IllegalArgumentException.class,
        () -> cli.add(new Flag("same", "w", "other", "Other")));
  }

  @Test
  void rejectsDuplicateOptionNames() {
    var cli = new MicroCLI("test").add(new Flag("verbose", "v", "verbose", "Verbose"));

    assertThrows(IllegalArgumentException.class,
        () -> cli.add(new Flag("version", "v", "version", "Version")));
    assertThrows(IllegalArgumentException.class,
        () -> cli.add(new Flag("verbosity", "w", "verbose", "Verbosity")));
  }

  @Test
  void matchesOptionNamesExactly() {
    var cli = new MicroCLI("test").add(new Flag("verbose", "v", "verbose", "Verbose"));

    assertThrows(IllegalArgumentException.class,
        () -> cli.parse(new String[]{"--verbose-extra"}));
    assertThrows(IllegalArgumentException.class,
        () -> cli.parse(new String[]{"-vv"}));
  }

  @Test
  void rejectsUnknownIdsAndTypeMismatches() {
    var cli = new MicroCLI("test").add(new Flag("verbose", "v", "verbose", "Verbose"));
    cli.parse(new String[]{"-v"});

    assertTrue(cli.find("verbose").isPresent());
    assertFalse(cli.find("missing").isPresent());
    assertThrows(IllegalArgumentException.class, () -> cli.getInt("missing"));
    assertThrows(IllegalArgumentException.class, () -> cli.getInt("verbose"));
  }

  @Test
  void requiresNonBlankIds() {
    assertThrows(NullPointerException.class, () -> new Flag(null, "v", "verbose", "Verbose"));
    assertThrows(IllegalArgumentException.class, () -> new Flag(" ", "v", "verbose", "Verbose"));
  }
}
