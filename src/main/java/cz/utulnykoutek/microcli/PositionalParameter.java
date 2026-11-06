package cz.utulnykoutek.microcli;

import cz.utulnykoutek.microcli.theme.Theme;

import java.io.PrintStream;
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
    usage(System.out, new Theme());
  }

  @Override public void usage(PrintStream out, Theme theme) {
    out.println("[" + theme.paint("parameter", name()) + "]"
        + "\t default: '" + theme.paint("default", defaultData()) + "'"
        + "\t" + theme.paint("description", description()));
  }
}
