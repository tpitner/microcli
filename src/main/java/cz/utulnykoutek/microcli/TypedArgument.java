package cz.utulnykoutek.microcli;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public abstract class TypedArgument<T> extends Argument {

  private static final Map<Class<?>, Function<String, ?>> PARSERS = Map.of(
      Integer.class, Integer::valueOf,
      Double.class, Double::valueOf,
      String.class, Function.identity(),
      Path.class, Path::of,
      Boolean.class, Boolean::valueOf);

  private final Class<T> type;
  private final T defaultValue;
  private final Function<String, T> parser;

  private String rawValue;
  private T parsedValue;

  protected TypedArgument(String id, Class<T> type, T defaultValue, String description) {
    this(id, type, defaultValue, parserFor(type), description);
  }

  protected TypedArgument(String id, Class<T> type, T defaultValue, Function<String, T> parser,
                          String description) {
    super(id, description);
    this.type = Objects.requireNonNull(type, "Argument type cannot be null");
    this.parser = Objects.requireNonNull(parser, "Argument parser cannot be null");

    if(defaultValue == null && type != String.class && type != Path.class) {
      throw new IllegalArgumentException(
          "Only String and Path arguments can have a null default value");
    }
    if(defaultValue != null && !type.isInstance(defaultValue)) {
      throw new IllegalArgumentException("Default value for argument '" + id + "' must be "
          + type.getName() + ", but was " + defaultValue.getClass().getName());
    }

    this.defaultValue = defaultValue;
  }

  @SuppressWarnings("unchecked")
  private static <T> Function<String, T> parserFor(Class<T> type) {
    Objects.requireNonNull(type, "Argument type cannot be null");
    Function<String, ?> parser = PARSERS.get(type);
    if(parser == null) {
      throw new IllegalArgumentException("No built-in parser for type " + type.getName()
          + "; provide a parser explicitly");
    }
    return (Function<String, T>) parser;
  }

  protected final void setRawValue(String rawValue) {
    this.rawValue = rawValue;
  }

  public String rawValue() { return rawValue; }

  public Class<T> type() { return type; }

  public T defaultValue() { return defaultValue; }

  public T get() { return value(); }

  public String defaultData() { return String.valueOf(defaultValue); }

  @Override public boolean isParsed() { return rawValue != null; }

  @Override public boolean valid() {
    if(!isParsed()) return false;
    try {
      T value = parser.apply(rawValue);
      if(!type.isInstance(value)) return false;
      parsedValue = value;
      return true;
    } catch(RuntimeException ex) {
      return false;
    }
  }

  @Override public T value() {
    return valid() ? parsedValue : defaultValue;
  }

  @Override public String toString() {
    return type.getSimpleName() + "(" + (isParsed() ? "" + value() : "EMPTY") + ") " + id();
  }
}
