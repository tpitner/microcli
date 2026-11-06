package cz.utulnykoutek.microcli;

public class Flag extends Option<Boolean> {

  public Flag(String id, String shortName, String longName, String description) {
    super(id, shortName, longName, 0, Boolean.class, false, description);
  }

  public boolean isTrue() {
    return value();
  }

  @Override
  public String toString() {
    return "boolean(" + isTrue() + ") " + id();
  }

  @Override public void usage() {
    System.out.println(names() + "\t" + description());
  }
}
