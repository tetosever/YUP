package app.YUP.controller;

import app.YUP.dto.request.ChangePasswordRequest;
import app.YUP.dto.request.RegistrationRequest;
import app.YUP.dto.request.UserUpdateRequest;
import app.YUP.dto.response.UserResponse;
import app.YUP.externalModel.EmailDetails;
import app.YUP.model.ExternalUser;
import app.YUP.model.InternalUser;
import app.YUP.model.User;
import app.YUP.service.AuthenticationService;
import app.YUP.externalService.EmailService;
import app.YUP.service.ExternalUserService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * This class is a controller for handling user related requests.
 * It provides endpoints for creating a new user and viewing user details.
 */
@Tag(name = "YUP - User Controller", description = "This page document the RestAPI of User Controller. All the end-point of the application are specified.")
@Controller
@RequestMapping("/user/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final InternalUserService internalUserService;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;

    /**
     * This method is used to register a new user.
     * It takes a RegistrationRequest object and a captcha string as parameters.
     * It uses the UserService to validate the captcha and create a new user.
     * It also sends an email to the user with the registration details.
     *
     * @param registrationRequest This is a request object that contains the details of the user to be registered.
     * @param captcha             This is a string that contains the captcha response.
     * @return String This returns a redirect URL to the login page.
     */
    @PostMapping("/api/newUser")
    @Operation(summary = "Register a new user", description = "Register a new user in the system, after passing the anti-robot check",
            tags = {"User", "POST"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registers a new user",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "302", description = "Redirects to the login view upon successful registration",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "400", description = "Bad Request if the request body does not contain valid user data",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "502", description = "Bad Gateway",
                    content = @Content(mediaType = "text/html"))
    })
    public String register(
            @ModelAttribute RegistrationRequest registrationRequest,
            @RequestParam("g-recaptcha-response") String captcha,
            HttpServletResponse response
    ) {
        response.setStatus(HttpStatus.OK.value());
        internalUserService.isValidCaptcha(captcha);
        internalUserService.createInternalUser(registrationRequest);
        EmailDetails details = new EmailDetails(registrationRequest.getEmail(), "<!DOCTYPE html>\n<html lang='en'>\n<body style='background-color: #5185d5; padding: 15px; max-width: 500px; margin: 0 auto;'>\n    <h2 style='text-align: center; padding: 0px; margin: 0px; padding-bottom: 10px; color: white; background-color: #5185d5;'><span style='font-weight: 800;'>Your Unique Party</span></h2>\n    <div style='overflow: hidden; padding: 0; margin: 0; border-radius: 10px; background-color: white;'>\n        <img src=\"https://www.theknot.com/tk-media/images/304b31fa-9de9-4b98-ba68-336372309614.jpg\" style='width: 100%; height: 160px; object-fit: cover;'>\n        <div style='padding: 15px 25px;'>\n            <p style='text-align: center; margin-top: 0;'><span style='font-weight: 800;'>You're officially a YUPper!</span></p>\n            <p>Hello <span style='font-weight: 800;'>"+registrationRequest.getFirstname()+"</span>, welcome to <span style='font-weight: 800;'>YUP!</span> We're glad to have you with us!</p>\n            <p style=\"margin: 0px; margin-bottom: 7px; margin-top: 7px;\">Are you ready to have some fun? &#127881;</p>\n            <p>Explore the site and join your first YUP, and remember...</p>\n            <p style='text-align: center;'><span style='font-weight: 800;'>... if it's a party, it's YUP!</span></p>\n            <div style='margin-top: 16px;'>\n                <p style=\"margin: 0px; margin-bottom: 2px;\"><span style='font-weight: 800;'>The YUP team.</span></p>\n                <p style='margin: 0px; margin-bottom: 2px; text-decoration: none; font-style: italic'><a style='text-decoration: none;' href='mailto:theyupteam@gmail.com'>theyupteam@gmail.com</a>\n                <p style='margin: 0px;  margin-bottom: 16px;'>&copy; 2024. YUP - Your Unique Party</p>\n            </div>  \n        </div>\n    </div>\n</body>\n</html>", "Welcome YUPper!", "");
        emailService.sendMailWithoutAttachment(details);

        return "redirect:/auth/login";
    }

    /**
     * This method is used to delete a user from the system.
     * It retrieves the user ID from the authentication cookie in the request.
     * It then uses the UserService to fetch the user from the database and delete it.
     * Finally, it redirects the user to the login page.
     *
     * @param request  The HttpServletRequest object containing the request details.
     * @param response The HttpServletResponse object to handle the response.
     * @return A string representing the redirect URL to the login page.
     */
    @PostMapping("/api/delete")
    @Operation(summary = "Delete a user", description = "Deletes a user based on the user ID retrieved from an authentication cookie.",
            tags = { "User", "POST" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Successfully deleted the user and redirected to login page"),
            @ApiResponse(responseCode = "403", description = "Bad request, if user ID is not found in the cookie"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, if the user is not authenticated"),
            @ApiResponse(responseCode = "404", description = "Not found, if no user corresponds to the given ID"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error, if there are issues deleting the user")
    })
    public String deleteUser(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        User user = userService.getUserById(authenticationService.getIdFromCookie(request));

        EmailDetails details = new EmailDetails(user.getEmail(),
                "<!DOCTYPE html>\n<html lang='en'>\n<body style='background-color: #5185d5; padding: 15px; max-width: 500px; margin: 0 auto;'>\n" +
                        "<h2 style='text-align: center; padding: 0px; margin: 0px; padding-bottom: 10px; color: white; background-color: #5185d5;'><span style='font-weight: 800;'>Your Unique Party</span></h2>\n" +
                        "<div style='overflow: hidden; padding: 0; margin: 0; border-radius: 10px; background-color: white;'>\n" +
                        "<img src=\"https://c.stocksy.com/a/Og4400/z9/971316.jpg\" style='width: 100%; height: 160px; object-fit: cover;'>\n " +
                        "<div style='padding: 15px 25px;'>\n" +
                        "<p style='text-align: center; margin-top: 0;'><span style='font-weight: 800;'>We're sorry to say goodbye!</span></p>\n" +
                        "<p>Hello <span style='font-weight: 800;'>"+user.getFirstname()+"</span>, we are here to tell you that despite our regret, your account has been successfully deleted from <span style='font-weight: 800;'>YUP</span>.</p>\n" +
                        "<p style=\"margin: 0px; margin-bottom: 7px; margin-top: 7px;\">We can't wait to have you back among us &#128542;</p>\n" +
                        "<p>When you change your mind you will have to re-register, and remember...</p>\n" +
                        "<p style='text-align: center;'><span style='font-weight: 800;'>... if it's a party, it's YUP!</span></p>\n            <div style='margin-top: 16px;'>\n                <p style=\"margin: 0px; margin-bottom: 2px;\"><span style='font-weight: 800;'>The YUP team.</span></p>\n                <p style='margin: 0px; margin-bottom: 2px; text-decoration: none; font-style: italic'><a style='text-decoration: none;' href='mailto:theyupteam@gmail.com'>theyupteam@gmail.com</a>\n                <p style='margin: 0px;  margin-bottom: 16px;'>&copy; 2024. YUP - Your Unique Party</p>\n            </div>  \n        </div>\n    </div>\n</body>\n</html>", "Welcome YUPper!", "");
        emailService.sendMailWithoutAttachment(details);

        userService.deleteUser(user);
        return "redirect:/auth/login";
    }

    /**
     * This method is used to update a user's details.
     *
     * @param userUpdateRequest The request object containing the updated user details.
     * @param request           The HTTP request object.
     * @param response          The HTTP response object.
     * @return A string representing a redirect to the user view page.
     */
    @PostMapping("/api/update")
    @Operation(summary = "Update user details", description = "Updates the user's details based on the provided UserUpdateRequest object.",
            tags = { "User", "POST" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "302", description = "Redirect to the user view page"),
            @ApiResponse(responseCode = "400", description = "Bad Request if the UserUpdateRequest is invalid"),
            @ApiResponse(responseCode = "404", description = "User not found with provided ID")
    })
    public String updateUser(@ModelAttribute UserUpdateRequest userUpdateRequest,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        User user = userService.getUserById(authenticationService.getIdFromCookie(request));
        userService.updateUser(user, userUpdateRequest);

        response.setStatus(HttpStatus.OK.value());
        return "redirect:/user/viewUser";
    }

    /**
     * This method is used to change a user's password.
     *
     * @param changePasswordRequest The request object containing the new password and its confirmation.
     * @param request               The HTTP request object.
     * @param response              The HTTP response object.
     * @return A string representing a redirect to the login page.
     */
    @PostMapping("/api/changePassword")
    @Operation(summary = "Change user password", description = "Changes the password for a user identified by a cookie in the request.",
            tags = { "User", "POST" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed"),
            @ApiResponse(responseCode = "302", description = "Redirect to the login page after successful password change"),
            @ApiResponse(responseCode = "401", description = "Unauthorized if the user is not authenticated"),
            @ApiResponse(responseCode = "404", description = "User not found with provided ID")
    })
    public String changePassword(ChangePasswordRequest changePasswordRequest,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        InternalUser internalUser = internalUserService.getUserById(authenticationService.getIdFromCookie(request));
        internalUserService.changePassword(changePasswordRequest, internalUser);

        authenticationService.logout(request, response);

        response.setStatus(HttpStatus.OK.value());

        return "redirect:/auth/login";
    }


    /**
     * This method is used to display the registration page.
     * It takes a Model object as a parameter and adds a new RegistrationRequest object to it.
     * It returns a string that represents the name of the view to be rendered.
     *
     * @param model This is the Model object that can be used to pass attributes to the view.
     * @return String This returns the name of the view to be rendered.
     */
    @GetMapping("/newUser")
    @Operation(summary = "Registration view", description = "Show the registration page",
            tags = {"User", "GET"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returns the registration page",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "text/html"))
    })
    public String registrationPage(Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        model.addAttribute("user", new RegistrationRequest());
        return "registration";
    }

    /**
     * This method is used to display the user page.
     * It takes a Model object, a HttpServletRequest object, and a HttpServletResponse object as parameters.
     * It uses the AuthenticationService to get the username from the cookie in the request.
     * It then uses the UserService to get the User object and map it to a UserResponse object.
     * It adds the UserResponse object to the model and sets the HTTP status code of the response to 200 (OK).
     * It returns a string that represents the name of the view to be rendered.
     *
     * @param model    This is the Model object that can be used to pass attributes to the view.
     * @param request  This is the HttpServletRequest object that contains the request details.
     * @param response This is the HttpServletResponse object that contains the response details.
     * @return String This returns the name of the view to be rendered.
     */
    @GetMapping("/viewUser")
    @Operation(summary = "Registration view", description = "View the details of the logged user",
            tags = {"User", "GET"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returns the user page with all the details",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "403", description = "Forbidden, cookie not found",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "text/html"))
    })
    public String userPage(Model model, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());

        User user = userService.getUserById(authenticationService.getIdFromCookie(request));
        UserResponse userResponse = userService.mapEntityToDTO(user);
        userResponse.setInternal(internalUserService.existsById(authenticationService.getIdFromCookie(request)));

        model.addAttribute("user", userResponse);

        return "profile";
    }

    /**
     * This method is used to check if a user with a given username exists in the system.
     *
     * @param payload A map containing the username to be checked. The key is "message".
     * @param response The HttpServletResponse object to handle the response.
     * @return A ResponseEntity containing a boolean value indicating whether a user with the given username exists.
     *         If the user exists, the boolean value is true; otherwise, it is false.
     */
    @PostMapping("/checkIfUserExist")
    @Operation(summary = "Returns if exist the user by username", description = "This end-point is useful to test if a username is available",
            tags = {"User", "Post"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returns a boolean if exist the user by username",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "text/html"))
    })
    public ResponseEntity<Boolean> checkIfUserExist(@RequestBody Map<String, String> payload, HttpServletResponse response) {
        boolean result;
        try{
            userService.getUserByUsername(payload.get("message"));
            result = true;
            return ResponseEntity.ok(result);
        }catch (Exception e){
            result = false;
            return ResponseEntity.ok(result);
        }
    }
}
