import java.util.SortedSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MicroCLI {

  private String usage;
  private SortedSet<Option> options = new TreeSet<Option>();
  private List<Parameter> parameters = new ArrayList<Parameter>();

  public MicroCLI(String usage) {
    this.usage = usage;
  }

  public void parse(String[] args) {
    int index = 0;
    int next = index;
    parseOptions: while (index < args.length) {
      // do for all args
      Iterator<Option> oi = options.iterator();
      while (oi.hasNext()) {
        // try whether next Option matches the arg
        Option o = oi.next();
        next = o.match(args, index);
        if(next < 0) break parseOptions; // was not option but parameter
        if(next > index) break; // Option o matched this arg
      }
      if(next == index && !oi.hasNext()) 
        throw new IllegalArgumentException(args[index]);  
      // shift to next argument if any
      index = next;
    } // nothing left to match
    if(next < 0) { 
      // was parameter, not parse parameters
      index = -next;
      int paramIndex = 0;
      while (index < args.length && paramIndex < parameters.size()) {
        //System.out.println("Parsing param " + args[index]);
        Parameter p = parameters.get(paramIndex);
        index = p.match(args, index);
        paramIndex++;
      }
    }
  }

  public MicroCLI add(Option... o) {
    options.addAll(Arrays.asList(o));
    return this;
  }

  public MicroCLI add(Parameter... o) {
    parameters.addAll(Arrays.asList(o));
    return this;
  }

  public Set<Option> parsedOptions() {
    return options.stream().filter(o -> o.isParsed()).collect(Collectors.toUnmodifiableSet());
  }

  public List<Parameter> parsedParameters() {
    return parameters.stream().filter(o -> o.isParsed()).collect(Collectors.toUnmodifiableList());
  }

  public void usage() {
    System.out.println(usage + "\nUsage:");
    options.forEach(o -> o.usage());
    parameters.forEach(o -> o.usage());
  }
}
