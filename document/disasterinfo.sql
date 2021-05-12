/*
Navicat MySQL Data Transfer

Source Server         : 3306
Source Server Version : 80019
Source Host           : localhost:3306
Source Database       : softwaremanage

Target Server Type    : MYSQL
Target Server Version : 80019
File Encoding         : 65001

Date: 2021-05-12 23:37:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for disasterinfo
-- ----------------------------
DROP TABLE IF EXISTS `disasterinfo`;
CREATE TABLE `disasterinfo` (
  `id` char(19) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `d_id` char(26) COLLATE utf8_bin DEFAULT NULL COMMENT '基本灾情码  地理信息+年+月+日+时+分+秒',
  `province` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `country` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `town` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `village` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `date` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `location` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `longitude` float(100,2) DEFAULT NULL,
  `latitude` float(100,2) DEFAULT NULL,
  `depth` float(255,0) DEFAULT NULL,
  `magnitude` float(255,1) DEFAULT NULL,
  `picture` blob,
  `reporting_unit` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `unified_id` char(19) COLLATE utf8_bin DEFAULT NULL COMMENT '统一编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of disasterinfo
-- ----------------------------
INSERT INTO `disasterinfo` VALUES ('1', '11010100100120210421101557', '北京', '市辖区', '东城区', '东华门街道', '多福巷社区', '2021-04-21 10:15:57', '北京东城区', '117.00', '40.00', '15', '7.0', null, '北京地震局', null);
INSERT INTO `disasterinfo` VALUES ('2', '', '山西', '长治', '潞州区', '西街街道', '', '2021-04-21 22:16:40', '山西省长治市潞州区', '113.01', '35.50', '10', '5.5', null, '山西地震局', null);
