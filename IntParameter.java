public class IntParameter extends Parameter {

  private int intData;
  private int defaultData;

  public IntParameter(String name, int defaultData, String description) {
    super(name, description);
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

  @Override public String toString() {
    return "int(" + (valid() ? "" + getInt() : "EMPTY") + ") " + name();
  }
} 