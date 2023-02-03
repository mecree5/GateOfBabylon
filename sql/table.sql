CREATE TABLE `vpn_vmess` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT 'vpn_user.id',
  `vmess_url` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT 'vmess配置地址',
  `client_type` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '客户端类型',
  `crt_date` datetime DEFAULT NULL,
  `upd_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE `vpn_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `rss_url` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '订阅地址',
  `is_check` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '是否签到0-否1-是',
  `crt_date` datetime DEFAULT NULL,
  `upd_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;