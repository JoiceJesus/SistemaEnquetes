CREATE DATABASE IF NOT EXISTS sistema_enquetes
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sistema_enquetes;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS voto;
DROP TABLE IF EXISTS opcao_resposta;
DROP TABLE IF EXISTS enquete;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS nivel_acesso;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE nivel_acesso (
    idnivel_acesso INT NOT NULL PRIMARY KEY,
    tipo VARCHAR(45) NOT NULL UNIQUE,
    permissoes VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

INSERT INTO nivel_acesso (idnivel_acesso, tipo, permissoes) VALUES
(1, 'comum', 'VOTAR,CONSULTAR'),
(2, 'administrador', 'CRIAR,EDITAR,EXCLUIR,VOTAR,RELATORIOS');

CREATE TABLE usuario (
    idusuario INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT NOT NULL DEFAULT 1,
    nivel_acesso_idnivel_acesso INT NOT NULL,
    CONSTRAINT fk_usuario_nivel_acesso
        FOREIGN KEY (nivel_acesso_idnivel_acesso)
        REFERENCES nivel_acesso (idnivel_acesso)
) ENGINE=InnoDB;

CREATE TABLE categoria (
    idcategoria INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome_categoria VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE enquete (
    idenquete INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao VARCHAR(1000) NOT NULL,
    tipo_votacao ENUM('UNICA','MULTIPLA') NOT NULL,
    limite_votos_ip INT NOT NULL DEFAULT 0,
    limite_quantidade_votos INT NOT NULL DEFAULT 0,
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_expiracao DATETIME NOT NULL,
    status ENUM('EM_CURSO','ENCERRADA') NOT NULL DEFAULT 'EM_CURSO',
    usuario_idusuario INT NOT NULL,
    categoria_idcategoria INT NOT NULL,
    CONSTRAINT fk_enquete_usuario
        FOREIGN KEY (usuario_idusuario)
        REFERENCES usuario (idusuario),
    CONSTRAINT fk_enquete_categoria
        FOREIGN KEY (categoria_idcategoria)
        REFERENCES categoria (idcategoria),
    INDEX idx_enquete_status_expiracao (status, data_expiracao)
) ENGINE=InnoDB;

CREATE TABLE opcao_resposta (
    idopcao INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    descricao_opcao VARCHAR(255) NOT NULL,
    enquete_idenquete INT NOT NULL,
    CONSTRAINT fk_opcao_resposta_enquete
        FOREIGN KEY (enquete_idenquete)
        REFERENCES enquete (idenquete)
        ON DELETE CASCADE,
    UNIQUE KEY uq_opcao_por_enquete (enquete_idenquete, descricao_opcao)
) ENGINE=InnoDB;

CREATE TABLE voto (
    idvoto BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    data_hora_voto DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_voto VARCHAR(45) NOT NULL,
    usuario_idusuario INT NOT NULL,
    enquete_idenquete INT NOT NULL,
    opcao_resposta_idopcao INT NOT NULL,
    CONSTRAINT fk_voto_usuario
        FOREIGN KEY (usuario_idusuario)
        REFERENCES usuario (idusuario),
    CONSTRAINT fk_voto_enquete
        FOREIGN KEY (enquete_idenquete)
        REFERENCES enquete (idenquete)
        ON DELETE CASCADE,
    CONSTRAINT fk_voto_opcao_resposta
        FOREIGN KEY (opcao_resposta_idopcao)
        REFERENCES opcao_resposta (idopcao)
        ON DELETE CASCADE,
    UNIQUE KEY uq_voto_usuario_enquete_opcao
        (usuario_idusuario, enquete_idenquete, opcao_resposta_idopcao),
    INDEX idx_voto_ip_enquete (ip_voto, enquete_idenquete)
) ENGINE=InnoDB;

INSERT INTO categoria (nome_categoria) VALUES
('Tecnologia'),
('Educação'),
('Entretenimento');
