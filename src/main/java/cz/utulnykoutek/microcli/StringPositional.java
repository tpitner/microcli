package cz.utulnykoutek.microcli;

public class StringPositional extends PositionalParameter {

  private final String defaultData;

  public StringPositional(String id, String name, String defaultData, String description) {
    super(id, name, description);
    this.defaultData = defaultData;
  }

  public String defaultDataToString() {
    return defaultData;
  }

  public String getString() {
    return valid() ? data() : defaultData;
  }

  @Override public Object value() { return getString(); }

  @Override public String toString() {
    return "String(" + (valid() ? getString() : "EMPTY") + ") " + id();
  }
}
