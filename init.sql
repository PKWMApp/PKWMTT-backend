-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: db
-- Generation Time: Aug 18, 2025 at 07:00 PM
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
  `title` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `exam_date` datetime NOT NULL,
  `exam_type_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Tabela Truncate przed wstawieniem `exams`
--

TRUNCATE TABLE `exams`;
--
-- Zrzut danych tabeli `exams`
--

INSERT INTO `exams` (`exam_id`, `title`, `description`, `exam_date`, `exam_type_id`) VALUES
(1, 'Kolokwium z matematyki', 'Pierwsze kolokwium obejmujące rozdziały 1–3', '2025-10-01 10:00:00', 1),
(2, 'Egzamin końcowy z programowania', 'Egzamin pisemny i praktyczny', '2025-01-20 09:00:00', 2),
(3, 'Projekt z baz danych', 'Oddanie projektu grupowego', '2025-06-15 23:59:00', 3),
(4, 'Kolokwium z fizyki', 'Druga część materiału: mechanika', '2025-11-05 12:00:00', 1),
(5, 'Egzamin końcowy z ekonomii', 'Egzamin pisemny testowy', '2025-02-10 08:30:00', 2),
(6, 'Projekt z systemów operacyjnych', 'Prezentacja projektu semestralnego', '2025-06-25 14:00:00', 3);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exams_groups`
--

DROP TABLE IF EXISTS `exams_groups`;
CREATE TABLE `exams_groups` (
  `exam_group_id` int NOT NULL,
  `exam_id` int NOT NULL,
  `group_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Tabela Truncate przed wstawieniem `exams_groups`
--

TRUNCATE TABLE `exams_groups`;
--
-- Zrzut danych tabeli `exams_groups`
--

INSERT INTO `exams_groups` (`exam_group_id`, `exam_id`, `group_id`) VALUES
(7, 1, 9),
(8, 1, 10),
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
(20, 6, 16);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exam_type`
--

DROP TABLE IF EXISTS `exam_type`;
CREATE TABLE `exam_type` (
  `exam_type_id` int NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Tabela Truncate przed wstawieniem `exam_type`
--

TRUNCATE TABLE `exam_type`;
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
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Tabela Truncate przed wstawieniem `general_group`
--

TRUNCATE TABLE `general_group`;
--
-- Zrzut danych tabeli `general_group`
--

INSERT INTO `general_group` (`general_group_id`, `name`) VALUES
(17, '11A'),
(18, '12E'),
(19, '13K'),
(20, '14M');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `groups`
--

DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups` (
  `group_id` int NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Tabela Truncate przed wstawieniem `groups`
--

TRUNCATE TABLE `groups`;
--
-- Zrzut danych tabeli `groups`
--

INSERT INTO `groups` (`group_id`, `name`) VALUES
(9, '11A1'),
(10, '11A2'),
(12, '12E1'),
(13, '12E2'),
(14, '12E3'),
(15, '13K1'),
(16, '13K2'),
(17, '13K3'),
(18, '14M1');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `otp_codes`
--

DROP TABLE IF EXISTS `otp_codes`;
CREATE TABLE `otp_codes` (
  `otp_code_id` int NOT NULL,
  `code` varchar(255) NOT NULL,
  `expire` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `general_group_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Tabela Truncate przed wstawieniem `otp_codes`
--

TRUNCATE TABLE `otp_codes`;
--
-- Zrzut danych tabeli `otp_codes`
--

INSERT INTO `otp_codes` (`otp_code_id`, `code`, `expire`, `general_group_id`) VALUES
(1, 'ABC123', '2025-08-18 19:51:40', 17),
(2, 'XYZ789', '2025-08-18 20:51:40', 18),
(3, 'QWE456', '2025-08-18 21:51:40', 19),
(4, 'JKL999', '2025-08-18 22:51:40', 20);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` int NOT NULL,
  `general_group_id` int NOT NULL,
  `email` varchar(255) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `role` enum('ADMIN','REPRESENTATIVE') NOT NULL DEFAULT 'REPRESENTATIVE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Tabela Truncate przed wstawieniem `users`
--

TRUNCATE TABLE `users`;
--
-- Zrzut danych tabeli `users`
--

INSERT INTO `users` (`user_id`, `general_group_id`, `email`, `is_active`, `role`) VALUES
(1, 17, 'user11a@example.com', 1, 'REPRESENTATIVE'),
(2, 18, 'user12e@example.com', 1, 'REPRESENTATIVE'),
(3, 19, 'user13k@example.com', 1, 'REPRESENTATIVE'),
(4, 20, 'user14m@example.com', 1, 'ADMIN');

--
-- Indeksy dla zrzutów tabel
--

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
-- Indeksy dla tabeli `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`group_id`);

--
-- Indeksy dla tabeli `otp_codes`
--
ALTER TABLE `otp_codes`
  ADD PRIMARY KEY (`otp_code_id`),
  ADD KEY `general_group_id_idx` (`general_group_id`);

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
-- AUTO_INCREMENT dla tabeli `exams`
--
ALTER TABLE `exams`
  MODIFY `exam_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT dla tabeli `exams_groups`
--
ALTER TABLE `exams_groups`
  MODIFY `exam_group_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT dla tabeli `exam_type`
--
ALTER TABLE `exam_type`
  MODIFY `exam_type_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT dla tabeli `general_group`
--
ALTER TABLE `general_group`
  MODIFY `general_group_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT dla tabeli `groups`
--
ALTER TABLE `groups`
  MODIFY `group_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT dla tabeli `otp_codes`
--
ALTER TABLE `otp_codes`
  MODIFY `otp_code_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

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
  ADD CONSTRAINT `exams_ibfk_1` FOREIGN KEY (`exam_type_id`) REFERENCES `exam_type` (`exam_type_id`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `exams_groups`
--
ALTER TABLE `exams_groups`
  ADD CONSTRAINT `exams_groups_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`exam_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `exams_groups_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `otp_codes`
--
ALTER TABLE `otp_codes`
  ADD CONSTRAINT `otp_codes_ibfk_1` FOREIGN KEY (`general_group_id`) REFERENCES `general_group` (`general_group_id`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`general_group_id`) REFERENCES `general_group` (`general_group_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
