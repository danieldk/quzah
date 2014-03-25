package eu.danieldk.quzah.pregen;

import com.google.common.collect.ImmutableList;
import eu.danieldk.quzah.colorspace.RGB;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * This class provides pre-generated lists of colors. Since color set optimization is slow
 * when e.g. a {@link eu.danieldk.quzah.colorset.SimulatedAnnealingGenerator} is used, it
 * is often useful to generate static lists of colors to avoid the generation time.
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class PregeneratedColors {
    /**
     * List of 50 lists of RGB colors. The <i>n<sup>th</sup></i> member of the list contains <i>n</i> distinct
     * colors, picked from the whole RGB color space.
     */
    public static final List<List<RGB>> UNIFORM;

    /**
     * List of 50 lists of RGB colors. The <i>n<sup>th</sup></i> member of the list contains <i>n</i> distinct
     * colors, picked from the pastel colors of the RGB color space.
     */
    public static final List<List<RGB>> PASTEL;


    static {
        UNIFORM = readColors("eu/danieldk/quzah/pregen/uniform.dat");
        PASTEL = readColors("eu/danieldk/quzah/pregen/pastel.dat");
    }

    private static List<List<RGB>> readColors(String resource) {
        ImmutableList.Builder<List<RGB>> allColors = ImmutableList.builder();


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getResourceAsStream(resource)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ImmutableList.Builder<RGB> colorsBuilder = ImmutableList.builder();

                String[] lineParts = StringUtils.split(line, ' ');

                for (String color: lineParts)
                    colorsBuilder.add(new RGB(Integer.parseInt(color)));

                allColors.add(colorsBuilder.build());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return allColors.build();
    }

    private static InputStream getResourceAsStream(String resource) {
        InputStream inputStream = PregeneratedColors.class.getClassLoader().getResourceAsStream(resource);
        if (inputStream == null)
            throw new IllegalArgumentException(String.format("Resource could not be found: %s", resource));

        return inputStream;
    }

    private PregeneratedColors() {
    }
}
