public abstract class Option extends Argument {

  private String longName;
  private int arity;

  private String[] params;
  
  public Option(String name, String longName, int arity, String description) {
    super(name, description);
    this.longName = longName;
    this.arity = arity;
  } 
  
  public int match(String[] args, int index) {

    String arg = checkArg(args, index);
    // this is no Option
    if(!arg.startsWith("-")) return -index;
    // this is an Option 
    if((arg.startsWith("-" + name()) || arg.startsWith("--" + longName())) 
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

  public String longName() { return longName; }

  public int arity() { return arity; }

  public String[] params() { return params; }

  public boolean isParsed() { return params != null; }
} 