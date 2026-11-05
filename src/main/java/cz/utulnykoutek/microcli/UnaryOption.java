package cz.utulnykoutek.microcli;

public abstract class UnaryOption extends Option {

  private final String paramName;

  public UnaryOption(String name, String longName, String paramName, String description) {
    super(name, longName, 1, description);
    this.paramName = paramName;
  }

  public String paramName() { return paramName; }

  public abstract String defaultData();

  @Override public void usage() {
    System.out.println("-" + name() + "\t" + "--" + longName() + "\t<" + paramName() + ">\t default: " + defaultData() + "\t" + description());
  }
}