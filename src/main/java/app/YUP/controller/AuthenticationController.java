package app.YUP.controller;

import app.YUP.dto.request.AuthenticationRequest;
import app.YUP.model.User;
import app.YUP.service.AuthenticationService;
import app.YUP.externalModel.EmailDetails;
import app.YUP.externalService.EmailService;
import app.YUP.service.InternalUserService;
import app.YUP.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * This class is a controller for handling authentication related requests.
 * It provides endpoints for logging in, logging out, and displaying the login page.
 */
@Controller
@Tag(name = "YUP - Authentication Controller", description = "This page document the RestAPI of Authentication Controller. All the end-point of the application are specified.")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;
    private final InternalUserService internalUserService;
    private final UserService userService;
    private final EmailService emailService;

    /**
     * This method is used to display the login page.
     * It takes a Model object as a parameter and adds a new AuthenticationRequest object to it.
     * It returns a string that represents the name of the view to be rendered.
     *
     * @param model This is the Model object that can be used to pass attributes to the view.
     * @return String This returns the name of the view to be rendered.
     */
    @GetMapping("/login")
    @Operation(summary = "Show login page",
            description = "Returns the view for the login page, with an AuthenticationRequest object for binding the data.",
            tags = { "Auth", "GET" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login page displayed successfully",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "text/html"))
    })
    public String loginPage(Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        model.addAttribute("authenticationRequest", new AuthenticationRequest());
        return "login";
    }

    /**
     * This method is used to authenticate a user.
     * It takes an AuthenticationRequest object, a HttpServletResponse object, and a captcha string as parameters.
     * It uses the UserService to validate the captcha and the AuthenticationService to authenticate the user and set a JWT token in a cookie.
     * It also sends an email to the user with the login details.
     *
     * @param authenticationRequest This is a request object that contains the username and password of the user.
     * @param response This is the HttpServletResponse object that contains the response details.
     * @param captcha This is a string that contains the captcha response.
     * @return String This returns a redirect URL to the homepage.
     */
    @PostMapping("/api/login")
    @Operation(summary = "Authenticate a user",
            description = "Authenticates the user with username and password, after passing the anti-robot check, sets a JWT token in a cookie, and redirects to the homepage.",
            tags = {"Auth", "POST"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authentication and redirection to the homepage",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "302", description = "Successful authentication and redirection to the homepage",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "400", description = "Invalid authentication request",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "502", description = "Bad Gateway",
                    content = @Content(mediaType = "text/html"))
    })
    public String apiLogin(
            @ModelAttribute AuthenticationRequest authenticationRequest,
            HttpServletResponse response,
            @RequestParam("g-recaptcha-response") String captcha
    ) {
        internalUserService.isValidCaptcha(captcha);
        authenticationService.authenticate(authenticationRequest, response);

        logger.info("Added token in cookie");

        User user = userService.getUserByUsername(authenticationRequest.getUsername());

        EmailDetails details = new EmailDetails(user.getEmail(), "<!DOCTYPE html>\n<html lang='en'>\n<body style='background-color: #5185d5; padding: 15px; max-width: 500px; margin: 0 auto;'>\n    <h2 style='text-align: center; padding: 0px; margin: 0px; padding-bottom: 10px; color: white; background-color: #5185d5;'><span style='font-weight: 800;'>Your Unique Party</span></h2>\n    <div style='overflow: hidden; padding: 0; margin: 0; border-radius: 10px; background-color: white;'>\n        <img src=\"https://i.pinimg.com/736x/1e/07/92/1e0792131bfdb4b259de255811ba7cc5.jpg\" style='width: 100%; height: 160px; object-fit: cover;'>\n        <div style='padding: 15px 25px;'>\n            <p style='text-align: center; margin-top: 0;'><span style='font-weight: 800;'>Welcome back YUPper!</span></p>\n            <p>Hello <span style='font-weight: 800;'>"+user.getFirstname()+"</span>, welcome back to <span style='font-weight: 800;'>YUP!</span> We're happy you're having fun with us!</p>\n            <p style=\"margin: 0px; margin-bottom: 7px; margin-top: 7px;\">Are you ready for the next exclusive YUP? &#127775;</p>\n            <p>Browse the site and discover the YUPs in your area, and remember...</p>\n            <p style='text-align: center;'><span style='font-weight: 800;'>... if it's a party, it's YUP!</span></p>\n            <p style='text-decoration: none; font-style: italic'>Wasn't it you? <a style='text-decoration: none;' href='mailto:theyupteam@gmail.com'>Contact us.</a></p>\n            <div style='margin-top: 16px;'>\n                <p style=\"margin: 0px; margin-bottom: 2px;\"><span style='font-weight: 800;'>The YUP team.</span></p>\n                <p style='margin: 0px; margin-bottom: 2px; text-decoration: none; font-style: italic'><a style='text-decoration: none;' href='mailto:theyupteam@gmail.com'>theyupteam@gmail.com</a>\n                <p style='margin: 0px;  margin-bottom: 16px;'>&copy; 2024. YUP - Your Unique Party</p>\n            </div>  \n        </div>\n    </div>\n</body>\n</html>", "New login in YUP!", "");
        emailService.sendMailWithoutAttachment(details);

        return "redirect:/event/all";
    }


    /**
     * This method is used to log out a user.
     * It takes a HttpServletRequest object and a HttpServletResponse object as parameters.
     * It uses the AuthenticationService to delete the JWT cookie.
     * It logs the logout operation and returns a string that represents the name of the view to be rendered.
     *
     * @param request This is the HttpServletRequest object that contains the request details.
     * @param response This is the HttpServletResponse object that contains the response details.
     * @return String This returns the name of the view to be rendered.
     */
    @GetMapping("/api/logout")
    @Operation(summary = "Log out the current user",
            description = "Logs out the current user by deleting the JWT cookie and redirects to the homepage.",
            tags = { "Auth", "GET" })
    @ApiResponses(value = {
            @ApiResponse( responseCode = "200", description = "Successfully logged out and redirected to the homepage",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "403", description = "Forbidden, cookie not found - user is not logged ",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "text/html"))
    })
    public String apiLogout(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        authenticationService.logout(request, response);
        logger.info("Logged out");
        return "around";
    }
}