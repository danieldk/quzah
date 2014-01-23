package eu.danieldk.quzah.colorset;

import eu.danieldk.quzah.colorspace.RGB;

import java.util.Set;

/**
 * @author Daniël de Kok <me@danieldk.eu>
 */
public interface ColorSetGenerator {
    /**
     * Generate a set of <i>n</i> different colors.
     * @param n The number of colors to generate.
     * @return A set of <i>n</i> distinct colors.
     */
    public Set<RGB> colorSet(int n);
}
