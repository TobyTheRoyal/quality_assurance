package at.tugraz.ist.qs2021.messageboard;

/**
 * Exception class used to signal that a worker does
 * not know a client given the communication ID
 */
public class UnknownClientException extends Exception {
    /**
     * Constructs a new UnknownClientException with the specified detail message.
     *
     * @param message the detail message.
     */
    public UnknownClientException(String message) {
        super(message);
    }
}
