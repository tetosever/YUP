package app.YUP.externalService;

import app.YUP.externalModel.EmailDetails;

/**
 * This interface provides methods to send emails.
 * It defines two methods, one for sending an email without an attachment and one for sending an email with an attachment.
 * The methods take an EmailDetails object as a parameter, which contains the details of the email to be sent.
 */
public interface EmailService {

    /**
     * Sends an email without an attachment.
     * This method takes an EmailDetails object as a parameter, which contains the details of the email to be sent.
     * The EmailDetails object should contain the recipient's email address, the subject of the email, and the body of the email.
     *
     * @param details an EmailDetails object containing the details of the email to be sent
     */
    void sendMailWithoutAttachment(EmailDetails details);

    /**
     * Sends an email with an attachment.
     * This method takes an EmailDetails object as a parameter, which contains the details of the email to be sent.
     * The EmailDetails object should contain the recipient's email address, the subject of the email, the body of the email, and the attachment.
     *
     * @param details an EmailDetails object containing the details of the email to be sent, including the attachment
     */
    void sendMailWithAttachment(EmailDetails details);
}