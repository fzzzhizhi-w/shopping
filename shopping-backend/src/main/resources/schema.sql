CREATE DATABASE IF NOT EXISTS shopping DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shopping;

CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `username`    VARCHAR(50)  NOT NULL UNIQUE,
    `password`    VARCHAR(255) NOT NULL,
    `email`       VARCHAR(100),
    `phone`       VARCHAR(20),
    `avatar`      VARCHAR(500),
    `role`        VARCHAR(20)  NOT NULL DEFAULT 'user',
    `create_time` DATETIME,
    `update_time` DATETIME,
    `deleted`     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `category` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(50) NOT NULL,
    `icon`        VARCHAR(255),
    `sort`        INT         DEFAULT 0,
    `parent_id`   BIGINT      DEFAULT 0,
    `create_time` DATETIME,
    `update_time` DATETIME,
    `deleted`     TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `product` (
    `id`          BIGINT         NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(200)   NOT NULL,
    `description` TEXT,
    `price`       DECIMAL(10, 2) NOT NULL,
    `stock`       INT            NOT NULL DEFAULT 0,
    `sales`       INT            NOT NULL DEFAULT 0,
    `category_id` BIGINT,
    `images`      TEXT,
    `main_image`  VARCHAR(500),
    `status`      TINYINT        NOT NULL DEFAULT 1,
    `create_time` DATETIME,
    `update_time` DATETIME,
    `deleted`     TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `cart` (
    `id`          BIGINT  NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT  NOT NULL,
    `product_id`  BIGINT  NOT NULL,
    `quantity`    INT     NOT NULL DEFAULT 1,
    `create_time` DATETIME,
    `update_time` DATETIME,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `order` (
    `id`             BIGINT         NOT NULL AUTO_INCREMENT,
    `order_no`       VARCHAR(64)    NOT NULL UNIQUE,
    `user_id`        BIGINT         NOT NULL,
    `total_amount`   DECIMAL(10, 2) NOT NULL,
    `status`         TINYINT        NOT NULL DEFAULT 0 COMMENT '0:pending 1:paid 2:shipped 3:delivered 4:cancelled',
    `address`        VARCHAR(500),
    `receiver_name`  VARCHAR(50),
    `receiver_phone` VARCHAR(20),
    `create_time`    DATETIME,
    `update_time`    DATETIME,
    `deleted`        TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `order_item` (
    `id`            BIGINT         NOT NULL AUTO_INCREMENT,
    `order_id`      BIGINT         NOT NULL,
    `product_id`    BIGINT         NOT NULL,
    `product_name`  VARCHAR(200)   NOT NULL,
    `product_image` VARCHAR(500),
    `price`         DECIMAL(10, 2) NOT NULL,
    `quantity`      INT            NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `chat_history` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL,
    `role`        VARCHAR(20)  NOT NULL COMMENT 'user or assistant',
    `content`     LONGTEXT     NOT NULL,
    `session_id`  VARCHAR(64),
    `create_time` DATETIME,
    PRIMARY KEY (`id`),
    KEY `idx_user_session` (`user_id`, `session_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `product_favorite` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT   NOT NULL,
    `product_id`  BIGINT   NOT NULL,
    `create_time` DATETIME,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `browse_history` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT   NOT NULL,
    `product_id`  BIGINT   NOT NULL,
    `view_time`   DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `address` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL,
    `name`        VARCHAR(50)  NOT NULL,
    `phone`       VARCHAR(20)  NOT NULL,
    `province`    VARCHAR(50),
    `city`        VARCHAR(50),
    `district`    VARCHAR(50),
    `detail`      VARCHAR(500) NOT NULL,
    `is_default`  TINYINT      NOT NULL DEFAULT 0,
    `create_time` DATETIME,
    `update_time` DATETIME,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `order_review` (
    `id`            BIGINT         NOT NULL AUTO_INCREMENT,
    `user_id`       BIGINT         NOT NULL,
    `order_id`      BIGINT         NOT NULL,
    `order_item_id` BIGINT         NOT NULL,
    `product_id`    BIGINT         NOT NULL,
    `rating`        TINYINT        NOT NULL DEFAULT 5 COMMENT '1-5 stars',
    `content`       TEXT,
    `images`        TEXT,
    `create_time`   DATETIME,
    `update_time`   DATETIME,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_item` (`order_item_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `banner` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `title`       VARCHAR(100),
    `image`       VARCHAR(500) NOT NULL,
    `link`        VARCHAR(500),
    `sort`        INT          NOT NULL DEFAULT 0,
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '1:active 0:inactive',
    `create_time` DATETIME,
    `update_time` DATETIME,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `ad` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `title`       VARCHAR(100),
    `image`       VARCHAR(500) NOT NULL,
    `link`        VARCHAR(500),
    `position`    VARCHAR(50)  COMMENT 'home_top, home_side, etc.',
    `sort`        INT          NOT NULL DEFAULT 0,
    `status`      TINYINT      NOT NULL DEFAULT 1,
    `create_time` DATETIME,
    `update_time` DATETIME,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
