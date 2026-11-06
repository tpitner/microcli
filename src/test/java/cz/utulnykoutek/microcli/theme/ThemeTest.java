package cz.utulnykoutek.microcli.theme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThemeTest {

  @Test
  void ansiColorsRenderForegroundAndBackgroundCodes() {
    assertEquals("31", Color.Ansi.RED.foreground());
    assertEquals("41", Color.Ansi.RED.background());
    assertEquals("97", Color.Ansi.BRIGHT_WHITE.foreground());
    assertEquals("107", Color.Ansi.BRIGHT_WHITE.background());
  }

  @Test
  void rgbColorsRenderTruecolorCodes() {
    var color = new Color.Rgb(12, 34, 56);

    assertEquals("38;2;12;34;56", color.foreground());
    assertEquals("48;2;12;34;56", color.background());
  }

  @Test
  void rgbColorsValidateTheirComponents() {
    assertThrows(IllegalArgumentException.class, () -> new Color.Rgb(-1, 0, 0));
    assertThrows(IllegalArgumentException.class, () -> new Color.Rgb(0, 256, 0));
    assertThrows(IllegalArgumentException.class, () -> new Color.Rgb(0, 0, 999));
  }

  @Test
  void plainStyleLeavesTextUnchanged() {
    var style = new Style();

    assertTrue(style.isPlain());
    assertEquals("text", style.apply("text"));
    assertEquals("", style.apply(""));
    assertEquals(null, style.apply(null));
  }

  @Test
  void styleRendersColorsAndAttributesInOrder() {
    var style = new Style(Color.Ansi.RED, Color.Ansi.BLUE)
        .bold()
        .dim()
        .italic()
        .underline();

    assertEquals("\u001B[31;44;1;2;3;4mtext\u001B[0m", style.apply("text"));
  }

  @Test
  void styleRendersRgbForeground() {
    var style = new Style(new Color.Rgb(255, 80, 80)).bold();

    assertEquals("\u001B[38;2;255;80;80;1mtext\u001B[0m", style.apply("text"));
  }

  @Test
  void themeReturnsPlainStyleForUnknownTags() {
    var theme = new Theme().withStyle("error", new Style(Color.Ansi.RED));

    assertTrue(theme.style("missing").isPlain());
    assertEquals("plain", theme.paint("missing", "plain"));
  }

  @Test
  void themeWithAddsAndOverridesTags() {
    var theme = new Theme()
        .withStyle("error", new Style(Color.Ansi.RED))
        .withStyle("error", new Style(Color.Ansi.BRIGHT_RED).bold());

    assertEquals("\u001B[91;1merror\u001B[0m", theme.paint("error", "error"));
  }

  @Test
  void newThemeIsPlainAndNamedPlain() {
    var theme = new Theme();

    assertEquals("plain", theme.name());
    assertTrue(theme.style("anything").isPlain());
  }

  @Test
  void namedThemePreservesItsName() {
    var theme = new Theme("Dark");

    assertEquals("Dark", theme.name());
    assertTrue(theme.styles().isEmpty());
  }

  @Test
  void withStylePreservesNameAndAddsStyle() {
    var theme = new Theme("Dark").withStyle("error", new Style(Color.Ansi.RED));

    assertEquals("Dark", theme.name());
    assertEquals(1, theme.styles().size());
    assertEquals("\u001B[31merror\u001B[0m", theme.paint("error", "error"));
  }

  @Test
  void withoutColorsPreservesName() {
    var theme = new Theme("Dark").withStyle("error", new Style(Color.Ansi.RED));

    assertEquals("Dark", theme.withoutColors().name());
    assertTrue(theme.withoutColors().style("error").isPlain());
  }

  @Test
  void withoutColorsReturnsPlainTheme() {
    var theme = new Theme().withStyle("error", new Style(Color.Ansi.RED).bold());

    assertTrue(theme.withoutColors().style("error").isPlain());
    assertEquals("error", theme.withoutColors().paint("error", "error"));
    assertFalse(theme.style("error").isPlain());
  }

  @Test
  void stylesReturnsUnmodifiableMap() {
    var theme = new Theme("Dark").withStyle("error", new Style(Color.Ansi.RED));

    assertThrows(UnsupportedOperationException.class,
        () -> theme.styles().put("x", new Style()));
  }
}
