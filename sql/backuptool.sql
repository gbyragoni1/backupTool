CREATE SCHEMA IF NOT EXISTS `backuptool` ;

CREATE TABLE IF NOT EXISTS `backuptool`.`snapshot` (
  `id` int NOT NULL,
  `directory` varchar(255) DEFAULT NULL,
  `create_date` datetime NOT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;


CREATE TABLE IF NOT EXISTS `backuptool`.`snapshot_file` (
  `id` int NOT NULL AUTO_INCREMENT,
  `snapshot_id` int NOT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `file_content_id` int DEFAULT NULL,
  `content_hash` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `snapshot_id_idx` (`snapshot_id`),
  CONSTRAINT `snapshot_id` FOREIGN KEY (`snapshot_id`) REFERENCES `snapshot` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=508 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;

CREATE TABLE  IF NOT EXISTS `backuptool`.`snapshot_file_content` (
  `id` int NOT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_content` longblob,
  `snapshot_file_id` int DEFAULT NULL,
  `snapshpathot_file_contentcol` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;


CREATE USER IF NOT EXISTS 'bktooluser'@'localhost' IDENTIFIED BY 'bktoolpw';
GRANT ALL PRIVILEGES ON backuptool.* TO 'bktooluser'@'localhost';