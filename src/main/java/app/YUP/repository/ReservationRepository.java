package app.YUP.repository;

import app.YUP.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for the Reservation entity.
 * This interface extends JpaRepository and provides methods to perform CRUD operations on the Reservation entity.
 * It is annotated with @Repository to indicate that it is a Spring Data repository.
 *
 * <p>
 * JpaRepository: This interface is a JPA specific extension of Repository. It contains the full API of CrudRepository
 * and PagingAndSortingRepository. So it contains API for basic CRUD operations and also API for pagination and sorting.
 * <p>
 * Reservation: This is the entity class for which this repository interface is created. It represents the reservation
 * table in the database.
 * <p>
 * UUID: This is the type of the primary key of the Reservation entity.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    /**
     * This method is used to fetch all reservations made by a specific user.
     * Spring Data JPA will automatically implement this method using a query derived from the method name.
     *
     * @param userId The ID of the user for whom to fetch reservations.
     * @return A list of Reservation objects that were made by the user with the given ID.
     */
    List<Reservation> findByUserId(UUID userId);

    /**
     * This method is used to fetch a reservation using its prenotation code.
     * Spring Data JPA will automatically implement this method using a query derived from the method name.
     *
     * @param prenotationCode The prenotation code of the reservation to fetch.
     * @return The Reservation object that has the given prenotation code. If no such reservation exists, this method returns null.
     */
    Reservation findByPrenotationCode(String prenotationCode);

    /**
     * This method is used to fetch a reservation using its prenotation code and the user ID.
     * Spring Data JPA will automatically implement this method using a query derived from the method name.
     *
     * @param userId The ID of the user for whom to fetch reservations.
     * @param eventId The ID of the event for which to fetch reservations.
     * @return The Reservation object that belongs to the user with the given ID and has the given event ID. If no such reservation exists, this method returns an empty Optional.
     */
    Optional<Reservation> findByUserIdAndEventId(UUID userId, UUID eventId);

    /**
     * This method is used to fetch all reservations made for a specific event.
     * Spring Data JPA will automatically implement this method using a query derived from the method name.
     *
     * @param eventId The ID of the event for which to fetch reservations.
     * @return A list of Reservation objects that were made for the event with the given ID.
     */
    List<Reservation> findByEventId(UUID eventId);
}