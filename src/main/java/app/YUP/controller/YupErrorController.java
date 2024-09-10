package app.YUP.controller;

import app.YUP.externalModel.EmailDetails;
import app.YUP.externalService.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * This class is a controller for handling errors in the application.
 * It implements the ErrorController interface provided by Spring Boot.
 */
@Controller
@RequiredArgsConstructor
@Tag(name = "YUP - Error Controller", description = "This page document the RestAPI of Error Controller. All the end-point of the application are specified.")
public class YupErrorController implements ErrorController {

    private final EmailService emailService;

    /**
     * This method is used to handle errors in the application.
     * It takes a HttpServletRequest object, a HttpServletResponse object, and a Model object as parameters.
     * It sets the HTTP status code of the response to 403 (Forbidden).
     * It adds an attribute to the model with the error message from the request.
     * It returns a string that represents the name of the view to be rendered.
     *
     * @param request  This is the HttpServletRequest object that contains the request details.
     * @param response This is the HttpServletResponse object that contains the response details.
     * @param model    This is the Model object that can be used to pass attributes to the view.
     * @return String This returns the name of the view to be rendered.
     */
    @RequestMapping("/error")
    @Operation(summary = "Error", description = "Handle errors in the application",
            tags = {"Error", "ALL"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Returns Forbidden if the user does not have permission to create a reservation",
                    content = @Content(mediaType = "text/html")),
    })
    public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        model.addAttribute("exception", request.getAttribute("javax.servlet.error.message"));
        return "exception";
    }


    /**
     * This method is used to send an error email to the team.
     * It takes a Map object containing the error message as a parameter.
     * It sets the HTTP status code of the response to 200 (OK).
     * It creates an EmailDetails object with the recipient's email address, the error message, a subject line, and an empty attachment.
     * It then calls the sendMailWithoutAttachment method of the EmailService class to send the email.
     *
     * @param payload  This is a Map object containing the error message.
     * @param response This is the HttpServletResponse object that contains the response details.
     */
    @PostMapping("/sendErrorEmail")
    @Operation(summary = "Sending error email", description = "This method allows reporting an exception to the team",
            tags = {"Error", "POST"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully sent error message", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "400", description = "Returns Bad Request if the  body does not contain valid data", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "502", description = "Returns Bad Gateway if there is an error while processing the request", content = @Content(mediaType = "text/html"))})
    @ResponseBody
    public void sendErrorEmail(@RequestBody Map<String, String> payload, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        String message = payload.get("message");
        EmailDetails details = new EmailDetails("theyupteam@gmail.com", message, "Exception report!", "");
        emailService.sendMailWithoutAttachment(details);
    }
}