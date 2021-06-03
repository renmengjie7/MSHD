
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for lifeline_disaster
-- ----------------------------
DROP TABLE IF EXISTS `lifeline_disaster`;
CREATE TABLE `lifeline_disaster` (
  `id` int NOT NULL AUTO_INCREMENT,
  `lifeline_id` char(19) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '生命线灾情编码',
  `province` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `country` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `town` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `village` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `category` int DEFAULT '0' COMMENT '1交通 2供水 3输油 4燃气 5电力 6通信 7水利 ',
  `grade` int unsigned DEFAULT NULL COMMENT '灾害程度',
  `type` int DEFAULT NULL COMMENT '小类 ',
  `picture` blob,
  `reporting_unit` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '上报单位',
  `note` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '灾情描述',
  `earthquake_id` char(26) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '震情编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
