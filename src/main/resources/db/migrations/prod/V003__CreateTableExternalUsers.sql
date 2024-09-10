CREATE TABLE ext_users (
   id uuid NOT NULL,
   external_id varchar(255) NOT NULL,
   provider varchar(255) NOT NULL,
   PRIMARY KEY (id),
   CONSTRAINT UK_external_id UNIQUE (external_id),
   CONSTRAINT FK_ext_users_to_users
       FOREIGN KEY (id)
           REFERENCES users (id)
           ON DELETE CASCADE
);
