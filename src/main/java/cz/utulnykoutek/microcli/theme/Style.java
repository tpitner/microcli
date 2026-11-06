package cz.utulnykoutek.microcli.theme;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable text style: foreground/background color and basic font attributes.
 * <p>
 * {@code new Style()} is the plain style and produces no ANSI sequences.
 */
public record Style(Color foreground, Color background,
                    boolean isBold, boolean isDim, boolean isItalic, boolean isUnderline) {

  public Style() {
    this(null, null, false, false, false, false);
  }

  public Style(Color foreground) {
    this(foreground, null, false, false, false, false);
  }

  public Style(Color foreground, Color background) {
    this(foreground, background, false, false, false, false);
  }

  public Style bold() {
    return new Style(foreground, background, true, isDim, isItalic, isUnderline);
  }

  public Style dim() {
    return new Style(foreground, background, isBold, true, isItalic, isUnderline);
  }

  public Style italic() {
    return new Style(foreground, background, isBold, isDim, true, isUnderline);
  }

  public Style underline() {
    return new Style(foreground, background, isBold, isDim, isItalic, true);
  }

  public Style bg(Color background) {
    return new Style(foreground, background, isBold, isDim, isItalic, isUnderline);
  }

  public boolean isPlain() {
    return foreground == null && background == null
        && !isBold && !isDim && !isItalic && !isUnderline;
  }

  /**
   * Wraps {@code text} in the ANSI sequence for this style.
   * Null, empty and plain text is returned unchanged.
   */
  public String apply(String text) {
    if (text == null || text.isEmpty() || isPlain()) return text;

    List<String> codes = new ArrayList<>();
    if (foreground != null) codes.add(foreground.foreground());
    if (background != null) codes.add(background.background());
    if (isBold) codes.add("1");
    if (isDim) codes.add("2");
    if (isItalic) codes.add("3");
    if (isUnderline) codes.add("4");

    return "\u001B[" + String.join(";", codes) + "m" + text + "\u001B[0m";
  }
}
