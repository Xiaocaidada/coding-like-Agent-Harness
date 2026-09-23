-- 本地文创市集摊位管理系统数据库表结构

-- 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    role VARCHAR(20) NOT NULL DEFAULT 'VENDOR' COMMENT '角色: ADMIN, VENDOR',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_role (role)
) COMMENT '用户表';

-- 摊主信息表
CREATE TABLE vendors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '摊主ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    business_name VARCHAR(100) COMMENT '摊位名称/店名',
    business_type VARCHAR(100) COMMENT '经营品类',
    business_desc TEXT COMMENT '经营描述',
    shop_address VARCHAR(200) COMMENT '店铺地址',
    contact_person VARCHAR(50) NOT NULL COMMENT '联系人',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    id_card_number VARCHAR(20) COMMENT '身份证号',
    bank_account VARCHAR(50) COMMENT '银行账号',
    bank_name VARCHAR(100) COMMENT '开户行',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_business_type (business_type),
    INDEX idx_status (status)
) COMMENT '摊主信息表';

-- 市集活动表
CREATE TABLE market_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID',
    event_name VARCHAR(100) NOT NULL COMMENT '活动名称',
    event_description TEXT COMMENT '活动描述',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    location VARCHAR(200) NOT NULL COMMENT '活动地点',
    poster_url VARCHAR(255) COMMENT '海报图片URL',
    status ENUM('NOT_STARTED', 'ACTIVE', 'ENDED', 'CANCELLED') NOT NULL DEFAULT 'NOT_STARTED' COMMENT '活动状态',
    max_vendors INT NOT NULL DEFAULT 0 COMMENT '最大摊位数',
    booth_price DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '摊位租金',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    INDEX idx_location (location)
) COMMENT '市集活动表';

-- 摊位表
CREATE TABLE booths (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '摊位ID',
    market_event_id BIGINT NOT NULL COMMENT '关联活动ID',
    booth_number VARCHAR(20) NOT NULL COMMENT '摊位编号',
    area VARCHAR(50) NOT NULL COMMENT '所在区域',
    area_code VARCHAR(20) NOT NULL COMMENT '区域代码',
    booth_area DECIMAL(8,2) NOT NULL COMMENT '摊位面积(平方米)',
    booth_price DECIMAL(10,2) NOT NULL COMMENT '摊位价格',
    description TEXT COMMENT '摊位描述',
    location_desc VARCHAR(200) COMMENT '位置描述',
    status ENUM('AVAILABLE', 'ASSIGNED', 'DISABLED') NOT NULL DEFAULT 'AVAILABLE' COMMENT '摊位状态',
    image_url VARCHAR(255) COMMENT '摊位图片URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (market_event_id) REFERENCES market_events(id),
    UNIQUE KEY uk_market_event_booth_number (market_event_id, booth_number),
    INDEX idx_market_event_id (market_event_id),
    INDEX idx_status (status),
    INDEX idx_area (area)
) COMMENT '摊位表';

-- 摊位申请表
CREATE TABLE applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    market_event_id BIGINT NOT NULL COMMENT '关联活动ID',
    vendor_id BIGINT NOT NULL COMMENT '摊主ID',
    application_status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
    business_type VARCHAR(100) COMMENT '期望经营品类',
    expectation_area VARCHAR(50) COMMENT '期望摊位区域',
    description TEXT COMMENT '申请描述',
    vendor_comment TEXT COMMENT '摊主备注',
    admin_comment TEXT COMMENT '管理员备注',
    rejection_reason TEXT COMMENT '驳回原因',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (market_event_id) REFERENCES market_events(id),
    FOREIGN KEY (vendor_id) REFERENCES vendors(id),
    INDEX idx_market_event_id (market_event_id),
    INDEX idx_vendor_id (vendor_id),
    INDEX idx_application_status (application_status),
    INDEX idx_create_time (create_time)
) COMMENT '摊位申请表';

