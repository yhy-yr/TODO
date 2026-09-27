/*
 Navicat Premium Dump SQL

 Source Server         : 本地MySQL
 Source Server Type    : MySQL
 Source Server Version : 90100 (9.1.0)
 Source Host           : localhost:3306
 Source Schema         : todo_db

 Target Server Type    : MySQL
 Target Server Version : 90100 (9.1.0)
 File Encoding         : 65001

 Date: 27/09/2026 12:43:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for flyway_schema_history
-- ----------------------------
DROP TABLE IF EXISTS `flyway_schema_history`;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of flyway_schema_history
-- ----------------------------
BEGIN;
INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES (1, '1', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>', NULL, 'root', '2026-09-13 12:59:41', 0, 1);
INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES (2, '2', 'add priority to todo', 'SQL', 'V2__add_priority_to_todo.sql', -740800520, 'root', '2026-09-13 13:16:25', 26, 1);
COMMIT;

-- ----------------------------
-- Table structure for todo
-- ----------------------------
DROP TABLE IF EXISTS `todo`;
CREATE TABLE `todo` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `done` bit(1) DEFAULT NULL,
  `title` varchar(50) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `priority` varchar(20) NOT NULL DEFAULT 'MEDIUM',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of todo
-- ----------------------------
BEGIN;
INSERT INTO `todo` (`id`, `done`, `title`, `created_at`, `updated_at`, `priority`) VALUES (1, b'1', '新的标题', NULL, NULL, 'MEDIUM');
INSERT INTO `todo` (`id`, `done`, `title`, `created_at`, `updated_at`, `priority`) VALUES (2, b'1', '写周报', NULL, NULL, 'MEDIUM');
INSERT INTO `todo` (`id`, `done`, `title`, `created_at`, `updated_at`, `priority`) VALUES (3, b'1', '健身一小时', NULL, NULL, 'MEDIUM');
INSERT INTO `todo` (`id`, `done`, `title`, `created_at`, `updated_at`, `priority`) VALUES (6, b'1', '写周报', NULL, NULL, 'MEDIUM');
INSERT INTO `todo` (`id`, `done`, `title`, `created_at`, `updated_at`, `priority`) VALUES (7, b'0', '买菜', NULL, NULL, 'MEDIUM');
INSERT INTO `todo` (`id`, `done`, `title`, `created_at`, `updated_at`, `priority`) VALUES (10, b'0', '买菜', NULL, NULL, 'MEDIUM');
INSERT INTO `todo` (`id`, `done`, `title`, `created_at`, `updated_at`, `priority`) VALUES (11, b'1', '写周报', NULL, NULL, 'MEDIUM');
INSERT INTO `todo` (`id`, `done`, `title`, `created_at`, `updated_at`, `priority`) VALUES (12, b'0', '买菜', NULL, NULL, 'MEDIUM');
INSERT INTO `todo` (`id`, `done`, `title`, `created_at`, `updated_at`, `priority`) VALUES (14, b'0', '买菜', NULL, NULL, 'MEDIUM');
INSERT INTO `todo` (`id`, `done`, `title`, `created_at`, `updated_at`, `priority`) VALUES (15, b'1', '写周报', NULL, NULL, 'MEDIUM');
INSERT INTO `todo` (`id`, `done`, `title`, `created_at`, `updated_at`, `priority`) VALUES (16, b'0', '重要任务', '2026-09-13 13:21:22.583893', '2026-09-13 13:21:22.583893', 'HIGH');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
