-- Schema update: add merchant support and group buying feature
-- Run this file to migrate an existing database

-- Add merchant_id to product table
ALTER TABLE `product` ADD COLUMN IF NOT EXISTS `merchant_id` BIGINT DEFAULT NULL COMMENT '商家ID';

-- Group buying tables
CREATE TABLE IF NOT EXISTS `group_buy` (
    `id`              BIGINT         NOT NULL AUTO_INCREMENT,
    `product_id`      BIGINT         NOT NULL,
    `group_price`     DECIMAL(10,2)  NOT NULL,
    `min_members`     INT            NOT NULL DEFAULT 2,
    `max_members`     INT            DEFAULT NULL,
    `current_members` INT            NOT NULL DEFAULT 0,
    `status`          TINYINT        NOT NULL DEFAULT 0 COMMENT '0:open 1:success 2:failed 3:expired',
    `share_code`      VARCHAR(20)    NOT NULL UNIQUE,
    `creator_id`      BIGINT         NOT NULL,
    `expire_time`     DATETIME       NOT NULL,
    `create_time`     DATETIME,
    `update_time`     DATETIME,
    `deleted`         TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_share_code` (`share_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `group_buy_member` (
    `id`           BIGINT   NOT NULL AUTO_INCREMENT,
    `group_buy_id` BIGINT   NOT NULL,
    `user_id`      BIGINT   NOT NULL,
    `join_time`    DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_group_buy_id` (`group_buy_id`),
    UNIQUE KEY `uk_group_user` (`group_buy_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
