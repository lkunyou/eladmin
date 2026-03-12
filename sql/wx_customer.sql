-- 微信客户表
CREATE TABLE IF NOT EXISTS `wx_customer` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `open_id` varchar(100) NOT NULL COMMENT '微信OpenID',
    `union_id` varchar(100) DEFAULT NULL COMMENT '微信UnionID',
    `nick_name` varchar(100) DEFAULT NULL COMMENT '昵称',
    `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
    `gender` tinyint(1) DEFAULT '0' COMMENT '性别 0-未知 1-男 2-女',
    `country` varchar(50) DEFAULT NULL COMMENT '国家',
    `province` varchar(50) DEFAULT NULL COMMENT '省份',
    `city` varchar(50) DEFAULT NULL COMMENT '城市',
    `language` varchar(20) DEFAULT NULL COMMENT '语言',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `session_key` varchar(100) DEFAULT NULL COMMENT '微信会话密钥',
    `status` tinyint(1) DEFAULT '1' COMMENT '状态 1-正常 0-禁用',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_open_id` (`open_id`),
    KEY `idx_union_id` (`union_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信客户表';
