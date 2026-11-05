package cz.utulnykoutek.microcli;

import java.nio.file.Path;

public class PathOption extends UnaryOption {

  private Path data;
  private final Path defaultData;

  public PathOption(String id, String shortName, String longName, String paramName, Path defaultData, String description) {
    super(id, shortName, longName, paramName, description);
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

  @Override public Object value() { return getPath(); }

  @Override public String defaultData() {
    return String.valueOf(defaultData);
  }

  @Override
  public String toString() {
    return "Path(" + (params() == null ? "EMPTY" : "" + getPath()) + ") " + id();
  }

  @Override public void usage() {
    System.out.println("-" + shortName() + "\t" + "--" + longName() + "\t<" + paramName() + ">\t default: '" + defaultData() + "'\t" + description());
  }
}
