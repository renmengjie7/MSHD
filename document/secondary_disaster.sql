

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for secondary_disaster
-- ----------------------------
DROP TABLE IF EXISTS `secondary_disaster`;
CREATE TABLE `secondary_disaster` (
  `id` int NOT NULL AUTO_INCREMENT,
  `secondary_id` char(19) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '次生灾情编码',
  `province` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `country` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `town` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `village` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `category` int DEFAULT '0' COMMENT '1崩塌 2滑坡 3泥石流 4岩溶塌陷 5地裂缝 6地面沉降 7其他  ',
  `type` int DEFAULT NULL COMMENT '小类 ',
  `status` int DEFAULT NULL COMMENT '破坏等级',
  `picture` blob,
  `reporting_unit` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '上报单位',
  `note` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '灾情描述',
  `earthquake_id` char(26) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '震情编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
