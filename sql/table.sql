-- ----------------------------
-- Table structure for sys_conf
-- ----------------------------
DROP TABLE IF EXISTS `sys_conf`;
CREATE TABLE `sys_conf`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `conf_key` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT 'key',
  `conf_val` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT 'war',
  `conf_remark` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `crt_date` datetime(0) NULL DEFAULT NULL,
  `upd_date` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `conf_key_uni`(`conf_key`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_conf
-- ----------------------------
INSERT INTO `sys_conf` VALUES (1, 'vpn_rss_repertory', '10', '默认库存数', NULL, NULL);
INSERT INTO `sys_conf` VALUES (2, 'vpn_rss_which', '8', '获取第几个节点', NULL, NULL);

-- ----------------------------
-- Table structure for vpn_user
-- ----------------------------
DROP TABLE IF EXISTS `vpn_user`;
CREATE TABLE `vpn_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `rss_url` varchar(500) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '订阅地址',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '状态（0-已注销，1-正常， 2-过期需购买）',
  `last_used_date` date NULL DEFAULT NULL COMMENT '上次使用时间',
  `last_check_date` date NULL DEFAULT NULL COMMENT '上次签到时间',
  `last_buy_time` date NULL DEFAULT NULL COMMENT '上次购买时间',
  `crt_date` datetime(0) NULL DEFAULT NULL,
  `upd_date` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vpn_user
-- ----------------------------

-- ----------------------------
-- Table structure for vpn_vmess
-- ----------------------------
DROP TABLE IF EXISTS `vpn_vmess`;
CREATE TABLE `vpn_vmess`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NULL DEFAULT NULL COMMENT 'vpn_user.id',
  `vmess_url` varchar(1000) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT 'vmess配置地址',
  `client_type` char(1) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '客户端类型(1-v2ray,2-kitsunebi,3-clash)',
  `crt_date` datetime(0) NULL DEFAULT NULL,
  `upd_date` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vpn_vmess
-- ----------------------------