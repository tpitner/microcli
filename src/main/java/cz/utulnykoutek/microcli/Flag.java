package cz.utulnykoutek.microcli;

public class Flag extends Option {

  public Flag(String id, String shortName, String longName, String description) {
    super(id, shortName, longName, 0, description);
  }

  public boolean isTrue() {
    return params() != null;
  }

  @Override public boolean valid() { return true; }

  @Override public Object value() { return isTrue(); }

  @Override public String toString() {
    return "boolean(" + isTrue() + ") " + id();
  }

  @Override public void usage() {
    System.out.println("-" + shortName() + "\t" + "--" + longName() + "\t" + description());
  }
}
