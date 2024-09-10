package app.YUP.externalModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The EmailDetails class represents the details of an email.
 * It includes the recipient's email address, the body of the message, the subject of the email, and the attachment (if any).
 * This class uses Lombok annotations for automatic generation of getters, setters, constructors, and toString methods.
 *
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {

    /**
     * The recipient's email address.
     * This is the email address to which the email will be sent.
     */
    private String recipient;

    /**
     * The body of the email message.
     * This is the main content of the email.
     */
    private String msgBody;

    /**
     * The subject of the email.
     * This is the title that will appear in the recipient's email inbox.
     */
    private String subject;

    /**
     * The attachment to be sent with the email.
     * This is the path to the file that will be attached to the email.
     * It can be null if there is no attachment.
     */
    private String attachment;
}