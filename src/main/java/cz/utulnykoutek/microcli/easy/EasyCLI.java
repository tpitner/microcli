package cz.utulnykoutek.microcli.easy;

import cz.utulnykoutek.microcli.Flag;
import cz.utulnykoutek.microcli.MicroCLI;
import cz.utulnykoutek.microcli.Option;
import cz.utulnykoutek.microcli.PositionalParameter;
import cz.utulnykoutek.microcli.ValuedOption;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public class EasyCLI extends MicroCLI {

  public EasyCLI(String usage) {
    super(usage);
  }

  @Override
  public EasyCLI add(Option<?>... opts) {
    super.add(opts);
    return this;
  }

  @Override
  public EasyCLI add(PositionalParameter<?>... params) {
    super.add(params);
    return this;
  }

  public EasyCLI stdFlags(StdFlag... flags) {
    Objects.requireNonNull(flags, "Standard flags cannot be null");
    for (StdFlag flag : flags) {
      add(Objects.requireNonNull(flag, "Standard flag cannot be null").create());
    }
    return this;
  }

  public boolean isFlag(StdFlag flag) {
    Objects.requireNonNull(flag, "Standard flag cannot be null");
    return find(flag.id()).isPresent() && getFlag(flag.id());
  }

  public boolean handleHelpAndVersion(String version) {
    if (isFlag(StdFlag.HELP)) {
      usage();
      return true;
    }
    if (isFlag(StdFlag.VERSION)) {
      System.out.println(version);
      return true;
    }
    return false;
  }

  public EasyCLI flag(String shortName, String longName, String description) {
    return flag(longName, shortName, longName, description);
  }

  public EasyCLI flag(String id, String shortName, String longName, String description) {
    return add(new Flag(id, shortName, longName, description));
  }

  public EasyCLI optInt(String shortName, String longName, int defaultValue,
                        String description) {
    return optInt(shortName, longName, upperName(longName), defaultValue, description);
  }

  public EasyCLI optInt(String shortName, String longName, String paramName,
                        int defaultValue, String description) {
    return opt(longName, shortName, longName, paramName, Integer.class,
        defaultValue, description);
  }

  public EasyCLI optDouble(String shortName, String longName, double defaultValue,
                           String description) {
    return optDouble(shortName, longName, upperName(longName), defaultValue, description);
  }

  public EasyCLI optDouble(String shortName, String longName, String paramName,
                           double defaultValue, String description) {
    return opt(longName, shortName, longName, paramName, Double.class,
        defaultValue, description);
  }

  public EasyCLI optString(String shortName, String longName, String defaultValue,
                           String description) {
    return optString(shortName, longName, upperName(longName), defaultValue, description);
  }

  public EasyCLI optString(String shortName, String longName, String paramName,
                           String defaultValue, String description) {
    return opt(longName, shortName, longName, paramName, String.class,
        defaultValue, description);
  }

  public EasyCLI optPath(String shortName, String longName, Path defaultValue,
                         String description) {
    return optPath(shortName, longName, upperName(longName), defaultValue, description);
  }

  public EasyCLI optPath(String shortName, String longName, String paramName,
                         Path defaultValue, String description) {
    return opt(longName, shortName, longName, paramName, Path.class,
        defaultValue, description);
  }

  public <T> EasyCLI opt(String shortName, String longName, String paramName,
                         Class<T> type, T defaultValue, String description) {
    return opt(longName, shortName, longName, paramName, type, defaultValue, description);
  }

  public <T> EasyCLI opt(String id, String shortName, String longName, String paramName,
                         Class<T> type, T defaultValue, String description) {
    return add(new ValuedOption<>(id, shortName, longName, paramName, type,
        defaultValue, description));
  }

  public <T> EasyCLI opt(String id, String shortName, String longName, String paramName,
                         Class<T> type, Function<String, T> parser, T defaultValue,
                         String description) {
    return add(new ValuedOption<>(id, shortName, longName, paramName, type, parser,
        defaultValue, description));
  }

  public EasyCLI posString(String name) {
    return posString(name, null, "");
  }

  public EasyCLI posString(String name, String description) {
    return posString(name, null, description);
  }

  public EasyCLI posString(String name, String defaultValue, String description) {
    return pos(name, upperName(name), String.class, defaultValue, description);
  }

  public EasyCLI posInt(String name, int defaultValue, String description) {
    return pos(name, upperName(name), Integer.class, defaultValue, description);
  }

  public EasyCLI posDouble(String name, double defaultValue, String description) {
    return pos(name, upperName(name), Double.class, defaultValue, description);
  }

  public EasyCLI posPath(String name, Path defaultValue, String description) {
    return pos(name, upperName(name), Path.class, defaultValue, description);
  }

  public <T> EasyCLI pos(String name, Class<T> type, T defaultValue, String description) {
    return pos(name, upperName(name), type, defaultValue, description);
  }

  public <T> EasyCLI pos(String id, String displayName, Class<T> type, T defaultValue,
                         String description) {
    return add(new PositionalParameter<>(id, displayName, type, defaultValue, description));
  }

  public <T> EasyCLI pos(String id, String displayName, Class<T> type,
                         Function<String, T> parser, T defaultValue, String description) {
    return add(new PositionalParameter<>(id, displayName, type, parser, defaultValue,
        description));
  }

  private static String upperName(String name) {
    return Objects.requireNonNull(name, "Name cannot be null")
        .toUpperCase(Locale.ROOT)
        .replace('-', '_');
  }
}
