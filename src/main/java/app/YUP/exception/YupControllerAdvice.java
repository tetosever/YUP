package app.YUP.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * This class provides centralized exception handling across all {@code @RequestMapping} methods through {@code @ExceptionHandler} methods.
 * It is a kind of interceptor of exceptions thrown by methods annotated with {@code @RequestMapping}.
 *
 * @version 1.0
 */
@ControllerAdvice
public class YupControllerAdvice {

    /**
     * Handles exceptions of type {@link java.security.InvalidParameterException} by setting the HTTP response status to 400 (Bad Request)
     * and returning a {@link ModelAndView} object that directs the user to an error view with an appropriate error message.
     * This method ensures that when an invalid parameter is passed to a controller method, the user is informed of the mistake
     * in a user-friendly manner.
     *
     * @param e The {@link Exception} object representing the caught {@link java.security.InvalidParameterException}.
     * @param response The {@link HttpServletResponse} object used to set the HTTP status code.
     * @return A {@link ModelAndView} object configured to display the error view with the error code and message.
     */
    @ExceptionHandler(java.security.InvalidParameterException.class)
    public ModelAndView errorInvalidParameterException(Exception e, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exception");
        modelAndView.addObject("status", HttpStatus.BAD_REQUEST.value());
        modelAndView.addObject("exception", e.getClass());
        modelAndView.addObject("message", e.getMessage());
        return modelAndView;
    }

    /**
     * Handles exceptions of type {@link ResourceNotFoundException} by setting the HTTP response status to 404 (Not Found)
     * and returning a ModelAndView object that displays an exception view with the exception message.
     * This method acts as a global exception handler within the controller to catch and process
     * {@link ResourceNotFoundException} instances that may be thrown during the handling of HTTP requests.
     *
     * @param e The {@link ResourceNotFoundException} exception that was thrown.
     * @param response The {@link HttpServletResponse} object used to set the HTTP status code.
     * @return A {@link ModelAndView} object configured to display the exception view with the exception message.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleException(ResourceNotFoundException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exception");
        modelAndView.addObject("status", HttpStatus.NOT_FOUND.value());
        modelAndView.addObject("exception", e.getClass());
        modelAndView.addObject("message", e.getMessage());
        return modelAndView;
    }

    /**
     * Handles exceptions of type {@link CookieNotFoundException} by setting the HTTP response status to 403 (Forbidden)
     * and returning a ModelAndView object that redirects the user to the login view with an error message.
     *
     * @param e The {@link CookieNotFoundException} exception that was thrown.
     * @param response The {@link HttpServletResponse} object used to set the HTTP status code.
     * @return A {@link ModelAndView} object configured to display the login view with the exception message.
     */
    @ExceptionHandler(CookieNotFoundException.class)
    public ModelAndView handleException(CookieNotFoundException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("status", HttpStatus.FORBIDDEN.value());
        modelAndView.addObject("exception", e.getClass());
        modelAndView.addObject("message", e.getMessage());
        return modelAndView;
    }

    /**
     * Handles exceptions of type {@link InvalidParameterException} by setting the HTTP response status to 403 (Forbidden)
     * and returning a ModelAndView object that displays an exception view with the exception message.
     * This method acts as a global exception handler within the controller to catch and process
     * {@link InvalidParameterException} instances that may be thrown during the handling of HTTP requests.
     *
     * @param e The {@link InvalidParameterException} exception that was thrown.
     * @param response The {@link HttpServletResponse} object used to set the HTTP status code.
     * @return A {@link ModelAndView} object configured to display the exception view with the exception message.
     */
    @ExceptionHandler(InvalidParameterException.class)
    public ModelAndView handleException(InvalidParameterException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exception");
        modelAndView.addObject("status", HttpStatus.FORBIDDEN.value());
        modelAndView.addObject("exception", e.getClass());
        modelAndView.addObject("message", e.getMessage());

        return modelAndView;
    }

    /**
     * Handles exceptions of type {@link InvalidPermissionException} by setting the HTTP response status to 401 (Unauthorized)
     * and returning a ModelAndView object that displays an exception view with the exception message.
     * This method acts as a global exception handler within the controller to catch and process
     * {@link InvalidPermissionException} instances that may be thrown during the handling of HTTP requests.
     *
     * @param e The {@link InvalidPermissionException} exception that was thrown.
     * @param response The {@link HttpServletResponse} object used to set the HTTP status code.
     * @return A {@link ModelAndView} object configured to display the exception view with the exception message.
     */
    @ExceptionHandler(InvalidPermissionException.class)
    public ModelAndView handleException(InvalidPermissionException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exception");
        modelAndView.addObject("status", HttpStatus.UNAUTHORIZED.value());
        modelAndView.addObject("exception", e.getClass());
        modelAndView.addObject("message", e.getMessage());

        return modelAndView;
    }

    /**
     * Handles exceptions of type {@link CaptchaNotValidException} by setting the HTTP response status to 403 (Forbidden)
     * and returning a ModelAndView object that displays an exception view with the exception message.
     * This method acts as a global exception handler within the controller to catch and process
     * {@link CaptchaNotValidException} instances that may be thrown during the handling of HTTP requests.
     *
     * @param e The {@link CaptchaNotValidException} exception that was thrown.
     * @param response The {@link HttpServletResponse} object used to set the HTTP status code.
     * @return A {@link ModelAndView} object configured to display the exception view with the exception message.
     */
    @ExceptionHandler(CaptchaNotValidException.class)
    public ModelAndView handleException(CaptchaNotValidException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exception");
        modelAndView.addObject("status", HttpStatus.FORBIDDEN.value());
        modelAndView.addObject("exception", e.getClass());
        modelAndView.addObject("message", e.getMessage());

        return modelAndView;
    }

