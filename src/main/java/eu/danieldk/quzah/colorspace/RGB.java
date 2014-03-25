package eu.danieldk.quzah.colorspace;

/**
 * A color in the RGB space.
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class RGB {
    public final int alpha;

    public final int r;

    public final int g;

    public final int b;

    public RGB(int rgb) {
        this.alpha = (rgb >>> 24) & 0xff;
        this.r = (rgb >>> 16) & 0xff;
        this.g = (rgb >>> 8) & 0xff;
        this.b = rgb & 0xff;
    }

    public RGB(int r, int g, int b) {
        this(0xff, r, g, b);
    }

    public RGB(int alpha, int r, int g, int b) {
        this.alpha = alpha;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getRGB() {
        return (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public String toString() {
        return "RGB{" +
                "alpha=" + alpha +
                ", r=" + r +
                ", g=" + g +
                ", b=" + b +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final RGB rgb = (RGB) o;

        if (alpha != rgb.alpha) return false;
        if (b != rgb.b) return false;
        if (g != rgb.g) return false;
        if (r != rgb.r) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = alpha;
        result = 31 * result + r;
        result = 31 * result + g;
        result = 31 * result + b;
        return result;
    }
}
