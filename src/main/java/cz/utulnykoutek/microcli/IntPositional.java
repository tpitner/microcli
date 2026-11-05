package cz.utulnykoutek.microcli;

public class IntPositional extends PositionalParameter {

  private int intData;
  private final int defaultData;

  public IntPositional(String id, String name, int defaultData, String description) {
    super(id, name, description);
    this.defaultData = defaultData;
  }

  public String defaultDataToString() {
    return String.valueOf(defaultData);
  }

  public int getInt() {
    return valid() ? intData : defaultData;
  }

  @Override public boolean valid() {
    try {
      intData = Integer.parseInt(data());
    } catch(RuntimeException re) {
      return false;
    }
    return true;
  }

  @Override public Object value() { return getInt(); }

  @Override public String toString() {
    return "int(" + (valid() ? "" + getInt() : "EMPTY") + ") " + id();
  }
}
