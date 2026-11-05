package cz.utulnykoutek.microcli;

public abstract class PositionalParameter extends Argument {

  private final String name;
  private String data;

  public PositionalParameter(String id, String name, String description) {
    super(id, description);
    this.name = requireNotNullBlank(name, "Positional parameter name cannot be null or blank");
  }

  protected abstract String defaultDataToString();

  protected String data() { return data; }

  public String name() { return name; }

  @Override public int match(String[] args, int index) {
    data = checkArg(args, index);
    return index + 1;
  }

  @Override public boolean isParsed() { return data != null; }

  @Override public boolean valid() { return isParsed(); }

  @Override public void usage() {
    System.out.println("[" + name() + "]\t default: '" + defaultDataToString() + "'\t" + description());
  }
}
