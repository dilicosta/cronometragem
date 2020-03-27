ALTER TABLE `atleta_inscricao`
	DROP COLUMN `desclassificado`;
	
ALTER TABLE `atleta_inscricao`
	ADD COLUMN `id_atleta` BIGINT(20) NULL DEFAULT NULL AFTER `id`,
	ADD COLUMN `id_categoria` BIGINT(20) NULL DEFAULT NULL AFTER `id_atleta`,
	ADD COLUMN `id_contato_urgencia` BIGINT(20) NULL DEFAULT NULL AFTER `id_categoria`,
	ADD COLUMN `id_responsavel` BIGINT(20) NULL DEFAULT NULL AFTER `id_contato_urgencia`,
	ADD COLUMN `id_dupla` BIGINT(20) NULL DEFAULT NULL AFTER `id_endereco_atleta_inscricao`;
	
update atleta_inscricao ai join inscricao i on i.id_atleta_inscricao = ai.id
set ai.id_atleta = i.id_atleta,
ai.id_categoria = i.id_categoria_da_prova,
ai.id_contato_urgencia = i.id_contato_urgencia,
ai.id_responsavel = i.id_responsavel;

ALTER TABLE `atleta_inscricao`
	ADD COLUMN `data_inscricao` DATETIME NULL DEFAULT NULL AFTER `id_dupla`;
	
update atleta_inscricao ai join inscricao i on i.id_atleta_inscricao = ai.id
set ai.data_inscricao=i.`data`;

ALTER TABLE `atleta_inscricao`
	ADD CONSTRAINT `FK_atleta_inscricao_atleta` FOREIGN KEY (`id_atleta`) REFERENCES `atleta` (`id`),
	ADD CONSTRAINT `FK_atleta_inscricao_categoria_da_prova` FOREIGN KEY (`id_categoria`) REFERENCES `categoria_da_prova` (`id`),
	ADD CONSTRAINT `FK_atleta_inscricao_contato_urgencia` FOREIGN KEY (`id_contato_urgencia`) REFERENCES `contato_urgencia` (`id`),
	ADD CONSTRAINT `FK_atleta_inscricao_responsavel` FOREIGN KEY (`id_responsavel`) REFERENCES `responsavel` (`id`),
	ADD CONSTRAINT `FK_atleta_inscricao_atleta_inscricao` FOREIGN KEY (`id_dupla`) REFERENCES `atleta_inscricao` (`id`);

ALTER TABLE `atleta_inscricao`
	ALTER `id_atleta` DROP DEFAULT,
	ALTER `id_categoria` DROP DEFAULT,
	ALTER `data_inscricao` DROP DEFAULT;
ALTER TABLE `atleta_inscricao`
	CHANGE COLUMN `id_atleta` `id_atleta` BIGINT(20) NOT NULL AFTER `id`,
	CHANGE COLUMN `id_categoria` `id_categoria` BIGINT(20) NOT NULL AFTER `id_atleta`,
	CHANGE COLUMN `data_inscricao` `data_inscricao` DATETIME NOT NULL AFTER `id_dupla`;

ALTER TABLE `cronometragem`
	DROP FOREIGN KEY `FK_cronometragem_inscricao`;
	
update cronometragem c 
join inscricao i on i.id=c.id_inscricao
join atleta_inscricao ai on ai.id=i.id_atleta_inscricao
set c.id_inscricao = ai.id;

ALTER TABLE `cronometragem`
	CHANGE COLUMN `id_inscricao` `id_atleta_inscricao` BIGINT(20) NULL DEFAULT NULL AFTER `id`;
	
ALTER TABLE `cronometragem`
	ADD CONSTRAINT `FK_cronometragem_atleta_inscricao` FOREIGN KEY (`id_atleta_inscricao`) REFERENCES `atleta_inscricao` (`id`);
	
	
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
DROP TABLE `inscricao`;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

ALTER TABLE `atleta_inscricao`
	ADD COLUMN `data_importacao` DATETIME NULL AFTER `data_inscricao`,
	ADD INDEX `data_inscricao` (`data_inscricao`),
	ADD INDEX `data_importacao` (`data_importacao`),
	ADD INDEX `numero_atleta` (`numero_atleta`),
	ADD INDEX `status` (`status`),
	ADD INDEX `posicao_categoria` (`posicao_categoria`),
	ADD INDEX `posicao_geral` (`posicao_geral`);

ALTER TABLE `categoria_atleta`
	ADD COLUMN `categoria_dupla` TINYINT(1) NULL AFTER `idade_maxima`;

update categoria_atleta set categoria_dupla = 0;

ALTER TABLE `categoria_atleta`
	ALTER `categoria_dupla` DROP DEFAULT;
ALTER TABLE `categoria_atleta`
	CHANGE COLUMN `categoria_dupla` `categoria_dupla` TINYINT(1) NOT NULL AFTER `idade_maxima`;
	
ALTER TABLE `atleta_inscricao`
	ADD COLUMN `tempo_dupla` INT(10) UNSIGNED NULL DEFAULT NULL AFTER `tempo`;
	
ALTER TABLE `percurso`
	ALTER `numero_volta` DROP DEFAULT;
ALTER TABLE `percurso`
	CHANGE COLUMN `numero_volta` `numero_volta` SMALLINT(3) NOT NULL AFTER `grau_dificuldade`;
	
ALTER TABLE `cronometragem`
	CHANGE COLUMN `volta` `volta` SMALLINT NULL DEFAULT NULL AFTER `hora_registro`;
	
ALTER TABLE `atleta_inscricao`
	ADD COLUMN `numero_volta` SMALLINT(3) NULL DEFAULT NULL AFTER `diferenca_geral`;
	
ALTER TABLE `atleta_inscricao`
	CHANGE COLUMN `numero_atleta` `numero_atleta` MEDIUMINT NULL DEFAULT NULL AFTER `data_importacao`;

ALTER TABLE `cronometragem`
	ALTER `numero_atleta` DROP DEFAULT;
ALTER TABLE `cronometragem`
	CHANGE COLUMN `numero_atleta` `numero_atleta` MEDIUMINT NOT NULL AFTER `id_prova`;

CREATE TABLE `cronometragem_imagem` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`imagem_byte` BLOB NULL,
	PRIMARY KEY (`id`)
)

ALTER TABLE `cronometragem`
	ADD COLUMN `id_imagem` BIGINT(20) NULL AFTER `id_prova`,
	ADD CONSTRAINT `FK_cronometragem_cronometragem_imagem` FOREIGN KEY (`id_imagem`) REFERENCES `cronometragem_imagem` (`id`);




