public class DoubleOption extends UnaryOption {

  private double data;
  private double defaultData;

  public DoubleOption(String name, String longName, String paramName, double defaultData, String description) {
    super(name, longName, paramName, description);
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

  @Override public String defaultData() {
    return String.valueOf(defaultData);
  }

  @Override 
  public String toString() {
    return "double(" + (params() == null ? "EMPTY" : "" + getDouble()) + ") " + name();
  }
}