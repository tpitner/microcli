package cz.utulnykoutek.microcli;

public abstract class Argument implements Comparable<Argument> {

  private final String id;
  private final String description;

  public Argument(String id, String description) {
    this.id = requireNotNullBlank(id, "Argument id cannot be null or blank");
    this.description = description;
  }

  public abstract int match(String[] args, int index);

  public abstract boolean isParsed();

  public abstract boolean valid();

  public abstract Object value();

  public String checkArg(String[] args, int index) {
    requireNotNullEmpty(args, "Arguments cannot be null or empty");
    requireInRange(args, index, "Not enough arguments");
    String arg = args[index];
    requireNotNullBlank(arg, "Argument cannot be blank");
    return arg;
  }

  public String id() { return id; }
  public String description() { return description; }

  public static String requireNotNullBlank(String s, String message) {
    if(s == null) throw new NullPointerException(message);
    if(s.isBlank()) throw new IllegalArgumentException(message);
    return s;
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
    return id().compareTo(o.id());
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Argument other && id().equals(other.id());
  }

  @Override
  public int hashCode() { return id().hashCode(); }

  public abstract void usage();
}
