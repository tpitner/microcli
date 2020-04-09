public class BooleanOption extends Option {
  
  public BooleanOption(String name, String longName, String description) {
    super(name, longName, 0, description);
  }

  public boolean isTrue() {
    return params() != null;
  }

  @Override public boolean valid() { return true; }

  @Override public String toString() {
    return "boolean(" + isTrue() + ") " + name();
  }

  @Override public void usage() {
    System.out.println("-" + name() + "\t" + "--" + longName() + "\t" + description());
  }
}