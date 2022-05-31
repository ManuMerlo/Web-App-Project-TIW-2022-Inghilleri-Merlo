CREATE DATABASE IF NOT EXISTS `quotemanagement`;
USE `quotemanagement`;

DROP TABLE IF EXISTS `productoptions`;
DROP TABLE IF EXISTS `quoteoptions`;
DROP TABLE IF EXISTS `quote`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `option`;

CREATE TABLE `user` (
  `id` int AUTO_INCREMENT,
  `username` varchar(45),
  `email` varchar(255),
  `password` varchar(45) NOT NULL,
  `role` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE(`email`),
  CONSTRAINT `UC_UsernameRole` UNIQUE (`username`,`role`),
  CONSTRAINT `NN_UsernameORRole` CHECK (`username` is not null OR `email` is not null)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `user` WRITE;
INSERT INTO `user` VALUES (1,'c1',null,'c1','client'),(2,'c2',null,'c2','client'),(3,'c3',null,'c3','client'),(4,'w1',null,'w1','worker'),(5,'w2',null,'w2','worker'),(6,'w3',null,'w3','worker'),(7,null,'c4@gmail.com','c4','client'),(8,null,'c5@gmail.com','c5','client'),(9,null,'c6@gmail.com','c6','client'),(10,null,'w4@gmail.com','w4','worker'),(11,null,'w5@gmail.com','w5','worker'),(12,null,'w6@gmail.com','w6','worker');
UNLOCK TABLES;

CREATE TABLE `product` (
  `code` int AUTO_INCREMENT,
  `image` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `product` WRITE;
INSERT INTO `product` VALUES (1,'product1','Zaino'),(2,'product2','Auto'),(3,'product3','Moto'),(4,'product4','Bici');
UNLOCK TABLES;

CREATE TABLE `option` (
  `code` int AUTO_INCREMENT,
  `type` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `option` WRITE;
INSERT INTO `option` VALUES (1,'normal','cerchi in lega'),(2,'on sale','porta bottiglie'),(3,'on sale','fari led');
UNLOCK TABLES;

CREATE TABLE `quote` (
  `id` int auto_increment,
  `clientId` int NOT NULL,
  `workerId` int default null,
  `productCode` int NOT NULL,
  `price` int default null,
  PRIMARY KEY (`id`),
  CONSTRAINT `asstoclient` FOREIGN KEY (`clientId`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `asstoworker` FOREIGN KEY (`workerId`) REFERENCES `user` (`id`) ON DELETE CASCADE,
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
INSERT INTO `productoptions` VALUES (1,2),(2,1),(2,2),(2,3),(3,1),(4,2);
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