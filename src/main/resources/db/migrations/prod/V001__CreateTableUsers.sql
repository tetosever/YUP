CREATE TABLE users (
   id uuid NOT NULL,
   email varchar(255) DEFAULT NULL,
   firstname varchar(255) DEFAULT NULL,
   lastname varchar(255) DEFAULT NULL,
   username varchar(255) DEFAULT NULL,
   role varchar(255) DEFAULT NULL,
   PRIMARY KEY (id),
   CONSTRAINT UK_username UNIQUE (username)
);