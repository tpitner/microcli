package cz.utulnykoutek.microcli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MicroCLITest {

  @Test
  void parsesOptionsAndParameters() {
    var verbose = new Flag("v", "verbose", "Produces verbose output");
    var count = new IntOption("c", "count", "COUNT", 2, "Number of items");
    var name = new StringPositional("NAME", "default", "Name to greet");

    new MicroCLI("test")
        .add(verbose, count)
        .add(name)
        .parse(new String[]{"-v", "--count", "5", "Ada"});

    assertTrue(verbose.isTrue());
    assertTrue(count.valid());
    assertEquals(5, count.getInt());
    assertTrue(name.valid());
    assertEquals("Ada", name.getString());
  }

  @Test
  void fallsBackToDefaultsForMissingOrInvalidValues() {
    var count = new IntOption("c", "count", "COUNT", 2, "Number of items");
    var name = new StringPositional("NAME", "default", "Name to greet");

    new MicroCLI("test")
        .add(count)
        .add(name)
        .parse(new String[]{"--count", "not-a-number"});

    assertFalse(count.valid());
    assertEquals(2, count.getInt());
    assertFalse(name.valid());
    assertEquals("default", name.getString());
  }
}
