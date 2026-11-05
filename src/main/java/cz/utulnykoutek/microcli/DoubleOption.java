package cz.utulnykoutek.microcli;

public class DoubleOption extends UnaryOption {

  private double data;
  private final double defaultData;

  public DoubleOption(String id, String shortName, String longName, String paramName, double defaultData, String description) {
    super(id, shortName, longName, paramName, description);
    this.defaultData = defaultData;
  }

  public double getDouble() {
    return valid() ? data : defaultData;
  }

  @Override public boolean valid() {
    try {
      data = Double.parseDouble(params()[0]);
    } catch(RuntimeException re) {
      return false;
    }
    return true;
  }

  @Override public Object value() { return getDouble(); }

  @Override public String defaultData() {
    return String.valueOf(defaultData);
  }

  @Override
  public String toString() {
    return "double(" + (params() == null ? "EMPTY" : "" + getDouble()) + ") " + id();
  }
}
