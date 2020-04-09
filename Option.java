public abstract class Option implements Comparable<Option> {

  private String name;
  private String longName;
  private int arity;
  private String description;

  private String[] params;
  
  public Option(String name, String longName, int arity, String description) {
    this.name = name;
    this.longName = longName;
    this.arity = arity;
    this.description = description;
  } 
  
  public int match(String[] args, int index) {

    String arg = checkArg(args, index);
    // is this Option
    if((arg.startsWith("-" + name) || arg.startsWith("--" + longName)) 
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

  protected String checkArg(String[] args, int index) {
    requireNotNullEmpty(args, "Arguments cannot be null or empty");
    requireInRange(args, index, "Not enough arguments");
    String arg = args[index];
    requireNotNullBlank(arg, "Argument cannot be blank");
    return arg;
  }

  public String name() { return name; }

  public String longName() { return longName; }

  public String description() { return description; }

  public int arity() { return arity; }

  public String[] params() { return params; }

  public boolean isParsed() { return params != null; }

  public static void requireNotNullBlank(String s, String message) {
    if(s == null) throw new NullPointerException(message);
    if(s.isBlank()) throw new IllegalArgumentException(message);
  }

  public static void requireNotNullEmpty(Object[] a, String message) {
    if(a == null) throw new NullPointerException(message);
    if(a.length == 0) throw new IllegalArgumentException(message);
  }

  public static void requireInRange(Object[] a, int index, String message) {
    if(index >= a.length) throw new ArrayIndexOutOfBoundsException(message);
  }

  @Override
  public int compareTo(Option o) {
    return name().compareTo(o.name());
  } 

  @Override
  public boolean equals(Object o) {
    if(o != null && this.getClass() == o.getClass()) {
      return this.name().equals(((Option)o).name());
    } 
    return false;
  }

  @Override
  public int hashCode() { return name().hashCode(); }

  public abstract boolean valid();
  public abstract void usage();
} 