    /**
     * Handles exceptions of type {@link EmailNotSentException} by setting the HTTP response status to 502 (Bad Gateway)
     * and returning a {@link ModelAndView} object that directs the user to an error view with an appropriate error message.
     * This method ensures that when an email is not sent, the user is informed of the mistake
     * in a user-friendly manner.
     *
     * @param e The {@link EmailNotSentException} object representing the caught exception.
     * @param response The {@link HttpServletResponse} object used to set the HTTP status code.
     * @return A {@link ModelAndView} object configured to display the error view with the error code and message.
     */
    @ExceptionHandler(EmailNotSentException.class)
    public ModelAndView handleException(EmailNotSentException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_GATEWAY.value());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exception");
        modelAndView.addObject("status", HttpStatus.BAD_GATEWAY.value());
        modelAndView.addObject("exception", e.getClass());
        modelAndView.addObject("message", e.getMessage());

        return modelAndView;
    }

    /**
     * Handles exceptions of type {@link IllegalStateException} by setting the HTTP response status to 500 (Internal Server Error)
     * and returning a ModelAndView object that displays an exception view with the exception message.
     * This method acts as a global exception handler within the controller to catch and process
     * {@link IllegalStateException} instances that may be thrown during the handling of HTTP requests.
     *
     * @param e The {@link IllegalStateException} exception that was thrown.
     * @param response The {@link HttpServletResponse} object used to set the HTTP status code.
     * @return A {@link ModelAndView} object configured to display the exception view with the exception message.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ModelAndView handleException(IllegalStateException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exception");
        modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        modelAndView.addObject("exception", e.getClass());
        modelAndView.addObject("message", e.getMessage());

        return modelAndView;
    }

    /**
     * Handles exceptions of type {@link IllegalArgumentException} by setting the HTTP response status to 400 (Bad Request)
     * and returning a ModelAndView object that displays an exception view with the exception message.
     * This method acts as a global exception handler within the controller to catch and process
     * {@link IllegalArgumentException} instances that may be thrown during the handling of HTTP requests.
     *
     * @param e The {@link IllegalArgumentException} exception that was thrown.
     * @param response The {@link HttpServletResponse} object used to set the HTTP status code.
     * @return A {@link ModelAndView} object configured to display the exception view with the exception message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleException(IllegalArgumentException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exception");
        modelAndView.addObject("status", HttpStatus.BAD_REQUEST.value());
        modelAndView.addObject("exception", e.getClass());
        modelAndView.addObject("message", e.getMessage());

        return modelAndView;
    }

    /**
     * Handles exceptions of type {@link ItemAlreadyExistException} by setting the HTTP response status to 500 (Bad Gateway)
     * and returning a {@link ModelAndView} object that directs the user to an error view with an appropriate error message.
     * This method ensures that when an email is not sent, the user is informed of the mistake
     * in a user-friendly manner.
     *
     * @param e The {@link EmailNotSentException} object representing the caught exception.
     * @param response The {@link HttpServletResponse} object used to set the HTTP status code.
     * @return A {@link ModelAndView} object configured to display the error view with the error code and message.
     */
    @ExceptionHandler(ItemAlreadyExistException.class)
    public ModelAndView handleException(ItemAlreadyExistException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_GATEWAY.value());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exception");
        modelAndView.addObject("status", HttpStatus.BAD_GATEWAY.value());
        modelAndView.addObject("exception", e.getClass());
        modelAndView.addObject("message", e.getMessage());

        return modelAndView;
    }


    /**
     * Handles exceptions of type {@link UnauthorizedAccessException} by setting the HTTP response status to 403 (Forbidden)
     * and returning a {@link ModelAndView} object that directs the user to an error view with an appropriate error message.
     * This method ensures that when an unauthorized access occurs, the user is informed in a user-friendly manner.
     *
     * @param e The {@link UnauthorizedAccessException} object representing the caught exception.
     * @param response The {@link HttpServletResponse} object used to set the HTTP status code.
     * @return A {@link ModelAndView} object configured to display the error view with the error code and message.
     */
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ModelAndView handleException(UnauthorizedAccessException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exception");
        modelAndView.addObject("status", HttpStatus.FORBIDDEN.value());
        modelAndView.addObject("exception", e.getClass());
        modelAndView.addObject("message", e.getMessage());

        return modelAndView;
    }

    /**
     * Handles exceptions of type {@link NoHandlerFoundException} by redirecting the user to the root ("/") URL.
     * This method is used to handle cases where a requested URL does not match any defined {@code @RequestMapping} methods.
     *
     * @param e The {@link NoHandlerFoundException} exception that was thrown.
     * @param response The {@link HttpServletResponse} object used to set the HTTP status code.
     * @return A {@link String} representing the redirect URL ("redirect:/").
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleException(NoHandlerFoundException e, HttpServletResponse response) {
        return "redirect:/home";
    }
}
