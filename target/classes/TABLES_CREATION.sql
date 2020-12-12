CREATE TABLE authors (
    id bigint NOT NULL,
    name varchar(255) NOT NULL,
    surname varchar(255) NOT NULL,
    second_name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE genres (
    id bigint NOT NULL,
    name varchar(255) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE books (
    id bigint NOT NULL,
    name varchar(255) NOT NULL,
    author bigint NOT NULL,
    genre bigint NOT NULL,
    publisher varchar(255) NOT NULL,
    year int CHECK (year > 0 AND year < 2021),
    city varchar(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (author) REFERENCES authors (id),
    FOREIGN KEY (genre) REFERENCES genres (id)
);

commit;
