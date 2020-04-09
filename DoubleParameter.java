public class DoubleParameter extends Parameter {

  private double doubleData;
  private double defaultData;

  public DoubleParameter(String name, double defaultData, String description) {
    super(name, description);
    this.defaultData = defaultData;
  } 

  public String defaultDataToString() {
    return String.valueOf(defaultData);
  }
  
  public double getDouble() {
    return valid() ? doubleData : defaultData;
  }

  @Override public boolean valid() { 
    try {
      doubleData = Double.parseDouble(data());
    } catch(RuntimeException re) {
      return false;
    }
    return true; 
  }

  @Override public String toString() {
    return "double(" + (valid() ? "" + getDouble() : "EMPTY") + ") " + name();
  }
} 