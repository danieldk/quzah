package eu.danieldk.quzah.random;

import eu.danieldk.quzah.colorspace.RGB;


/**
 * Interface for random RGB colors. This adds to the {@link eu.danieldk.quzah.random.RandomI}
 * interface by adding a method for selecting a random RGB color within a cube centered on
 * another color.
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public interface RandomRGB extends RandomI<RGB> {
    @Override
    public RGB next();

    /**
     * Obtain a random RGB value in the <tt>((maxDistance * 2) +1)^3</tt> cube that is centered
     * on an RGB value.
     *
     * @param rgb The RGB value that is the center of the cube.
     * @param maxDistance The maximum distance in a particular dimension (R, G, or B).
     * @return A random RGB value in the cube.
     */
    public RGB nextWithinBox(RGB rgb, int maxDistance);
}
