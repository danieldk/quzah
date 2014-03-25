package eu.danieldk.quzah.cli;

import eu.danieldk.quzah.colorset.ColorSetGenerator;
import eu.danieldk.quzah.colorset.SimulatedAnnealingGenerator;
import eu.danieldk.quzah.colorspace.RGB;
import eu.danieldk.quzah.random.PastelRandomRGB;
import eu.danieldk.quzah.random.RandomRGB;
import eu.danieldk.quzah.random.UniformRandomRGB;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Small program to generate N lists of distinct colors. A list in
 * 1..M..N contains M different colors.
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class GenerateColors {
    public static void main(String[] args) {
        Options options = programOptions();
        CommandLine commandLine = parseOptions(args, options);

        if (commandLine.getArgs().length != 1)
            usage(options);

        RandomRGB randomRGB = createColorGenerator(options, commandLine);

        ColorSetGenerator colorSetGenerator = new SimulatedAnnealingGenerator(randomRGB);

        int n = Integer.parseInt(commandLine.getArgs()[0]);
        for (int i = 1; i <= n; ++i) {
            Set<RGB> colorSet = colorSetGenerator.colorSet(i);
            List<Integer> intValues = new ArrayList<>();
            for (RGB rgb : colorSet)
                intValues.add(rgb.getRGB());

            System.out.println(StringUtils.join(intValues, ' '));
        }
    }

    /**
     * Pick the color generator, based on the <i>-g</i> option.
     */
    private static RandomRGB createColorGenerator(Options options, CommandLine commandLine) {
        RandomRGB randomRGB = null;
        if (commandLine.hasOption('g')) {
            switch (commandLine.getOptionValue('g')) {
                case "uniform":
                    randomRGB = new UniformRandomRGB();
                    break;
                case "pastel":
                    randomRGB = new PastelRandomRGB();
                    break;
                default:
                    System.err.println(String.format("Unknown generator: %s", commandLine.getOptionValue('g')));
                    usage(options);
            }
        } else {
            randomRGB = new UniformRandomRGB();
        }

        return randomRGB;
    }

    /**
     * Parse command line options, using GNU-style option parsing.
     * @param args Program arguments.
     * @param options Options.
     * @return Parsed options.
     */
    private static CommandLine parseOptions(String[] args, Options options) {
        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            usage(options);
        }
        return commandLine;
    }

    private static void usage(Options options) {
        new HelpFormatter().printHelp("Usage: stanford-worker [OPTIONS] maxcolors", options);
        System.exit(1);
    }

    private static Options programOptions() {
        Options options = new Options();
        options.addOption("g", "generator", true, "Generator to use: uniform, pastel (default: uniform)");
        return options;
    }

    private GenerateColors() {
    }
}
