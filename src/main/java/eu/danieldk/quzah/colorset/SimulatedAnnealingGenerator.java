package eu.danieldk.quzah.colorset;

import eu.danieldk.quzah.colorspace.RGB;
import eu.danieldk.quzah.random.RandomRGB;
import org.apache.sanselan.color.ColorCIELab;
import org.apache.sanselan.color.ColorConversions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class implements a {@link ColorSetGenerator} that attempts to find <i>n</i>
 * random RGB colors that are as visually distinct as possible. To do this, we pick
 * <i>n</i> RGB colors and iteratively refine them by optimizing the distance between
 * the colors in the CIE Lab color space.
 * <p/>
 * Since there can be local optima, simulated annealing is used for attempting to
 * find the global optimum.
 * <p/>
 * This method was proposed by: <i>C.A. Glasbey, et al., 2006</i>
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class SimulatedAnnealingGenerator implements ColorSetGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatedAnnealingGenerator.class);

    // The maximum number of replacements that is considered.
    private static final int MAX_REPLACEMENTS = 25600;

    // The number of iterations in simulated annealing.
    private static final int N_ITERATIONS = 100;

    // Initial temperature for simulated annealing.
    public static final double INITIAL_TEMPERATURE = 10.;

    // The generator used to create random colors.
    private final RandomRGB colorGenerator;

    private final Random random;

    /**
     * Create a {@link SimulatedAnnealingGenerator} using a {@link RandomRGB} to create
     * the initial set of colors and a random number generator for the annealing algorithm.
     *
     * @param colorGenerator The random color generator.
     * @param random         The random number generator.
     */
    public SimulatedAnnealingGenerator(RandomRGB colorGenerator, Random random) {
        this.colorGenerator = colorGenerator;
        this.random = random;
    }

    /**
     * Create a {@link SimulatedAnnealingGenerator} using a {@link RandomRGB} to create
     * the initial set of colors. The random number generator for the annealing algorithm
     * is created using the no-arg constructor of {@link java.util.Random}.
     *
     * @param colorGenerator The random color generator.
     */
    public SimulatedAnnealingGenerator(RandomRGB colorGenerator) {
        this(colorGenerator, new Random());
    }

    @Override
    public Set<RGB> colorSet(int n) {
        List<RGB> colors = new ArrayList<>();

        for (int i = 0; i < n; i++)
            colors.add(colorGenerator.next());

        if (n == 1)
            return new HashSet<>(colors);

        refineColors(colors);

        return new HashSet<>(colors);
    }

    /**
     * Compute the sum of distances between colors.
     *
     * @param labs The colors in CIE Lab.
     * @return The sum of Eucledian distances.
     */
    private double totalDistance(List<ColorCIELab> labs) {
        double sum = 0.;

        for (int i = 0; i < labs.size(); ++i)
            for (int j = i + 1; j < labs.size(); ++j)
                sum += cieLabDistance(labs.get(i), labs.get(j));

        return sum;
    }

    /**
     * Find the pair of colors that have the smallest Eucledian distance.
     *
     * @param labs The colors in CIE Lab.
     * @return The pair of colors with the smalles Eucledian distance.
     */
    public IndexPair minDistance(List<ColorCIELab> labs) {
        double minDistance = Double.MAX_VALUE;
        int minI = 0;
        int minJ = 0;

        for (int i = 0; i < labs.size(); ++i)
            for (int j = i + 1; j < labs.size(); ++j) {
                double distance = cieLabDistance(labs.get(i), labs.get(j));

                if (distance < minDistance) {
                    minDistance = distance;
                    minI = i;
                    minJ = j;
                }
            }

        return new IndexPair(minI, minJ, minDistance);
    }


    /**
     * Find the minimum the distance between a candidate color and all other colors.
     *
     * @param labs         The colors in CIE Lab.
     * @param candidate    The new candidate color.
     * @param candidateIdx The index of the color the candidate may replace.
     * @return The minimum distance.
     */
    private double minCandidateDistance(List<ColorCIELab> labs, ColorCIELab candidate, int candidateIdx) {
        double distance = Double.MAX_VALUE;

        for (int i = 0; i < labs.size(); ++i) {
            if (i == candidateIdx)
                continue;

            distance = Math.min(distance, cieLabDistance(candidate, labs.get(i)));
        }

        return distance;
    }


    /**
     * Execute a simulated annealing iteration. This will change the list of colors (in RGB and Lab)
     * in-place.
     *
     * @param rgbs        The colors in RGB.
     * @param labs        The colors in CIE Lab.
     * @param n           The iteration number.
     * @param maxN        The maximum number of iterations.
     * @param temperature The temperature.
     */
    public void iteration(List<RGB> rgbs, List<ColorCIELab> labs, int n, int maxN, double temperature) {
        // Get the overall minimum distance.
        final IndexPair minDistancePair = minDistance(labs);
        double distance = minDistancePair.getDistance();

        // We will modify one of the colors that form the minimum distance pair.
        final int tuneIdx = random.nextBoolean() ? minDistancePair.getIdx1() : minDistancePair.getIdx2();

        // The replacement color.
        RGB replacementRGB = rgbs.get(tuneIdx);
        ColorCIELab replacementLab = labs.get(tuneIdx);

        // Probability of choosing rule 1 (see below).
        final double pRule1 = (double) (maxN - n) / (double) maxN;

        // The number of color changes that were accepted in this iteration.
        int accepted = 0;

        // Stopping conditions:
        //
        // - MAX_REPLACEMENTS replacements are considered.
        // - 10% of the replacements have been accepted.
        //
        for (int i = 0; i < MAX_REPLACEMENTS && accepted < MAX_REPLACEMENTS / 10; i++) {
            // Two rules can be used for selecting a new color:
            //
            // 1. Pick a random color in the RGB space.
            // 2. Pick a color from the 5x5x5 box centered around the current color.
            RGB newRGB;
            if (random.nextDouble() <= pRule1)
                newRGB = colorGenerator.next();
            else
                newRGB = colorGenerator.nextWithinBox(rgbs.get(tuneIdx), 2);

            ColorCIELab newLab = rgbToCIELab(newRGB);

            double newDist = minCandidateDistance(labs, newLab, tuneIdx);

            // The new color increases the distance between the two colors, use the new
            // color. Otherwise, attempt replacements with a certain probability (to avoid
            // getting stuck in a local maximum), that decreases with the temperature.
            if (newDist > distance) {
                replacementRGB = newRGB;
                replacementLab = newLab;
                distance = newDist;
                ++accepted;
            } else {
                double pReplace = Math.min(1., Math.exp((newDist - distance) / temperature));
                if (random.nextDouble() <= pReplace) {
                    replacementRGB = newRGB;
                    replacementLab = newLab;
                    distance = newDist;
                    ++accepted;
                }
            }
        }

        rgbs.set(tuneIdx, replacementRGB);
        labs.set(tuneIdx, replacementLab);
    }

    private List<RGB> refineColors(List<RGB> colors) {
        double temperature = INITIAL_TEMPERATURE;

        List<ColorCIELab> labs = new ArrayList<>(colors.size());
        for (RGB rgb : colors)
            labs.add(rgbToCIELab(rgb));

        for (int i = 0; i < N_ITERATIONS; ++i) {

            double distanceBefore = totalDistance(labs);

            iteration(colors, labs, i, N_ITERATIONS, temperature);
            iteration(colors, labs, i, N_ITERATIONS, temperature);

            temperature *= 0.9;

            LOGGER.info("Iteration {}: {} -> {}", i, distanceBefore, totalDistance(labs));
        }

        return colors;
    }

    /**
     * Eucledian distance between to CIE Lab colors.
     *
     * @param lab1 The first Lab color.
     * @param lab2 The second Lab color.
     * @return The distance.
     */
    private double cieLabDistance(ColorCIELab lab1, ColorCIELab lab2) {
        return Math.sqrt(Math.pow(lab1.L - lab2.L, 2.) + Math.pow(lab1.a - lab2.a, 2.) + Math.pow(lab1.b - lab2.b, 2.));
    }

    private ColorCIELab rgbToCIELab(RGB rgb) {
        return ColorConversions.convertXYZtoCIELab(ColorConversions.convertRGBtoXYZ(rgb.getRGB()));
    }

    private static class IndexPair {
        public int idx1;

        public int idx2;

        private final double distance;

        private IndexPair(int idx1, int idx2, double distance) {
            this.idx1 = idx1;
            this.idx2 = idx2;
            this.distance = distance;
        }

        public double getDistance() {
            return distance;
        }

        public int getIdx1() {
            return idx1;
        }

        public int getIdx2() {
            return idx2;
        }
    }
}
