-- yup.reservations definition

CREATE TABLE `reservations` (
    `id` binary(16) NOT NULL,
    `event_id` binary(16) DEFAULT NULL,
    `user_id` binary(16) DEFAULT NULL,
    `prenotation_code` varchar(255) DEFAULT NULL,
    `presence` bit(1) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_prenotation_code` (`prenotation_code`),
    UNIQUE KEY `UK_event_user` (`event_id`, `user_id`), -- Chiave unica combinata
    KEY `FK_reservations_to_users` (`user_id`),
    CONSTRAINT `FK_reservations_to_users`
        FOREIGN KEY (`user_id`)
            REFERENCES `users` (`id`)
            ON DELETE CASCADE,
    CONSTRAINT `FK_reservations_to_events`
        FOREIGN KEY (`event_id`)
            REFERENCES `events` (`id`)
            ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
