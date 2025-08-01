-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: db
-- Generation Time: Aug 01, 2025 at 01:55 PM
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
-- Struktura tabeli dla tabeli `exams`
--

DROP TABLE IF EXISTS `exams`;
CREATE TABLE `exams` (
  `exam_id` int NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `date` datetime(6) DEFAULT NULL,
  `groups` varchar(255) DEFAULT NULL,
  `exam_type_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Zrzut danych tabeli `exams`
--

INSERT INTO `exams` (`exam_id`, `title`, `description`, `date`, `groups`, `exam_type_id`) VALUES
(1, 'Matematyka Dyskretna', 'Egzamin końcowy z matematyki dyskretnej', '2025-07-30 00:00:00.000000', '12K3,11L1', 2),
(2, 'Programowanie C++', 'Kolokwium z programowania w C++', '2025-08-05 00:00:00.000000', '12K2,13S3', 1),
(3, 'Sieci Komputerowe', 'Projekt zespołowy na sieciach komputerowych', '2025-09-10 00:00:00.000000', '14S4,12K1', 3);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exam_type`
--

DROP TABLE IF EXISTS `exam_type`;
CREATE TABLE `exam_type` (
  `exam_type_id` int NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Zrzut danych tabeli `general_group`
--

INSERT INTO `general_group` (`general_group_id`, `name`) VALUES
(11, '1'),
(12, '2'),
(13, '3'),
(14, '4');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `groups`
--

DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups` (
  `group_id` int NOT NULL,
  `letter` char(1) NOT NULL,
  `group_count` int NOT NULL,
  `general_group_id` int NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Zrzut danych tabeli `groups`
--

INSERT INTO `groups` (`group_id`, `letter`, `group_count`, `general_group_id`, `name`) VALUES
(1, 'K', 1, 11, NULL),
(2, 'K', 2, 12, NULL),
(3, 'L', 1, 11, NULL),
(4, 'L', 2, 12, NULL),
(5, 'S', 3, 13, NULL),
(6, 'S', 4, 14, NULL),
(7, 'K', 3, 12, NULL),
(8, 'L', 4, 14, NULL);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `otp_codes`
--

DROP TABLE IF EXISTS `otp_codes`;
CREATE TABLE `otp_codes` (
  `code` varchar(255) DEFAULT NULL,
  `expire` timestamp NOT NULL,
  `used` tinyint(1) NOT NULL,
  `user_id` int NOT NULL,
  `otp_code_id` int NOT NULL,
  `timestamp` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` int NOT NULL,
  `general_group_id` int NOT NULL,
  `email` varchar(254) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `role` enum('ADMIN','REPRESENTATIVE') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Zrzut danych tabeli `users`
--

INSERT INTO `users` (`user_id`, `general_group_id`, `email`, `is_active`, `role`) VALUES
(1, 12, 'jan.kowalski@example.com', 1, 'ADMIN'),
(2, 11, 'anna.nowak@example.com', 1, 'REPRESENTATIVE'),
(3, 13, 'piotr.zielinski@example.com', 0, 'REPRESENTATIVE'),
(4, 14, 'ewa.wisniewska@example.com', 1, 'ADMIN');

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `exams`
--
ALTER TABLE `exams`
  ADD PRIMARY KEY (`exam_id`),
  ADD KEY `exam_type` (`exam_type_id`),
  ADD KEY `exam_type_id` (`exam_type_id`);

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
-- Indeksy dla tabeli `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`group_id`),
  ADD KEY `general_group` (`general_group_id`),
  ADD KEY `general_group_id` (`general_group_id`);

--
-- Indeksy dla tabeli `otp_codes`
--
ALTER TABLE `otp_codes`
  ADD PRIMARY KEY (`otp_code_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indeksy dla tabeli `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD KEY `general_group_id` (`general_group_id`);

--
-- AUTO_INCREMENT dla zrzuconych tabel
--

--
-- AUTO_INCREMENT dla tabeli `exams`
--
ALTER TABLE `exams`
  MODIFY `exam_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT dla tabeli `exam_type`
--
ALTER TABLE `exam_type`
  MODIFY `exam_type_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT dla tabeli `general_group`
--
ALTER TABLE `general_group`
  MODIFY `general_group_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT dla tabeli `groups`
--
ALTER TABLE `groups`
  MODIFY `group_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT dla tabeli `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Ograniczenia dla zrzutów tabel
--

--
-- Ograniczenia dla tabeli `exams`
--
ALTER TABLE `exams`
  ADD CONSTRAINT `exams_ibfk_1` FOREIGN KEY (`exam_type_id`) REFERENCES `exam_type` (`exam_type_id`);

--
-- Ograniczenia dla tabeli `groups`
--
ALTER TABLE `groups`
  ADD CONSTRAINT `groups_ibfk_1` FOREIGN KEY (`general_group_id`) REFERENCES `general_group` (`general_group_id`);

--
-- Ograniczenia dla tabeli `otp_codes`
--
ALTER TABLE `otp_codes`
  ADD CONSTRAINT `otp_codes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Ograniczenia dla tabeli `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`general_group_id`) REFERENCES `general_group` (`general_group_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
