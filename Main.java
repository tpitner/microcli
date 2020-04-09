import java.nio.file.Path;
import java.util.Arrays;

public class Main {
  public static void main(String[] args) {

    var cli = new MicroCLI("Demo for MicroCLI. Shows all Option types:\nBooleanOption\tisTrue()\nIntOption\tgetInt()\nDoubleOption\tgetDouble()\nStringOption\tgetString()\nPathOption\tgetPath()\nThe demo prints <count> random (0..1) numbers + <add>. Output goes to <path>. <help> prints help.\n");

    var verbose = new BooleanOption("v", "verbose", "Produces verbose info during processing");
    var help = new BooleanOption("h", "help", "Prints help");
    var count = new IntOption("c", "count", "COUNT", 2, "Count of numbers to print");
    var prefix = new StringOption("p", "prefix", "PREFIX", "", "Prefix to print before each number");
    var add = new DoubleOption("a", "add", "NUMBER", 10.0, "Constant to add to each number");
    var path = new PathOption("o", "out", "OUTFILE", Path.of("outfile.txt"), "File to save the result");

    var greeting = new StringParameter("GREETING", "Hi", "Greeting at beginning");
    var farewell = new StringParameter("FAREWELL", "Bye", "Farewell at end");
    //var file = new PathParameter("INFILE", Path.of("in.txt"), "File to read");
    var num = new DoubleParameter("NUM", 1.2, "Number to process");
    var howmany = new IntParameter("HOWMANY", 2, "How many");

    cli.add(verbose, help, count, prefix, add, path);
    cli.add(greeting, farewell, num, howmany);

    var arguments = new String[] { "-a", "100x.0", "--prefix", "### ", "--help", "-v", "-o", "::out-file.txt", "--count", "5c" , "-a", "1000", "param1", "param2", "7.8", "9" };
    System.out.println("Raw arguments " + Arrays.asList(arguments));

    cli.parse(arguments);

    System.out.println("Parsed options " + cli.parsedOptions());
    System.out.println("Parsed parameters " + cli.parsedParameters());
    System.out.println();
    
    if(help.isTrue()) cli.usage();
    if(path.valid()) System.out.println("Goes to file " + path.getPath());

    for(int i = 0; i < count.getInt(); i++) {
      double rnd = Math.random() + add.getDouble();
      System.out.println(prefix.getString() + i + " = " + rnd);
    }
  }  
}