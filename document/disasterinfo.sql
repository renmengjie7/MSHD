/*
Navicat MySQL Data Transfer

Source Server         : 3306
Source Server Version : 80019
Source Host           : localhost:3306
Source Database       : softwaremanage

Target Server Type    : MYSQL
Target Server Version : 80019
File Encoding         : 65001

Date: 2021-05-11 20:26:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for disasterinfo
-- ----------------------------
DROP TABLE IF EXISTS `disasterinfo`;
CREATE TABLE `disasterinfo` (
  `id`int auto_increment NOT NULL,
  `unified_id` char(19) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '统一编码',
  `d_id` char(26) COLLATE utf8_bin DEFAULT NULL COMMENT '基本灾情码  地理信息+年+月+日+时+分+秒',
  `province` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `country` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `town` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `village` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `date` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `location` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `longitude` float(100,0) DEFAULT NULL,
  `latitude` float(100,0) DEFAULT NULL,
  `depth` float(255,0) DEFAULT NULL,
  `magnitude` float(255,0) DEFAULT NULL,
  `picture` blob,
  `reporting_unit` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of disasterinfo
-- ----------------------------
INSERT INTO `disasterinfo` VALUES (1,null, null, '北京市', '市辖区', '东城区', '东华门街道', '多福巷社区居委会', '2021-04-21 10:15:57', '北京东城区', '117', '40', '15', '7', null, '北京地震局');
