package cz.utulnykoutek.microcli;

import cz.utulnykoutek.microcli.theme.Theme;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MicroCLI {

  private final String usage;
  private final List<Option<?>> options = new ArrayList<>();
  private final List<PositionalParameter<?>> positionalParameters = new ArrayList<>();
  private final Map<String, Argument> argumentsById = new LinkedHashMap<>();
  private final Map<String, Option<?>> optionsByShortName = new LinkedHashMap<>();
  private final Map<String, Option<?>> optionsByLongName = new LinkedHashMap<>();
  private Theme theme = new Theme();
  private boolean colors = detectColors();

  public MicroCLI(String usage) {
    this.usage = usage;
  }

  public MicroCLI theme(Theme theme) {
    this.theme = Objects.requireNonNull(theme, "Theme cannot be null");
    return this;
  }

  public Theme theme() {
    return theme;
  }

  public MicroCLI colors(boolean colors) {
    this.colors = colors;
    return this;
  }

  public boolean colors() {
    return colors;
  }

  /**
   * Paints {@code text} with the style registered for {@code tag}, or returns
   * it unchanged when colors are disabled.
   */
  public String paint(String tag, String text) {
    return colors ? theme.paint(tag, text) : text;
  }

  private boolean detectColors() {
    var console = System.console();
    return detectColors(console != null && console.isTerminal(), System.getenv());
  }

  boolean detectColors(boolean terminal, Map<String, String> env) {
    if (!terminal) return false;
    String noColor = env.get("NO_COLOR");
    if (noColor != null && !noColor.isEmpty()) return false;
    return !"dumb".equalsIgnoreCase(env.get("TERM"));
  }

  public void parse(String[] args) {
    if(args == null) throw new NullPointerException("Arguments cannot be null");
    int index = 0;
    int paramIndex = 0;
    boolean optionsEnded = false;

    while (index < args.length) {
      String arg = args[index];

      if(!optionsEnded && isArgumentTerminator(arg)) {
        optionsEnded = true;
        index++;
        continue;
      }

      if(!optionsEnded && !isPositionalToken(arg)) {
        int next = index;

        for (Option<?> option : options) {
          next = option.match(args, index);
          if(next > index) break;
        }

        if(next > index) {
          index = next;
          continue;
        }

        throw new IllegalArgumentException("Unknown option: " + arg);
      }

      if(paramIndex >= positionalParameters.size()) {
        throw new IllegalArgumentException(
            "Unexpected positional argument: " + arg);
      }
      PositionalParameter<?> parameter = positionalParameters.get(paramIndex);
      index = parameter.match(args, index);
      paramIndex++;
    }

    validateParsedArguments();
  }

  private static boolean isArgumentTerminator(String arg) {
    return "--".equals(arg);
  }

  private static boolean isPositionalToken(String arg) {
    return "-".equals(arg) || !arg.startsWith("-");
  }

  private void validateParsedArguments() {
    for (Argument argument : argumentsById.values()) {
      if(!argument.isParsed() || argument.valid()) continue;

      if(argument instanceof TypedArgument<?> typed) {
        throw new IllegalArgumentException("Invalid value for argument '" + argument.id()
            + "': '" + typed.rawValue() + "' is not a valid "
            + typed.type().getSimpleName() + " value");
      }
      throw new IllegalArgumentException("Invalid value for argument '" + argument.id() + "'");
    }
  }

  public MicroCLI add(Option<?>... opts) {
    for (Option<?> option : opts) {
      register(option);
      options.add(option);
    }
    return this;
  }

  public MicroCLI add(PositionalParameter<?>... params) {
    for (PositionalParameter<?> parameter : params) {
      register(parameter);
      positionalParameters.add(parameter);
    }
    return this;
  }

  private void register(Argument argument) {
    if(argumentsById.containsKey(argument.id())) {
      throw new IllegalArgumentException("Duplicate argument id: " + argument.id());
    }
    if(argument instanceof Option<?> option) {
      if(option.hasShortName() && optionsByShortName.containsKey(option.shortName())) {
        throw new IllegalArgumentException("Duplicate option short name: -" + option.shortName());
      }
      if(optionsByLongName.containsKey(option.longName())) {
        throw new IllegalArgumentException("Duplicate option long name: --" + option.longName());
      }
    }

    argumentsById.put(argument.id(), argument);
    if(argument instanceof Option<?> option) {
      if(option.hasShortName()) {
        optionsByShortName.put(option.shortName(), option);
      }
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
    Argument argument = get(id);
    if(argument instanceof TypedArgument<?> typed && !type.isAssignableFrom(typed.type())) {
      throw new IllegalArgumentException("Argument '" + id + "' has declared type "
          + typed.type().getName() + ", not " + type.getName());
    }
    Object value = argument.value();
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

  public boolean isFlag(String id) { return getFlag(id); }

  public boolean isParsed(String id) {
    return find(id).map(Argument::isParsed).orElse(false);
  }

  public boolean isValid(String id) {
    return find(id).map(Argument::valid).orElse(false);
  }

  public Set<Option<?>> parsedOptions() {
    return options.stream().filter(Argument::isParsed).collect(Collectors.toUnmodifiableSet());
  }

  public List<PositionalParameter<?>> parsedParameters() {
    return positionalParameters.stream().filter(Argument::isParsed).toList();
  }

  public void usage() {
    usage(System.out);
  }

  public void usage(PrintStream out) {
    Theme effective = colors ? theme : theme.withoutColors();
    out.println(effective.paint("title", usage));
    out.println(effective.paint("heading", "Usage:"));
    options.forEach(option -> option.usage(out, effective));
    positionalParameters.forEach(parameter -> parameter.usage(out, effective));
  }

  private String requireId(String id) {
    return Argument.requireNotNullBlank(id, "Argument id cannot be null or blank");
  }
}
