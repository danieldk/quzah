package eu.danieldk.quzah.random;

import java.util.Random;

/**
 * Pick a random color pastel-ish color in the RGB space.
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class PastelRandomRGB extends UniformRandomRGB {
    private final static int PASTEL_MIN = 128;
    private final static int PASTEL_MAX = 256;

    public PastelRandomRGB() {
        super(new Random(), PASTEL_MIN, PASTEL_MAX, PASTEL_MIN, PASTEL_MAX, PASTEL_MIN, PASTEL_MAX);
    }

    public PastelRandomRGB(Random random) {
        super(random, PASTEL_MIN, PASTEL_MAX, PASTEL_MIN, PASTEL_MAX, PASTEL_MIN, PASTEL_MAX);
    }
}
