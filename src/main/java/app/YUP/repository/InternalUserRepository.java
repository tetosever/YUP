package app.YUP.repository;

import app.YUP.model.InternalUser;
import app.YUP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for the InternalUsers entity.
 */
@Repository
public interface InternalUserRepository extends JpaRepository<InternalUser, UUID> {
    Optional<InternalUser> findByUsername(String username);
}
