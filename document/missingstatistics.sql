/*
Navicat MySQL Data Transfer

Source Server         : 3306
Source Server Version : 80019
Source Host           : localhost:3306
Source Database       : softwaremanage

Target Server Type    : MYSQL
Target Server Version : 80019
File Encoding         : 65001

Date: 2021-05-20 21:41:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for missingstatistics
-- ----------------------------
DROP TABLE IF EXISTS `missingstatistics`;
CREATE TABLE `missingstatistics` (
  `id` int NOT NULL AUTO_INCREMENT,
  `people_id` char(19) COLLATE utf8_bin NOT NULL COMMENT '人员失踪灾情编码',
  `location` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `date` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `number` int DEFAULT NULL COMMENT '失踪人数',
  `reporting_unit` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '上报单位',
  `earthquakeId` char(26) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '震情编码',
  PRIMARY KEY (`id`),
  KEY `missing_disaster` (`earthquakeId`),
  CONSTRAINT `missing_disaster` FOREIGN KEY (`earthquakeId`) REFERENCES `disasterinfo` (`d_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of missingstatistics
-- ----------------------------
