package eu.danieldk.quzah.random;

/**
 * Interface for random object generators.
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public interface RandomI<T> {
    /**
     * Generate a random instance of an object of type <tt>T</tt>.
     * @return A random instance.
     */
    public T next();
}
