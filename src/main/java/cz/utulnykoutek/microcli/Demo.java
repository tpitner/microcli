package cz.utulnykoutek.microcli;

import java.nio.file.Path;
import java.util.Arrays;

public class Demo {
    static void main(String[] args) {

        var cli = new MicroCLI("Demo for MicroCLI. Shows all argument types:\nFlag\tgetFlag()\nValuedOption\tgetInt()/getDouble()/getString()/getPath()\nPositionalParameter\tgetInt()/getDouble()/getString()/getPath()\nValues are read by their IDs, independent of the CLI names.\nThe demo prints <count> random (0..1) numbers + <add>. Output goes to <path>. <help> prints help.\n");

        var verbose = new Flag("verbose", "v", "verbose", "Produces verbose info during processing");
        var help = new Flag("help", "h", "help", "Prints help");
        var count = new ValuedOption<>("itemCount", "c", "count", "COUNT",
            Integer.class, 2, "Count of numbers to print");
        var prefix = new ValuedOption<>("prefix", "p", "prefix", "PREFIX",
            String.class, "", "Prefix to print before each number");
        var add = new ValuedOption<>("add", "a", "add", "NUMBER",
            Double.class, 10.0, "Constant to add to each number");
        var path = new ValuedOption<>("outputFile", "o", "out", "OUTFILE",
            Path.class, Path.of("outfile.txt"), "File to save the result");

        var greeting = new PositionalParameter<>("greeting", "GREETING",
            String.class, "Hi", "Greeting at beginning");
        var farewell = new PositionalParameter<>("farewell", "FAREWELL",
            String.class, "Bye", "Farewell at end");
        //var file = new PositionalParameter<>("infile", "INFILE", Path.class, Path.of("in.txt"), "File to read");
        var num = new PositionalParameter<>("number", "NUM",
            Double.class, 1.2, "Number to process");
        var howMany = new PositionalParameter<>("howMany", "HOWMANY",
            Integer.class, 2, "How many");

        cli.add(verbose, help, count, prefix, add, path);
        cli.add(greeting, farewell, num, howMany);

        var arguments = new String[]{"-a", "100.0", "--prefix", "### ", "--help", "-v",
            "-o", "out-file.txt", "--count", "5", "-a", "1000",
            "param1", "param2", "7.8", "9"};
        System.out.println("Raw arguments " + Arrays.asList(arguments));

        cli.parse(arguments);

        System.out.println("Parsed options " + cli.parsedOptions());
        System.out.println("Parsed parameters " + cli.parsedParameters());
        System.out.println();

        if(cli.getFlag("help")) cli.usage();
        if(cli.isValid("outputFile")) System.out.println("Goes to file " + cli.getPath("outputFile"));

        for (int i = 0; i < cli.getInt("itemCount"); i++) {
            double rnd = Math.random() + cli.getDouble("add");
            System.out.println(cli.getString("prefix") + i + " = " + rnd);
        }
    }
}
