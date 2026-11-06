package cz.utulnykoutek.microcli;

import cz.utulnykoutek.microcli.theme.Theme;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.function.Function;

public class ValuedOption<T> extends Option<T> {

  private final String paramName;

  public ValuedOption(String id, String shortName, String longName, String paramName,
                      Class<T> type, T defaultValue, String description) {
    super(id, shortName, longName, 1, type, defaultValue, description);
    this.paramName = requireNotNullBlank(paramName, "Option parameter name cannot be null or blank");
  }

  public ValuedOption(String id, String shortName, String longName, String paramName,
                      Class<T> type, Function<String, T> parser, T defaultValue,
                      String description) {
    super(id, shortName, longName, 1, type, parser, defaultValue, description);
    this.paramName = requireNotNullBlank(paramName, "Option parameter name cannot be null or blank");
  }

  public String paramName() { return paramName; }

  @Override public void usage() {
    usage(System.out, new Theme());
  }

  @Override public void usage(PrintStream out, Theme theme) {
    out.println(theme.paint("option", names())
        + "\t<" + theme.paint("parameter", paramName()) + ">"
        + "\t default: " + theme.paint("default", defaultDataText())
        + "\t" + theme.paint("description", description()));
  }

  private String defaultDataText() {
    if(type() == String.class) return "\"" + defaultData() + "\"";
    if(type() == Path.class) return "'" + defaultData() + "'";
    return defaultData();
  }
}
