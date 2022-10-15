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
INSERT INTO `user` VALUES (1,'c1',null,'c1','client'),(2,'c2',null,'c2','client'),(3,'c3',null,'c3','client'),(4,'w1',null,'w1','worker'),(5,'w2',null,'w2','worker'),(6,'w3',null,'w3','worker'),(7,'c4','c4@gmail.com','c4','client'),(8,'c5','c5@gmail.com','c5','client'),(9,'c6','c6@gmail.com','c6','client'),(10,'w4','w4@gmail.com','w4','worker'),(11,'w5','w5@gmail.com','w5','worker'),(12,'w6','w6@gmail.com','w6','worker');
UNLOCK TABLES;

CREATE TABLE `product` (
  `code` int AUTO_INCREMENT,
  `image` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `product` WRITE;
INSERT INTO `product` VALUES (1,'/images/mercedesa45s.png','Mercedes A45s'),(2,'/images/audirs3.png','Audi RS3'),(3,'/images/aventador.png','Lamborghini Aventador'),(4,'/images/urus.png','Lamborghini Urus'),(5,'/images/ferrari.png','Ferrari 488'),(6,'/images/cbr1000.png','Honda cbr 1000 rr'),(7,'/images/f4.png','Yamaha MV Augusta F4'),(8,'/images/ninja.png','Kawasaki Ninja');
UNLOCK TABLES;

CREATE TABLE `option` (
  `code` int AUTO_INCREMENT,
  `type` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `option` WRITE;
INSERT INTO `option` VALUES (1,'Normal','Cerchi in lega'),(2,'On sale','Sistema infotainment'),(3,'On sale','Fari full LED'),(4,'Normal','Cruise control adattivo'),(5,'Normal','Sensori di parcheggio'),(6,'Normal','Sedili riscaldabili'),(7,'On sale','Sistema keyless'),(8,'On sale','ABS'),(9,'Normal','Sospensioni elettroniche'),(10,'Normal','Fendinebbia'),(11,'Normal','Manopole riscaldate'),(12,'On sale','Bauletti posteriori'),(13,'Normal','Scarico Arrow');
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
 
LOCK TABLES `quote` WRITE;
INSERT INTO `quote` VALUES (1,1,10,1,1000),(2,1,null,2,null),(3,3,10,2,3000),(4,3,null,1,null),(5,7,10,1,30000),(6,7,null,1,null);
UNLOCK TABLES;

CREATE TABLE `productoptions`(
  `productCode` int,
  `optionCode` int,
  PRIMARY KEY (`productCode`,`optionCode`),
  CONSTRAINT `relwithproduct` FOREIGN KEY (`productCode`) REFERENCES `product` (`code`) ON DELETE CASCADE,
  CONSTRAINT `relwithoption` FOREIGN KEY (`optionCode`) REFERENCES `option` (`code`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `productoptions` WRITE;
INSERT INTO `productoptions` VALUES (1,1),(1,2),(1,3),(2,3),(2,4),(2,5),(2,6),(3,1),(3,2),(3,7),(4,1),(4,2),(5,1),(5,3),(5,5),(6,8),(6,9),(6,10),(6,11),(7,11),(7,8),(7,9),(7,13),(8,10),(8,11),(8,12);
UNLOCK TABLES;

CREATE TABLE `quoteoptions`(
  `quoteId` int NOT NULL,
  `optionCode` int NOT NULL,
  PRIMARY KEY (`quoteId`,`optionCode`),
  CONSTRAINT `relwithquote` FOREIGN KEY (`quoteId`) REFERENCES `quote` (`id`) ON DELETE CASCADE,
  CONSTRAINT `selectedoption` FOREIGN KEY (`optionCode`) REFERENCES `option` (`code`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `quoteoptions` WRITE;
INSERT INTO `quoteoptions` VALUES (1,2),(2,1),(2,2),(3,2),(4,2),(5,2),(6,1);
UNLOCK TABLES;