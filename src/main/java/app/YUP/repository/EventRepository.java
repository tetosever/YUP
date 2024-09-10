package app.YUP.repository;

import app.YUP.model.Event;
import app.YUP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;
import java.util.List;

/**
 * Repository interface for the Event entity.
 * This interface extends JpaRepository and provides methods to perform CRUD operations on the Event entity.
 * It is annotated with @Repository to indicate that it is a Spring Data repository.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, UUID>{

    /**
     * Finds an event by its ID.
     *
     * @param id the ID of the event
     * @return an Optional containing the event if found, or an empty Optional if not found
     */
    Optional<Event> findById(UUID id);

    /**
     * Finds events by their owner.
     *
     * @param owner the owner of the events
     * @return a list of events owned by the given user
     */
    List<Event> findByOwner(User owner);

    /**
     * Finds an event by its name.
     *
     * @param name the name of the event
     * @return an Optional containing the event if found, or an empty Optional if not found
     */
    Optional<Event> findByName(String name);
    /**
     * Updates the number of participants for an event by its ID.
     *
     * @param id the ID of the event
     * @param participants the new number of participants
     */
    @Modifying
    @Query("update Event e set e.participants = :participants where e.id = :id")
    void updateParticipantsById(UUID id, int participants);
}