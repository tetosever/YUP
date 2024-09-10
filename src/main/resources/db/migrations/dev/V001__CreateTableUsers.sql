-- yup.users definition

CREATE TABLE `users` (
     `id` binary(16) NOT NULL,
     `email` varchar(255) DEFAULT NULL,
     `firstname` varchar(255) DEFAULT NULL,
     `lastname` varchar(255) DEFAULT NULL,
     `username` varchar(255) DEFAULT NULL,
     `role` enum('LOGGED_USER') DEFAULT NULL,
     PRIMARY KEY (`id`),
     UNIQUE KEY `UK_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;