package cz.utulnykoutek.microcli;

import java.util.function.Function;

public class PositionalParameter<T> extends TypedArgument<T> {

  private final String name;

  public PositionalParameter(String id, String name, Class<T> type, T defaultValue,
                             String description) {
    super(id, type, defaultValue, description);
    this.name = requireNotNullBlank(name, "Positional parameter name cannot be null or blank");
  }

  public PositionalParameter(String id, String name, Class<T> type, Function<String, T> parser,
                             T defaultValue, String description) {
    super(id, type, defaultValue, parser, description);
    this.name = requireNotNullBlank(name, "Positional parameter name cannot be null or blank");
  }

  public String name() { return name; }

  @Override public int match(String[] args, int index) {
    setRawValue(checkArg(args, index));
    return index + 1;
  }

  @Override public void usage() {
    System.out.println("[" + name() + "]\t default: '" + defaultData() + "'\t" + description());
  }
}
