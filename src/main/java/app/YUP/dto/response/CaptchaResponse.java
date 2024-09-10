package app.YUP.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the data transfer object (DTO) for a Captcha response.
 * It is used to transfer data between processes or systems and can carry information for the captcha validation.
 * The class is annotated with Lombok annotations to automatically generate boilerplate code like getters, setters, constructors, and builders.
 *
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaResponse {

    /**
     * The success status of the captcha validation.
     * This is a boolean value that indicates whether the captcha validation was successful or not.
     */
    private boolean success;

    /**
     * The timestamp of the captcha challenge.
     * This is a string value that represents the timestamp of when the captcha challenge was issued.
     */
    private String challenge_ts;

    /**
     * The hostname of the site where the captcha was solved.
     * This is a string value that represents the hostname of the site where the captcha was solved.
     */
    private String hostname;
}