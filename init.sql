-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: db
-- Generation Time: Paź 05, 2025 at 08:29 PM
-- Wersja serwera: 9.3.0
-- Wersja PHP: 8.2.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Baza danych: `pktt`
--
CREATE DATABASE IF NOT EXISTS `pktt` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `pktt`;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `admin_keys`
--

DROP TABLE IF EXISTS `admin_keys`;
CREATE TABLE `admin_keys` (
  `key_id` int NOT NULL,
  `value` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `admin_keys`
--

INSERT INTO `admin_keys` (`key_id`, `value`, `description`) VALUES
(3, '0923cd6f-cd33-4883-87e4-ae3b50b80a3f', 'mikolaj'),
(4, '2868b02b-a5dd-4386-a723-e450e5f54418', 'desc');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `api_keys`
--

DROP TABLE IF EXISTS `api_keys`;
CREATE TABLE `api_keys` (
  `key_id` int NOT NULL,
  `value` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `api_keys`
--

INSERT INTO `api_keys` (`key_id`, `value`, `description`) VALUES
(1, 'ca3bdabb-b559-41ca-9e96-2c27d6199017', 'test');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exams`
--

DROP TABLE IF EXISTS `exams`;
CREATE TABLE `exams` (
  `exam_id` int NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `exam_date` datetime NOT NULL,
  `exam_type_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `exams`
--

INSERT INTO `exams` (`exam_id`, `title`, `description`, `exam_date`, `exam_type_id`) VALUES
(2, 'Egzamin końcowy z programowania', 'Egzamin pisemny i praktyczny', '2025-01-20 09:00:00', 2),
(3, 'Projekt z baz danych', 'Oddanie projektu grupowego', '2025-06-15 23:59:00', 3),
(4, 'Kolokwium z fizyki', 'Druga część materiału: mechanika', '2025-11-05 12:00:00', 1),
(5, 'Egzamin końcowy z ekonomii', 'Egzamin pisemny testowy', '2025-02-10 08:30:00', 2),
(6, 'Projekt z systemów operacyjnych', 'Prezentacja projektu semestralnego', '2025-06-25 14:00:00', 3),
(7, 'test authorities', 'do usuniecia', '2027-09-01 09:00:00', 3),
(8, 'test authorities', 'do usuniecia', '2027-09-01 09:00:00', 3),
(9, 'test authorities', 'do usunieciaaaaa', '2027-09-01 09:00:00', 3),
(10, 'test authorities', 'do usunieciaaaa', '2027-09-01 09:00:00', 3),
(11, 'test authorities', 'do usunieciaaaa', '2027-09-01 09:00:00', 3);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exams_groups`
--

DROP TABLE IF EXISTS `exams_groups`;
CREATE TABLE `exams_groups` (
  `exam_group_id` int NOT NULL,
  `exam_id` int NOT NULL,
  `group_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `exams_groups`
--

INSERT INTO `exams_groups` (`exam_group_id`, `exam_id`, `group_id`) VALUES
(9, 2, 12),
(10, 2, 13),
(11, 2, 14),
(12, 3, 15),
(13, 3, 16),
(14, 3, 17),
(15, 4, 9),
(16, 4, 10),
(17, 5, 12),
(18, 5, 13),
(19, 6, 15),
(20, 6, 16),
(21, 7, 21),
(22, 7, 22),
(23, 8, 9),
(24, 9, 9),
(25, 10, 23),
(26, 11, 9);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exam_type`
--

DROP TABLE IF EXISTS `exam_type`;
CREATE TABLE `exam_type` (
  `exam_type_id` int NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `exam_type`
--

INSERT INTO `exam_type` (`exam_type_id`, `name`) VALUES
(1, 'Kolokwium'),
(2, 'Egzamin końcowy'),
(3, 'Projekt');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `general_group`
--

DROP TABLE IF EXISTS `general_group`;
CREATE TABLE `general_group` (
  `general_group_id` int NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `general_group`
--

INSERT INTO `general_group` (`general_group_id`, `name`) VALUES
(17, '11A'),
(18, '12E'),
(19, '13K'),
(20, '14M'),
(21, '12K'),
(22, '11K'),
(23, '14A'),
(24, '11M');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `moderators`
--

DROP TABLE IF EXISTS `moderators`;
CREATE TABLE `moderators` (
  `moderator_id` binary(16) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Zrzut danych tabeli `moderators`
--

INSERT INTO `moderators` (`moderator_id`, `password`, `role`) VALUES
(0x10b4cd4f840445ba9fff930fda8229c7, '$2a$10$zjcQISWSqPpMWQv99XWneOaHWiqTRhiXUJq5FT8iXbET.3hZfO0GO', 'MODERATOR'),
(0x561e6e496eab4e9e965ac6c8ffe24293, '$2a$10$puIitW1sPdjyqCs2nbpco.wRAcGOpuWiOj6iQ0siFKOBaKmIS9ghK', 'MODERATOR'),
(0x9e39a89631924bd6a38b0ee6e56be221, '$2a$10$e6H5n6xu4NymerBHqvO42OhUVg3aOHPpCPo0.TSDH1b/graC5FomC', 'MODERATOR'),
(0xd45dd77e68ac45908a12340b35d04b6c, '$2a$10$k6aoa0OU8RKbCA4WHu4yDuMOZFmxP2zeX7Cjw3GmLVml2dDm6QGEG', 'MODERATOR');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `otp_codes`
--

DROP TABLE IF EXISTS `otp_codes`;
CREATE TABLE `otp_codes` (
  `otp_code_id` int NOT NULL,
  `code` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `expire` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `general_group_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `otp_codes`
--

INSERT INTO `otp_codes` (`otp_code_id`, `code`, `expire`, `general_group_id`) VALUES
(2, 'XYZ789', '2025-08-18 20:51:40', 18),
(3, 'QWE456', '2025-08-18 21:51:40', 19),
(4, 'JKL999', '2025-08-18 22:51:40', 20),
(7, '9Y3TNZ', '2025-09-26 22:22:35', 17);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `refresh_token`
--

DROP TABLE IF EXISTS `refresh_token`;
CREATE TABLE `refresh_token` (
  `token_id` bigint NOT NULL,
  `token` char(64) NOT NULL,
  `user_id` int NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `student_groups`
--

DROP TABLE IF EXISTS `student_groups`;
CREATE TABLE `student_groups` (
  `group_id` int NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `student_groups`
--

INSERT INTO `student_groups` (`group_id`, `name`) VALUES
(22, '11A'),
(9, '11A1'),
(10, '11A2'),
(30, '11M'),
(12, '12E1'),
(13, '12E2'),
(14, '12E3'),
(15, '13K1'),
(16, '13K2'),
(17, '13K3'),
(23, '14A1'),
(18, '14M1'),
(29, 'L01'),
(32, 'L02'),
(31, 'L03'),
(24, 'L04'),
(25, 'L07'),
(21, 'P01'),
(27, 'P02'),
(26, 'P03'),
(28, 'P04');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` int NOT NULL,
  `general_group_id` int NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `role` enum('ADMIN','REPRESENTATIVE') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'REPRESENTATIVE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Zrzut danych tabeli `users`
--

INSERT INTO `users` (`user_id`, `general_group_id`, `email`, `is_active`, `role`) VALUES
(2, 18, 'user12e@example.com', 1, 'REPRESENTATIVE'),
(3, 19, 'user13k@example.com', 1, 'REPRESENTATIVE'),
(4, 20, 'user14m@example.com', 1, 'ADMIN'),
(9, 21, 'email@ex.com', 0, 'REPRESENTATIVE'),
(21, 23, 'kokim33314@etenx.com', 1, 'REPRESENTATIVE'),
(23, 17, 'cijofo8356@gddcorp.com', 1, 'REPRESENTATIVE'),
(24, 24, 'wogece3463@etenx.com', 1, 'REPRESENTATIVE');

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `admin_keys`
--
ALTER TABLE `admin_keys`
  ADD PRIMARY KEY (`key_id`),
  ADD UNIQUE KEY `unique_value` (`value`);

--
-- Indeksy dla tabeli `api_keys`
--
ALTER TABLE `api_keys`
  ADD PRIMARY KEY (`key_id`),
  ADD UNIQUE KEY `unique_value` (`value`);

--
-- Indeksy dla tabeli `exams`
--
ALTER TABLE `exams`
  ADD PRIMARY KEY (`exam_id`),
  ADD KEY `exam_type_id_idx` (`exam_type_id`);

--
-- Indeksy dla tabeli `exams_groups`
--
ALTER TABLE `exams_groups`
  ADD PRIMARY KEY (`exam_group_id`),
  ADD KEY `exam_id_idx` (`exam_id`),
  ADD KEY `group_id_idx` (`group_id`);

--
-- Indeksy dla tabeli `exam_type`
--
ALTER TABLE `exam_type`
  ADD PRIMARY KEY (`exam_type_id`);

--
-- Indeksy dla tabeli `general_group`
--
ALTER TABLE `general_group`
  ADD PRIMARY KEY (`general_group_id`);

--
-- Indeksy dla tabeli `moderators`
--
ALTER TABLE `moderators`
  ADD PRIMARY KEY (`moderator_id`);

--
-- Indeksy dla tabeli `otp_codes`
--
ALTER TABLE `otp_codes`
  ADD PRIMARY KEY (`otp_code_id`),
  ADD KEY `general_group_id_idx` (`general_group_id`);

--
-- Indeksy dla tabeli `refresh_token`
--
ALTER TABLE `refresh_token`
  ADD PRIMARY KEY (`token_id`),
  ADD UNIQUE KEY `token` (`token`),
  ADD KEY `idx_user_id` (`user_id`);

--
-- Indeksy dla tabeli `student_groups`
--
ALTER TABLE `student_groups`
  ADD PRIMARY KEY (`group_id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indeksy dla tabeli `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD KEY `general_group_id_idx` (`general_group_id`);

--
-- AUTO_INCREMENT dla zrzuconych tabel
--

--
-- AUTO_INCREMENT dla tabeli `admin_keys`
--
ALTER TABLE `admin_keys`
  MODIFY `key_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT dla tabeli `api_keys`
--
ALTER TABLE `api_keys`
  MODIFY `key_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT dla tabeli `exams`
--
ALTER TABLE `exams`
  MODIFY `exam_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT dla tabeli `exams_groups`
--
ALTER TABLE `exams_groups`
  MODIFY `exam_group_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=57;

--
-- AUTO_INCREMENT dla tabeli `exam_type`
--
ALTER TABLE `exam_type`
  MODIFY `exam_type_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT dla tabeli `general_group`
--
ALTER TABLE `general_group`
  MODIFY `general_group_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT dla tabeli `otp_codes`
--
ALTER TABLE `otp_codes`
  MODIFY `otp_code_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT dla tabeli `refresh_token`
--
ALTER TABLE `refresh_token`
  MODIFY `token_id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT dla tabeli `student_groups`
--
ALTER TABLE `student_groups`
  MODIFY `group_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT dla tabeli `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- Ograniczenia dla zrzutów tabel
--

--
-- Ograniczenia dla tabeli `exams`
--
ALTER TABLE `exams`
  ADD CONSTRAINT `exams_ibfk_1` FOREIGN KEY (`exam_type_id`) REFERENCES `exam_type` (`exam_type_id`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `exams_groups`
--
ALTER TABLE `exams_groups`
  ADD CONSTRAINT `exams_groups_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`exam_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `exams_groups_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `student_groups` (`group_id`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `otp_codes`
--
ALTER TABLE `otp_codes`
  ADD CONSTRAINT `otp_codes_ibfk_1` FOREIGN KEY (`general_group_id`) REFERENCES `general_group` (`general_group_id`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `refresh_token`
--
ALTER TABLE `refresh_token`
  ADD CONSTRAINT `fk_refresh_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`general_group_id`) REFERENCES `general_group` (`general_group_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
