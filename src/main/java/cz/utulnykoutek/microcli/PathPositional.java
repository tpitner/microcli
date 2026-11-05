package cz.utulnykoutek.microcli;

import java.nio.file.Path;

public class PathPositional extends PositionalParameter {

  private final Path defaultData;

  public PathPositional(String name, Path defaultData, String description) {
    super(name, description);
    this.defaultData = defaultData;
  } 

  public String defaultDataToString() {
    return String.valueOf(defaultData);
  }
  
  public Path getPath() {
    return valid() ? Path.of(data()) : defaultData;
  }

  @Override public String toString() {
    return "Path(" + (valid() ? String.valueOf(getPath()) : "EMPTY") + ") " + name();
  }
} 