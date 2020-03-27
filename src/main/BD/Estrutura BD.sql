-- --------------------------------------------------------
-- Servidor:                     127.0.0.1
-- Versão do servidor:           10.2.14-MariaDB - mariadb.org binary distribution
-- OS do Servidor:               Win64
-- HeidiSQL Versão:              9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Copiando estrutura do banco de dados para taurus_racing_timing
CREATE DATABASE IF NOT EXISTS `taurus_racing_timing_prod` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `taurus_racing_timing_prod`;

-- Copiando estrutura para tabela taurus_racing_timing.atleta
CREATE TABLE IF NOT EXISTS `atleta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_endereco` bigint(20) DEFAULT 0,
  `nome` varchar(50) NOT NULL,
  `data_nascimento` date NOT NULL,
  `sexo` tinyint(1) NOT NULL COMMENT '0-masculino; 1-feminino',
  `cpf` varchar(13) DEFAULT NULL,
  `rg` varchar(20) DEFAULT NULL,
  `data_rg` date DEFAULT NULL,
  `orgao_rg` varchar(20) DEFAULT NULL,
  `tipo_sanguineo` tinyint(1) DEFAULT NULL,
  `telefone1` varchar(11) DEFAULT NULL,
  `telefone2` varchar(11) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `profissao` varchar(30) DEFAULT NULL,
  `equipamento` varchar(30) DEFAULT NULL,
  `codigo_federacao` varchar(8) DEFAULT NULL,
  `codigo_cbc` varchar(12) DEFAULT NULL,
  `codigo_uci` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cpf` (`cpf`),
  KEY `nome` (`nome`),
  KEY `FK_atleta_endereco` (`id_endereco`),
  CONSTRAINT `FK_atleta_endereco` FOREIGN KEY (`id_endereco`) REFERENCES `endereco` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.atleta_inscricao
CREATE TABLE IF NOT EXISTS `atleta_inscricao` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_atleta` bigint(20) NOT NULL,
  `id_categoria` bigint(20) NOT NULL,
  `id_contato_urgencia` bigint(20) DEFAULT NULL,
  `id_responsavel` bigint(20) DEFAULT NULL,
  `id_endereco_atleta_inscricao` bigint(20) NOT NULL,
  `id_dupla` bigint(20) DEFAULT NULL,
  `data_inscricao` datetime DEFAULT NULL,
  `data_importacao` datetime DEFAULT NULL,
  `numero_atleta` mediumint(9) DEFAULT NULL,
  `equipe` varchar(40) DEFAULT NULL,
  `telefone1` varchar(11) DEFAULT NULL,
  `telefone2` varchar(11) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `profissao` varchar(30) DEFAULT NULL,
  `equipamento` varchar(30) DEFAULT NULL,
  `codigo_federacao` varchar(8) DEFAULT NULL,
  `codigo_cbc` varchar(15) DEFAULT NULL,
  `codigo_uci` varchar(15) DEFAULT NULL,
  `status_corrida` tinyint(1) NOT NULL,
  `motivo_desclassificacao` varchar(50) DEFAULT NULL,
  `tempo` int(10) unsigned DEFAULT NULL COMMENT 'tempo em milesegundos',
  `tempo_dupla` int(10) unsigned DEFAULT NULL,
  `posicao_categoria` mediumint(5) DEFAULT NULL,
  `posicao_geral` mediumint(5) DEFAULT NULL,
  `diferenca_categoria` int(10) unsigned DEFAULT NULL COMMENT 'tempo em milesegundos',
  `diferenca_geral` int(10) unsigned DEFAULT NULL COMMENT 'tempo em milesegundos',
  `numero_volta` smallint(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_atleta_inscricao_endereco_atleta_inscricao` (`id_endereco_atleta_inscricao`),
  KEY `tempo` (`tempo`),
  KEY `FK_atleta_inscricao_atleta` (`id_atleta`),
  KEY `FK_atleta_inscricao_categoria_da_prova` (`id_categoria`),
  KEY `FK_atleta_inscricao_contato_urgencia` (`id_contato_urgencia`),
  KEY `FK_atleta_inscricao_responsavel` (`id_responsavel`),
  KEY `FK_atleta_inscricao_atleta_inscricao` (`id_dupla`),
  KEY `data_inscricao` (`data_inscricao`),
  KEY `data_importacao` (`data_importacao`),
  KEY `numero_atleta` (`numero_atleta`),
  KEY `status` (`status_corrida`),
  KEY `posicao_categoria` (`posicao_categoria`),
  KEY `posicao_geral` (`posicao_geral`),
  CONSTRAINT `FK_atleta_inscricao_atleta` FOREIGN KEY (`id_atleta`) REFERENCES `atleta` (`id`),
  CONSTRAINT `FK_atleta_inscricao_atleta_inscricao` FOREIGN KEY (`id_dupla`) REFERENCES `atleta_inscricao` (`id`),
  CONSTRAINT `FK_atleta_inscricao_categoria_da_prova` FOREIGN KEY (`id_categoria`) REFERENCES `categoria_da_prova` (`id`),
  CONSTRAINT `FK_atleta_inscricao_contato_urgencia` FOREIGN KEY (`id_contato_urgencia`) REFERENCES `contato_urgencia` (`id`),
  CONSTRAINT `FK_atleta_inscricao_endereco_atleta_inscricao` FOREIGN KEY (`id_endereco_atleta_inscricao`) REFERENCES `endereco_atleta_inscricao` (`id`),
  CONSTRAINT `FK_atleta_inscricao_responsavel` FOREIGN KEY (`id_responsavel`) REFERENCES `responsavel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.categoria_atleta
CREATE TABLE IF NOT EXISTS `categoria_atleta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `descricao` varchar(100) DEFAULT NULL,
  `sexo` tinyint(1) DEFAULT NULL COMMENT '0-masculino; 1-feminino',
  `idade_minima` tinyint(2) DEFAULT NULL,
  `idade_maxima` tinyint(2) DEFAULT NULL,
  `categoria_dupla` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `nome` (`nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.categoria_da_prova
CREATE TABLE IF NOT EXISTS `categoria_da_prova` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_prova` bigint(20) NOT NULL,
  `id_categoria_atleta` bigint(20) NOT NULL,
  `id_largada` bigint(20) NOT NULL,
  `id_percurso` bigint(20) NOT NULL,
  `numeracao_automatica` tinyint(1) NOT NULL,
  `digitos_numeracao` tinyint(1) DEFAULT NULL,
  `inicio_numeracao` int(11) DEFAULT NULL,
  `fim_numeracao` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_prova_has_representante_federacao_prova` (`id_prova`),
  KEY `FK_prova_has_representante_federacao_representante_federacao` (`id_categoria_atleta`),
  KEY `FK_prova_has_categoria_largada` (`id_largada`),
  KEY `FK_prova_has_categoria_percurso` (`id_percurso`),
  CONSTRAINT `FK_categoria_da_prova_categoria_atleta` FOREIGN KEY (`id_categoria_atleta`) REFERENCES `categoria_atleta` (`id`),
  CONSTRAINT `FK_categoria_da_prova_largada` FOREIGN KEY (`id_largada`) REFERENCES `largada` (`id`),
  CONSTRAINT `FK_categoria_da_prova_percurso` FOREIGN KEY (`id_percurso`) REFERENCES `percurso` (`id`),
  CONSTRAINT `FK_categoria_da_prova_prova` FOREIGN KEY (`id_prova`) REFERENCES `prova` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.configuracao
CREATE TABLE IF NOT EXISTS `configuracao` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome_camera` varchar(50) DEFAULT NULL,
  `resolucao_camera` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.contato_urgencia
CREATE TABLE IF NOT EXISTS `contato_urgencia` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `parentesco` varchar(15) NOT NULL,
  `telefone1` varchar(11) NOT NULL,
  `telefone2` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.cronometragem
CREATE TABLE IF NOT EXISTS `cronometragem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_atleta_inscricao` bigint(20) DEFAULT NULL,
  `id_prova` bigint(20) NOT NULL,
  `id_imagem` bigint(20) DEFAULT NULL,
  `numero_atleta` mediumint(9) NOT NULL,
  `hora_registro` datetime(3) NOT NULL,
  `volta` smallint(6) DEFAULT NULL,
  `tempo_volta` int(10) unsigned DEFAULT NULL,
  `duvida` tinyint(1) NOT NULL,
  `excluida` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_cronometragem_inscricao` (`id_atleta_inscricao`),
  KEY `hora_registro` (`hora_registro`),
  KEY `numero_atleta` (`numero_atleta`),
  KEY `FK_cronometragem_prova` (`id_prova`),
  KEY `FK_cronometragem_cronometragem_imagem` (`id_imagem`),
  CONSTRAINT `FK_cronometragem_atleta_inscricao` FOREIGN KEY (`id_atleta_inscricao`) REFERENCES `atleta_inscricao` (`id`),
  CONSTRAINT `FK_cronometragem_cronometragem_imagem` FOREIGN KEY (`id_imagem`) REFERENCES `cronometragem_imagem` (`id`),
  CONSTRAINT `FK_cronometragem_prova` FOREIGN KEY (`id_prova`) REFERENCES `prova` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.cronometragem_imagem
CREATE TABLE IF NOT EXISTS `cronometragem_imagem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `imagem_byte` blob DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.endereco
CREATE TABLE IF NOT EXISTS `endereco` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `logradouro` varchar(50) DEFAULT NULL,
  `numero` smallint(5) unsigned DEFAULT NULL,
  `complemento` varchar(20) DEFAULT NULL,
  `bairro` varchar(40) DEFAULT NULL,
  `cidade` varchar(40) NOT NULL,
  `uf` varchar(2) NOT NULL,
  `cep` varchar(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.endereco_atleta_inscricao
CREATE TABLE IF NOT EXISTS `endereco_atleta_inscricao` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `logradouro` varchar(50) DEFAULT NULL,
  `numero` smallint(5) unsigned DEFAULT NULL,
  `complemento` varchar(20) DEFAULT NULL,
  `bairro` varchar(40) DEFAULT NULL,
  `cidade` varchar(40) NOT NULL,
  `uf` varchar(2) NOT NULL,
  `cep` varchar(8) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.federacao
CREATE TABLE IF NOT EXISTS `federacao` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `sigla` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `nome` (`nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.largada
CREATE TABLE IF NOT EXISTS `largada` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_prova` bigint(20) NOT NULL DEFAULT 0,
  `nome` varchar(40) NOT NULL,
  `hora_prevista` datetime NOT NULL,
  `hora_inicio` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_largada_prova` (`id_prova`),
  CONSTRAINT `FK_largada_prova` FOREIGN KEY (`id_prova`) REFERENCES `prova` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.organizacao_prova
CREATE TABLE IF NOT EXISTS `organizacao_prova` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nome` (`nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.percurso
CREATE TABLE IF NOT EXISTS `percurso` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_prova` bigint(20) NOT NULL,
  `nome` varchar(40) NOT NULL,
  `distancia` float(6,2) NOT NULL,
  `grau_dificuldade` tinyint(1) NOT NULL,
  `numero_volta` smallint(3) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_percurso_prova` (`id_prova`),
  CONSTRAINT `FK_percurso_prova` FOREIGN KEY (`id_prova`) REFERENCES `prova` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.prova
CREATE TABLE IF NOT EXISTS `prova` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_endereco` bigint(20) NOT NULL,
  `nome` varchar(50) NOT NULL,
  `data` date NOT NULL,
  `status` tinyint(1) NOT NULL,
  `motivo_pendencia` varchar(200) DEFAULT NULL,
  `demo` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `FK_prova_endereco` (`id_endereco`),
  KEY `nome` (`nome`),
  CONSTRAINT `FK_prova_endereco` FOREIGN KEY (`id_endereco`) REFERENCES `endereco` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.prova_has_representante_federacao
CREATE TABLE IF NOT EXISTS `prova_has_representante_federacao` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_prova` bigint(20) NOT NULL,
  `id_representante_federacao` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_prova_has_representante_federacao_prova` (`id_prova`),
  KEY `FK_prova_has_representante_federacao_representante_federacao` (`id_representante_federacao`),
  CONSTRAINT `FK_prova_has_representante_federacao_prova` FOREIGN KEY (`id_prova`) REFERENCES `prova` (`id`),
  CONSTRAINT `FK_prova_has_representante_federacao_representante_federacao` FOREIGN KEY (`id_representante_federacao`) REFERENCES `representante_federacao` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.prova_has_representante_organizacao_prova
CREATE TABLE IF NOT EXISTS `prova_has_representante_organizacao_prova` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_prova` bigint(20) NOT NULL,
  `id_representante_organizacao_prova` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_prova_has_representante_federacao_prova` (`id_prova`),
  KEY `FK_prova_has_representante_federacao_representante_federacao` (`id_representante_organizacao_prova`),
  CONSTRAINT `FK_prova_has_representante_organizacao_prova_organizacao_prova` FOREIGN KEY (`id_representante_organizacao_prova`) REFERENCES `representante_organizacao_prova` (`id`),
  CONSTRAINT `FK_prova_has_representante_organizacao_prova_prova` FOREIGN KEY (`id_prova`) REFERENCES `prova` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.representante_federacao
CREATE TABLE IF NOT EXISTS `representante_federacao` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_federacao` bigint(20) NOT NULL,
  `nome` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `nome` (`nome`),
  KEY `FK_representante_federacao_federacao` (`id_federacao`),
  CONSTRAINT `FK_representante_federacao_federacao` FOREIGN KEY (`id_federacao`) REFERENCES `federacao` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.representante_organizacao_prova
CREATE TABLE IF NOT EXISTS `representante_organizacao_prova` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_organizacao_prova` bigint(20) NOT NULL,
  `nome` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `nome` (`nome`),
  KEY `FK_representante_federacao_federacao` (`id_organizacao_prova`),
  CONSTRAINT `FK_representante_organizacao_prova_organizacao_prova` FOREIGN KEY (`id_organizacao_prova`) REFERENCES `organizacao_prova` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela taurus_racing_timing.responsavel
CREATE TABLE IF NOT EXISTS `responsavel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `cpf` varchar(13) NOT NULL,
  `rg` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
