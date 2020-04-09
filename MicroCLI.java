import java.util.SortedSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MicroCLI {

  private String usage;
  private SortedSet<Option> options = new TreeSet<Option>();

  public MicroCLI(String usage) {
    this.usage = usage;
  }

  public void parse(String[] args) {
    int index = 0;
    while (index < args.length) {
      // do for all args
      Iterator<Option> oi = options.iterator();
      int next = index;
      while (oi.hasNext()) {
        // try whether next Option matches the arg
        Option o = oi.next();
        next = o.match(args, index);
        if(next > index) break; // Option o matched this arg
      }
      if(next == index && !oi.hasNext()) throw new IllegalArgumentException(args[index]);  
      // shift to next argument if any
      index = next;
    } // nothing left to match
  }

  public MicroCLI add(Option... o) {
    options.addAll(Arrays.asList(o));
    return this;
  }

  public Set<Option> parsedOptions() {
    return options.stream().filter(o -> o.isParsed()).collect(Collectors.toUnmodifiableSet());
  }

  public void usage() {
    System.out.println(usage + "\nUsage:");
    options.forEach(o -> o.usage());
  }
}
