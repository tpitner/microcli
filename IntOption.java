public class IntOption extends UnaryOption {

  private int data;
  private int defaultData;

  public IntOption(String name, String longName, String paramName, int defaultData, String description) {
    super(name, longName, paramName, description);
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

  @Override public String defaultData() {
    return String.valueOf(defaultData);
  }

  @Override 
  public String toString() {
    return "int(" + (params() == null ? "EMPTY" : "" + getInt()) + ") " + name();
  }
}