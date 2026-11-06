package cz.utulnykoutek.microcli;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MicroCLITest {

  @Test
  void parsesOptionsAndParametersByStableIds() {
    var cli = new MicroCLI("test")
        .add(
            new Flag("verbose", "v", "verbose", "Produces verbose output"),
            new ValuedOption<>("itemCount", "c", "count", "COUNT",
                Integer.class, 2, "Number of items"))
        .add(new PositionalParameter<>("name", "NAME", String.class, "default", "Name to greet"));

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
        .add(new ValuedOption<>("itemCount", "c", "count", "COUNT",
            Integer.class, 2, "Number of items"));
    shortCli.parse(new String[]{"-c", "4"});
    assertEquals(4, shortCli.getInt("itemCount"));

    var longCli = new MicroCLI("test")
        .add(new ValuedOption<>("itemCount", "c", "count", "COUNT",
            Integer.class, 2, "Number of items"));
    longCli.parse(new String[]{"--count", "6"});
    assertEquals(6, longCli.getInt("itemCount"));
  }

  @Test
  void usesDefaultsForMissingValues() {
    var cli = new MicroCLI("test")
        .add(new ValuedOption<>("itemCount", "c", "count", "COUNT",
            Integer.class, 2, "Number of items"))
        .add(new PositionalParameter<>("name", "NAME", String.class, "default", "Name to greet"));

    cli.parse(new String[]{});

    assertFalse(cli.isParsed("itemCount"));
    assertFalse(cli.isValid("itemCount"));
    assertEquals(2, cli.getInt("itemCount"));

    assertFalse(cli.isParsed("name"));
    assertFalse(cli.isValid("name"));
    assertEquals("default", cli.getString("name"));
  }

  @Test
  void rejectsInvalidOptionValuesDuringParse() {
    var cli = new MicroCLI("test")
        .add(new ValuedOption<>("itemCount", "c", "count", "COUNT",
            Integer.class, 2, "Number of items"));

    var ex = assertThrows(IllegalArgumentException.class,
        () -> cli.parse(new String[]{"--count", "not-a-number"}));

    assertTrue(ex.getMessage().contains("itemCount"));
    assertTrue(ex.getMessage().contains("not-a-number"));
    assertTrue(ex.getMessage().contains("Integer"));
    assertTrue(cli.isParsed("itemCount"));
    assertFalse(cli.isValid("itemCount"));
    assertEquals(2, cli.getInt("itemCount"));
  }

  @Test
  void rejectsInvalidPositionalValuesDuringParse() {
    var cli = new MicroCLI("test")
        .add(new PositionalParameter<>("name", "NAME", String.class, "default", "Name to greet"),
            new PositionalParameter<>("age", "AGE", Integer.class, 0, "Age"));

    var ex = assertThrows(IllegalArgumentException.class,
        () -> cli.parse(new String[]{"Ada", "not-a-number"}));

    assertTrue(ex.getMessage().contains("age"));
    assertTrue(ex.getMessage().contains("not-a-number"));
  }

  @Test
  void treatsDoubleDashAsTerminatorAndIgnoresTheRest() {
    var cli = new MicroCLI("test")
        .add(
            new Flag("verbose", "v", "verbose", "Verbose"),
            new ValuedOption<>("itemCount", "c", "count", "COUNT",
                Integer.class, 2, "Number of items"))
        .add(new PositionalParameter<>("name", "NAME", String.class, "default", "Name"));

    cli.parse(new String[]{"-v", "--", "--count", "not-a-number", "Ada"});

    assertTrue(cli.getFlag("verbose"));
    assertEquals(2, cli.getInt("itemCount"));
    assertFalse(cli.isParsed("name"));
    assertEquals("default", cli.getString("name"));
  }

  @Test
  void stopsPositionalParsingAtTerminator() {
    var cli = new MicroCLI("test")
        .add(new PositionalParameter<>("name", "NAME", String.class, "default", "Name"));

    cli.parse(new String[]{"Ada", "--", "Bob", "Cid"});

    assertEquals("Ada", cli.getString("name"));
  }

  @Test
  void rejectsExtraPositionalParametersBeforeTerminator() {
    var cli = new MicroCLI("test")
        .add(new PositionalParameter<>("name", "NAME", String.class, "default", "Name"));

    var ex = assertThrows(IllegalArgumentException.class,
        () -> cli.parse(new String[]{"Ada", "Bob", "--", "Cid"}));

    assertTrue(ex.getMessage().contains("Unexpected positional argument"));
    assertTrue(ex.getMessage().contains("Bob"));
    assertEquals("Ada", cli.getString("name"));

    assertThrows(IllegalArgumentException.class,
        () -> cli.parse(new String[]{"Ada", "Bob"}));
  }

  @Test
  void acceptsTerminatorWithNoRegisteredPositionalParameters() {
    var cli = new MicroCLI("test").add(new Flag("verbose", "v", "verbose", "Verbose"));

    assertDoesNotThrow(() -> cli.parse(new String[]{"--", "-v", "ignored"}));

    assertFalse(cli.getFlag("verbose"));
  }

  @Test
  void stillValidatesArgumentsBeforeTerminator() {
    var cli = new MicroCLI("test")
        .add(new ValuedOption<>("itemCount", "c", "count", "COUNT",
            Integer.class, 2, "Number of items"));

    var ex = assertThrows(IllegalArgumentException.class,
        () -> cli.parse(new String[]{"--count", "bad", "--", "ignored"}));

    assertTrue(ex.getMessage().contains("itemCount"));
  }

  @Test
  void rejectsMissingOptionValues() {
    var cli = new MicroCLI("test")
        .add(new ValuedOption<>("itemCount", "c", "count", "COUNT",
            Integer.class, 2, "Number of items"));

    var ex = assertThrows(IllegalArgumentException.class,
        () -> cli.parse(new String[]{"--count"}));

    assertTrue(ex.getMessage().contains("Missing value"));
    assertTrue(ex.getMessage().contains("--count"));
  }

  @Test
  void parsesAllBuiltInTypes() {
    var cli = new MicroCLI("test")
        .add(
            new ValuedOption<>("intValue", "i", "int", "INT",
                Integer.class, 1, "Integer"),
            new ValuedOption<>("doubleValue", "d", "double", "DOUBLE",
                Double.class, 1.5, "Double"),
            new ValuedOption<>("stringValue", "s", "string", "STRING",
                String.class, "x", "String"),
            new ValuedOption<>("pathValue", "p", "path", "PATH",
                Path.class, Path.of("x"), "Path"),
            new Flag("flag", "f", "flag", "Flag"))
        .add(
            new PositionalParameter<>("pInt", "PINT", Integer.class, 1, "Integer"),
            new PositionalParameter<>("pDouble", "PDOUBLE", Double.class, 1.5, "Double"),
            new PositionalParameter<>("pString", "PSTRING", String.class, "x", "String"),
            new PositionalParameter<>("pPath", "PPATH", Path.class, Path.of("x"), "Path"));

    cli.parse(new String[]{"-i", "42", "-d", "2.5", "-s", "hello", "-p", "out.txt",
        "-f", "7", "8.5", "world", "in.txt"});

    assertEquals(42, cli.getInt("intValue"));
    assertEquals(2.5, cli.getDouble("doubleValue"));
    assertEquals("hello", cli.getString("stringValue"));
    assertEquals(Path.of("out.txt"), cli.getPath("pathValue"));
    assertTrue(cli.getFlag("flag"));

    assertEquals(7, cli.getInt("pInt"));
    assertEquals(8.5, cli.getDouble("pDouble"));
    assertEquals("world", cli.getString("pString"));
    assertEquals(Path.of("in.txt"), cli.getPath("pPath"));
  }

  @Test
  void exposesTypedValueAndTypeFromArgument() {
    var count = new ValuedOption<>("itemCount", "c", "count", "COUNT",
        Integer.class, 2, "Number of items");
    var cli = new MicroCLI("test").add(count);

    cli.parse(new String[]{"--count", "5"});

    assertEquals(Integer.class, count.type());
    assertEquals(5, count.get());
    assertEquals(5, cli.get("itemCount", Integer.class));
    assertThrows(IllegalArgumentException.class,
        () -> cli.get("itemCount", String.class));
  }

  @Test
  void supportsCustomParsers() {
    var upper = new ValuedOption<>("upper", "u", "upper", "VALUE",
        String.class, String::toUpperCase, "default", "Upper-cased value");
    var positionalUpper = new PositionalParameter<>("positionalUpper", "VALUE",
        String.class, String::toUpperCase, "default", "Upper-cased positional value");
    var cli = new MicroCLI("test").add(upper).add(positionalUpper);

    cli.parse(new String[]{"--upper", "abc", "def"});

    assertEquals("ABC", cli.getString("upper"));
    assertEquals("DEF", cli.getString("positionalUpper"));
  }

  @Test
  @SuppressWarnings({"rawtypes", "unchecked"})
  void rejectsDefaultValuesOfWrongType() {
    assertThrows(IllegalArgumentException.class,
        () -> new ValuedOption("count", "c", "count", "COUNT",
            Integer.class, "two", "Number of items"));
    assertThrows(IllegalArgumentException.class,
        () -> new PositionalParameter("age", "AGE", Integer.class, "two", "Age"));
  }

  @Test
  void allowsNullDefaultsOnlyForStringAndPath() {
    assertDoesNotThrow(() -> new ValuedOption<>("text", "t", "text", "TEXT",
        String.class, null, "Text"));
    assertDoesNotThrow(() -> new ValuedOption<>("path", "p", "path", "PATH",
        Path.class, null, "Path"));
    assertThrows(IllegalArgumentException.class,
        () -> new ValuedOption<>("count", "c", "count", "COUNT",
            Integer.class, null, "Count"));
    assertThrows(IllegalArgumentException.class,
        () -> new PositionalParameter<>("ratio", "RATIO", Double.class, null, "Ratio"));

    var cli = new MicroCLI("test").add(
        new ValuedOption<>("text", "t", "text", "TEXT", String.class, null, "Text"));
    cli.parse(new String[]{});
    assertNull(cli.getString("text"));
    assertThrows(IllegalArgumentException.class, () -> cli.getInt("text"));
  }

  @Test
  void rejectsUnsupportedTypesWithoutExplicitParser() {
    assertThrows(IllegalArgumentException.class,
        () -> new ValuedOption<>("longValue", "l", "long", "LONG",
            Long.class, 1L, "Long value"));
  }

  @Test
  void rejectsDuplicateIdsAcrossOptionsAndParameters() {
    var cli = new MicroCLI("test").add(new Flag("same", "v", "verbose", "Verbose"));

    assertThrows(IllegalArgumentException.class,
        () -> cli.add(new PositionalParameter<>("same", "NAME", String.class, "value", "Name")));
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
