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
var countOpt = new IntOption("itemCount", "c", "count", "COUNT", 2, "Count of numbers to print");
var filePar = new PathPositional("infile", "INFILE", Path.of("in.txt"), "File to read");
```

Here `itemCount` is the ID, while `-c` and `--count` are the CLI names. The
constructor layouts are:

- `Flag(id, shortName, longName, description)`
- unary option: `(id, shortName, longName, paramName, defaultValue, description)`
- positional parameter: `(id, displayName, defaultValue, description)`

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

### Read values by ID (or defaults)
```java
boolean verbose = app.getFlag("verbose");
int count = app.getInt("itemCount");
Path file = app.getPath("infile");
```

The typed getters on the original objects still work as well:

```java
int count = countOpt.getInt();
```

### Check whether the values are present and valid
```java
if(app.isValid("itemCount")) { // was the value a valid integer?
 ...
}
boolean present = app.isParsed("itemCount");
```

### Look up an argument by ID
```java
Optional<Argument> argument = app.find("itemCount");
Argument required = app.get("itemCount"); // throws for an unknown ID
```

IDs must be unique across all options and positional parameters; short names
and long names must be unique among options. Duplicate registrations fail fast
with `IllegalArgumentException`.

## Limitations
- _MicroCLI_ accepts only a fixed number of positional parameters.
- Cannot specify `Option` without a default value. Workaround: specify any value as default but always check `isValid(id)` before using its data.
- Option values must be separate tokens; `--count=5` is not supported.
- Combined short flags such as `-abc` are not supported.

## Known bugs
