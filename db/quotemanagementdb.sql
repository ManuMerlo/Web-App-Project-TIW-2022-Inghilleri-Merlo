CREATE DATABASE IF NOT EXISTS `quotemanagement`;
USE `quotemanagement`;


DROP TABLE IF EXISTS `productoptions`;
DROP TABLE IF EXISTS `quoteoptions`;
DROP TABLE IF EXISTS `quote`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `client`;
DROP TABLE IF EXISTS `worker`;
DROP TABLE IF EXISTS `option`;

CREATE TABLE `client` (
  `id` int AUTO_INCREMENT,
  `username` varchar(45) UNIQUE NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `client` WRITE;
INSERT INTO `client` VALUES (1,'Dani','dani1'),(2,'Motta','motta2'),(3,'Mormi','mormi3');
UNLOCK TABLES;

CREATE TABLE `worker` (
  `id` int AUTO_INCREMENT,
  `username` varchar(45) UNIQUE NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `worker` WRITE;
INSERT INTO `worker` VALUES (1,'Teo','teo1'),(2,'Roby','roby2'),(3,'Max','max3');
UNLOCK TABLES;


CREATE TABLE `product` (
  `code` int AUTO_INCREMENT,
  `image` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `product` WRITE;
INSERT INTO `product` VALUES (1,'product1','Zaino'),(2,'product2','Auto'),(3,'product3','Moto');
UNLOCK TABLES;

CREATE TABLE `option` (
  `code` int AUTO_INCREMENT,
  `type` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `option` WRITE;
INSERT INTO `option` VALUES (1,'normal','cerchi in lega'),(2,'on sale','porta bottiglie');
UNLOCK TABLES;

CREATE TABLE `quote` (
  `id` int auto_increment,
  `clientId` int NOT NULL,
  `workerId` int,
  `productCode` int NOT NULL,
  `price` int default null,
  PRIMARY KEY (`id`),
  CONSTRAINT `asstoclient` FOREIGN KEY (`clientId`) REFERENCES `client` (`id`) ON DELETE CASCADE,
  CONSTRAINT `asstoworker` FOREIGN KEY (`workerId`) REFERENCES `worker` (`id`) ON DELETE CASCADE,
  CONSTRAINT `asstoproduct` FOREIGN KEY (`productCode`) REFERENCES `product` (`code`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
 
LOCK TABLES `quote` WRITE;
INSERT INTO `quote` VALUES (1,1,2,1,1000),(2,1,null,2,null),(3,3,2,2,3000),(4,3,null,1,null);
UNLOCK TABLES;

CREATE TABLE `productoptions`(
  `productCode` int,
  `optionCode` int,
  PRIMARY KEY (`productCode`,`optionCode`),
  CONSTRAINT `relwithproduct` FOREIGN KEY (`productCode`) REFERENCES `product` (`code`) ON DELETE CASCADE,
  CONSTRAINT `relwithoption` FOREIGN KEY (`optionCode`) REFERENCES `option` (`code`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `productoptions` WRITE;
INSERT INTO `productoptions` VALUES (1,2),(2,1),(3,1),(2,2);
UNLOCK TABLES;


CREATE TABLE `quoteoptions`(
  `quoteId` int NOT NULL,
  `optionCode` int NOT NULL,
  PRIMARY KEY (`quoteId`,`optionCode`),
  CONSTRAINT `relwithquote` FOREIGN KEY (`quoteId`) REFERENCES `quote` (`id`) ON DELETE CASCADE,
  CONSTRAINT `selectedoption` FOREIGN KEY (`optionCode`) REFERENCES `option` (`code`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `quoteoptions` WRITE;
INSERT INTO `quoteoptions` VALUES (1,2),(2,1),(2,2),(3,2),(4,2);
UNLOCK TABLES;
