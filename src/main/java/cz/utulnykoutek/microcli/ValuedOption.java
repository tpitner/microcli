package cz.utulnykoutek.microcli;

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
    System.out.println(names() + "\t<" + paramName() + ">\t default: "
        + defaultDataText() + "\t" + description());
  }

  private String defaultDataText() {
    if(type() == String.class) return "\"" + defaultData() + "\"";
    if(type() == Path.class) return "'" + defaultData() + "'";
    return defaultData();
  }
}
