-- yup.ixt_users definition

CREATE TABLE `int_users` (
     `id` binary(16) NOT NULL,
     `password` varchar(255) NOT NULL,
     PRIMARY KEY (`id`),
     CONSTRAINT `FK_int_users_to_users`
         FOREIGN KEY (`id`)
         REFERENCES `users` (`id`)
         ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;