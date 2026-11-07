package main.exceptions;

/**
 * Thrown to indicate that an operation requiring an element was invoked on an
 * empty container.
 */
public class EmptyException extends RuntimeException {

    /**
     * Constructs an EmptyException with no detail message.
     */
    public EmptyException() {
        super();
    }

    /**
     * Constructs an EmptyException with the specified detail message.
     *
     * @param message the detail message
     */
    public EmptyException(String message) {
        super(message);
    }
}

