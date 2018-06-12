package ca.marshallasch.veil.exceptions;

/**
 * This exception is thrown when the hash table is expecting there to be only 1 result but more
 * were found.
 * @author Marshall Asch
 * @version 1.0
 * @since 2018-06-08
 */
public class TooManyResultsException extends Exception
{
    public TooManyResultsException() {
        super("Too many results were found.");
    }

    public TooManyResultsException(String message) {
        super(message);
    }
}
