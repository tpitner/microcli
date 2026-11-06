package cz.utulnykoutek.microcli.theme;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An immutable set of styles keyed by semantic tags.
 * <p>
 * The library uses these standard tags: {@code title}, {@code heading},
 * {@code option}, {@code parameter}, {@code default}, {@code description},
 * {@code error}, {@code warning}, {@code success}, {@code info},
 * {@code muted} and {@code emphasis}. Unknown tags are rendered as plain text,
 * and applications are free to use their own tags.
 * <p>
 * {@code new Theme()} is the plain theme with the name {@code "plain"}.
 */
public final class Theme {

  private final String name;
  private final Map<String, Style> styles;

  public Theme() {
    this("plain", Map.of());
  }

  public Theme(String name) {
    this(name, Map.of());
  }

  public Theme(String name, Map<String, Style> styles) {
    this.name = requireName(name);
    this.styles = Map.copyOf(styles);
  }

  public Theme(Map<String, Style> styles) {
    this("plain", styles);
  }

  public String name() {
    return name;
  }

  public Map<String, Style> styles() {
    return styles;
  }

  public Style style(String tag) {
    return styles.getOrDefault(tag, new Style());
  }

  /** Returns a new theme with {@code tag} mapped to {@code style}. */
  public Theme withStyle(String tag, Style style) {
    Objects.requireNonNull(tag, "Tag cannot be null");
    Objects.requireNonNull(style, "Style cannot be null");
    var copy = new LinkedHashMap<>(styles);
    copy.put(tag, style);
    return new Theme(name, copy);
  }

  /** Returns a plain copy of this theme with no colors or attributes. */
  public Theme withoutColors() {
    return new Theme(name);
  }

  public String paint(String tag, String text) {
    return style(tag).apply(text);
  }

  private static String requireName(String name) {
    if (name == null) throw new NullPointerException("Theme name cannot be null");
    if (name.isBlank()) throw new IllegalArgumentException("Theme name cannot be blank");
    return name;
  }
}
