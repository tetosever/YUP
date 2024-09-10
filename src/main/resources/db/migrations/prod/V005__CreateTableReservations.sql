CREATE TABLE reservations (
      id uuid NOT NULL,
      event_id uuid DEFAULT NULL,
      user_id uuid DEFAULT NULL,
      prenotation_code varchar(255) DEFAULT NULL,
      presence boolean NOT NULL,
      PRIMARY KEY (id),
      CONSTRAINT UK_prenotation_code UNIQUE (prenotation_code),
      CONSTRAINT UK_event_user UNIQUE (event_id, user_id),
      FOREIGN KEY (user_id)
          REFERENCES users (id)
          ON DELETE CASCADE,
      FOREIGN KEY (event_id)
          REFERENCES events (id)
          ON DELETE CASCADE
);
