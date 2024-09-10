CREATE TABLE int_users (
   id uuid NOT NULL,
   password varchar(255) NOT NULL,
   PRIMARY KEY (id),
   CONSTRAINT FK_int_users_to_users
       FOREIGN KEY (id)
           REFERENCES users (id)
           ON DELETE CASCADE
);
