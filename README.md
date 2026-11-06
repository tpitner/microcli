[![Run on Repl.it](https://repl.it/badge/github/tpitner/microcli)](https://repl.it/github/tpitner/microcli)
# microcli 
Ultra light-weight command line arguments parser and validator for Java

## Requirements
- Java 27+ (Homebrew OpenJDK 27)
- Maven 3.8+

## Build

This is a standard Maven project:

```sh
mvn clean package   # builds the JAR into target/
mvn test            # runs the test suite
java -jar target/microcli-*.jar   # runs the demo (Demo)
```

If JDK 27 is not your default, point Maven at the Homebrew JDK:

```sh
JAVA_HOME=$(brew --prefix openjdk) mvn clean package
```

## Usage

### Create MicroCLI
```java
MicroCLI app = new MicroCLI("Usage of your application");
```

### Create options and parameters

Every option and positional parameter has a mandatory, unique **ID**. The ID is
used to read values and is completely independent of the command-line names:

```java
var verboseOpt = new Flag("verbose", "v", "verbose", "Produces verbose info during processing");
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
arguments need a non-null default value.

### Add options and parameters
```java
app.add(verboseOpt, countOpt);
// add parameters
app.add(filePar);
```

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

`--` terminates argument parsing; everything after it is silently ignored. Only
the registered number of positional parameters may appear before `--`; any
extra positional argument fails fast:

```java
app.parse(new String[]{"input.txt", "--", "-v", "ignored"});
// "-v" and "ignored" are ignored
```

### Read values by ID (or defaults)
```java
boolean verbose = app.getFlag("verbose");
int count = app.getInt("itemCount");
Path file = app.getPath("infile");
```

The generic argument object exposes the same typed value:

```java
int count = countOpt.get();
Class<Integer> type = countOpt.type();
```

You can also ask for any type explicitly:

```java
int count = app.get("itemCount", Integer.class);
```

### Check whether the values are present and valid
```java
boolean present = app.isParsed("itemCount");
boolean valid = app.isValid("itemCount");
```

After a successful `parse`, every present argument is valid. `isValid` is false
for a missing argument. If you work with an argument object directly, its
`valid()` method still reflects whether its raw value can be parsed.

### Look up an argument by ID
```java
Optional<Argument> argument = app.find("itemCount");
Argument required = app.get("itemCount"); // throws for an unknown ID
```

IDs must be unique across all options and positional parameters; short names
and long names must be unique among options. Duplicate registrations fail fast
with `IllegalArgumentException`.

## Limitations
- _MicroCLI_ accepts only a fixed number of positional parameters. Extra
  positional arguments before `--` fail fast.
- Numeric options and positional parameters need a non-null default value.
  `String` and `Path` arguments may use `null`.
- Option values must be separate tokens; `--count=5` is not supported.
- Combined short flags such as `-abc` are not supported.
- Invalid values of present arguments abort parsing with `IllegalArgumentException`.

## Known bugs
