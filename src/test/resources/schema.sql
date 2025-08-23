DROP TABLE IF EXISTS exams_groups;
DROP TABLE IF EXISTS exams;
DROP TABLE IF EXISTS exam_type;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS general_group;
DROP TABLE IF EXISTS otp_codes;
DROP TABLE IF EXISTS users;

CREATE TABLE exam_type
(
    exam_type_id INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL
);

CREATE TABLE exams
(
    exam_id      INT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  VARCHAR(255),
    exam_date    TIMESTAMP    NOT NULL,
    exam_type_id INT          NOT NULL,
    CONSTRAINT fk_exam_type FOREIGN KEY (exam_type_id) REFERENCES exam_type (exam_type_id) ON DELETE CASCADE
);

CREATE TABLE general_group
(
    general_group_id INT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(255) NOT NULL
);

CREATE TABLE groups
(
    group_id INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(255) NOT NULL
);

CREATE TABLE exams_groups
(
    exam_group_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id       INT NOT NULL,
    group_id      INT NOT NULL,
    CONSTRAINT fk_exam FOREIGN KEY (exam_id) REFERENCES exams (exam_id) ON DELETE CASCADE,
    CONSTRAINT fk_group FOREIGN KEY (group_id) REFERENCES groups (group_id) ON DELETE CASCADE
);

CREATE TABLE otp_codes
(
    otp_code_id      INT AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(255) NOT NULL,
    expire           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    general_group_id INT          NOT NULL,
    CONSTRAINT fk_otp_group FOREIGN KEY (general_group_id) REFERENCES general_group (general_group_id) ON DELETE CASCADE
);

CREATE TABLE users
(
    user_id          INT AUTO_INCREMENT PRIMARY KEY,
    general_group_id INT          NOT NULL,
    email            VARCHAR(255) NOT NULL,
    is_active        BOOLEAN      NOT NULL DEFAULT TRUE,
    role             VARCHAR(50)  NOT NULL DEFAULT 'REPRESENTATIVE',
    CONSTRAINT fk_user_group FOREIGN KEY (general_group_id) REFERENCES general_group (general_group_id) ON DELETE CASCADE
);