package app.YUP.repository;

import app.YUP.model.ExternalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for the ExternalUsers entity.
 */
@Repository
public interface ExternalUserRepository extends JpaRepository<ExternalUser, UUID> {
    Optional<ExternalUser> findByExternalId(String ext_id);
}
