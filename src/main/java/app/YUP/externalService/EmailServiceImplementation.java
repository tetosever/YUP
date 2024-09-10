package app.YUP.externalService;

import app.YUP.exception.EmailNotSentException;
import app.YUP.externalModel.EmailDetails;

import java.io.File;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * This class implements the EmailService interface and provides the functionality to send emails with or without attachments.
 * It uses Spring's JavaMailSender to send emails.
 * It is annotated with @Service to indicate that it is a Spring service component.
 */
@Service
public class EmailServiceImplementation implements EmailService {

    /**
     * The JavaMailSender instance used to send emails.
     * It is automatically injected by Spring (denoted by @Autowired).
     */
    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * The sender's email address.
     * This is currently hardcoded to "theyupteam@gmail.com", but in a real application, it would be better to externalize this to a configuration file.
     */
    public String sender = "theyupteam@gmail.com";

    /**
     * Sends an email without an attachment.
     * This method creates a MimeMessage, sets the sender, recipient, and subject from the provided EmailDetails,
     * and then sends the email using the JavaMailSender.
     *
     * @param details an EmailDetails object containing the details of the email to be sent
     * @throws EmailNotSentException if an error occurs while sending the email
     */
    public void sendMailWithoutAttachment(EmailDetails details) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessage.setContent(details.getMsgBody(), "text/html");
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setSubject(details.getSubject());

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new EmailNotSentException("Error while sending mail: " + e.getMessage());
        }
    }

    /**
     * Sends an email with an attachment.
     * This method creates a MimeMessage, sets the sender, recipient, and subject from the provided EmailDetails,
     * attaches the file specified in the EmailDetails, and then sends the email using the JavaMailSender.
     *
     * @param details an EmailDetails object containing the details of the email to be sent, including the attachment
     * @throws EmailNotSentException if an error occurs while sending the email
     */
    public void sendMailWithAttachment(EmailDetails details) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessage.setContent(details.getMsgBody(), "text/html");
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setSubject(details.getSubject());
            FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));

            mimeMessageHelper.addAttachment(file.getFilename(), file);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new EmailNotSentException("Error while sending mail: " + e.getMessage());
        }
    }
}