package app.YUP.exception;

/**
 * This class represents a custom exception that is thrown when a user tries to perform an action they do not have permission for.
 * It extends the RuntimeException class, meaning it is an unchecked exception.
 *
 * @version 1.0
 */
public class InvalidPermissionException extends RuntimeException{

    /**
     * Constructs a new InvalidPermissionException with the specified detail message.
     * The detail message is saved for later retrieval by the Throwable.getMessage() method.
     *
     * @param message the detail message.
     */
    public InvalidPermissionException(final String message)
    {
        super(message);
    }

    /**
     * Constructs a new InvalidPermissionException with the specified cause.
     * The cause is saved for later retrieval by the Throwable.getCause() method.
     * A null value is permitted, and indicates that the cause is nonexistent or unknown.
     *
     * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method).
     */
    public InvalidPermissionException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a new InvalidPermissionException with the specified detail message and cause.
     * The detail message is saved for later retrieval by the Throwable.getMessage() method.
     * The cause is saved for later retrieval by the Throwable.getCause() method.
     * A null value is permitted, and indicates that the cause is nonexistent or unknown.
     *
     * @param message the detail message.
     * @param cause the cause.
     */
    public InvalidPermissionException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}