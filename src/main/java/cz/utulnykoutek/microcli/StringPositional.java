package cz.utulnykoutek.microcli;

public class StringPositional extends PositionalParameter {

  private final String defaultData;

  public StringPositional(String name, String defaultData, String description) {
    super(name, description);
    this.defaultData = defaultData;
  } 

  public String defaultDataToString() {
    return defaultData;
  }
  
  public String getString() {
    return valid() ? data() : defaultData;
  }

  @Override public String toString() {
    return "String(" + (valid() ? getString() : "EMPTY") + ") " + name();
  }
} 