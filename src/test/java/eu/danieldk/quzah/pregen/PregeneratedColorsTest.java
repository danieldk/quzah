package eu.danieldk.quzah.pregen;

import eu.danieldk.quzah.colorspace.RGB;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for {@link eu.danieldk.quzah.pregen.PregeneratedColors}.
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class PregeneratedColorsTest {
    private static final int MAX_COLORS = 50;

    @Test
    public void uniformColorsTest() {
        checkNumberOfColors(PregeneratedColors.UNIFORM);
    }

    @Test
    public void pastelColorsTest() {
       checkNumberOfColors(PregeneratedColors.PASTEL);
    }

    private void checkNumberOfColors(List<List<RGB>> colors) {
        Assert.assertEquals(String.format("Pregenerated color set should contain %d colors", MAX_COLORS),
                MAX_COLORS, colors.size());

        for (int i = 0; i < MAX_COLORS; ++i)
            Assert.assertEquals("Unexpected number of colors", i + 1, colors.get(i).size());
    }

}
