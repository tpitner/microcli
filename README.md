[![Run on Repl.it](https://repl.it/badge/github/tpitner/microcli)](https://repl.it/github/tpitner/microcli)
# microcli 
Ultra light-weight command line arguments parser and validator for Java

## Requirements
- Java 8+

## Usage

### Create MicroCLI
```java
var MicroCLI app = new MicroCLI("Usage of your application");
```
### Create options
```java
var verboseOpt = new BooleanOption("v", "verbose", "Produces verbose info during processing");
var countOpt = new IntOption("c", "count", "COUNT", 2, "Count of numbers to print");
```

### Create parameters
```java
var filePar = new PathParameter("INFILE", Path.of("in.txt"), "File to read");
```

### Add options
```java
app.add(verboseOpt, countOpt);
// add parameters
app.add(filePar);
```

### Parse arguments
```java
app.parse(args);
```
### Read values (or defaults)
```java
boolean verbose = verboseOpt.isTrue();
int count = countOpt.getInt();
Path file = filePar.getPath();
```
### Check whether the options/params read are valid
```java
if(count.valid()) { // was the option 'count' valid integer?
 ...
} 
```

## Limitations
- _MicroCLI_ accepts only fixed number of parameters.
- Cannot specify `Option` without default value. Workaround: specify any value as default but always check `option.valid()` before using its data.

## Known bugs
