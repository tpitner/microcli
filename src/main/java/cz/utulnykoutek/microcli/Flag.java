package cz.utulnykoutek.microcli;

import cz.utulnykoutek.microcli.theme.Theme;

import java.io.PrintStream;

public class Flag extends Option<Boolean> {

  public Flag(String id, String shortName, String longName, String description) {
    super(id, shortName, longName, 0, Boolean.class, false, description);
  }

  public boolean isTrue() {
    return value();
  }

  @Override
  public String toString() {
    return "boolean(" + isTrue() + ") " + id();
  }

  @Override public void usage() {
    usage(System.out, new Theme());
  }

  @Override public void usage(PrintStream out, Theme theme) {
    out.println(theme.paint("option", names()) + "\t"
        + theme.paint("description", description()));
  }
}
