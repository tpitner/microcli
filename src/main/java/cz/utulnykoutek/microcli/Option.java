package cz.utulnykoutek.microcli;

import java.util.function.Function;

public abstract class Option<T> extends TypedArgument<T> {

  private final String shortName;
  private final String longName;
  private final int arity;

  protected Option(String id, String shortName, String longName, int arity,
                   Class<T> type, T defaultValue, String description) {
    super(id, type, defaultValue, description);
    this.shortName = normalizeShortName(shortName);
    this.longName = requireNotNullBlank(longName, "Option long name cannot be null or blank");
    if(arity < 0 || arity > 1) {
      throw new IllegalArgumentException("Only options with arity 0 or 1 are supported");
    }
    this.arity = arity;
  }

  protected Option(String id, String shortName, String longName, int arity,
                   Class<T> type, Function<String, T> parser, T defaultValue, String description) {
    super(id, type, defaultValue, parser, description);
    this.shortName = normalizeShortName(shortName);
    this.longName = requireNotNullBlank(longName, "Option long name cannot be null or blank");
    if(arity < 0 || arity > 1) {
      throw new IllegalArgumentException("Only options with arity 0 or 1 are supported");
    }
    this.arity = arity;
  }

  @Override
  public int match(String[] args, int index) {
    String arg = checkArg(args, index);
    // this is no Option
    if(!arg.startsWith("-")) return -index;

    String longWithValue = "--" + longName + "=";
    if(arity == 1 && arg.startsWith(longWithValue)) {
      setRawValue(arg.substring(longWithValue.length()));
      return index + 1;
    }

    boolean shortMatch = shortName != null && arg.equals("-" + shortName);
    boolean longMatch = arg.equals("--" + longName);
    if(!shortMatch && !longMatch) return index;

    if(arity == 0) {
      setRawValue("true");
      return index + 1;
    }

    if(index + 1 >= args.length) {
      throw new IllegalArgumentException("Missing value for option " + arg);
    }
    setRawValue(args[index + 1]);
    return index + 2;
  }

  public String shortName() { return shortName; }

  public String longName() { return longName; }

  public int arity() { return arity; }

  public boolean hasShortName() { return shortName != null; }

  public String names() {
    return hasShortName() ? "-" + shortName + ", --" + longName : "--" + longName;
  }

  private static String normalizeShortName(String shortName) {
    return shortName == null || shortName.isBlank() ? null : shortName;
  }
}
