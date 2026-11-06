package cz.utulnykoutek.microcli.easy;

import cz.utulnykoutek.microcli.Flag;

public enum StdFlag {

  ALL("all", "a", "all", "Processes all items"),
  DEBUG("debug", "d", "debug", "Produces debug output"),
  DRY_RUN("dryRun", "n", "dry-run", "Runs without making changes"),
  FORCE("force", "f", "force", "Forces the operation"),
  HELP("help", "h", "help", "Prints help"),
  JSON("json", null, "json", "Prints JSON output"),
  NO_COLOR("noColor", null, "no-color", "Disables colored output"),
  NO_INPUT("noInput", null, "no-input", "Disables interactive input"),
  QUIET("quiet", "q", "quiet", "Suppresses non-essential output"),
  VERBOSE("verbose", "v", "verbose", "Produces verbose output"),
  VERSION("version", "V", "version", "Prints version"),
  YES("yes", "y", "yes", "Answers yes to prompts");

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
