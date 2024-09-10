-- yup.ext_users definition

CREATE TABLE `ext_users` (
     `id` binary(16) NOT NULL,
     `external_id` varchar(255) NOT NULL,
     `provider` enum('GOOGLE') NOT NULL,
     PRIMARY KEY (`id`),
     UNIQUE KEY `UK_external_id` (`external_id`),
     CONSTRAINT `FK_ext_users_to_users`
         FOREIGN KEY (`id`)
             REFERENCES `users` (`id`)
             ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
