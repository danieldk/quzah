package eu.danieldk.quzah.random;

import eu.danieldk.quzah.colorspace.RGB;

import java.util.Random;

/**
 * Pick a random color in the RGB space.
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class UniformRandomRGB implements RandomRGB {
    public static final int DEFAULT_MIN = 0;

    public static final int DEFAULT_MAX = 256;

    private final Random random;

    private final int rMin;
    private final int rMax;
    private final int gMin;
    private final int gMax;
    private final int bMin;
    private final int bMax;

    /**
     * Construct the object using the no-arg {@link Random} constructor and the standard RGB
     * range (0-255).
     */
    public UniformRandomRGB() {
        this(new Random(), DEFAULT_MIN, DEFAULT_MAX, DEFAULT_MIN, DEFAULT_MAX, DEFAULT_MIN, DEFAULT_MAX);
    }

    /**
     * Construct the object using a {@link Random} object and the standard RGB range (0-255).
     *
     * @param random The random number generator to use.
     */
    public UniformRandomRGB(Random random) {
        this(random, DEFAULT_MIN, DEFAULT_MAX, DEFAULT_MIN, DEFAULT_MAX, DEFAULT_MIN, DEFAULT_MAX);
    }

    /**
     * Construct the object using a {@link Random} object and minimum/maximum values for each
     * component.
     *
     * @param random The random number generator.
     * @param rMin The minimum value of red.
     * @param rMax The maximum value of red.
     * @param gMin The minimum value of green.
     * @param gMax The maximum value of green.
     * @param bMin The minimum value of blue.
     * @param bMax The maximum value of blue.
     */
    public UniformRandomRGB(Random random, int rMin, int rMax, int gMin, int gMax, int bMin, int bMax) {
        this.random = random;

        this.rMin = rMin;
        this.rMax = rMax;
        this.gMin = gMin;
        this.gMax = gMax;
        this.bMin = bMin;
        this.bMax = bMax;
    }

    @Override
    public RGB next() {
        return new RGB(randomRange(rMin, rMax), randomRange(gMin, gMax), randomRange(bMin, bMax));
    }

    @Override
    public RGB nextWithinBox(RGB rgb, int maxDistance) {
        return new RGB(randomSubRange(rgb.r, maxDistance, rMin, rMax), randomSubRange(rgb.g, maxDistance, gMin, gMax),
                randomSubRange(rgb.b, maxDistance, bMin, bMax));

    }

    private int randomSubRange(int cur, int distance, int min, int max) {
        int lower = Math.max(min, cur - distance);
        int upper = Math.min(max, cur + distance + 1);

        return randomRange(lower, upper);
    }

    /**
     * Choose a random number from an integer range.
     *
     * @param min The minimum value.
     * @param max The maximum value (exclusive).
     * @return A random number in the given range.
     */
    private int randomRange(int min, int max) {
        return min + random.nextInt(max - min);
    }
}
