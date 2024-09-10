package app.YUP.controller;

import app.YUP.dto.request.EventRequest;
import app.YUP.dto.request.ReservationRequest;
import app.YUP.dto.response.EventResponse;
import app.YUP.Enum.EventTag;
import app.YUP.dto.response.ParticipantResponse;
import app.YUP.model.Event;
import app.YUP.externalModel.EmailDetails;
import app.YUP.model.User;
import app.YUP.service.AuthenticationService;
import app.YUP.externalService.EmailService;
import app.YUP.service.EventService;
import app.YUP.service.ReservationService;
import app.YUP.wrapper.GeoJsonWrapper;
import app.YUP.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * This class is a controller for handling event related requests.
 * It provides endpoints for creating a new event, viewing event details, and viewing all events.
 */
@Tag(name = "YUP - EventController", description = "This page document the RestAPI of Event  Controller. All the end-point of the application are specified.")
@Controller
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private final UserService userService;
    private final ReservationService reservationService;

    /**
     * This method is used to display the event creation page.
     * It takes a Model object, a HttpServletRequest object, and a HttpServletResponse object as parameters.
     * It uses the AuthenticationService to get the username from the cookie in the request.
     * It then uses the UserService to get the firstname of the user and adds it to the model.
     * It also adds a new EventRequest object and the values of the EventTag enum to the model.
     * It sets the HTTP status code of the response to 200 (OK).
     * It returns a string that represents the name of the view to be rendered.
     *
     * @param model    This is the Model object that can be used to pass attributes to the view.
     * @param request  This is the HttpServletRequest object that contains the request details.
     * @param response This is the HttpServletResponse object that contains the response details.
     * @return String This returns the name of the view to be rendered.
     */
    @GetMapping("/newEvent")
    @Operation(summary = "Show Event Form", description = "Display the form for creating a new event.",
            tags = {"Event", "GET"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event form displayed successfully",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "403", description = "Unauthorized if the user is not authenticated",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "text/html"))})
    public String eventPage(Model model, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        String userId = authenticationService.getIdFromCookie(request);
        model.addAttribute("firstname", userService.getUserById(userId).getFirstname());
        model.addAttribute("eventDTO", new EventRequest());
        model.addAttribute("tags", EventTag.values());
        return "event";
    }

    /**
     * This method is used to create a new event.
     * It takes an EventRequest object, a HttpServletRequest object, a BindingResult object, a MultipartFile object, a Model object, and a HttpServletResponse object as parameters.
     * It uses the AuthenticationService to get the username from the cookie in the request.
     * It then uses the EventService to create a new event.
     * It also sends an email to the user with the event creation details.
     * It sets the HTTP status code of the response to 200 (OK).
     * It returns a string that represents the name of the view to be rendered.
     *
     * @param event          This is a request object that contains the details of the event to be created.
     * @param request        This is the HttpServletRequest object that contains the request details.
     * @param result         This is the BindingResult object that contains the results of the validation of the event request.
     * @param eventImageFile This is the MultipartFile object that contains the image of the event.
     * @param model          This is the Model object that can be used to pass attributes to the view.
     * @param response       This is the HttpServletResponse object that contains the response details.
     * @return String This returns the name of the view to be rendered.
     */
    @PostMapping("/api/newEvent")
    @Operation(summary = "Create a new event", description = "Creates a new event with the provided details.",
            tags = {"Event", "POST"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event created successfully",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "403", description = "Unauthorized if the user is not authenticated",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "502", description = "Bad Gateway",
                    content = @Content(mediaType = "text/html"))
    })
    public String createEvent(@Valid EventRequest event, HttpServletRequest request, BindingResult result, @RequestParam("eventImageFile") MultipartFile eventImageFile, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        String userId = authenticationService.getIdFromCookie(request);

        eventService.createEvent(event, userId, result, eventImageFile, model);
        User user = userService.getUserById(authenticationService.getIdFromCookie(request));

        EmailDetails details = new EmailDetails(user.getEmail(), "<!DOCTYPE html>\n" + "<html lang='en'>\n" + "<body style='background-color: #5185d5; padding: 15px; max-width: 500px; margin: 0 auto;'>\n" + "    <h2 style='text-align: center; padding: 0px; margin: 0px; padding-bottom: 10px; color: white; background-color: #5185d5;'><span style='font-weight: 800;'>Your Unique Party</span></h2>\n" + "    <div style='overflow: hidden; padding: 0; margin: 0; border-radius: 10px; background-color: white;'>\n" + "        <img src=\"https://st2.depositphotos.com/1017986/11248/i/450/depositphotos_112488044-stock-photo-happy-friends-dancing-in-club.jpg\" style='width: 100%; height: 160px; object-fit: cover;'>\n" + "        <div style='padding: 15px 25px;'>\n" + "            <p style='text-align: center; margin-top: 0;'><span style='font-weight: 800;'>Have you heard? A new YUP is online!</span></p>\n" + "            <p>Hello <span style='font-weight: 800;'>" + user.getFirstname() + "</span>, your event is online on <span style='font-weight: 800;'>YUP!</span> Thank you for choosing us YUPper!</p>\n" + "            <p style=\"margin: 0px; margin-bottom: 7px; margin-top: 7px;\">We are sure that <span style='font-weight: 800;'>" + event.getName() + "</span> will be a #" + event.getTag().toString().toLowerCase() + " <span style='font-weight: 800;'>YUPpantastic!</span> &#129321;</p>\n" + "            <p>Browse the site and discover the participants, and remember...</p>\n" + "            <p style='text-align: center;'><span style='font-weight: 800;'>... if it's a party, it's YUP!</span></p>\n" + "            <div style='margin-top: 16px;'>\n" + "                <p style=\"margin: 0px; margin-bottom: 2px;\"><span style='font-weight: 800;'>The YUP team.</span></p>\n" + "                <p style='margin: 0px; margin-bottom: 2px; text-decoration: none; font-style: italic'><a style='text-decoration: none;' href='mailto:theyupteam@gmail.com'>theyupteam@gmail.com</a>\n" + "                <p style='margin: 0px;  margin-bottom: 16px;'>&copy; 2024. YUP - Your Unique Party</p>\n" + "            </div>  \n" + "        </div>\n" + "    </div>\n" + "</body>\n" + "</html>", "You have created a new YUP!", "");
        emailService.sendMailWithoutAttachment(details);

        return "redirect:/event/myEvents";
    }


    /**
     * This method is used to retrieve events owned by the authenticated user.
     * It takes a Model object, a HttpServletRequest object, and a HttpServletResponse object as parameters.
     * It uses the AuthenticationService to get the username from the cookie in the request.
     * It then uses the UserService to get the firstname of the user and adds it to the model.
     * It also adds the events owned by the user to the model.
     * It sets the HTTP status code of the response to 200 (OK).
     * It returns a string that represents the name of the view to be rendered.
     *
     * @param model    This is the Model object that can be used to pass attributes to the view.
     * @param request  This is the HttpServletRequest object that contains the request details.
     * @param response This is the HttpServletResponse object that contains the response details.
     * @return String This returns the name of the view to be rendered.
     */
    @GetMapping("/myEvents")
    @Operation(summary = "Get events by owner", description = "Retrieves a list of events owned by the authenticated user.",
            tags = {"MyEvent", "GET"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved events created by the logged user",
                    content = @Content(mediaType = "html/text")),
            @ApiResponse(responseCode = "403", description = "Unauthorized if the user is not authenticated",
                    content = @Content(mediaType = "html/text")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "html/text"))})
    public String getEventsByOwner(Model model, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        String userId = authenticationService.getIdFromCookie(request);
        model.addAttribute("firstname", userService.getUserById(userId).getFirstname());
        model.addAttribute("myEvents", eventService.getEventsByOwner(userId));
        return "myEvents";
    }

    /**
     * This method is used to retrieve a specific event with an owner view.
     * It takes a UUID eventId, a Model object, a HttpServletRequest object, and a HttpServletResponse object as parameters.
     * It uses the EventService to get the event by its ID and maps it to a DTO.
     * It verifies if the user is the owner of the event and adds relevant attributes to the model.
     * It sets the HTTP status code of the response to 200 (OK).
     * It returns a string that represents the name of the view to be rendered.
     *
     * @param eventId  This is the UUID of the event to be retrieved.
     * @param model    This is the Model object that can be used to pass attributes to the view.
     * @param request  This is the HttpServletRequest object that contains the request details.
     * @param response This is the HttpServletResponse object that contains the response details.
     * @return String This returns the name of the view to be rendered.
     */
    @GetMapping("/myEvents/{eventId}")
    @Operation(summary = "Get a specific event with owner view", description = "Retrieve the specified event using the ID of the event",
            tags = { "Event", "GET" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved successfully",
                    content = @Content(mediaType = "html/text")),
            @ApiResponse(responseCode = "403", description = "Unauthorized if the user is not authenticated and if the user is not the owner of the event",
                    content = @Content(mediaType = "html/text")),
            @ApiResponse(responseCode = "500", description = "Internal server error ",
                    content = @Content(mediaType = "text/html"))
    })
    public String getEventByOwner(@PathVariable UUID eventId, Model model, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        String userId = authenticationService.getIdFromCookie(request);
        eventService.isEventOwner(userId, eventId);
        Event event = eventService.getEventById(eventId);
        EventResponse eventResponse = eventService.mapEntityToDTO(event);

        model.addAttribute("firstname", userService.getUserById(userId).getFirstname());
        model.addAttribute("event", eventResponse);
        model.addAttribute("tags", EventTag.values());
        return "singleEventOwnerView";
    }


    /**
     * This method is used to retrieve all events.
     * It takes a Model object, a HttpServletRequest object, and a HttpServletResponse object as parameters.
     * It uses the EventService to get all events and adds them to the model.
     * It also gets the username from the cookie in the request and adds the firstname of the user to the model.
     * It sets the HTTP status code of the response to 200 (OK).
     * It returns a string that represents the name of the view to be rendered.
     *
     * @param model    This is the Model object that can be used to pass attributes to the view.
     * @param request  This is the HttpServletRequest object that contains the request details.
     * @param response This is the HttpServletResponse object that contains the response details.
     * @return String This returns the name of the view to be rendered.
     */
    @GetMapping("/all")
    @Operation(summary = "Get All Events", description = "Retrieve a list of all events.",
            tags = {"Event", "GET"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of events retrieved successfully",
                    content = @Content(mediaType = "html/text")),
            @ApiResponse(responseCode = "500", description = "Internal server error ",
                    content = @Content(mediaType = "text/html"))})
    public String getAllEvents(Model model, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        String userId = authenticationService.checkIdFromCookie(request);
        List<EventResponse> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        model.addAttribute("firstname", userId != null ? userService.getUserById(userId).getFirstname() : null);
        return "around";
    }

    /**
     * Retrieves the image of a specific event by its ID.
     *
     * @param eventId The UUID of the event for which the image is to be retrieved.
     * @return A ResponseEntity containing the image data in byte array format.
     *         The content type is set to 'image/jpeg'.
     */
    @GetMapping("/{eventId}/image")
    @Operation(summary = "Retrieve the image for a specific event", description = "Returns the image associated with the given event ID. The image is returned as a JPEG file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of event image", content = @Content(mediaType = "image/jpeg")),
            @ApiResponse(responseCode = "404", description = "Event not found", content = @Content)
    })
    public ResponseEntity<byte[]> getEventImage(@PathVariable UUID eventId) {
        Event event = eventService.getEventById(eventId);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(event.getEventImage());
    }

    /**
     * This method is used to retrieve a specific event by its ID.
     * It takes a UUID representing the event ID, a Model object, a HttpServletRequest object, and a HttpServletResponse object as parameters.
     * It uses the EventService to get the event by its ID and maps it to an EventResponse object.
     * It then adds the EventResponse object to the model.
     * It also gets the username from the cookie in the request and adds the firstname and username of the user to the model.
     * It sets the HTTP status code of the response to 200 (OK).
     * It returns a string that represents the name of the view to be rendered.
     *
     * @param eventId  This is the UUID that represents the ID of the event.
     * @param model    This is the Model object that can be used to pass attributes to the view.
     * @param request  This is the HttpServletRequest object that contains the request details.
     * @param response This is the HttpServletResponse object that contains the response details.
     * @return String This returns the name of the view to be rendered.
     */
    @GetMapping("/{eventId}")
    @Operation(summary = "Get a specific Event", description = "Retrieve the specified event using the ID of the event",
            tags = {"Event", "GET"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved successfully",
            content = @Content(mediaType = "html/text")),
            @ApiResponse(responseCode = "404", description = "Event not found",
                    content = @Content(mediaType = "text/html")),
            @ApiResponse(responseCode = "500", description = "Internal server error ",
                    content = @Content(mediaType = "text/html"))
    })
    public String getEventById(@PathVariable UUID eventId, Model model, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());

        Event event = eventService.getEventById(eventId);
        EventResponse eventResponse = eventService.mapEntityToDTO(event);

        String userId = authenticationService.checkIdFromCookie(request);

        if (userId != null){
            User user = userService.getUserById(authenticationService.getIdFromCookie(request));

            model.addAttribute("event", eventResponse);
            model.addAttribute("firstname", user.getFirstname());
            model.addAttribute("reservationRequest", new ReservationRequest(event.getId(), user.getId()));
            model.addAttribute("imOwner", event.getOwner().getId().equals(user.getId()));
            model.addAttribute("actionBooking", "/reservation/newReservation");
            model.addAttribute("actionDeleteReservation", "/reservation/api/delete");
            model.addAttribute("method", "post");
            model.addAttribute("isReserved", reservationService.checkIfEventIsReservedByUser(user, eventId));
            model.addAttribute("userCanReserve", reservationService.checkUserCanReserveBeforeStartDateTime(event));
            model.addAttribute("code", reservationService.returnReservationCode(user, eventId));
        } else{
            model.addAttribute("event", eventResponse);
            model.addAttribute("firstname", null);
            model.addAttribute("actionBooking", "/auth/login");
            model.addAttribute("reservationRequest", new ReservationRequest(null, null));
            model.addAttribute("method", "get");
            model.addAttribute("imOwner", false);
            model.addAttribute("actionDeleteReservation", "");
            model.addAttribute("isReserved", false);
            model.addAttribute("userCanReserve", reservationService.checkUserCanReserveBeforeStartDateTime(event));
            model.addAttribute("code", null);

        }

        return "singleEventView";
    }

    /**
     * Deletes an event.
     *      * Retrieves the specified event using the event ID and deletes it if the user is authenticated and is the owner of the event.
     *
     * @param eventId  The UUID of the event to delete.
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @return A redirection to the user's events page.
     */
    @Operation(summary = "Delete an event", description = "Deletes an event using the event ID if the user is authenticated and is the owner of the event.",
            tags = { "Event", "POST" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Event deleted successfully and redirected to MyEvents page",
                    content = @Content(mediaType = "html/text")),
            @ApiResponse(responseCode = "403", description = "Unauthorized if the user is not authenticated and if the user is not the owner of the event",
                    content = @Content(mediaType = "html/text")),
            @ApiResponse(responseCode = "500", description = "Internal server error ",
                    content = @Content(mediaType = "text/html"))})
    @PostMapping("/api/delete")
    public String deleteEvent(@RequestParam UUID eventId, HttpServletRequest request, HttpServletResponse response){
        response.setStatus(HttpStatus.OK.value());
        String userId = authenticationService.getIdFromCookie(request);
        eventService.isEventOwner(userId, eventId);
        Event event = eventService.getEventById(eventId);

        Set<User> participants  = eventService.getEventParticipants(event.getId());

        eventService.deleteEvent(event);

        User user = userService.getUserById(userId);

        // send email to event owner
        EmailDetails detailsOwner = new EmailDetails(user.getEmail(), "<!DOCTYPE html>\n<html lang='en'>\n<body style='background-color: #5185d5; padding: 15px; max-width: 500px; margin: 0 auto;'>\n    <h2 style='text-align: center; padding: 0px; margin: 0px; padding-bottom: 10px; color: white; background-color: #5185d5;'><span style='font-weight: 800;'>Your Unique Party</span></h2>\n    <div style='overflow: hidden; padding: 0; margin: 0; border-radius: 10px; background-color: white;'>\n        <img src=\"https://images0.persgroep.net/rcs/2xLeALQ2V506XuO-4MyqJIpG9Yk/diocontent/177907778/_fitwidth/694/?appId=21791a8992982cd8da851550a453bd7f&quality=0.8\" style='width: 100%; height: 160px; object-fit: cover;'>\n        <div style='padding: 15px 25px;'>\n            <p style='text-align: center; margin-top: 0;'><span style='font-weight: 800;'>We are sorry! Your YUP is offline!</span></p>\n            <p>Hello <span style='font-weight: 800;'>" + user.getFirstname() + "</span>, your event is offline on <span style='font-weight: 800;'>YUP!</span></p>\n            <p style=\"margin: 0px; margin-bottom: 7px; margin-top: 7px;\">We are sure that <span style='font-weight: 800;'>" + event.getName() + "</span> would have been a #" + event.getTag().toString().toLowerCase() + " <span style='font-weight: 800;'>YUPpantastic!</span> &#129321;</p>\n            <p>If you want to create a new party, we wait you on <span style='font-weight: 800;'>YUP</span></p>\n            <p style='text-align: center;'><span style='font-weight: 800;'>... if it's a party, it's YUP!</span></p>\n            <div style='margin-top: 16px;'>\n                <p style=\"margin: 0px; margin-bottom: 2px;\"><span style='font-weight: 800;'>The YUP team.</span></p>\n                <p style='margin: 0px; margin-bottom: 2px; text-decoration: none; font-style: italic'><a style='text-decoration: none;' href='mailto:theyupteam@gmail.com'>theyupteam@gmail.com</a></p>\n                <p style='margin: 0px;  margin-bottom: 16px;'>&copy; 2024. YUP - Your Unique Party</p>\n            </div>\n        </div>\n    </div>\n</body>\n</html>", "You have deleted your YUP!", "" );
        emailService.sendMailWithoutAttachment(detailsOwner);

        // Send email to all the participants
        for (User userPartecipant : participants) {
            EmailDetails detailsParticipant = new EmailDetails(userPartecipant.getEmail(), "<!DOCTYPE html>\n<html lang='en'>\n<body style='background-color: #5185d5; padding: 15px; max-width: 500px; margin: 0 auto;'>\n    <h2 style='text-align: center; padding: 0px; margin: 0px; padding-bottom: 10px; color: white; background-color: #5185d5;'><span style='font-weight: 800;'>Your Unique Party</span></h2>\n    <div style='overflow: hidden; padding: 0; margin: 0; border-radius: 10px; background-color: white;'>\n        <img src=\"https://images0.persgroep.net/rcs/2xLeALQ2V506XuO-4MyqJIpG9Yk/diocontent/177907778/_fitwidth/694/?appId=21791a8992982cd8da851550a453bd7f&quality=0.8\" style='width: 100%; height: 160px; object-fit: cover;'>\n        <div style='padding: 15px 25px;'>\n            <p style='text-align: center; margin-top: 0;'><span style='font-weight: 800;'>We are sorry! Your YUP has been canceled!</span></p>\n            <p>Hello <span style='font-weight: 800;'>"+userPartecipant.getFirstname()+"</span>, the event you booked is offline on <span style='font-weight: 800;'>YUP!</span> Thank you for choosing us YUPper!</p>\n            <p style=\"margin: 0px; margin-bottom: 7px; margin-top: 7px;\">Next time, we are sure that <span style='font-weight: 800;'>"+event.getName()+"</span> will be a #"+event.getTag().toString().toLowerCase()+" <span style='font-weight: 800;'>YUPpantastic!</span> &#129321;</p>\n            <p style='text-align: center;'><span style='font-weight: 800;'>... if it's a party, it's YUP!</span></p>\n            <div style='margin-top: 16px;'>\n                <p style=\"margin: 0px; margin-bottom: 2px;\"><span style='font-weight: 800;'>The YUP team.</span></p>\n                <p style='margin: 0px; margin-bottom: 2px; text-decoration: none; font-style: italic'><a style='text-decoration: none;' href='mailto:theyupteam@gmail.com'>theyupteam@gmail.com</a>\n                <p style='margin: 0px;  margin-bottom: 16px;'>&copy; 2024. YUP - Your Unique Party</p>\n            </div>  \n        </div>\n    </div>\n</body>\n</html>", "Your YUP has been cancel!", "");
            emailService.sendMailWithoutAttachment(detailsParticipant);
        }

        return "redirect:/event/myEvents";
    }

    /**
     * Handles HTTP GET requests to retrieve the list of participants for a specified event.
     * <p>
     * The endpoint verifies if the user is authenticated and is the owner of the event before
     * retrieving the participants' details.
     * </p>
     *
     * @param eventId  The UUID of the event for which participants are to be retrieved.
     * @param model    The model object to add attributes used for rendering views.
     * @param request  The HttpServletRequest object to access request information.
     * @param response The HttpServletResponse object to set the response status.
     * @return         The name of the view to render the participants' details.
     */
    @GetMapping("/{eventId}/participants")
    @Operation(summary = "Get participants details of specified event",
            description = "Retrieves the participants list of an event using the event ID  if the user is authenticated and is the owner of the event.",
            tags = { "Event", "GET" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The details of event participants have been successfully retrieved",
                    content = @Content(mediaType = "html/text")),
            @ApiResponse(responseCode = "403", description = "Unauthorized if the user is not authenticated and if the user is not the owner of the event",
                    content = @Content(mediaType = "html/text")),
            @ApiResponse(responseCode = "500", description = "Internal server error ",
                    content = @Content(mediaType = "text/html"))
    })
    public String getEventParticipants(@PathVariable UUID eventId, Model model, HttpServletRequest request, HttpServletResponse response){
        response.setStatus(HttpStatus.OK.value());
        String userId = authenticationService.getIdFromCookie(request);
        eventService.isEventOwner(userId, eventId);
        Event event = eventService.getEventById(eventId);

        Set<ParticipantResponse> participants = reservationService.getAllReservationsForEvent(event.getId());
        Map<String, Integer> presenceCounts = reservationService.getPresenceCounts(participants);

        int presentParticipants =  presenceCounts.get("present");
        int absentParticipants =  presenceCounts.get("notPresent");

        model.addAttribute("participants", participants);
        model.addAttribute("eventId", eventId);
        model.addAttribute("eventName", event.getName());
        model.addAttribute("presentParticipants", presentParticipants);
        model.addAttribute("absentParticipants", absentParticipants);
        model.addAttribute("firstname", userId != null ? userService.getUserById(userId).getFirstname() : null);
        return "participants";
    }

    /**
     * This method is used to retrieve all events in GeoJSON format.
     * It takes a HttpServletResponse object as a parameter.
     * It uses the EventService to get all events in GeoJSON format.
     * It sets the HTTP status code of the response to 200 (OK).
     * It returns a GeoJsonWrapper object that contains the GeoJSON data of all events.
     *
     * @param response This is the HttpServletResponse object that contains the response details.
     * @return GeoJsonWrapper This returns the GeoJSON data of all events.
     */
    @GetMapping("/api/geo/all")
    @ResponseBody
    @Operation(summary = "Get All Events in format GEO Json", description = "Retrieve  all events in GeoJSON format",
            tags = {"Event", "GET"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of events retrieved successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error ",
                    content = @Content(mediaType = "text/plain"))})
    public GeoJsonWrapper getAllGEOEvents(HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        return eventService.getAllGEOEvents();
    }

}