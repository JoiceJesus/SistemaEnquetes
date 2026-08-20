-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: sistema_enquetes
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `idcategoria` int NOT NULL AUTO_INCREMENT,
  `nome_categoria` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`idcategoria`),
  UNIQUE KEY `nome_categoria` (`nome_categoria`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES (2,'Educação'),(3,'Entretenimento'),(1,'Tecnologia');
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enquete`
--

DROP TABLE IF EXISTS `enquete`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enquete` (
  `idenquete` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descricao` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_votacao` enum('UNICA','MULTIPLA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `limite_votos_ip` int NOT NULL DEFAULT '0',
  `limite_quantidade_votos` int NOT NULL DEFAULT '0',
  `data_criacao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `data_expiracao` datetime NOT NULL,
  `status` enum('EM_CURSO','ENCERRADA') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'EM_CURSO',
  `usuario_idusuario` int NOT NULL,
  `categoria_idcategoria` int NOT NULL,
  PRIMARY KEY (`idenquete`),
  KEY `fk_enquete_usuario` (`usuario_idusuario`),
  KEY `fk_enquete_categoria` (`categoria_idcategoria`),
  KEY `idx_enquete_status_expiracao` (`status`,`data_expiracao`),
  CONSTRAINT `fk_enquete_categoria` FOREIGN KEY (`categoria_idcategoria`) REFERENCES `categoria` (`idcategoria`),
  CONSTRAINT `fk_enquete_usuario` FOREIGN KEY (`usuario_idusuario`) REFERENCES `usuario` (`idusuario`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enquete`
--

LOCK TABLES `enquete` WRITE;
/*!40000 ALTER TABLE `enquete` DISABLE KEYS */;
INSERT INTO `enquete` VALUES (1,'EnqueteTeste','testey','UNICA',1,1,'2026-08-20 11:51:55','2026-09-28 09:30:00','EM_CURSO',1,1),(2,'testeenq','enqtest','UNICA',0,1,'2026-08-20 12:26:54','2026-09-17 10:10:00','EM_CURSO',1,2),(3,'multiplaa','multipla','MULTIPLA',1,2,'2026-08-20 12:28:18','2027-02-09 10:10:00','EM_CURSO',1,1);
/*!40000 ALTER TABLE `enquete` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nivel_acesso`
--

DROP TABLE IF EXISTS `nivel_acesso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nivel_acesso` (
  `idnivel_acesso` int NOT NULL,
  `tipo` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL,
  `permissoes` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`idnivel_acesso`),
  UNIQUE KEY `tipo` (`tipo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nivel_acesso`
--

LOCK TABLES `nivel_acesso` WRITE;
/*!40000 ALTER TABLE `nivel_acesso` DISABLE KEYS */;
INSERT INTO `nivel_acesso` VALUES (1,'comum','VOTAR,CONSULTAR'),(2,'administrador','CRIAR,EDITAR,EXCLUIR,VOTAR,RELATORIOS');
/*!40000 ALTER TABLE `nivel_acesso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `opcao_resposta`
--

DROP TABLE IF EXISTS `opcao_resposta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `opcao_resposta` (
  `idopcao` int NOT NULL AUTO_INCREMENT,
  `descricao_opcao` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `enquete_idenquete` int NOT NULL,
  PRIMARY KEY (`idopcao`),
  UNIQUE KEY `uq_opcao_por_enquete` (`enquete_idenquete`,`descricao_opcao`),
  CONSTRAINT `fk_opcao_resposta_enquete` FOREIGN KEY (`enquete_idenquete`) REFERENCES `enquete` (`idenquete`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `opcao_resposta`
--

LOCK TABLES `opcao_resposta` WRITE;
/*!40000 ALTER TABLE `opcao_resposta` DISABLE KEYS */;
INSERT INTO `opcao_resposta` VALUES (2,'that',1),(1,'this',1),(4,'that',2),(3,'this',2),(6,'and',3),(7,'that',3),(5,'this',3);
/*!40000 ALTER TABLE `opcao_resposta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `idusuario` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `senha` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `data_cadastro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint NOT NULL DEFAULT '1',
  `nivel_acesso_idnivel_acesso` int NOT NULL,
  PRIMARY KEY (`idusuario`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_usuario_nivel_acesso` (`nivel_acesso_idnivel_acesso`),
  CONSTRAINT `fk_usuario_nivel_acesso` FOREIGN KEY (`nivel_acesso_idnivel_acesso`) REFERENCES `nivel_acesso` (`idnivel_acesso`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'admini','admin@email.com','1234','2026-08-20 11:06:43',1,2),(2,'username','user@email.com','1234','2026-08-20 11:07:02',1,1),(3,'usuario1','mam@email.com','12334','2026-08-20 13:25:50',1,1);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voto`
--

DROP TABLE IF EXISTS `voto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `voto` (
  `idvoto` bigint NOT NULL AUTO_INCREMENT,
  `data_hora_voto` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_voto` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL,
  `usuario_idusuario` int NOT NULL,
  `enquete_idenquete` int NOT NULL,
  `opcao_resposta_idopcao` int NOT NULL,
  PRIMARY KEY (`idvoto`),
  UNIQUE KEY `uq_voto_usuario_enquete_opcao` (`usuario_idusuario`,`enquete_idenquete`,`opcao_resposta_idopcao`),
  KEY `fk_voto_enquete` (`enquete_idenquete`),
  KEY `fk_voto_opcao_resposta` (`opcao_resposta_idopcao`),
  KEY `idx_voto_ip_enquete` (`ip_voto`,`enquete_idenquete`),
  CONSTRAINT `fk_voto_enquete` FOREIGN KEY (`enquete_idenquete`) REFERENCES `enquete` (`idenquete`) ON DELETE CASCADE,
  CONSTRAINT `fk_voto_opcao_resposta` FOREIGN KEY (`opcao_resposta_idopcao`) REFERENCES `opcao_resposta` (`idopcao`) ON DELETE CASCADE,
  CONSTRAINT `fk_voto_usuario` FOREIGN KEY (`usuario_idusuario`) REFERENCES `usuario` (`idusuario`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voto`
--

LOCK TABLES `voto` WRITE;
/*!40000 ALTER TABLE `voto` DISABLE KEYS */;
INSERT INTO `voto` VALUES (1,'2026-08-20 12:28:58','127.0.0.1',2,2,3),(2,'2026-08-20 12:29:08','127.0.0.1',2,3,5),(3,'2026-08-20 12:29:08','127.0.0.1',2,3,6),(4,'2026-08-20 12:29:19','127.0.0.1',2,1,1);
/*!40000 ALTER TABLE `voto` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-20 13:56:59
