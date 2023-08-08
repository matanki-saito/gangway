SET NAMES utf8;
SET character_set_client = utf8mb4;

CREATE TABLE IF NOT EXISTS `fingerprint`
(
    `fingerprint_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `key`            VARCHAR(255) NOT NULL,
    `created_at`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`fingerprint_id`),
    UNIQUE INDEX `key_UNIQUE` (`key` ASC)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `country`
(
    `country_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(255) NOT NULL,
    `created_at` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`country_id`),
    UNIQUE INDEX `key_UNIQUE` (`name` ASC)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `ip`
(
    `ip_id`      INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `string`     VARCHAR(255) NOT NULL,
    `created_at` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`ip_id`),
    UNIQUE INDEX `key_UNIQUE` (`string` ASC)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;