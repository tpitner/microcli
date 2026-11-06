[![Run on Repl.it](https://repl.it/badge/github/tpitner/microcli)](https://repl.it/github/tpitner/microcli)
# microcli

Ultra light-weight command line arguments parser and validator for Java.

_MicroCLI_ supports flags, options with a single value, and a fixed number of
positional parameters. Every argument is identified by a stable **ID**, so you
can rename its command-line spelling without touching the code that reads the
value.

## Requirements

- Java 27+ (Homebrew OpenJDK 27)
- Maven 3.8+

## Build

This is a standard Maven project:

```sh
mvn clean package   # builds target/microcli-2.1.0.jar
mvn test            # runs the test suite
```

If JDK 27 is not your default, point Maven and Java at the Homebrew JDK:

```sh
export JAVA_HOME=$(brew --prefix openjdk)
mvn clean package
"$JAVA_HOME/bin/java" -jar target/microcli-2.1.0.jar
```

## Quick start

```java
import cz.utulnykoutek.microcli.Flag;
import cz.utulnykoutek.microcli.MicroCLI;
import cz.utulnykoutek.microcli.PositionalParameter;
import cz.utulnykoutek.microcli.ValuedOption;

public class Greet {
  public static void main(String[] args) {
    var cli = new MicroCLI("Greet someone")
        .add(
            new Flag("verbose", "v", "verbose", "Produces verbose output"),
            new ValuedOption<>("count", "c", "count", "COUNT",
                Integer.class, 1, "How many times to greet"))
        .add(
            new PositionalParameter<>("name", "NAME", String.class, "world",
                "Who to greet"));

    cli.parse(args);

    for (int i = 0; i < cli.getInt("count"); i++) {
      if (cli.getFlag("verbose")) System.out.println("Greeting...");
      System.out.println("Hello, " + cli.getString("name") + "!");
    }
  }
}
```

Run it with:

```sh
javac -cp target/classes -d target/classes Greet.java
java -cp target/classes Greet -v --count 3 Ada
```

```text
Greeting...
Hello, Ada!
Greeting...
Hello, Ada!
Greeting...
Hello, Ada!
```

## Running the demo

`Demo` shows all supported argument types and prints the parsed values:

```sh
"$JAVA_HOME/bin/java" -jar target/microcli-2.1.0.jar
```

The demo uses a fixed set of arguments, including `-v`, `--help`, `--count 5`,
`--prefix`, `--out`, and four positional parameters.

The easy variant is available as `EasyDemo`:

```sh
"$JAVA_HOME/bin/java" -cp target/microcli-2.1.0.jar \
  cz.utulnykoutek.microcli.easy.EasyDemo
```

It uses its own default arguments when no arguments are supplied; any arguments
you pass replace those defaults.

For convenience, the executable `easydemo` script in the project root builds the
classpath and passes all arguments through:

```sh
./easydemo --help
./easydemo --no-color --count=2 --add=5
```

The script prefers the Homebrew JDK 27, then `JAVA_HOME`, then `java` on
`PATH`. Run `mvn clean package` first so that the jar exists.

## Usage

### Create MicroCLI

```java
MicroCLI app = new MicroCLI("Usage of your application");
```

The constructor argument is the text printed at the beginning of `usage()`.

### Create options and parameters

Every option and positional parameter has a mandatory, unique **ID**. The ID is
used to read values and is completely independent of the command-line names:

```java
var verboseOpt = new Flag("verbose", "v", "verbose",
    "Produces verbose info during processing");

var countOpt = new ValuedOption<>("itemCount", "c", "count", "COUNT",
    Integer.class, 2, "Count of numbers to print");

var filePar = new PositionalParameter<>("infile", "INFILE",
    Path.class, Path.of("in.txt"), "File to read");
```

Here `itemCount` is the ID, while `-c` and `--count` are the CLI names. The
constructor layouts are:

- `Flag(id, shortName, longName, description)`
- `ValuedOption(id, shortName, longName, paramName, type, defaultValue, description)`
- `ValuedOption(id, shortName, longName, paramName, type, parser, defaultValue, description)` for custom types
- `PositionalParameter(id, displayName, type, defaultValue, description)`
- `PositionalParameter(id, displayName, type, parser, defaultValue, description)` for custom types

A flag has arity `0` and is either present (`true`) or missing (`false`). A
valued option has arity `1` and consumes the next token as its value.
Positional parameters are matched in the order in which they were added.

An option can be long-only by passing `null` (or a blank string) as its short
name:

```java
var quiet = new Flag("quiet", null, "quiet", "Suppresses output");
var level = new ValuedOption<>("level", null, "level", "LEVEL",
    Integer.class, 1, "Log level");
```

`shortName()` then returns `null`; use `hasShortName()` to check, and `names()`
to get a human-readable `-s, --long` or `--long` string.

The type token is mandatory. It is used for validation and to choose the
built-in parser:

| Type | Parser |
| --- | --- |
| `Integer.class` | `Integer::valueOf` |
| `Double.class` | `Double::valueOf` |
| `String.class` | identity |
| `Path.class` | `Path::of` |
| `Boolean.class` | `Boolean::valueOf` |

For other types, pass a parser explicitly:

```java
var dateOpt = new ValuedOption<>("date", "d", "date", "DATE",
    LocalDate.class, LocalDate::parse, LocalDate.now(), "Date to process");

var datePar = new PositionalParameter<>("start", "START",
    LocalDate.class, LocalDate::parse, LocalDate.now(), "Start date");
```

`null` default values are allowed only for `String` and `Path`. Numeric
arguments need a non-null default value. A default value of the wrong type is
rejected immediately.

### Add options and parameters

`add` can be called repeatedly and returns the `MicroCLI` instance, so calls can
be chained:

```java
app.add(verboseOpt, countOpt)
   .add(filePar);
```

Options and positional parameters are registered separately:

```java
app.add(verboseOpt, countOpt);   // options
app.add(filePar);                // positional parameters
```

IDs must be unique across all options and positional parameters. Short names
and long names must be unique among options. Duplicate registrations fail fast
with `IllegalArgumentException`.

### Parse arguments

```java
app.parse(args);
```

Values are validated during parsing. If an argument is present but its value
cannot be converted to the declared type, `parse` fails fast:

```java
app.parse(new String[]{"--count", "abc"});
// IllegalArgumentException: Invalid value for argument 'itemCount':
// 'abc' is not a valid Integer value
```

Defaults are used only when the argument is missing, not as a fallback for an
invalid value.

If an option is repeated, the last occurrence wins:

```java
app.parse(new String[]{"--count", "2", "--count", "5"});
// app.getInt("itemCount") == 5
```

By default, option values are separate tokens. The next token is taken as the
value even if it starts with `-`:

```java
app.parse(new String[]{"--count", "-5"});
// app.getInt("itemCount") == -5
```

Valued long options also accept the `--name=value` form:

```java
app.parse(new String[]{"--count=5", "--prefix=a=b"});
// app.getInt("itemCount") == 5
// app.getString("prefix") == "a=b"
```

Short options do not support the `=` form (`-c=5` is not supported).

Parsing is order-independent: options may appear before, between, or after
positional parameters.

```java
app.parse(new String[]{"input.txt", "-v", "--count", "5"});
// the positional parameter is input.txt, -v is set, count is 5
```

A single `-` is treated as a positional value, which makes the usual
stdin/stdout convention possible:

```java
app.parse(new String[]{"-"});   // first positional parameter is "-"
```

### Terminate argument parsing with `--`

`--` ends option parsing. Every token after it is treated as a positional
parameter, even if it starts with `-`. This is how you pass values that would
otherwise look like options, including negative positional numbers:

```java
var cli = new MicroCLI("test")
    .add(new PositionalParameter<>("first", "FIRST", String.class, "a", "First"),
         new PositionalParameter<>("second", "SECOND", String.class, "b", "Second"));

cli.parse(new String[]{"--", "--count", "-5"});
// first: "--count", second: "-5"
```

The registered number of positional parameters still applies. Extra positional
arguments after `--` fail fast with `IllegalArgumentException`.

```java
app.parse(new String[]{"Ada", "--", "Bob"});
// first parameter: Ada, second parameter: Bob (if two are registered)
```

### Read values by ID (or defaults)

```java
boolean verbose = app.getFlag("verbose");
boolean alsoVerbose = app.isFlag("verbose"); // alias for getFlag
int count = app.getInt("itemCount");
double ratio = app.getDouble("ratio");
String text = app.getString("text");
Path file = app.getPath("infile");
```

`isFlag` is a more natural spelling for boolean flags. It behaves exactly like
`getFlag`: an absent registered flag is `false`, and an unknown ID throws
`IllegalArgumentException`.

The generic argument object exposes the same typed value:

```java
int count = countOpt.get();
Class<Integer> type = countOpt.type();
Integer defaultValue = countOpt.defaultValue();
String rawValue = countOpt.rawValue(); // null when not present
```

You can also ask for any compatible type explicitly:

```java
int count = app.get("itemCount", Integer.class);
```

Asking for an incompatible type fails with `IllegalArgumentException`:

```java
app.getInt("verbose"); // verbose is declared as Boolean
```

### Check whether the values are present and valid

```java
boolean present = app.isParsed("itemCount");
boolean valid = app.isValid("itemCount");
```

After a successful `parse`, every present argument is valid. `isValid` is false
for a missing argument. If you work with an argument object directly, its
`valid()` method still reflects whether its raw value can be parsed.

An argument can be present but invalid after a failed `parse`:

```java
try {
  app.parse(new String[]{"--count", "abc"});
} catch (IllegalArgumentException ex) {
  app.isParsed("itemCount"); // true
  app.isValid("itemCount");  // false
  app.getInt("itemCount");   // the default value
}
```

### Look up an argument by ID

```java
Optional<Argument> argument = app.find("itemCount");
Argument required = app.get("itemCount"); // throws for an unknown ID
```

### List what was parsed

```java
Set<Option<?>> options = app.parsedOptions();
List<PositionalParameter<?>> parameters = app.parsedParameters();
```

Both collections contain only arguments that were present on the command line.
`parsedParameters()` preserves registration order.

### Print usage

```java
app.usage();
```

This prints the usage text passed to the constructor followed by every option
and positional parameter, including its names, parameter name, default value,
and description.

A typical help flag:

```java
var help = new Flag("help", "h", "help", "Prints help");
app.add(help);

app.parse(args);

if (app.isFlag("help")) {
  app.usage();
  return;
}
```

Use `usage(PrintStream)` to send the help to another stream:

```java
app.usage(System.err);
```

## Themes and colors

Colored output is optional and has no external dependencies. Three small types
live in `cz.utulnykoutek.microcli.theme`:

- `Color` – a sealed interface with `Color.Ansi` (16 standard colors) and
  `Color.Rgb` (24-bit truecolor)
- `Style` – foreground/background color plus `bold`, `dim`, `italic`, and
  `underline`
- `Theme` – an immutable map from a string tag to a `Style`

`new Style()` and `new Theme()` are **plain**: they emit no ANSI sequences, so
text is printed exactly as before.

A theme can carry an optional name, which is useful for logging and for
configuration:

```java
new Theme()              // plain, name "plain"
new Theme("Dark")        // empty theme named "Dark"
new Theme("Dark", styles) // full theme named "Dark"
```

The name and the underlying styles are available through:

```java
String name = theme.name();
Map<String, Style> styles = theme.styles(); // unmodifiable
```

`withStyle` and `withoutColors` preserve the theme name; `withoutColors` only
drops the styles.

### Define a theme

```java
import cz.utulnykoutek.microcli.theme.Color;
import cz.utulnykoutek.microcli.theme.Style;
import cz.utulnykoutek.microcli.theme.Theme;

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

var app = new MicroCLI("mytool - processes an input file").theme(theme);
```

The library uses these standard tags for its own output:

| Tag | Used for |
| --- | --- |
| `title` | usage text passed to the constructor |
| `heading` | the `Usage:` heading |
| `option` | option names such as `-v, --verbose` |
| `parameter` | parameter names such as `<COUNT>` or `[INPUT]` |
| `default` | default values |
| `description` | argument descriptions |
| `error`, `warning`, `success`, `info`, `muted`, `emphasis` | application output |

Unknown tags are rendered as plain text, so applications can use their own tags.
`theme.withStyle(tag, style)` returns a new theme and never mutates the original.

### Paint your own output

```java
System.out.println(app.paint("success", "Done"));
System.err.println(app.paint("error", "File not found"));
```

`paint` returns the text unchanged when colors are disabled.

### When are colors enabled?

Colors are detected automatically and are enabled only when all of these hold:

- standard output is a terminal,
- `NO_COLOR` is not set to a non-empty value,
- `TERM` is not `dumb`.

You can override the detection explicitly:

```java
app.colors(true);   // force ANSI output, useful in tests
app.colors(false);  // always plain output
```

The `NO_COLOR` standard flag (registered with `stdFlags(NO_COLOR)`) sets
`colors(false)` when `--no-color` is present. `parseAndHandle` checks
`--no-color` before printing help, so `--no-color --help` is always plain.

`usage(PrintStream)` and `paint` both respect the color flag, and the default
`MicroCLI` theme is plain, so existing applications are unaffected.

## Easy API

The optional `cz.utulnykoutek.microcli.easy` package provides `EasyCLI`, a
fluent convenience layer on top of `MicroCLI`. `EasyCLI` extends `MicroCLI`, so
all parsing and value-reading methods are still available.

```java
import cz.utulnykoutek.microcli.easy.EasyCLI;

import java.nio.file.Path;

import static cz.utulnykoutek.microcli.easy.StdFlag.DEBUG;
import static cz.utulnykoutek.microcli.easy.StdFlag.DRY_RUN;
import static cz.utulnykoutek.microcli.easy.StdFlag.HELP;
import static cz.utulnykoutek.microcli.easy.StdFlag.NO_COLOR;
import static cz.utulnykoutek.microcli.easy.StdFlag.VERBOSE;
import static cz.utulnykoutek.microcli.easy.StdFlag.VERSION;

var cli = new EasyCLI("mytool - processes an input file")
    .stdFlags(HELP, VERBOSE, VERSION, DRY_RUN, DEBUG, NO_COLOR)
    .optInt("l", "level", 1, "Log level")
    .optPath("o", "outfile", Path.of("out.txt"), "Output file")
    .optString("p", "prefix", "", "Line prefix")
    .posString("input", "Input file");

if (cli.parseAndHandle(args, "mytool 1.0.0")) {
  return;
}

int level = cli.getInt("level");
Path outfile = cli.getPath("outfile");
String input = cli.getString("input");

if (cli.isFlag(VERBOSE)) System.out.println("level = " + level);
if (cli.isFlag(DEBUG)) System.out.println("debugging enabled");
if (cli.isFlag(DRY_RUN)) System.out.println("dry run, writing nothing to " + outfile);
if (cli.isFlag(NO_COLOR)) System.out.println("color output disabled");
```

### Standard flags

`stdFlags(...)` registers preconfigured common flags:

| Constant | ID | Short | Long | Description |
| --- | --- | --- | --- | --- |
| `HELP` | `help` | `-h` | `--help` | Prints help |
| `VERBOSE` | `verbose` | `-v` | `--verbose` | Produces verbose output |
| `VERSION` | `version` | `-V` | `--version` | Prints version |
| `DRY_RUN` | `dryRun` | `-n` | `--dry-run` | Runs without making changes |
| `DEBUG` | `debug` | `-d` | `--debug` | Produces debug output |
| `NO_COLOR` | `noColor` | — | `--no-color` | Disables colored output |

Read them either through the enum or through their ID:

```java
boolean help = cli.isFlag(HELP);
boolean helpToo = cli.isFlag("help");
```

`parseAndHandle(args, version)` scans the raw arguments before normal parsing.
If `--help`/`-h` is present and `HELP` is registered, it prints usage and returns
`true`. If `--version`/`-V` is present and `VERSION` is registered, it prints
the supplied version string and returns `true`. Otherwise it calls `parse` and
returns `false`. This means `--help` works even when the other arguments are
invalid:

```java
cli.parseAndHandle(new String[]{"--help", "--count", "not-a-number"}, "mytool 1.0.0");
// prints usage, does not validate --count
```

The pre-scan stops at `--`, so `-- --help` is treated as a positional value.
Unregistered standard flags are not handled specially.

`handleHelpAndVersion(version)` is the older post-parse variant: it checks the
already parsed flags and returns `true` if help or version was handled. It does
not call `System.exit`, so the caller decides whether to continue.

### Quick options and positional parameters

The typed shortcuts derive the argument ID from the long name:

```java
cli.optInt("l", "level", 1, "Log level")        // ID: level
   .optDouble("r", "ratio", 1.5, "Ratio")       // ID: ratio
   .optString("p", "prefix", "", "Prefix")      // ID: prefix
   .optPath("o", "outfile", Path.of("out.txt"), "Output file"); // ID: outfile
```

Omit the short name to create a long-only option:

```java
cli.optInt("level", 1, "Log level")             // --level only
   .optString("output", "out.txt", "Output file");
```

The parameter name shown in usage is derived from the long name and
upper-cased, for example `level` becomes `LEVEL`. Pass it explicitly if needed:

```java
cli.optInt("l", "level", "LOG_LEVEL", 1, "Log level");
```

Positional parameters use the name as both ID and the basis for the display
name:

```java
cli.posString("input")                          // ID: input, display: INPUT
   .posString("output", "out.txt", "Output file") // default: out.txt
   .posInt("count", 1, "How many")
   .posDouble("ratio", 1.0, "Ratio")
   .posPath("config", Path.of("config.txt"), "Config file");
```

`posString(name)` uses `null` as the default value. The overload with a
description, `posString(name, description)`, also keeps the default `null`.

For custom types or a stable ID independent of the long name, use the generic
methods:

```java
cli.opt("logLevel", "l", "level", "LEVEL",
        Integer.class, 1, "Log level")
   .opt("date", "d", "date", "DATE",
        LocalDate.class, LocalDate::parse, LocalDate.now(), "Date to process")
   .pos("start", "START",
        LocalDate.class, LocalDate::parse, LocalDate.now(), "Start date");
```

Custom flags can be added with:

```java
cli.flag("q", "quiet", "Suppresses non-error output"); // ID: quiet
```

Duplicate IDs and option names are still rejected, and the core `MicroCLI`
remains available if you need its lower-level API.

## Error handling

`parse` throws `IllegalArgumentException` for:

- an unknown option (a token starting with `-` that matches no option),
- a missing value for a valued option,
- an extra positional argument,
- a value that cannot be converted to the declared type.

```java
app.parse(new String[]{"--unknown"});
// IllegalArgumentException: Unknown option: --unknown

app.parse(new String[]{"--count"});
// IllegalArgumentException: Missing value for option --count

app.parse(new String[]{"Ada", "Bob"});
// IllegalArgumentException: Unexpected positional argument: Bob
```

## Limitations

- _MicroCLI_ accepts only a fixed number of positional parameters. Extra
  positional arguments fail fast, including after `--`.
- Negative positional numbers must be passed after `--` (or use an option)
  because a leading `-` otherwise looks like an option.
- Numeric options and positional parameters need a non-null default value.
  `String` and `Path` arguments may use `null`.
- `--count=5` is supported for long options; the short form `-c=5` is not.
- Combined short flags such as `-abc` are not supported.
- Short and long option names are matched exactly; `-vv` and `--verbose-extra`
  do not match `-v` / `--verbose`.
- Flags cannot be negated; a present flag is always `true`.
- Invalid values of present arguments abort parsing with
  `IllegalArgumentException`.

## Known bugs

No known bugs at the moment. Please report issues at
<https://github.com/tpitner/microcli/issues>.
