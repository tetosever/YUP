package app.YUP.externalService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.internet.MimeMessage;

import app.YUP.externalModel.EmailDetails;
import app.YUP.exception.EmailNotSentException;

class EmailServiceImplementationTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImplementation emailService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailService.sender = "mattiapiazzalunga@gmail.com";
    }

    @Test
    void testSendMailWithoutAttachment() throws Exception {
        EmailDetails details = new EmailDetails("test@example.com", "Test Subject", "Hello, world!", "");

        emailService.sendMailWithoutAttachment(details);

        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void testSendMailWithoutAttachmentThrowsEmailNotSentException() {
        EmailDetails details = new EmailDetails("test@example.com", "Test Subject", "Hello, world!", "");
        doThrow(new RuntimeException("Failure")).when(javaMailSender).send(any(MimeMessage.class));

        Exception exception = assertThrows(EmailNotSentException.class, () -> {
            emailService.sendMailWithoutAttachment(details);
        });

        assertTrue(exception.getMessage().contains("Error while sending mail"));
    }

    @Test
    void testSendMailWithAttachment() throws Exception {
        EmailDetails details = new EmailDetails("test@example.com", "Test Subject", "Hello, world!", "fileForTest.txt");

        emailService.sendMailWithAttachment(details);

        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void testSendMailWithAttachmentThrowsEmailNotSentException() {
        EmailDetails details = new EmailDetails("test@example.com", "Test Subject", "Hello, world!", "path/to/attachment.txt");
        doThrow(new RuntimeException("Failure")).when(javaMailSender).send(any(MimeMessage.class));

        Exception exception = assertThrows(EmailNotSentException.class, () -> {
            emailService.sendMailWithAttachment(details);
        });

        assertTrue(exception.getMessage().contains("Error while sending mail"));
    }
}
