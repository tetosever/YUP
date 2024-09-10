package app.YUP.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration class for Google reCAPTCHA.
 * This class is used to encapsulate the site key and secret key for Google reCAPTCHA.
 * The keys are configured in the application properties with the prefix "google.recaptcha.key".
 * It uses the Lombok library to automatically generate getter methods for the fields.
 * It is annotated with @Component to indicate that it is a Spring Bean and with @ConfigurationProperties to bind the properties prefixed with "google.recaptcha.key" to the fields of this class.
 */
@Getter
@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
public class CaptchaSettings {

    /**
     * The site key for Google reCAPTCHA.
     * This key is public and is used in the HTML code your site serves to users.
     */
    private String site = "6LcMDrwpAAAAAHtq8URwTbj3Etf5ye9FAwf2Hcc5";

    /**
     * The secret key for Google reCAPTCHA.
     * This key is private and used for communication between your site and Google.
     * Be sure to keep it a secret.
     */
    private String secret = "6LcMDrwpAAAAACsaMNWX1zVbiFMykvDv4pDG8TzG";

}