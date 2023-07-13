/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : localhost:3306
 Source Schema         : blogy-dev

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 13/07/2023 21:36:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_article
-- ----------------------------
DROP TABLE IF EXISTS `tb_article`;
CREATE TABLE `tb_article`
(
    `id`          mediumint(15) UNSIGNED ZEROFILL                         NOT NULL AUTO_INCREMENT,
    `title`       varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL,
    `content`     longtext CHARACTER SET utf8 COLLATE utf8_general_ci     NULL,
    `author`      int(10)                                                 NULL DEFAULT NULL,
    `tags`        json                                                    NULL,
    `status`      tinyint(1)                                              NULL DEFAULT NULL,
    `views`       int(10)                                                 NULL DEFAULT NULL,
    `comments`    int(7)                                                  NULL DEFAULT NULL,
    `like_num`    int(10)                                                 NULL DEFAULT NULL,
    `create_time` datetime(0)                                             NULL DEFAULT NULL,
    `update_time` datetime(0)                                             NULL DEFAULT NULL,
    `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `title_index` (`title`) USING BTREE
) ENGINE = MyISAM
  AUTO_INCREMENT = 11067
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_article_comment
-- ----------------------------
DROP TABLE IF EXISTS `tb_article_comment`;
CREATE TABLE `tb_article_comment`
(
    `article_comment_id` bigint(64)                                               NOT NULL,
    `article_id`         int(15)                                                  NULL DEFAULT NULL,
    `user_id`            int(10)                                                  NULL DEFAULT NULL,
    `content`            varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `like_num`           int(10)                                                  NULL DEFAULT NULL,
    `create_time`        datetime(0)                                              NULL DEFAULT NULL,
    `status`             tinyint(1)                                               NULL DEFAULT NULL,
    PRIMARY KEY (`article_comment_id`) USING BTREE,
    INDEX `article_id_index` (`article_id`) USING BTREE,
    INDEX `user_id_index` (`user_id`) USING BTREE
) ENGINE = MyISAM
  AUTO_INCREMENT = 299972
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_article_comment_like
-- ----------------------------
DROP TABLE IF EXISTS `tb_article_comment_like`;
CREATE TABLE `tb_article_comment_like`
(
    `user_id`     int(10)     NULL DEFAULT NULL,
    `comment_id`  bigint(64)  NULL DEFAULT NULL,
    `create_time` datetime(0) NULL DEFAULT NULL,
    UNIQUE INDEX `unique_user_comment` (`user_id`, `comment_id`) USING BTREE COMMENT '用户和文章评论唯一约束'
) ENGINE = MyISAM
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Fixed;

-- ----------------------------
-- Table structure for tb_article_of_sort
-- ----------------------------
DROP TABLE IF EXISTS `tb_article_of_sort`;
CREATE TABLE `tb_article_of_sort`
(
    `sort_id`    int(10)       NOT NULL,
    `article_id` mediumint(15) NULL DEFAULT NULL,
    UNIQUE INDEX `sort_article_unique` (`sort_id`, `article_id`) USING BTREE COMMENT 'sort_id和article_id的唯一索引'
) ENGINE = MyISAM
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Fixed;

-- ----------------------------
-- Table structure for tb_like_article
-- ----------------------------
DROP TABLE IF EXISTS `tb_like_article`;
CREATE TABLE `tb_like_article`
(
    `user_id`    int(10)     NOT NULL,
    `article_id` int(15)     NOT NULL,
    `like_time`  datetime(0) NULL DEFAULT NULL,
    UNIQUE INDEX `unique_user_article` (`user_id`, `article_id`) USING BTREE COMMENT '用户和文章的唯一索引'
) ENGINE = MyISAM
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Fixed;

-- ----------------------------
-- Table structure for tb_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_log`;
CREATE TABLE `tb_log`
(
    `type`       char(10) CHARACTER SET utf8 COLLATE utf8_general_ci     NULL DEFAULT NULL,
    `user_id`    int(10)                                                 NULL DEFAULT NULL,
    `start_time` datetime(0)                                             NULL DEFAULT NULL,
    `spend_time` int(11)                                                 NULL DEFAULT NULL,
    `url`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `method`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `ip`         varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `parameter`  json                                                    NULL,
    `result`     text CHARACTER SET utf8 COLLATE utf8_general_ci         NULL
) ENGINE = MyISAM
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_relation
-- ----------------------------
DROP TABLE IF EXISTS `tb_relation`;
CREATE TABLE `tb_relation`
(
    `from_id` int(10)    NULL DEFAULT NULL,
    `to_id`   int(10)    NULL DEFAULT NULL,
    `status`  tinyint(1) NULL DEFAULT NULL,
    UNIQUE INDEX `relation_unique` (`from_id`, `to_id`) USING BTREE COMMENT '关注的唯一索引'
) ENGINE = MyISAM
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Fixed;

-- ----------------------------
-- Table structure for tb_reply_comment
-- ----------------------------
DROP TABLE IF EXISTS `tb_reply_comment`;
CREATE TABLE `tb_reply_comment`
(
    `reply_comment_id` bigint(64)                                               NOT NULL,
    `comment_id`       bigint(64)                                               NULL DEFAULT NULL,
    `type`             tinyint(1)                                               NULL DEFAULT NULL,
    `content`          varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `user_id`          int(10)                                                  NULL DEFAULT NULL,
    `like_num`         int(10)                                                  NULL DEFAULT NULL,
    `status`           tinyint(1)                                               NULL DEFAULT NULL,
    `create_time`      datetime(0)                                              NULL DEFAULT NULL,
    PRIMARY KEY (`reply_comment_id`) USING BTREE,
    INDEX `comment_id_index` (`comment_id`) USING BTREE,
    INDEX `user_id_index` (`user_id`) USING BTREE
) ENGINE = MyISAM
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_reply_comment_like
-- ----------------------------
DROP TABLE IF EXISTS `tb_reply_comment_like`;
CREATE TABLE `tb_reply_comment_like`
(
    `user_id`     int(10)     NOT NULL,
    `comment_id`  bigint(64)  NULL DEFAULT NULL,
    `create_time` datetime(0) NULL DEFAULT NULL,
    UNIQUE INDEX `unique_user_reply_comment` (`user_id`, `comment_id`) USING BTREE COMMENT '用户和二级评论的唯一约束'
) ENGINE = MyISAM
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Fixed;

-- ----------------------------
-- Table structure for tb_sort
-- ----------------------------
DROP TABLE IF EXISTS `tb_sort`;
CREATE TABLE `tb_sort`
(
    `id`          int(10) UNSIGNED ZEROFILL                              NOT NULL AUTO_INCREMENT,
    `owner`       int(10)                                                NULL DEFAULT NULL,
    `title`       varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `cover`       char(255) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL,
    `status`      tinyint(1)                                             NULL DEFAULT NULL,
    `create_time` datetime(0)                                            NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM
  AUTO_INCREMENT = 11
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_tag
-- ----------------------------
DROP TABLE IF EXISTS `tb_tag`;
CREATE TABLE `tb_tag`
(
    `id`          int(5) UNSIGNED ZEROFILL                               NOT NULL AUTO_INCREMENT,
    `content`     varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `create_time` datetime(0)                                            NULL DEFAULT NULL,
    `status`      tinyint(1)                                             NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`
(
    `id`                int(10) UNSIGNED ZEROFILL                              NOT NULL AUTO_INCREMENT,
    `username`          char(30) CHARACTER SET utf8 COLLATE utf8_general_ci    NOT NULL,
    `passwd`            char(32) CHARACTER SET utf8 COLLATE utf8_general_ci    NOT NULL,
    `nickname`          varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `avatar`            char(255) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL,
    `email`             char(50) CHARACTER SET utf8 COLLATE utf8_general_ci    NOT NULL,
    `role`              tinyint(1)                                             NULL DEFAULT NULL,
    `status`            tinyint(1)                                             NULL DEFAULT NULL,
    `registration_time` datetime(0)                                            NULL DEFAULT NULL,
    `last_login_time`   datetime(0)                                            NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `unique_username` (`username`) USING BTREE COMMENT '用户名唯一索引',
    UNIQUE INDEX `unique_email` (`email`) USING BTREE COMMENT '邮箱唯一索引'
) ENGINE = MyISAM
  AUTO_INCREMENT = 10009
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
