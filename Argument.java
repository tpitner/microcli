public abstract class Argument implements Comparable<Argument> {

  private String name;
  private String description;

  public Argument(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public abstract int match(String[] args, int index);

  public abstract boolean isParsed();

  public abstract boolean valid();

  public String checkArg(String[] args, int index) {
    requireNotNullEmpty(args, "Arguments cannot be null or empty");
    requireInRange(args, index, "Not enough arguments");
    String arg = args[index];
    requireNotNullBlank(arg, "Argument cannot be blank");
    return arg;
  }

  public String name() { return name; }
  public String description() { return description; }

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
  public int compareTo(Argument o) {
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

  public abstract void usage();
}