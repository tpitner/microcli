package cz.utulnykoutek.microcli.easy;

import cz.utulnykoutek.microcli.Flag;

public enum StdFlag {

  HELP("help", "h", "help", "Prints help"),
  VERBOSE("verbose", "v", "verbose", "Produces verbose output"),
  VERSION("version", "V", "version", "Prints version"),
  DRY_RUN("dryRun", "n", "dry-run", "Runs without making changes"),
  DEBUG("debug", "d", "debug", "Produces debug output"),
  NO_COLOR("noColor", null, "no-color", "Disables colored output");

  private final String id;
  private final String shortName;
  private final String longName;
  private final String description;

  StdFlag(String id, String shortName, String longName, String description) {
    this.id = id;
    this.shortName = shortName;
    this.longName = longName;
    this.description = description;
  }

  public String id() { return id; }

  public String shortName() { return shortName; }

  public String longName() { return longName; }

  public String description() { return description; }

  public Flag create() {
    return new Flag(id, shortName, longName, description);
  }
}
