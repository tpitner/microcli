package cz.utulnykoutek.microcli;

import java.nio.file.Path;
import java.util.Arrays;

public class Demo {
    public static void main(String[] args) {

        var cli = new MicroCLI("Demo for MicroCLI. Shows all Option types:\nFlag\tgetFlag()\nIntOption\tgetInt()\nDoubleOption\tgetDouble()\nStringOption\tgetString()\nPathOption\tgetPath()\nValues are read by their IDs, independent of the CLI names.\nThe demo prints <count> random (0..1) numbers + <add>. Output goes to <path>. <help> prints help.\n");

        var verbose = new Flag("verbose", "v", "verbose", "Produces verbose info during processing");
        var help = new Flag("help", "h", "help", "Prints help");
        var count = new IntOption("itemCount", "c", "count", "COUNT", 2, "Count of numbers to print");
        var prefix = new StringOption("prefix", "p", "prefix", "PREFIX", "", "Prefix to print before each number");
        var add = new DoubleOption("add", "a", "add", "NUMBER", 10.0, "Constant to add to each number");
        var path = new PathOption("outputFile", "o", "out", "OUTFILE", Path.of("outfile.txt"), "File to save the result");

        var greeting = new StringPositional("greeting", "GREETING", "Hi", "Greeting at beginning");
        var farewell = new StringPositional("farewell", "FAREWELL", "Bye", "Farewell at end");
        //var file = new PathPositional("infile", "INFILE", Path.of("in.txt"), "File to read");
        var num = new DoublePositional("number", "NUM", 1.2, "Number to process");
        var howMany = new IntPositional("howMany", "HOWMANY", 2, "How many");

        cli.add(verbose, help, count, prefix, add, path);
        cli.add(greeting, farewell, num, howMany);

        var arguments = new String[]{"-a", "100x.0", "--prefix", "### ", "--help", "-v", "-o", "::out-file.txt", "--count", "5c", "-a", "1000", "param1", "param2", "7.8", "9"};
        System.out.println("Raw arguments " + Arrays.asList(arguments));

        cli.parse(arguments);

        System.out.println("Parsed options " + cli.parsedOptions());
        System.out.println("Parsed parameters " + cli.parsedParameters());
        System.out.println();

        if (cli.getFlag("help")) cli.usage();
        if (cli.isValid("outputFile")) System.out.println("Goes to file " + cli.getPath("outputFile"));

        for (int i = 0; i < cli.getInt("itemCount"); i++) {
            double rnd = Math.random() + cli.getDouble("add");
            System.out.println(cli.getString("prefix") + i + " = " + rnd);
        }
    }
}
