/*
Navicat MySQL Data Transfer

Source Server         : 3306
Source Server Version : 80019
Source Host           : localhost:3306
Source Database       : softwaremanage

Target Server Type    : MYSQL
Target Server Version : 80019
File Encoding         : 65001

Date: 2021-05-20 21:41:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for deathstatistics
-- ----------------------------
DROP TABLE IF EXISTS `deathstatistics`;
CREATE TABLE `deathstatistics` (
  `id` int NOT NULL AUTO_INCREMENT,
  `death_id` char(19) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '人员伤亡灾情码 12位地理位置信息编码+7位灾情信息码',
  `location` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `date` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '年-月-日 时:分:秒（24小时制）',
  `number` int DEFAULT NULL COMMENT '死亡人数',
  `reporting_unit` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '上报单位',
  `earthquakeId` char(26) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '震情编码',
  PRIMARY KEY (`id`),
  KEY `death_disaster` (`earthquakeId`),
  CONSTRAINT `death_disaster` FOREIGN KEY (`earthquakeId`) REFERENCES `disasterinfo` (`d_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of deathstatistics
-- ----------------------------
