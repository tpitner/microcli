package cz.utulnykoutek.microcli.theme;

/**
 * A terminal color. Colors are rendered as ANSI SGR sequences by {@link Style}.
 */
public sealed interface Color permits Color.Ansi, Color.Rgb {

  /** SGR code for this color as a foreground color. */
  String foreground();

  /** SGR code for this color as a background color. */
  String background();

  /** One of the 16 standard ANSI colors. */
  enum Ansi implements Color {
    BLACK(30),
    RED(31),
    GREEN(32),
    YELLOW(33),
    BLUE(34),
    MAGENTA(35),
    CYAN(36),
    WHITE(37),
    BRIGHT_BLACK(90),
    BRIGHT_RED(91),
    BRIGHT_GREEN(92),
    BRIGHT_YELLOW(93),
    BRIGHT_BLUE(94),
    BRIGHT_MAGENTA(95),
    BRIGHT_CYAN(96),
    BRIGHT_WHITE(97);

    private final int code;

    Ansi(int code) {
      this.code = code;
    }

    @Override
    public String foreground() {
      return Integer.toString(code);
    }

    @Override
    public String background() {
      return Integer.toString(code + 10);
    }
  }

  /** A 24-bit RGB color. */
  record Rgb(int r, int g, int b) implements Color {

    public Rgb {
      if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
        throw new IllegalArgumentException("RGB values must be between 0 and 255");
      }
    }

    @Override
    public String foreground() {
      return "38;2;" + r + ";" + g + ";" + b;
    }

    @Override
    public String background() {
      return "48;2;" + r + ";" + g + ";" + b;
    }
  }
}
