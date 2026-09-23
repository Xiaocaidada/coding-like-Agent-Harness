-- 创建数据库
CREATE DATABASE IF NOT EXISTS craft_market_booth DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE craft_market_booth;

-- 用户表
CREATE TABLE `sys_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(100) NOT NULL COMMENT '密码',
    `real_name` varchar(50) COMMENT '真实姓名',
    `phone` varchar(20) COMMENT '手机号',
    `role` varchar(20) NOT NULL COMMENT '角色: ADMIN, VENDOR',
    `status` tinyint DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 市集活动表
CREATE TABLE `market_activity` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    `name` varchar(100) NOT NULL COMMENT '活动名称',
    `start_time` datetime NOT NULL COMMENT '开始时间',
    `end_time` datetime NOT NULL COMMENT '结束时间',
    `location` varchar(200) NOT NULL COMMENT '活动地点',
    `description` text COMMENT '活动简介',
    `poster_url` varchar(500) COMMENT '海报URL',
    `status` varchar(20) NOT NULL DEFAULT 'NOT_STARTED' COMMENT '状态: NOT_STARTED, IN_PROGRESS, ENDED',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='市集活动表';

-- 摊位表
CREATE TABLE `booth` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '摊位ID',
    `booth_number` varchar(50) NOT NULL COMMENT '摊位编号',
    `area` varchar(50) NOT NULL COMMENT '所在区域: CULTURAL_AREA, FOOD_AREA, HANDICRAFT_AREA, OTHER_AREA',
    `area_name` varchar(50) NOT NULL COMMENT '区域名称',
    `area_size` decimal(10,2) NOT NULL COMMENT '面积(平方米)',
    `rent_price` decimal(10,2) NOT NULL COMMENT '租金价格(元/天)',
    `status` varchar(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '状态: AVAILABLE, ALLOCATED, DISABLED',
    `activity_id` bigint NOT NULL COMMENT '所属活动ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_booth_number_activity` (`booth_number`, `activity_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_area_status` (`area`, `status`),
    CONSTRAINT `fk_booth_activity` FOREIGN KEY (`activity_id`) REFERENCES `market_activity` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='摊位表';

-- 摊主报名表
CREATE TABLE `vendor_application` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名ID',
    `activity_id` bigint NOT NULL COMMENT '活动ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `vendor_name` varchar(100) NOT NULL COMMENT '摊主姓名',
    `phone` varchar(20) NOT NULL COMMENT '联系电话',
    `business_type` varchar(50) NOT NULL COMMENT '经营品类',
    `description` text COMMENT '简介',
    `preferred_area` varchar(50) COMMENT '期望摊位区域',
    `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING, APPROVED, REJECTED',
    `reject_reason` text COMMENT '驳回原因',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_application_activity` FOREIGN KEY (`activity_id`) REFERENCES `market_activity` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_application_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='摊主报名表';

-- 订单表
CREATE TABLE `order_info` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` varchar(50) NOT NULL COMMENT '订单编号',
    `activity_id` bigint NOT NULL COMMENT '活动ID',
    `vendor_application_id` bigint NOT NULL COMMENT '摊主报名ID',
    `booth_id` bigint NOT NULL COMMENT '摊位ID',
    `amount` decimal(10,2) NOT NULL COMMENT '订单金额',
    `status` varchar(20) NOT NULL DEFAULT 'PENDING_PAYMENT' COMMENT '状态: PENDING_PAYMENT, PAID, CANCELLED',
    `payment_time` datetime COMMENT '支付时间',
    `cancel_time` datetime COMMENT '取消时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_vendor_application_id` (`vendor_application_id`),
    KEY `idx_booth_id` (`booth_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_order_activity` FOREIGN KEY (`activity_id`) REFERENCES `market_activity` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_order_application` FOREIGN KEY (`vendor_application_id`) REFERENCES `vendor_application` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_order_booth` FOREIGN KEY (`booth_id`) REFERENCES `booth` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 客流统计表
CREATE TABLE `visitor_statistics` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    `activity_id` bigint NOT NULL COMMENT '活动ID',
    `statistics_date` date NOT NULL COMMENT '统计日期',
    `visitor_count` int NOT NULL DEFAULT 0 COMMENT '到访人数',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_date` (`activity_id`, `statistics_date`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_statistics_date` (`statistics_date`),
    CONSTRAINT `fk_visitor_activity` FOREIGN KEY (`activity_id`) REFERENCES `market_activity` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客流统计表';

-- 插入测试数据
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVYITI', '系统管理员', 'ADMIN', 1),
('vendor1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVYITI', '张三', 'VENDOR', 1);

INSERT INTO `market_activity` (`name`, `start_time`, `end_time`, `location`, `description`, `status`) VALUES
('春季文创市集', '2024-03-15 09:00:00', '2024-03-17 18:00:00', '市中心文化广场', '春季文创市集活动', 'NOT_STARTED'),
('夏季美食节', '2024-06-01 10:00:00', '2024-06-03 20:00:00', '滨江公园', '夏季美食节活动', 'NOT_STARTED');

INSERT INTO `booth` (`booth_number`, `area`, `area_name`, `area_size`, `rent_price`, `status`, `activity_id`) VALUES
('A001', 'CULTURAL_AREA', '文创区', 4.5, 200.00, 'AVAILABLE', 1),
('A002', 'CULTURAL_AREA', '文创区', 4.5, 200.00, 'AVAILABLE', 1),
('B001', 'FOOD_AREA', '美食区', 6.0, 300.00, 'AVAILABLE', 1),
('B002', 'FOOD_AREA', '美食区', 6.0, 300.00, 'AVAILABLE', 1),
('C001', 'HANDICRAFT_AREA', '手工艺品区', 5.0, 250.00, 'AVAILABLE', 1),
('C002', 'HANDICRAFT_AREA', '手工艺品区', 5.0, 250.00, 'AVAILABLE', 1),
('A101', 'CULTURAL_AREA', '文创区', 4.5, 250.00, 'AVAILABLE', 2),
('B101', 'FOOD_AREA', '美食区', 6.0, 350.00, 'AVAILABLE', 2);