package cz.utulnykoutek.microcli.easy;

import cz.utulnykoutek.microcli.theme.Color;
import cz.utulnykoutek.microcli.theme.Style;
import cz.utulnykoutek.microcli.theme.Theme;

import java.nio.file.Path;
import java.util.Arrays;

import static cz.utulnykoutek.microcli.easy.StdFlag.DEBUG;
import static cz.utulnykoutek.microcli.easy.StdFlag.DRY_RUN;
import static cz.utulnykoutek.microcli.easy.StdFlag.HELP;
import static cz.utulnykoutek.microcli.easy.StdFlag.NO_COLOR;
import static cz.utulnykoutek.microcli.easy.StdFlag.VERBOSE;
import static cz.utulnykoutek.microcli.easy.StdFlag.VERSION;

public class EasyDemo {

  static void main(String[] args) {
    var theme = new Theme()
        .withStyle("title", new Style(Color.Ansi.BRIGHT_WHITE).bold())
        .withStyle("heading", new Style(Color.Ansi.BRIGHT_CYAN).bold())
        .withStyle("option", new Style(Color.Ansi.BRIGHT_GREEN))
        .withStyle("parameter", new Style(Color.Ansi.BRIGHT_YELLOW))
        .withStyle("default", new Style(Color.Ansi.BRIGHT_BLACK))
        .withStyle("description", new Style(Color.Ansi.WHITE))
        .withStyle("error", new Style(new Color.Rgb(255, 80, 80)).bold())
        .withStyle("warning", new Style(Color.Ansi.BRIGHT_YELLOW).bold())
        .withStyle("success", new Style(Color.Ansi.BRIGHT_GREEN).bold())
        .withStyle("info", new Style(Color.Ansi.BRIGHT_BLUE))
        .withStyle("muted", new Style(Color.Ansi.BRIGHT_BLACK));

    var cli = new EasyCLI("""
        Demo for EasyCLI. Shows standard flags and quick options.
        Options use their long name as ID, positional parameters use their name.
        The demo prints <count> random (0..1) numbers + <add>.
        <help> prints help, <version> prints the version.
        """)
        .theme(theme)
        .stdFlags(HELP, VERBOSE, VERSION, DRY_RUN, DEBUG, NO_COLOR)
        .optInt("c", "count", 2, "Count of numbers to print")
        .optString("p", "prefix", "", "Prefix to print before each number")
        .optDouble("a", "add", 10.0, "Constant to add to each number")
        .optPath("o", "out", Path.of("outfile.txt"), "File to save the result")
        .posString("greeting", "Hi", "Greeting at beginning")
        .posString("farewell", "Bye", "Farewell at end")
        .posDouble("number", 1.2, "Number to process")
        .posInt("howMany", 2, "How many");

    var arguments = args.length > 0 ? args : new String[]{
        "-a", "100.0", "--prefix", "### ", "-v", "-n", "-d", "--no-color",
        "-o", "out-file.txt", "--count", "3", "-a", "1000",
        "param1", "param2", "7.8", "9"
    };
    System.out.println("Raw arguments " + Arrays.asList(arguments));

    if (cli.parseAndHandle(arguments, "EasyCLI demo 2.1.0")) {
      return;
    }

    System.out.println(cli.paint("info", "Parsed options ") + cli.parsedOptions());
    System.out.println(cli.paint("info", "Parsed parameters ") + cli.parsedParameters());
    System.out.println();

    if (cli.isFlag(VERBOSE)) System.out.println(cli.paint("info", "Verbose mode enabled"));
    if (cli.isFlag(DEBUG)) System.out.println(cli.paint("muted", "Debug mode enabled"));
    if (cli.isFlag(DRY_RUN)) System.out.println(cli.paint("warning", "Dry run: no files will be written"));
    if (cli.isFlag(NO_COLOR)) System.out.println(cli.paint("warning", "Color output disabled"));

    for (int i = 0; i < cli.getInt("count"); i++) {
      double rnd = Math.random() + cli.getDouble("add");
      System.out.println(cli.paint("success", cli.getString("prefix") + i + " = " + rnd));
    }
  }
}
