import java.nio.file.Path;
public class PathOption extends UnaryOption {

  private Path data;
  private Path defaultData; 

  public PathOption(String name, String longName, String paramName, Path defaultData, String description) {
    super(name, longName, paramName, description);
    this.defaultData = defaultData;
  }

  public Path getPath() {
    return valid() ? data : defaultData;
  }

  @Override public boolean valid() { 
    try {
      data = Path.of(params()[0]);
    } catch(RuntimeException re) {
      return false;
    }
    return true; 
  }

  @Override public String defaultData() {
    return String.valueOf(defaultData);
  }

  @Override 
  public String toString() {
    return "Path(" + (params() == null ? "EMPTY" : "" + getPath()) + ") " + name();
  }

  @Override public void usage() {
    System.out.println("-" + name() + "\t" + "--" + longName() + "\t<" + paramName() + ">\t default: '" + defaultData() + "'\t" + description());
  }
}