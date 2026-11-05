package cz.utulnykoutek.microcli;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MicroCLI {

  private final String usage;
  private final List<Option> options = new ArrayList<>();
  private final List<PositionalParameter> positionalParameters = new ArrayList<>();
  private final Map<String, Argument> argumentsById = new LinkedHashMap<>();
  private final Map<String, Option> optionsByShortName = new LinkedHashMap<>();
  private final Map<String, Option> optionsByLongName = new LinkedHashMap<>();

  public MicroCLI(String usage) {
    this.usage = usage;
  }

  public void parse(String[] args) {
    int index = 0;
    boolean positionals = false;

    parseOptions: while (index < args.length) {
      Iterator<Option> oi = options.iterator();
      int next = index;

      while (oi.hasNext()) {
        Option o = oi.next();
        next = o.match(args, index);
        if(next < 0) {
          positionals = true;
          break parseOptions;
        }
        if(next > index) break;
      }

      if(next == index) {
        if(!args[index].startsWith("-")) {
          positionals = true;
          break parseOptions;
        }
        throw new IllegalArgumentException("Unknown option: " + args[index]);
      }

      index = next;
    }

    if(positionals) {
      int paramIndex = 0;
      while (index < args.length && paramIndex < positionalParameters.size()) {
        PositionalParameter p = positionalParameters.get(paramIndex);
        index = p.match(args, index);
        paramIndex++;
      }
    }
  }

  public MicroCLI add(Option... opts) {
    for (Option option : opts) {
      register(option);
      options.add(option);
    }
    return this;
  }

  public MicroCLI add(PositionalParameter... params) {
    for (PositionalParameter parameter : params) {
      register(parameter);
      positionalParameters.add(parameter);
    }
    return this;
  }

  private void register(Argument argument) {
    if(argumentsById.containsKey(argument.id())) {
      throw new IllegalArgumentException("Duplicate argument id: " + argument.id());
    }
    if(argument instanceof Option option) {
      if(optionsByShortName.containsKey(option.shortName())) {
        throw new IllegalArgumentException("Duplicate option short name: -" + option.shortName());
      }
      if(optionsByLongName.containsKey(option.longName())) {
        throw new IllegalArgumentException("Duplicate option long name: --" + option.longName());
      }
    }

    argumentsById.put(argument.id(), argument);
    if(argument instanceof Option option) {
      optionsByShortName.put(option.shortName(), option);
      optionsByLongName.put(option.longName(), option);
    }
  }

  public Optional<Argument> find(String id) {
    return Optional.ofNullable(argumentsById.get(requireId(id)));
  }

  public Argument get(String id) {
    String argumentId = requireId(id);
    Argument argument = argumentsById.get(argumentId);
    if(argument == null) {
      throw new IllegalArgumentException("Unknown argument id: " + argumentId);
    }
    return argument;
  }

  public <T> T get(String id, Class<T> type) {
    if(type == null) throw new NullPointerException("Value type cannot be null");
    Object value = get(id).value();
    if(value == null) return null;
    if(!type.isInstance(value)) {
      throw new IllegalArgumentException("Argument '" + id + "' has value type "
          + value.getClass().getName() + ", not " + type.getName());
    }
    return type.cast(value);
  }

  public int getInt(String id) { return get(id, Integer.class); }

  public double getDouble(String id) { return get(id, Double.class); }

  public String getString(String id) { return get(id, String.class); }

  public Path getPath(String id) { return get(id, Path.class); }

  public boolean getFlag(String id) { return get(id, Boolean.class); }

  public boolean isParsed(String id) {
    return find(id).map(Argument::isParsed).orElse(false);
  }

  public boolean isValid(String id) {
    return find(id).map(Argument::valid).orElse(false);
  }

  public Set<Option> parsedOptions() {
    return options.stream().filter(Argument::isParsed).collect(Collectors.toUnmodifiableSet());
  }

  public List<PositionalParameter> parsedParameters() {
    return positionalParameters.stream().filter(Argument::isParsed).collect(Collectors.toUnmodifiableList());
  }

  public void usage() {
    System.out.println(usage + "\nUsage:");
    options.forEach(Option::usage);
    positionalParameters.forEach(PositionalParameter::usage);
  }

  private String requireId(String id) {
    return Argument.requireNotNullBlank(id, "Argument id cannot be null or blank");
  }
}
