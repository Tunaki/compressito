package fr.tunaki.compressito.exec;

/**
 * Represents an exception thrown by the compression process.
 * @author gboue
 */
public class CompressionException extends RuntimeException {

    private static final long serialVersionUID = 6311765854606039728L;

    /**
     * Construct a new instance with the given message.
     * @param message Exception message.
     */
    public CompressionException(String message) {
        super(message);
    }

    /**
     * Construct a new instance with the given message and cause.
     * @param message Exception message.
     * @param cause Cause of the exception.
     */
    public CompressionException(String message, Throwable cause) {
        super(message, cause);
    }

}