DROP TABLE IF EXISTS exams;
DROP TABLE IF EXISTS exam_type;
DROP TABLE IF EXISTS general_group;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS otp_codes;
DROP TABLE IF EXISTS users;

CREATE TABLE exam_type
(
    exam_type_id INT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(255)
);

CREATE TABLE general_group
(
    general_group_id INT PRIMARY KEY AUTO_INCREMENT,
    name             VARCHAR(255)
);

CREATE TABLE exams
(
    exam_id      INT PRIMARY KEY AUTO_INCREMENT,
    title        VARCHAR(255),
    description  VARCHAR(255),
    date         TIMESTAMP(6),
    "groups"       VARCHAR(255),
    exam_type_id INT NOT NULL,
    FOREIGN KEY (exam_type_id) REFERENCES exam_type (exam_type_id)
);

CREATE TABLE groups
(
    group_id         INT PRIMARY KEY AUTO_INCREMENT,
    letter           CHAR(1) NOT NULL,
    group_count      INT     NOT NULL,
    general_group_id INT     NOT NULL,
    name             VARCHAR(255),
    FOREIGN KEY (general_group_id) REFERENCES general_group (general_group_id)
);

CREATE TABLE users
(
    user_id          INT PRIMARY KEY AUTO_INCREMENT,
    general_group_id INT          NOT NULL,
    email            VARCHAR(254) NOT NULL,
    is_active        BOOLEAN      NOT NULL,
    role             VARCHAR(20)  NOT NULL, -- enum zamieniony na VARCHAR
    FOREIGN KEY (general_group_id) REFERENCES general_group (general_group_id)
);

CREATE TABLE otp_codes
(
    otp_code_id INT PRIMARY KEY AUTO_INCREMENT,
    code        VARCHAR(255),
    expire      TIMESTAMP NOT NULL,
    used        BOOLEAN   NOT NULL,
    user_id     INT       NOT NULL,
    timestamp   TIMESTAMP(6),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);