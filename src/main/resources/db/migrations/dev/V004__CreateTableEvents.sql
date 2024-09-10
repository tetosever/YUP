-- yup.events definition

CREATE TABLE `events` (
    `id` BINARY(16) NOT NULL,
    `owner` BINARY(16) DEFAULT NULL,
    `name` VARCHAR(20) DEFAULT NULL,
    `description` VARCHAR(200) DEFAULT NULL,
    `location` VARCHAR(200) DEFAULT NULL,
    `event_image` LONGBLOB,
    `tag` ENUM('PARTY', 'LIVEMUSIC', 'APERITIVO') NOT NULL,
    `latitude` DOUBLE NOT NULL,
    `longitude` DOUBLE NOT NULL,
    `participants` INT NOT NULL CHECK (`participants` >= 0),
    `participants_max_number` INT NOT NULL CHECK (`participants_max_number` >= 2 AND `participants_max_number` <= 5000),
    `start_date_time` DATETIME(6) NOT NULL,
    `end_date_time` DATETIME(6) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_events_to_users` (`owner`),
    CONSTRAINT `FK_events_to_users`
        FOREIGN KEY (`owner`)
        REFERENCES `users` (`id`)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
