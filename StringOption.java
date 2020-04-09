public class StringOption extends UnaryOption {

  private String data;
  private String defaultData;

  public StringOption(String name, String longName, String paramName, String defaultData, String description) {
    super(name, longName, paramName, description);
    this.defaultData = defaultData;
  }

  public String getString() {
    return valid() ? data : defaultData;
  }

  @Override public boolean valid() { 
    try {
      data = params()[0];
    } catch(RuntimeException re) {
      return false;
    }
    return true; 
  }

  @Override public String defaultData() {
    return String.valueOf(defaultData);
  }

  @Override public String toString() {
    return "String(" + (params() == null ? "EMPTY" : "" + getString()) + ") " + name();
  }

  @Override public void usage() {
    System.out.println("-" + name() + "\t" + "--" + longName() + "\t<" + paramName() + ">\t default: \"" + defaultData() + "\"\t" + description());
  }
}