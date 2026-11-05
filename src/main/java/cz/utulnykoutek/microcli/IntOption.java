package cz.utulnykoutek.microcli;

public class IntOption extends UnaryOption {

  private int data;
  private final int defaultData;

  public IntOption(String id, String shortName, String longName, String paramName, int defaultData, String description) {
    super(id, shortName, longName, paramName, description);
    this.defaultData = defaultData;
  }

  public int getInt() {
    return valid() ? data : defaultData;
  }

  @Override public boolean valid() {
    try {
      data = Integer.parseInt(params()[0]);
    } catch(RuntimeException re) {
      return false;
    }
    return true;
  }

  @Override public Object value() { return getInt(); }

  @Override public String defaultData() {
    return String.valueOf(defaultData);
  }

  @Override
  public String toString() {
    return "int(" + (params() == null ? "EMPTY" : "" + getInt()) + ") " + id();
  }
}
