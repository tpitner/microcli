public abstract class Parameter extends Argument {

  private String data;
  
  public Parameter(String name, String description) {
    super(name, description);
  } 

  protected abstract String defaultDataToString();

  protected String data() { return data; }
  
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