/*
Navicat MySQL Data Transfer

Source Server         : 3306
Source Server Version : 80019
Source Host           : localhost:3306
Source Database       : softwaremanage

Target Server Type    : MYSQL
Target Server Version : 80019
File Encoding         : 65001

Date: 2021-05-20 21:41:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for injurestatistics
-- ----------------------------
DROP TABLE IF EXISTS `injurestatistics`;
CREATE TABLE `injurestatistics` (
  `id` int NOT NULL AUTO_INCREMENT,
  `people_id` char(19) COLLATE utf8_bin NOT NULL COMMENT '受伤灾情编码',
  `location` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `date` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `number` int DEFAULT NULL COMMENT '受伤人数',
  `reporting_unit` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '上报单位',
  `earthquakeId` char(26) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '震情编码',
  PRIMARY KEY (`id`),
  KEY `injure_disaster` (`earthquakeId`),
  CONSTRAINT `injure_disaster` FOREIGN KEY (`earthquakeId`) REFERENCES `disasterinfo` (`d_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of injurestatistics
-- ----------------------------
