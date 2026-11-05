package cz.utulnykoutek.microcli;

public abstract class Option extends Argument {

  private final String shortName;
  private final String longName;
  private final int arity;

  private String[] params;

  public Option(String id, String shortName, String longName, int arity, String description) {
    super(id, description);
    this.shortName = requireNotNullBlank(shortName, "Option short name cannot be null or blank");
    this.longName = requireNotNullBlank(longName, "Option long name cannot be null or blank");
    this.arity = arity;
  }

  @Override
  public int match(String[] args, int index) {
    String arg = checkArg(args, index);
    // this is no Option
    if(!arg.startsWith("-")) return -index;
    // this is an Option
    if((arg.equals("-" + shortName) || arg.equals("--" + longName))
       && index + arity < args.length) {
      params = new String[arity];
      if(arity > 0) {
        System.arraycopy(args, index + 1, params, 0, arity);
      }
      return index + arity + 1;
    }
    // not this Option
    return index;
  }

  public String shortName() { return shortName; }

  public String longName() { return longName; }

  public int arity() { return arity; }

  public String[] params() { return params; }

  public boolean isParsed() { return params != null; }
}
