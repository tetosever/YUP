package app.YUP.exception;

/**
 * Exception thrown when a user attempts to access a resource they do not have permission to access.
 * This class extends the {@link RuntimeException} to provide unchecked exception capabilities.
 */
public class UnauthorizedAccessException extends RuntimeException {

    /**
     * Constructs a new UnauthorizedAccessException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    /**
     * Constructs a new UnauthorizedAccessException with the specified cause.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public UnauthorizedAccessException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a new UnauthorizedAccessException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

