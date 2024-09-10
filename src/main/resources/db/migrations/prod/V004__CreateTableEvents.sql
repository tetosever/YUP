CREATE TABLE events (
    id uuid NOT NULL,
    owner uuid DEFAULT NULL,
    name VARCHAR(20) DEFAULT NULL,
    description VARCHAR(200) DEFAULT NULL,
    location VARCHAR(200) DEFAULT NULL,
    event_image BYTEA,
    tag varchar(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    participants INT NOT NULL CHECK (participants >= 0),
    participants_max_number INT NOT NULL CHECK (participants_max_number >= 2 AND participants_max_number <= 5000),
    start_date_time TIMESTAMPTZ NOT NULL,
    end_date_time TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_events_to_users
        FOREIGN KEY (owner)
            REFERENCES users (id)
            ON DELETE CASCADE
);