-- 订单表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_number VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    vendor_id BIGINT NOT NULL COMMENT '摊主ID',
    market_event_id BIGINT NOT NULL COMMENT '活动ID',
    booth_id BIGINT NOT NULL COMMENT '摊位ID',
    order_amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    order_status ENUM('PENDING_PAYMENT', 'CONFIRMED', 'COMPLETED', 'CANCELLED', 'REFUNDED') NOT NULL DEFAULT 'PENDING_PAYMENT' COMMENT '订单状态',
    payment_time DATETIME COMMENT '支付时间',
    payment_method VARCHAR(20) COMMENT '支付方式',
    transaction_id VARCHAR(100) COMMENT '交易ID',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (vendor_id) REFERENCES vendors(id),
    FOREIGN KEY (market_event_id) REFERENCES market_events(id),
    FOREIGN KEY (booth_id) REFERENCES booths(id),
    UNIQUE KEY uk_order_number (order_number),
    INDEX idx_vendor_id (vendor_id),
    INDEX idx_market_event_id (market_event_id),
    INDEX idx_booth_id (booth_id),
    INDEX idx_order_status (order_status),
    INDEX idx_create_time (create_time)
) COMMENT '订单表';

-- 客流量统计表
CREATE TABLE visitor_statistics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    market_event_id BIGINT NOT NULL COMMENT '活动ID',
    statistics_date DATE NOT NULL COMMENT '统计日期',
    visitor_count INT NOT NULL COMMENT '到访人数',
    peak_hour INT COMMENT '高峰时段(小时)',
    male_count INT DEFAULT 0 COMMENT '男性人数',
    female_count INT DEFAULT 0 COMMENT '女性人数',
    child_count INT DEFAULT 0 COMMENT '儿童人数',
    adult_count INT DEFAULT 0 COMMENT '成人人数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (market_event_id) REFERENCES market_events(id),
    UNIQUE KEY uk_market_event_date (market_event_id, statistics_date),
    INDEX idx_market_event_id (market_event_id),
    INDEX idx_statistics_date (statistics_date)
) COMMENT '客流量统计表';

-- 系统配置表
CREATE TABLE system_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_desc VARCHAR(255) COMMENT '配置描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '系统配置表';

-- 操作日志表
CREATE TABLE operation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    user_name VARCHAR(50) COMMENT '用户名',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operation_desc TEXT COMMENT '操作描述',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_create_time (create_time)
) COMMENT '操作日志表';

-- 插入默认管理员用户
INSERT INTO users (username, password, phone, role) 
VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '13800138000', 'ADMIN');

-- 插入默认系统配置
INSERT INTO system_configs (config_key, config_value, config_desc) VALUES
('max_vendors_per_event', '100', '每个活动最大摊位数'),
('booth_assign_auto_confirm', 'false', '摊位分配后是否自动确认订单'),
('visitor_statistics_auto_create', 'true', '是否自动创建客流统计记录');

-- 创建一些示例数据
INSERT INTO market_events (event_name, event_description, start_time, end_time, location, poster_url, status, max_vendors, booth_price)
VALUES 
('春季文创市集', '展示春季文创产品，包括手工艺品、艺术品等', '2024-03-15 09:00:00', '2024-03-17 18:00:00', '市中心文化广场', '/posters/spring-event.jpg', 'NOT_STARTED', 50, 200.00),
('周末创意市集', '展示各种创意产品和小商品', '2024-03-22 10:00:00', '2024-03-22 18:00:00', '步行街中心广场', '/posters/weekend-event.jpg', 'NOT_STARTED', 30, 150.00);

INSERT INTO booths (market_event_id, booth_number, area, area_code, booth_area, booth_price, description, location_desc)
VALUES 
(1, 'A01', '文创区', 'A', 9.0, 200.00, '文创区1号摊位', '东区A区'),
(1, 'A02', '文创区', 'A', 9.0, 200.00, '文创区2号摊位', '东区A区'),
(1, 'B01', '美食区', 'B', 12.0, 300.00, '美食区1号摊位', '东区B区'),
(1, 'B02', '美食区', 'B', 12.0, 300.00, '美食区2号摊位', '东区B区'),
(2, 'C01', '创意区', 'C', 8.0, 150.00, '创意区1号摊位', '中心广场'),
(2, 'C02', '创意区', 'C', 8.0, 150.00, '创意区2号摊位', '中心广场');

-- 为应用创建必要的索引
CREATE INDEX idx_applications_status ON applications(application_status);
CREATE INDEX idx_orders_amount ON orders(order_amount);