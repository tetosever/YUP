package app.YUP.exception;

/**
 * Custom exception class for handling scenarios where a cookie is not found.
 * Extends the {@link RuntimeException} to allow unchecked exceptions.
 */
public class ItemAlreadyExistException extends RuntimeException{

    /**
     * Constructs a new CookieNotFoundException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public ItemAlreadyExistException(final String message)
    {
        super(message);
    }

    /**
     * Constructs a new CookieNotFoundException with the specified cause.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ItemAlreadyExistException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a new CookieNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *              (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ItemAlreadyExistException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}