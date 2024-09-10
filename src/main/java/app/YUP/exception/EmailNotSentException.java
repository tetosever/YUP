package app.YUP.exception;

/**
 * This class represents a custom exception that is thrown when an email cannot be sent.
 * It extends the RuntimeException class, which is a form of Throwable that indicates conditions that a reasonable application might want to catch.
 *
 * @version 1.0
 */
public class EmailNotSentException extends RuntimeException{

    /**
     * Constructs a new EmailNotSentException with the specified detail message.
     * The detail message is saved for later retrieval by the getMessage() method.
     *
     * @param message the detail message.
     */
    public EmailNotSentException(final String message)
    {
        super(message);
    }

    /**
     * Constructs a new EmailNotSentException with the specified cause.
     * The cause is saved for later retrieval by the getCause() method.
     * A null value is permitted, and indicates that the cause is nonexistent or unknown.
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public EmailNotSentException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a new EmailNotSentException with the specified detail message and cause.
     * The detail message is saved for later retrieval by the getMessage() method.
     * The cause is saved for later retrieval by the getCause() method.
     * A null value is permitted, and indicates that the cause is nonexistent or unknown.
     *
     * @param message the detail message.
     * @param cause the cause.
     */
    public EmailNotSentException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}