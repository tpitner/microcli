package cz.utulnykoutek.microcli;

import cz.utulnykoutek.microcli.easy.EasyCLI;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static cz.utulnykoutek.microcli.easy.StdFlag.DEBUG;
import static cz.utulnykoutek.microcli.easy.StdFlag.DRY_RUN;
import static cz.utulnykoutek.microcli.easy.StdFlag.HELP;
import static cz.utulnykoutek.microcli.easy.StdFlag.NO_COLOR;
import static cz.utulnykoutek.microcli.easy.StdFlag.VERBOSE;
import static cz.utulnykoutek.microcli.easy.StdFlag.VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EasyCLITest {

  @Test
  void registersStandardFlagsWithExpectedNamesAndIds() {
    var cli = new EasyCLI("test")
        .stdFlags(HELP, VERBOSE, VERSION, DRY_RUN, DEBUG, NO_COLOR);

    cli.parse(new String[]{"-h", "-v", "-V", "-n", "-d", "--no-color"});

    assertTrue(cli.isFlag(HELP));
    assertTrue(cli.isFlag(VERBOSE));
    assertTrue(cli.isFlag(VERSION));
    assertTrue(cli.isFlag(DRY_RUN));
    assertTrue(cli.isFlag(DEBUG));
    assertTrue(cli.isFlag(NO_COLOR));

    assertTrue(cli.isFlag("help"));
    assertTrue(cli.isFlag("verbose"));
    assertTrue(cli.isFlag("version"));
    assertTrue(cli.isFlag("dryRun"));
    assertTrue(cli.isFlag("debug"));
    assertTrue(cli.isFlag("noColor"));
  }

  @Test
  void noColorIsLongOnly() {
    var cli = new EasyCLI("test").stdFlags(NO_COLOR);
    cli.parse(new String[]{"--no-color"});
    assertTrue(cli.isFlag(NO_COLOR));
    assertTrue(cli.isFlag("noColor"));

    var shortCli = new EasyCLI("test").stdFlags(NO_COLOR);
    var ex = assertThrows(IllegalArgumentException.class,
        () -> shortCli.parse(new String[]{"-C"}));
    assertTrue(ex.getMessage().contains("Unknown option"));
  }

  @Test
  void unregisteredStandardFlagIsFalse() {
    var cli = new EasyCLI("test");

    assertFalse(cli.isFlag(HELP));
    assertFalse(cli.handleHelpAndVersion("1.0.0"));
  }

  @Test
  void rejectsDuplicateStandardFlags() {
    assertThrows(IllegalArgumentException.class,
        () -> new EasyCLI("test").stdFlags(HELP, HELP));
  }

  @Test
  void quickOptionsUseLongNameAsId() {
    var cli = new EasyCLI("test")
        .optInt("l", "level", 1, "Log level")
        .optDouble("r", "ratio", 1.5, "Ratio")
        .optString("p", "prefix", "", "Prefix")
        .optPath("o", "outfile", Path.of("out.txt"), "Output file");

    cli.parse(new String[]{"-l", "3", "-r", "2.5", "-p", "### ", "-o", "result.txt"});

    assertEquals(3, cli.getInt("level"));
    assertEquals(2.5, cli.getDouble("ratio"));
    assertEquals("### ", cli.getString("prefix"));
    assertEquals(Path.of("result.txt"), cli.getPath("outfile"));
  }

  @Test
  void quickOptionsAcceptExplicitParameterNames() {
    var cli = new EasyCLI("test")
        .optInt("l", "level", "LEVEL", 1, "Log level");

    cli.parse(new String[]{"--level", "4"});

    assertEquals(4, cli.getInt("level"));
  }

  @Test
  void quickPositionalsUseNameAsIdAndUpperCaseDisplayName() {
    var cli = new EasyCLI("test")
        .posString("input")
        .posInt("count", 1, "How many")
        .posDouble("ratio", 1.0, "Ratio")
        .posPath("config", Path.of("config.txt"), "Config");

    cli.parse(new String[]{"in.txt", "5", "2.5", "cfg.txt"});

    assertEquals("in.txt", cli.getString("input"));
    assertEquals(5, cli.getInt("count"));
    assertEquals(2.5, cli.getDouble("ratio"));
    assertEquals(Path.of("cfg.txt"), cli.getPath("config"));

    var input = (PositionalParameter<?>) cli.get("input");
    assertEquals("INPUT", input.name());
  }

  @Test
  void supportsExplicitIdsAndCustomParsers() {
    var cli = new EasyCLI("test")
        .opt("upper", "u", "upper", "VALUE", String.class, String::toUpperCase,
            "default", "Upper-cased value")
        .pos("positionalUpper", "VALUE", String.class, String::toUpperCase,
            "default", "Upper-cased positional value");

    cli.parse(new String[]{"--upper", "abc", "def"});

    assertEquals("ABC", cli.getString("upper"));
    assertEquals("DEF", cli.getString("positionalUpper"));
  }

  @Test
  void flagHelperUsesLongNameAsId() {
    var cli = new EasyCLI("test").flag("q", "quiet", "Suppresses output");

    cli.parse(new String[]{"-q"});

    assertTrue(cli.isFlag("quiet"));
  }

  @Test
  void addReturnsEasyCliForFluentMixing() {
    var cli = new EasyCLI("test")
        .add(new Flag("verbose", "v", "verbose", "Verbose"))
        .optInt("l", "level", 1, "Log level");

    cli.parse(new String[]{"-v", "-l", "2"});

    assertTrue(cli.isFlag("verbose"));
    assertEquals(2, cli.getInt("level"));
  }

  @Test
  void handleHelpAndVersionPrintsAndReportsHandled() {
    var help = new EasyCLI("test usage").stdFlags(HELP);
    help.parse(new String[]{"--help"});
    var helpOutput = captureOutput(() -> assertTrue(help.handleHelpAndVersion("1.0.0")));
    assertTrue(helpOutput.contains("test usage"));
    assertTrue(helpOutput.contains("Usage:"));

    var version = new EasyCLI("test").stdFlags(VERSION);
    version.parse(new String[]{"--version"});
    var versionOutput = captureOutput(
        () -> assertTrue(version.handleHelpAndVersion("mytool 1.0.0")));
    assertTrue(versionOutput.contains("mytool 1.0.0"));
  }

  @Test
  void parseAndHandlePrintsHelpBeforeValidation() {
    var cli = new EasyCLI("test usage")
        .stdFlags(HELP, VERSION)
        .optInt("c", "count", 1, "Count");

    var output = captureOutput(
        () -> assertTrue(cli.parseAndHandle(new String[]{"--help", "--count", "bad"},
            "mytool 1.0.0")));

    assertTrue(output.contains("test usage"));
    assertTrue(output.contains("Usage:"));
    assertFalse(cli.isParsed("count"));
  }

  @Test
  void parseAndHandlePrintsVersionBeforeValidation() {
    var cli = new EasyCLI("test usage")
        .stdFlags(HELP, VERSION)
        .optInt("c", "count", 1, "Count");

    var output = captureOutput(
        () -> assertTrue(cli.parseAndHandle(new String[]{"--version", "--count", "bad"},
            "mytool 1.0.0")));

    assertTrue(output.contains("mytool 1.0.0"));
    assertFalse(cli.isParsed("count"));
  }

  @Test
  void parseAndHandleParsesNormallyWithoutHelpOrVersion() {
    var cli = new EasyCLI("test usage")
        .stdFlags(HELP, VERSION)
        .optInt("c", "count", 1, "Count");

    assertFalse(cli.parseAndHandle(new String[]{"--count", "3"}, "mytool 1.0.0"));
    assertEquals(3, cli.getInt("count"));
  }

  @Test
  void parseAndHandleStopsScanningAtTerminator() {
    var cli = new EasyCLI("test usage")
        .stdFlags(HELP, VERSION)
        .posString("input");

    var output = captureOutput(
        () -> assertFalse(cli.parseAndHandle(new String[]{"--", "--help"}, "mytool 1.0.0")));

    assertEquals("", output);
    assertEquals("--help", cli.getString("input"));
  }

  @Test
  void parseAndHandleIgnoresUnregisteredStandardFlags() {
    var cli = new EasyCLI("test usage").optInt("c", "count", 1, "Count");

    assertFalse(cli.parseAndHandle(new String[]{"--count", "3"}, "mytool 1.0.0"));
    assertEquals(3, cli.getInt("count"));

    assertThrows(IllegalArgumentException.class,
        () -> cli.parseAndHandle(new String[]{"--help"}, "mytool 1.0.0"));
  }

  private static String captureOutput(Runnable action) {
    var original = System.out;
    var output = new ByteArrayOutputStream();
    try {
      System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
      action.run();
    } finally {
      System.setOut(original);
    }
    return output.toString(StandardCharsets.UTF_8);
  }
}
