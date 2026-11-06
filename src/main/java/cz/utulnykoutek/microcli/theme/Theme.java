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
 * {@code new Theme()} is the plain theme.
 */
public final class Theme {

  private final Map<String, Style> styles;

  public Theme() {
    this(Map.of());
  }

  public Theme(Map<String, Style> styles) {
    this.styles = Map.copyOf(styles);
  }

  public Style style(String tag) {
    return styles.getOrDefault(tag, new Style());
  }

  /** Returns a new theme with {@code tag} mapped to {@code style}. */
  public Theme with(String tag, Style style) {
    Objects.requireNonNull(tag, "Tag cannot be null");
    Objects.requireNonNull(style, "Style cannot be null");
    var copy = new LinkedHashMap<>(styles);
    copy.put(tag, style);
    return new Theme(copy);
  }

  /** Returns a plain copy of this theme with no colors or attributes. */
  public Theme withoutColors() {
    return new Theme();
  }

  public String paint(String tag, String text) {
    return style(tag).apply(text);
  }
}
