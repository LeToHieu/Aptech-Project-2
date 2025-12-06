-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 05, 2025 at 04:38 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `storygraph_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `author`
--

CREATE TABLE `author` (
  `Id` int(11) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Description` text DEFAULT NULL,
  `Image` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `author`
--

INSERT INTO `author` (`Id`, `Name`, `Description`, `Image`) VALUES
(1, 'Nguyễn Nhật Ánh', 'Nhà văn Việt Nam với nhiều tác phẩm viết cho tuổi thơ.', 'images/Nguyễn Nhật Ánh.jpg'),
(2, 'Yuval Noah Harari', 'Nhà sử học và triết gia người Israel, tác giả của Sapiens và Homo Deus.', 'images/Yuval Noah Harari.jpg'),
(3, 'Dale Carnegie', 'Nhà văn, diễn giả người Mỹ, tác giả của \"Đắc Nhân Tâm\".', 'images/Dale Carnegie.jpg'),
(4, 'Paulo Coelho', 'Nhà văn người Brazil, nổi tiếng với \"Nhà Giả Kim\".', 'images/Paulo Coelho.jpg'),
(5, 'Nguyễn Nhật Ánh', 'Nhà văn Việt Nam với nhiều tác phẩm viết cho tuổi thơ.', 'images/Nguyễn Nhật Ánh.jpg'),
(6, 'Yuval Noah Harari', 'Nhà sử học và triết gia người Israel, tác giả của Sapiens và Homo Deus.', 'images/Yuval Noah Harari.jpg'),
(7, 'J.K. Rowling', 'Tác giả người Anh, nổi tiếng với loạt truyện Harry Potter.', 'images/J.K. Rowling.jpg'),
(8, 'George Orwell', 'Nhà văn người Anh, tác giả của 1984 và Animal Farm.', 'images/George Orwell.jpg'),
(9, 'Mark Manson', 'Tác giả người Mỹ chuyên viết về phát triển bản thân.', 'images/Mark Manson.jpg'),
(10, 'Napoleon Hill', 'Nhà văn và diễn giả người Mỹ về lĩnh vực thành công cá nhân.', 'images/Napoleon Hill.jpg'),
(11, 'James Clear', 'Chuyên gia về thói quen và năng suất cá nhân.', 'images/James Clear.jpg'),
(12, 'Morgan Housel', 'Nhà văn và nhà đầu tư người Mỹ.', 'images/Morgan Housel.jpg'),
(13, 'Frank Herbert', 'Nhà văn Mỹ nổi tiếng với tác phẩm khoa học viễn tưởng Dune.', 'images/Frank Herbert.jpg'),
(14, 'Haruki Murakami', 'Nhà văn Nhật Bản, nổi tiếng với phong cách kỳ ảo hiện đại.', 'images/Haruki Murakami.jpg'),
(15, 'Khaled Hosseini', 'Tác giả người Afghanistan - Mỹ, nổi tiếng với The Kite Runner.', 'images/Khaled Hosseini.jpg'),
(16, 'Stephen Hawking', 'Nhà vật lý lý thuyết người Anh, tác giả A Brief History of Time.', 'images/Stephen Hawking.jpg'),
(17, 'Daniel Kahneman', 'Nhà tâm lý học và kinh tế hành vi, tác giả Thinking, Fast and Slow.', 'images/Daniel Kahneman.jpg'),
(18, 'Jared Diamond', 'Nhà nhân chủng học / địa lý lịch sử, tác giả Guns, Germs, and Steel.', 'images/Jared Diamond.jpg'),
(19, 'J.R.R. Tolkien', 'Nhà văn người Anh, tác giả The Hobbit và The Lord of the Rings.', 'images/JRRTolkien.jpg'),
(20, 'Eric Ries', 'Doanh nhân, tác giả The Lean Startup và The Startup Way.', 'images/Eric Ries.jpg'),
(21, 'Stephen R. Covey', 'Tác giả The 7 Habits of Highly Effective People.', 'images/StephenRCovey.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `book`
--

CREATE TABLE `book` (
  `Id` int(11) NOT NULL,
  `Title` varchar(255) NOT NULL,
  `Description` text DEFAULT NULL,
  `TotalBook` int(11) NOT NULL,
  `BorrowBook` int(11) NOT NULL,
  `Image` varchar(500) DEFAULT NULL,
  `Url` varchar(500) DEFAULT NULL,
  `CreateTime` timestamp NOT NULL DEFAULT current_timestamp(),
  `UpdateTime` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `book`
--

INSERT INTO `book` (`Id`, `Title`, `Description`, `TotalBook`, `BorrowBook`, `Image`, `Url`, `CreateTime`, `UpdateTime`) VALUES
(1, 'Cho Tôi Xin Một Vé Đi Tuổi Thơ', 'Hồi ức tuổi thơ', 0, 0, 'ChoToiXinMotVeDiTuoiTho.jpg', NULL, '2025-11-09 20:32:59', '2025-11-30 17:06:47'),
(2, 'Sapiens', 'Lược sử loài người', 0, 0, 'Sapiens.jpg', NULL, '2025-11-09 20:32:59', '2025-11-30 17:06:47'),
(3, 'Đắc Nhân Tâm', 'Cuốn sách kinh điển về nghệ thuật giao tiếp và thuyết phục của Dale Carnegie.', 0, 0, 'Đắc Nhân Tâm.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(4, 'Nhà Giả Kim', 'Hành trình đi tìm ước mơ đầy triết lý của chàng chăn cừu Santiago.', 0, 0, 'Nhà Giả Kim.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(5, 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Tác phẩm nổi tiếng của Nguyễn Nhật Ánh viết về tuổi thơ và tình cảm gia đình.', 0, 0, 'ToiThayHoaVangTrenCoXanh.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(6, 'Sapiens: Lược Sử Loài Người', 'Khám phá quá trình tiến hóa và hình thành xã hội loài người.', 0, 0, 'Sapiens Lược Sử Loài Người.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(7, 'Homo Deus: Lược Sử Tương Lai', 'Dự đoán về tương lai loài người khi công nghệ phát triển.', 0, 0, 'Homo Deus Lược Sử Tương Lai.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(8, '21 Bài Học Cho Thế Kỷ 21', 'Những vấn đề lớn của thế giới hiện đại.', 0, 0, '21 Bài Học Cho Thế Kỷ 21.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(9, 'Harry Potter và Hòn Đá Phù Thủy', 'Cuốn đầu tiên trong loạt truyện nổi tiếng Harry Potter.', 0, 0, 'Harry Potter và Hòn Đá Phù Thủy.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(10, 'Harry Potter và Chiếc Cốc Lửa', 'Cuộc thi Tam Pháp Thuật và bóng đen Voldemort trở lại.', 0, 0, 'Harry Potter và Chiếc Cốc Lửa.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(11, '1984', 'Tác phẩm dystopia kinh điển của George Orwell, nói về xã hội bị kiểm soát toàn diện.', 0, 0, '1984.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(12, 'Animal Farm', 'Ngụ ngôn chính trị phản ánh chế độ độc tài.', 0, 0, 'Animal Farm.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(13, 'The Subtle Art of Not Giving a F*ck', 'Cách sống thực tế, tập trung vào điều quan trọng.', 0, 0, 'The Subtle Art of Not Giving a Fuck.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(14, 'Think and Grow Rich', 'Tác phẩm kinh điển về tư duy và thành công của Napoleon Hill.', 0, 0, 'Think and Grow Rich.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(15, 'Atomic Habits', 'James Clear hướng dẫn cách xây dựng thói quen tốt.', 0, 0, 'Atomic Habits.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(16, 'The Psychology of Money', 'Morgan Housel bàn về tâm lý tài chính và đầu tư.', 0, 0, 'The Psychology of Money.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(17, 'Dune', 'Tác phẩm viễn tưởng nổi tiếng của Frank Herbert.', 0, 0, 'Dune.jpg', NULL, '2025-11-09 22:47:34', '2025-11-30 17:06:47'),
(18, 'Norwegian Wood', 'Tiểu thuyết nổi tiếng của Haruki Murakami về tuổi trẻ và mất mát.', 0, 0, 'Norwegian Wood.jpg', NULL, '2025-11-10 01:00:00', '2025-11-30 17:06:47'),
(19, 'Kafka on the Shore', 'Tiểu thuyết kỳ ảo của Haruki Murakami, đan xen hai tuyến truyện.', 0, 0, 'Kafka on the Shore.jpg', NULL, '2025-11-10 01:05:00', '2025-11-30 17:06:47'),
(20, 'The Kite Runner', 'Tiểu thuyết của Khaled Hosseini về tình bạn và ăn năn.', 0, 0, 'The Kite Runner.jpg', NULL, '2025-11-10 01:10:00', '2025-11-30 17:06:47'),
(21, 'A Thousand Splendid Suns', 'Tiểu thuyết của Khaled Hosseini về số phận hai người phụ nữ Afghanistan.', 0, 0, 'A Thousand Splendid Suns.jpg', NULL, '2025-11-10 01:15:00', '2025-11-30 17:06:47'),
(22, 'A Brief History of Time', 'Stephen Hawking giới thiệu vũ trụ học cho độc giả phổ thông.', 0, 0, 'A Brief History of Time.jpg', NULL, '2025-11-10 01:20:00', '2025-11-30 17:06:47'),
(23, 'The Universe in a Nutshell', 'Tiếp nối và mở rộng ý tưởng vũ trụ học của Hawking.', 0, 0, 'The Universe in a Nutshell.jpg', NULL, '2025-11-10 01:25:00', '2025-11-30 17:06:47'),
(24, 'Thinking, Fast and Slow', 'Daniel Kahneman phân tích hai hệ tư duy: nhanh và chậm.', 0, 0, 'Thinking Fast and Slow.jpg', NULL, '2025-11-10 01:30:00', '2025-11-30 17:06:47'),
(25, 'Noise', 'Sách về sai lệch quyết định (Daniel Kahneman cùng tác giả khác).', 0, 0, 'Noise.jpg', NULL, '2025-11-10 01:35:00', '2025-11-30 17:06:47'),
(26, 'Guns, Germs, and Steel', 'Jared Diamond giải thích khác biệt phát triển của các xã hội.', 0, 0, 'Guns Germs and Steel.jpg', NULL, '2025-11-10 01:40:00', '2025-11-30 17:06:47'),
(27, 'Collapse', 'Jared Diamond nghiên cứu cách và lý do các nền văn minh sụp đổ.', 0, 0, 'Collapse.jpg', NULL, '2025-11-10 01:45:00', '2025-11-30 17:06:47'),
(28, 'The Hobbit', 'Tiền truyện điện ảnh của The Lord of the Rings - J.R.R. Tolkien.', 0, 0, 'The Hobbit.jpg', NULL, '2025-11-10 01:50:00', '2025-11-30 17:06:47'),
(29, 'The Fellowship of the Ring', 'Tập 1 trong The Lord of the Rings - J.R.R. Tolkien.', 0, 0, 'The Fellowship of the Ring.jpg', NULL, '2025-11-10 01:55:00', '2025-11-30 17:06:47'),
(30, 'The Lean Startup', 'Eric Ries giới thiệu phương pháp khởi nghiệp tinh gọn.', 0, 0, 'The Lean Startup.jpg', NULL, '2025-11-10 02:00:00', '2025-11-30 17:06:47'),
(31, 'The Startup Way', 'Eric Ries: áp dụng tư duy startup vào doanh nghiệp lớn.', 0, 0, 'The Startup Way.jpg', NULL, '2025-11-10 02:05:00', '2025-11-30 17:06:47'),
(32, 'The 7 Habits of Highly Effective People', 'Stephen R. Covey hướng dẫn 7 thói quen để hiệu quả cá nhân.', 0, 0, 'The 7 Habits of Highly Effective People.jpg', NULL, '2025-11-10 02:10:00', '2025-11-30 17:06:47'),
(33, 'First Things First', 'Stephen R. Covey cùng cộng sự về quản trị thời gian và ưu tiên.', 12, 0, '20251130_233322_448.jpg', NULL, '2025-11-10 02:15:00', '2025-11-30 17:06:47'),
(41, 'thêm sách', 'asd', 12, 0, '20251201_000341_340.jpg', NULL, '2025-11-30 17:03:50', '2025-11-30 17:07:30'),
(43, 'asd', '', 12, 0, '20251201_003731_238.jpg', NULL, '2025-11-30 17:37:05', '2025-11-30 17:37:33');

-- --------------------------------------------------------

--
-- Table structure for table `bookauthor`
--

CREATE TABLE `bookauthor` (
  `BookId` int(11) NOT NULL,
  `AuthorId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `bookauthor`
--

INSERT INTO `bookauthor` (`BookId`, `AuthorId`) VALUES
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 6),
(8, 6),
(9, 7),
(10, 7),
(11, 8),
(12, 8),
(13, 9),
(14, 10),
(15, 11),
(16, 12),
(17, 13),
(18, 14),
(19, 14),
(20, 15),
(21, 15),
(22, 16),
(23, 16),
(24, 17),
(25, 17),
(26, 18),
(27, 18),
(28, 19),
(29, 19),
(30, 20),
(31, 20),
(32, 21),
(33, 21),
(41, 8),
(43, 4);

-- --------------------------------------------------------

--
-- Table structure for table `bookcategory`
--

CREATE TABLE `bookcategory` (
  `BookId` int(11) NOT NULL,
  `CategoryId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `bookcategory`
--

INSERT INTO `bookcategory` (`BookId`, `CategoryId`) VALUES
(3, 8),
(4, 6),
(5, 1),
(6, 2),
(7, 2),
(8, 2),
(9, 7),
(10, 7),
(11, 6),
(12, 6),
(13, 4),
(14, 5),
(15, 8),
(16, 5),
(17, 7),
(18, 6),
(19, 6),
(20, 1),
(21, 1),
(22, 2),
(23, 2),
(24, 4),
(25, 4),
(26, 3),
(27, 3),
(28, 7),
(29, 7),
(30, 5),
(31, 5),
(32, 8),
(33, 8),
(41, 7),
(43, 4);

-- --------------------------------------------------------

--
-- Table structure for table `bookshelves`
--

CREATE TABLE `bookshelves` (
  `ShelvesId` int(11) NOT NULL,
  `BookId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `bookshelves`
--

INSERT INTO `bookshelves` (`ShelvesId`, `BookId`) VALUES
(1, 32),
(1, 33),
(2, 30),
(2, 31),
(3, 18),
(3, 20),
(4, 22),
(4, 30),
(5, 19),
(5, 28),
(6, 22),
(6, 23),
(7, 25),
(7, 32),
(8, 26),
(8, 27),
(9, 28),
(9, 29),
(10, 18),
(10, 19);

-- --------------------------------------------------------

--
-- Table structure for table `borrow`
--

CREATE TABLE `borrow` (
  `Id` int(11) NOT NULL,
  `BorrowDay` timestamp NOT NULL DEFAULT current_timestamp(),
  `ExpireDay` timestamp NOT NULL DEFAULT '2038-01-18 20:14:07',
  `ReturnDateTime` timestamp NULL DEFAULT NULL,
  `UserId` int(11) NOT NULL,
  `BookId` int(11) NOT NULL,
  `Status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0=Đang mượn, 1=Đã trả, 2=Quá hạn'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `borrow`
--

INSERT INTO `borrow` (`Id`, `BorrowDay`, `ExpireDay`, `ReturnDateTime`, `UserId`, `BookId`, `Status`) VALUES
(1, '2025-11-01 02:00:00', '2025-11-15 02:00:00', NULL, 2, 20, 2),
(2, '2025-10-25 07:30:00', '2025-11-08 07:30:00', '2025-11-05 04:00:00', 3, 11, 1),
(3, '2025-11-05 09:00:00', '2025-11-19 09:00:00', NULL, 4, 18, 2),
(4, '2025-10-20 03:00:00', '2025-11-03 03:00:00', NULL, 2, 6, 2),
(5, '2025-11-08 02:30:00', '2025-11-22 02:30:00', NULL, 1, 30, 2),
(6, '2025-10-15 06:00:00', '2025-10-29 06:00:00', '2025-10-28 03:00:00', 4, 3, 1),
(7, '2025-11-09 11:20:00', '2025-11-23 11:20:00', NULL, 2, 32, 2),
(8, '2025-09-28 01:00:00', '2025-10-12 01:00:00', NULL, 3, 27, 2),
(9, '2025-11-02 05:15:00', '2025-11-16 05:15:00', NULL, 1, 29, 2),
(10, '2025-10-30 02:45:00', '2025-11-13 02:45:00', '2025-11-10 03:00:00', 2, 22, 1);

--
-- Triggers `borrow`
--
DELIMITER $$
CREATE TRIGGER `trg_set_expire_day` BEFORE INSERT ON `borrow` FOR EACH ROW BEGIN
    IF NEW.ExpireDay = '2038-01-19 03:14:07' THEN
        SET NEW.ExpireDay = DATE_ADD(NEW.BorrowDay, INTERVAL 14 DAY);
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `Id` int(11) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `Description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`Id`, `Name`, `Description`) VALUES
(1, 'Tiểu thuyết', NULL),
(2, 'Khoa học', NULL),
(3, 'Lịch sử', NULL),
(4, 'Tâm lý học', 'Các tác phẩm về tâm lý và phát triển bản thân'),
(5, 'Kinh doanh', 'Sách về kinh tế, quản lý, marketing'),
(6, 'Văn học nước ngoài', 'Tác phẩm văn học nổi tiếng thế giới'),
(7, 'Giả tưởng', 'Truyện khoa học viễn tưởng, fantasy'),
(8, 'Kỹ năng sống', 'Sách hướng dẫn kỹ năng sống và giao tiếp');

-- --------------------------------------------------------

--
-- Table structure for table `review`
--

CREATE TABLE `review` (
  `Id` int(11) NOT NULL,
  `UserId` int(11) NOT NULL,
  `BookId` int(11) NOT NULL,
  `Rating` decimal(3,2) DEFAULT NULL CHECK (`Rating` >= 0 and `Rating` <= 5),
  `Comment` text DEFAULT NULL,
  `CreateTime` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `review`
--

INSERT INTO `review` (`Id`, `UserId`, `BookId`, `Rating`, `Comment`, `CreateTime`) VALUES
(1, 2, 18, 4.50, 'Rất sâu sắc, lối kể cuốn hút.', '2025-11-10 04:00:00'),
(2, 3, 18, 4.00, 'Phong cách Murakami đặc trưng.', '2025-11-10 04:05:00'),
(3, 4, 18, 4.20, 'Đọc một mạch không dứt.', '2025-11-10 04:10:00'),
(4, 2, 19, 4.10, 'Kỳ ảo và triết lý.', '2025-11-10 04:15:00'),
(5, 3, 19, 3.90, 'Một chút khó hiểu nhưng hay.', '2025-11-10 04:20:00'),
(6, 4, 19, 4.30, 'Ngôn từ đẹp và ám ảnh.', '2025-11-10 04:25:00'),
(7, 2, 20, 5.00, 'Câu chuyện cảm động, khó quên.', '2025-11-10 04:30:00'),
(8, 3, 20, 4.70, 'Cảm xúc chân thực, đề tài quan trọng.', '2025-11-10 04:35:00'),
(9, 4, 20, 4.80, 'Rất nên đọc.', '2025-11-10 04:40:00'),
(10, 2, 21, 4.60, 'Rất cảm động, nhân văn.', '2025-11-10 04:45:00'),
(11, 3, 21, 4.20, 'Mạnh mẽ và day dứt.', '2025-11-10 04:50:00'),
(12, 4, 21, 4.40, 'Nội dung sâu sắc.', '2025-11-10 04:55:00'),
(13, 2, 22, 4.90, 'Khái quát vũ trụ tuyệt vời.', '2025-11-10 05:00:00'),
(14, 3, 22, 4.30, 'Khó nhưng rất giá trị.', '2025-11-10 05:05:00'),
(15, 4, 22, 4.00, 'Nhiều ý tưởng lớn.', '2025-11-10 05:10:00'),
(16, 2, 23, 3.80, 'Hình ảnh đẹp nhưng nặng kỹ thuật.', '2025-11-10 05:15:00'),
(17, 3, 23, 3.90, 'Thích cách trình bày.', '2025-11-10 05:20:00'),
(18, 4, 23, 3.70, 'Thích hợp cho người quan tâm vũ trụ.', '2025-11-10 05:25:00'),
(19, 2, 24, 5.00, 'Thay đổi cách suy nghĩ của tôi.', '2025-11-10 05:30:00'),
(20, 3, 24, 4.80, 'Rất thuyết phục và thực tế.', '2025-11-10 05:35:00'),
(21, 4, 24, 4.60, 'Đầy insight về tâm lý.', '2025-11-10 05:40:00'),
(22, 2, 25, 4.10, 'Ý tưởng hay về sai lệch quyết định.', '2025-11-10 05:45:00'),
(23, 3, 25, 3.95, 'Bổ sung tốt cho Thinking, Fast and Slow.', '2025-11-10 05:50:00'),
(24, 4, 25, 4.00, 'Đáng đọc.', '2025-11-10 05:55:00'),
(25, 2, 26, 4.70, 'Cách nhìn lịch sử rất khác biệt.', '2025-11-10 06:00:00'),
(26, 3, 26, 4.50, 'Rất nhiều thông tin giá trị.', '2025-11-10 06:05:00'),
(27, 4, 26, 4.20, 'Phân tích sắc bén.', '2025-11-10 06:10:00'),
(28, 2, 27, 4.00, 'Bài học từ các nền văn minh.', '2025-11-10 06:15:00'),
(29, 3, 27, 3.85, 'Một số đoạn hơi dài.', '2025-11-10 06:20:00'),
(30, 4, 27, 4.10, 'Đáng để suy ngẫm.', '2025-11-10 06:25:00'),
(31, 2, 28, 4.95, 'Kỳ diệu, huyền thoại.', '2025-11-10 06:30:00'),
(32, 3, 28, 4.70, 'Thích hợp cho mọi lứa tuổi.', '2025-11-10 06:35:00'),
(33, 4, 28, 4.50, 'Một hành trình tuyệt vời.', '2025-11-10 06:40:00'),
(34, 2, 29, 4.60, 'Khởi đầu cho một cuộc phiêu lưu.', '2025-11-10 06:45:00'),
(35, 3, 29, 4.30, 'Xây dựng thế giới xuất sắc.', '2025-11-10 06:50:00'),
(36, 4, 29, 4.40, 'Ngôn từ lôi cuốn.', '2025-11-10 06:55:00'),
(37, 2, 30, 4.80, 'Quyển must-read cho startup.', '2025-11-10 07:00:00'),
(38, 3, 30, 4.50, 'Ý tưởng thực tiễn.', '2025-11-10 07:05:00'),
(39, 4, 30, 4.60, 'Rất hữu ích cho doanh nghiệp.', '2025-11-10 07:10:00'),
(40, 2, 31, 4.20, 'Tiếp nối tư duy Lean.', '2025-11-10 07:15:00'),
(41, 3, 31, 4.00, 'Một số case study hay.', '2025-11-10 07:20:00'),
(42, 4, 31, 3.90, 'Có nhiều bài học ứng dụng.', '2025-11-10 07:25:00'),
(43, 2, 32, 5.00, 'Thay đổi cuộc đời tôi.', '2025-11-10 07:30:00'),
(44, 3, 32, 4.70, 'Rất thực tế và logic.', '2025-11-10 07:35:00'),
(45, 4, 32, 4.80, 'Áp dụng được ngay.', '2025-11-10 07:40:00'),
(46, 2, 33, 4.10, 'Bổ trợ cho 7 Habits.', '2025-11-10 07:45:00'),
(47, 3, 33, 3.95, 'Có nhiều mẹo hữu ích.', '2025-11-10 07:50:00'),
(48, 4, 33, 4.00, 'Dễ đọc, dễ áp dụng.', '2025-11-10 07:55:00');

-- --------------------------------------------------------

--
-- Table structure for table `shelves`
--

CREATE TABLE `shelves` (
  `Id` int(11) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `UserId` int(11) NOT NULL,
  `Description` text DEFAULT NULL,
  `CreateTime` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `shelves`
--

INSERT INTO `shelves` (`Id`, `Name`, `UserId`, `Description`, `CreateTime`) VALUES
(1, 'Huy - Yêu thích', 4, 'Kệ cá nhân của Huy: sách yêu thích', '2025-11-10 03:00:00'),
(2, 'Admin - Staff Picks', 1, 'Lựa chọn bởi admin', '2025-11-10 03:05:00'),
(3, 'John - To Read', 2, 'Danh sách muốn đọc của John', '2025-11-10 03:10:00'),
(4, 'General - Kinh doanh', 3, 'Sách về kinh doanh dành cho thư viện', '2025-11-10 03:15:00'),
(5, 'Top Fiction', 1, 'Các tác phẩm hư cấu nổi bật', '2025-11-10 03:20:00'),
(6, 'Science & Tech', 2, 'Sách khoa học và công nghệ', '2025-11-10 03:25:00'),
(7, 'Self Improvement', 4, 'Sách kỹ năng sống và phát triển bản thân', '2025-11-10 03:30:00'),
(8, 'Classics', 3, 'Tuyển chọn kinh điển', '2025-11-10 03:35:00'),
(9, 'Fantasy Collection', 1, 'Sách fantasy cho thiếu nhi và người lớn', '2025-11-10 03:40:00'),
(10, 'World Literature', 2, 'Tác phẩm văn học thế giới', '2025-11-10 03:45:00');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `Id` int(11) NOT NULL,
  `UserName` varchar(100) NOT NULL,
  `Email` varchar(255) NOT NULL,
  `PhoneNumber` bigint(20) DEFAULT NULL,
  `Password` varchar(255) NOT NULL,
  `Role` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0=User, 1=Admin, 2=Librarian',
  `CreateTime` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`Id`, `UserName`, `Email`, `PhoneNumber`, `Password`, `Role`, `CreateTime`) VALUES
(1, 'admin', 'admin@library.com', 901234567, 'admin123', 1, '2025-11-09 20:32:58'),
(2, 'john_doe', 'john@example.com', 901234568, 'user123', 0, '2025-11-09 20:32:58'),
(3, 'admin1', 'admin1@gmail.com', 123456789, '$2a$10$C/oOJ5hHTs5A2t1hPN.jEOrC/ZmsIttpMaBRme2I3wK8HF0fyvJ5W', 1, '2025-11-09 20:42:54'),
(4, 'Vu Duc Huy', 'huy@gmail.com', 965904537, '$2a$10$xOZKAcKmXFPSlZ0rGdgtgutSe94QCplannnNAzss57vOooWkSPoYq', 0, '2025-11-09 21:20:12'),
(8, 'Admin2', 'admin2@gmail.com', 914631188, '$2a$10$lf9CLNcrNA2/ANZsTbjmt.0vJipkiEcvGjNKVIctQfeqBNYPNow3O', 2, '2025-11-24 16:13:12'),
(9, 'Admin3', 'admin3@gmail.com', 945786315, '$2a$10$xpUq58T4gwPpbdw/MAZcuOj6V74ugNb87KpZyEkdVP4zhg/wt3KA2', 0, '2025-11-24 16:51:51');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `author`
--
ALTER TABLE `author`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `idx_author_name` (`Name`);

--
-- Indexes for table `book`
--
ALTER TABLE `book`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `idx_title` (`Title`);
ALTER TABLE `book` ADD FULLTEXT KEY `idx_search` (`Title`,`Description`);

--
-- Indexes for table `bookauthor`
--
ALTER TABLE `bookauthor`
  ADD PRIMARY KEY (`BookId`,`AuthorId`),
  ADD KEY `AuthorId` (`AuthorId`);

--
-- Indexes for table `bookcategory`
--
ALTER TABLE `bookcategory`
  ADD PRIMARY KEY (`BookId`,`CategoryId`),
  ADD KEY `CategoryId` (`CategoryId`);

--
-- Indexes for table `bookshelves`
--
ALTER TABLE `bookshelves`
  ADD PRIMARY KEY (`ShelvesId`,`BookId`),
  ADD KEY `BookId` (`BookId`);

--
-- Indexes for table `borrow`
--
ALTER TABLE `borrow`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `BookId` (`BookId`),
  ADD KEY `idx_user_status` (`UserId`,`Status`),
  ADD KEY `idx_expire_status` (`ExpireDay`,`Status`),
  ADD KEY `idx_borrowday` (`BorrowDay`);

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`Id`),
  ADD UNIQUE KEY `Name` (`Name`),
  ADD KEY `idx_category_name` (`Name`);

--
-- Indexes for table `review`
--
ALTER TABLE `review`
  ADD PRIMARY KEY (`Id`),
  ADD UNIQUE KEY `uniq_user_book` (`UserId`,`BookId`),
  ADD KEY `idx_book_rating` (`BookId`,`Rating`);

--
-- Indexes for table `shelves`
--
ALTER TABLE `shelves`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `idx_user_shelves` (`UserId`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`Id`),
  ADD UNIQUE KEY `UserName` (`UserName`),
  ADD UNIQUE KEY `Email` (`Email`),
  ADD KEY `idx_email` (`Email`),
  ADD KEY `idx_username` (`UserName`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `author`
--
ALTER TABLE `author`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `book`
--
ALTER TABLE `book`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;

--
-- AUTO_INCREMENT for table `borrow`
--
ALTER TABLE `borrow`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `review`
--
ALTER TABLE `review`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=67;

--
-- AUTO_INCREMENT for table `shelves`
--
ALTER TABLE `shelves`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bookauthor`
--
ALTER TABLE `bookauthor`
  ADD CONSTRAINT `bookauthor_ibfk_1` FOREIGN KEY (`BookId`) REFERENCES `book` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `bookauthor_ibfk_2` FOREIGN KEY (`AuthorId`) REFERENCES `author` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `bookcategory`
--
ALTER TABLE `bookcategory`
  ADD CONSTRAINT `bookcategory_ibfk_1` FOREIGN KEY (`BookId`) REFERENCES `book` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `bookcategory_ibfk_2` FOREIGN KEY (`CategoryId`) REFERENCES `category` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `bookshelves`
--
ALTER TABLE `bookshelves`
  ADD CONSTRAINT `bookshelves_ibfk_1` FOREIGN KEY (`ShelvesId`) REFERENCES `shelves` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `bookshelves_ibfk_2` FOREIGN KEY (`BookId`) REFERENCES `book` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `borrow`
--
ALTER TABLE `borrow`
  ADD CONSTRAINT `borrow_ibfk_1` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `borrow_ibfk_2` FOREIGN KEY (`BookId`) REFERENCES `book` (`Id`) ON UPDATE CASCADE;

--
-- Constraints for table `review`
--
ALTER TABLE `review`
  ADD CONSTRAINT `review_ibfk_1` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `review_ibfk_2` FOREIGN KEY (`BookId`) REFERENCES `book` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `shelves`
--
ALTER TABLE `shelves`
  ADD CONSTRAINT `shelves_ibfk_1` FOREIGN KEY (`UserId`) REFERENCES `user` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
