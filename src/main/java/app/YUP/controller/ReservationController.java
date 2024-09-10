package app.YUP.controller;

import app.YUP.dto.request.ReservationRequest;
import app.YUP.dto.response.EventResponse;
import app.YUP.externalModel.EmailDetails;
import app.YUP.externalService.EmailService;
import app.YUP.utils.DateTimeConverter;
import app.YUP.utils.ImageConverter;
import app.YUP.utils.QRCodeService;
import app.YUP.model.Event;
import app.YUP.model.Reservation;
import app.YUP.model.User;
import app.YUP.service.AuthenticationService;
import app.YUP.service.EventService;
import app.YUP.service.ReservationService;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is a controller for handling reservation related requests.
 * It provides endpoints for creating a new reservation.
 */
@Tag(name = "YUP - Reservation Controller", description = "This page document the RestAPI of Reservation Controller. All the end-point of the application are specified.")
@Controller
@RequestMapping("/reservation/")
@RequiredArgsConstructor
public class ReservationController {
    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private final ReservationService reservationService;
    private final UserService userService;
    private final EventService eventService;

    /**
     * This method is used to create a new reservation.
     * It takes a ReservationRequest object and a HttpServletRequest object as parameters.
     * It uses the AuthenticationService to get the username from the cookie in the request.
     * It then uses the ReservationService to create a new reservation.
     * It also generates a QR code for the reservation and sends an email to the user with the details of the reservation.
     *
     * @param reservationRequest This is a request object that contains the details of the reservation to be created.
     * @param request            This is the HttpServletRequest object that contains the request details.
     * @return String This returns a redirect URL to the reservations page.
     * @throws IOException This exception is thrown if there is an error while generating the QR code or sending the email.
     */
    @PostMapping("/newReservation")
    @Operation(summary = "Add a new reservation", description = "Add a new reservation in the system for the couple User-Event",
            tags = {"Reservation", "POST"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully creates a new reservation",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "302", description = "Redirects to the login view if the user is not logged",
                            content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "400", description = "Returns Bad Request if the reservation request body does not contain valid data",
                            content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "403", description = "Returns Forbidden if the user does not have permission to create a reservation",
                            content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "404", description = "Returns Resource Not Found if the requested event for reservation does not exist",
                            content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "502", description = "Returns Bad Gateway if there is an error while processing the reservation",
                            content = @Content(mediaType = "text/html"))
    })
    public String newReservation(@ModelAttribute ReservationRequest reservationRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        User user = userService.getUserById(authenticationService.getIdFromCookie(request));
        Event event = eventService.getEventById(reservationRequest.getEventId());

        reservationService.checkIfUserIsOwnerOfReservation(reservationRequest.getUserId(), user, event);

        Reservation reservation = reservationService.createReservation(reservationRequest, user);

        BufferedImage tempImage = QRCodeService.generateQrcode(reservation.getPrenotationCode());

        EmailDetails details = new EmailDetails(user.getEmail(), "<!DOCTYPE html>\n" + "<html lang='en'>\n" + "<body style='background-color: #5185d5; padding: 15px; max-width: 500px; margin: 0 auto;'>\n" + "    <h2 style='text-align: center; padding: 0px; margin: 0px; padding-bottom: 10px; color: white; background-color: #5185d5;'><span style='font-weight: 800;'>Your Unique Party</span></h2>\n" + "    <div style='overflow: hidden; padding: 0; margin: 0; border-radius: 10px; background-color: white;'>\n" + "        <img src=\"https://www.coravin.co.uk/cdn/shop/articles/wine-tasting-party.jpg?v=1714429693\" style='width: 100%; height: 160px; object-fit: cover;'>\n" + "        <div style='padding: 15px 25px;'>\n" + "            <p style='text-align: center; margin-top: 0;'><span style='font-weight: 800;'>You have a new event on the agenda YUPPER!</span></p>\n" + "            <p>Hello <span style='font-weight: 800;'>" + user.getFirstname() + "</span>, your presence at the <span style='font-weight: 800;'>" + event.getLocation() + "</span> on <span style='font-weight: 800;'>" + DateTimeConverter.convertToMonthDay(event.getStartDateTime()) + " at " + DateTimeConverter.convertTo24Hour(event.getStartDateTime()) + "</span> it is confirmed. Thank you for choosing us YUPper!</p>\n" + "            <p style=\"margin: 0px; margin-bottom: 7px; margin-top: 7px;\">We are sure that <span style='font-weight: 800;'>" + event.getName() + "</span> will be a #" + event.getTag().toString().toLowerCase() + " <span style='font-weight: 800;'>unforgettable!</span> &#129321;</p>\n" + "            <p>This is your reservation...</p>\n" + "            <img src='data:image/jpeg;base64," + ImageConverter.convertToBase64(tempImage) + "' style='width: 110px; display: block; height: 110px; margin: 0 auto; text-align: center'>" + "            <p style='text-align: center; margin-top: 10px; font-size: 10px;'>" + reservation.getPrenotationCode() + "</p>\n" + "            <p>Don't share it with anyone, and remember...</p>\n" + "            <p style='text-align: center;'><span style='font-weight: 800;'>... if it's a party, it's YUP!</span></p>\n" + "            <div style='margin-top: 16px;'>\n" + "                <p style=\"margin: 0px; margin-bottom: 2px;\"><span style='font-weight: 800;'>The YUP team.</span></p>\n" + "                <p style='margin: 0px; margin-bottom: 2px; text-decoration: none; font-style: italic'><a style='text-decoration: none;' href='mailto:theyupteam@gmail.com'>theyupteam@gmail.com</a>\n" + "                <p style='margin: 0px;  margin-bottom: 16px;'>&copy; 2024. YUP - Your Unique Party</p>\n" + "            </div>  \n" + "        </div>\n" + "    </div>\n" + "</body>\n" + "</html>", "You will participate in an exclusive YUP!", "");
        emailService.sendMailWithAttachment(details);

        return "redirect:/event/"+event.getId();
    }

    /**
     * This method is used to delete a reservation.
     * It takes an eventId as a parameter, a HttpServletRequest object, and a HttpServletResponse object.
     * It uses the AuthenticationService to get the user ID from the cookie in the request.
     * It then uses the ReservationService to delete the reservation for the given eventId and user.
     * It sets the response status to OK.
     * Finally, it returns a redirect URL to the myReservations page.
     *
     * @param eventId  The unique identifier of the event for which the reservation needs to be deleted.
     * @param request  The HttpServletRequest object that contains the request details.
     * @param response The HttpServletResponse object that can be used to set the response details.
     * @return String  This returns a redirect URL to the myReservations page.
     */
    @PostMapping("api/delete")
    @Operation(summary = "Delete a reservation", description = "Deletes a reservation for a given event ID and authenticated user.")
    @ApiResponse(responseCode = "200", description = "Reservation successfully deleted")
    @ApiResponse(responseCode = "302", description = "Redirects to the reservation list upon successful deletion")
    @ApiResponse(responseCode = "400", description = "Bad request if the Event ID is not provided")
    @ApiResponse(responseCode = "401", description = "Unauthorized if the user is not authenticated")
    @ApiResponse(responseCode = "404", description = "Not found if no reservation matches the given ID")
    public String deleteReservation(@RequestParam UUID eventId, @RequestParam UUID userId, HttpServletRequest request, HttpServletResponse response) {
        User user = userService.getUserById(authenticationService.getIdFromCookie(request));
        Event event = eventService.getEventById(eventId);

        reservationService.checkIfUserIsOwnerOfReservation(userId, user, event);
        reservationService.deleteReservation(event, user);

        response.setStatus(HttpStatus.OK.value());
        return "redirect:/reservation/myReservations";
    }

    /**
     * This method is used to retrieve all the reservations made by the user.
     * It takes a Model object, a HttpServletRequest object, and a HttpServletResponse object as parameters.
     * It uses the AuthenticationService to get the user ID from the cookie in the request.
     * It then uses the ReservationService to get all events reserved by the user.
     * It sets the list of events and the user's first name as attributes to the request and model respectively.
     * It also sets the response status to OK.
     * Finally, it returns the name of the view to be rendered, in this case, "myAgenda".
     *
     * @param model    This is the Model object that can be used to add attributes to the model.
     * @param request  This is the HttpServletRequest object that contains the request details.
     * @param response This is the HttpServletResponse object that can be used to set the response details.
     * @return String This returns the name of the view to be rendered.
     */
    @GetMapping("/myReservations")
    @Operation(summary = "Get all the reservations", description = "Get all the events reserved from the system inside the 'My Reservations' page",
            tags = {"Reservation", "GET"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserved events retrieved successfully",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "400", description = "Returns Bad Request if the body does not contain valid data",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "403", description = "Returns Forbidden if the user does not have permission to view reservations",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "502", description = "Returns Bad Gateway if there is an error while processing the request",
                    content = @Content(mediaType = "text/html"))
    })
    public String myReservation(Model model, HttpServletRequest request, HttpServletResponse response) {
        UUID userId = UUID.fromString(authenticationService.checkIdFromCookie(request));

        List<EventResponse> eventList = reservationService.getAllEventsReservedByUser(userId);
        model.addAttribute("events", eventList);
        model.addAttribute("firstname", userService.getUserById(authenticationService.checkIdFromCookie(request)).getFirstname());

        response.setStatus(HttpStatus.OK.value());
        return "myAgenda";
    }

    /**
     * This method is used to check the reservation of a participant.
     * It takes a payload containing the event ID and the QR code message,
     * a HttpServletRequest object, and a HttpServletResponse object as parameters.
     * It uses the AuthenticationService to get the user ID from the cookie in the request.
     * It then uses the ReservationService to update the presence of the participant based on the QR code message.
     * The method returns a ResponseEntity containing the number of affected rows.
     *
     * @param payload This is a payload containing the event ID and the QR code message.
     * @param request  This is the HttpServletRequest object that contains the request details.
     * @param response This is the HttpServletResponse object that can be used to set the response details.
     * @return ResponseEntity This returns a ResponseEntity containing the number of affected rows.
     */
    @PostMapping("/checkQrCode")
    @Operation(summary = "Check the reservation", description = "This method allows to check the reservation of a participant", tags = {"Reservation", "POST"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation checked successfully", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "400", description = "Returns Bad Request if the body does not contain valid data", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "403", description = "Returns Forbidden if the user does not have permission to check reservations", content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "502", description = "Returns Bad Gateway if there is an error while processing the request", content = @Content(mediaType = "text/html"))})
    public ResponseEntity<Integer> checkQrCode(@RequestBody Map<String, String> payload, HttpServletRequest request, HttpServletResponse response) {
        UUID userId = UUID.fromString(authenticationService.checkIdFromCookie(request));
        UUID eventId = UUID.fromString(payload.get("eventID"));
        response.setStatus(HttpStatus.OK.value());
        int result = reservationService.updatePresenceByPrenotationCode(payload.get("message"), userId, eventId);
        return ResponseEntity.ok(result);
    }

}