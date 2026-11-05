package cz.utulnykoutek.microcli;

public abstract class UnaryOption extends Option {

  private final String paramName;

  public UnaryOption(String id, String shortName, String longName, String paramName, String description) {
    super(id, shortName, longName, 1, description);
    this.paramName = requireNotNullBlank(paramName, "Option parameter name cannot be null or blank");
  }

  public String paramName() { return paramName; }

  public abstract String defaultData();

  @Override public void usage() {
    System.out.println("-" + shortName() + "\t" + "--" + longName() + "\t<" + paramName() + ">\t default: " + defaultData() + "\t" + description());
  }
}
