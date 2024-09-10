package app.YUP.controller;

import app.YUP.service.AuthenticationService;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller that handles generic operations for the application.
 *
 * <p>This controller provides the endpoint to return the homepage of the application.
 * The controller is annotated with Swagger annotations to document the API endpoints.
 * It uses {@link AuthenticationService} to manage authentication and {@link UserService} for user-related operations.</p>
 */
@Controller
@Tag(name = "YUP - Generic Controller", description = "This page document the RestAPI of Generic Controller. All the end-point of the application are specified.")
@RequiredArgsConstructor
public class GenericController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    /**
     * Returns the homepage of the application.
     *
     * @param request  The HTTP request object.
     * @param response The HTTP response object.
     * @param model     The model object to add attributes to.
     *
     * @return The name of the view to render, which is "index" in this case.
     *
     * @see HttpServletRequest
     * @see HttpServletResponse
     * @see Model
     */
    @GetMapping("/home")
    @Operation(summary = "Return the homepage", description = "This end-point is used to return the homepage of the application",
            tags = {"Generic", "GET"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returns the homepage",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error ",
                    content = @Content(mediaType = "text/html"))
    })
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {

        String firstName = authenticationService.getFirstNameFromCookie(request);
        model.addAttribute("firstname", firstName);
        response.setStatus(HttpStatus.OK.value());
        return "index";
    }
}
