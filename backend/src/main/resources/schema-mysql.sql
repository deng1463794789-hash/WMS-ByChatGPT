-- ============================================
-- WMS 管理系统数据库初始化脚本
-- ============================================

-- 商品表
CREATE TABLE IF NOT EXISTS wms_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    unit VARCHAR(32) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    safe_stock INT NOT NULL DEFAULT 0,
    category_id BIGINT DEFAULT NULL,
    price DECIMAL(10, 2) DEFAULT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    status VARCHAR(16) DEFAULT 'active',
    version INT NOT NULL DEFAULT 0,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_sku (sku),
    INDEX idx_product_name (name),
    INDEX idx_product_category (category_id),
    INDEX idx_product_deleted (is_deleted)
);

-- 商品分类表
CREATE TABLE IF NOT EXISTS wms_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    sort INT NOT NULL DEFAULT 0,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category_parent (parent_id),
    INDEX idx_category_deleted (is_deleted)
);

-- 库存变动记录表
CREATE TABLE IF NOT EXISTS wms_stock_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    biz_type VARCHAR(32) NOT NULL,
    change_quantity INT NOT NULL,
    after_quantity INT NOT NULL,
    biz_no VARCHAR(64) DEFAULT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_stock_product (product_id),
    INDEX idx_stock_created (created_at),
    INDEX idx_stock_deleted (is_deleted),
    CONSTRAINT fk_wms_stock_record_product FOREIGN KEY (product_id) REFERENCES wms_product (id)
);

-- 系统用户表
CREATE TABLE IF NOT EXISTS wms_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    real_name VARCHAR(64) NOT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    email VARCHAR(64) DEFAULT NULL,
    avatar VARCHAR(255) DEFAULT NULL,
    role_id BIGINT DEFAULT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'active',
    login_fail_count INT NOT NULL DEFAULT 0,
    lock_until DATETIME NULL DEFAULT NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_username (username),
    INDEX idx_user_role (role_id),
    INDEX idx_user_deleted (is_deleted)
);

-- 系统角色表
CREATE TABLE IF NOT EXISTS wms_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'active',
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_role_code (code),
    INDEX idx_role_deleted (is_deleted)
);

-- 菜单/权限表
CREATE TABLE IF NOT EXISTS wms_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(64) NOT NULL,
    path VARCHAR(128) DEFAULT NULL,
    component VARCHAR(128) DEFAULT NULL,
    icon VARCHAR(64) DEFAULT NULL,
    sort INT NOT NULL DEFAULT 0,
    type VARCHAR(16) NOT NULL DEFAULT 'menu',
    permission VARCHAR(128) DEFAULT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'active',
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_menu_parent (parent_id),
    INDEX idx_menu_deleted (is_deleted)
);

-- 角色-菜单权限关联表
CREATE TABLE IF NOT EXISTS wms_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    INDEX idx_rm_role (role_id),
    INDEX idx_rm_menu (menu_id),
    CONSTRAINT fk_wms_role_menu_role FOREIGN KEY (role_id) REFERENCES wms_role (id),
    CONSTRAINT fk_wms_role_menu_menu FOREIGN KEY (menu_id) REFERENCES wms_menu (id)
);

-- 系统配置表
CREATE TABLE IF NOT EXISTS wms_system_setting (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    system_name VARCHAR(128) NOT NULL DEFAULT 'WMS管理系统',
    logo VARCHAR(255) DEFAULT NULL,
    password_min_length INT NOT NULL DEFAULT 6,
    login_max_retry INT NOT NULL DEFAULT 5,
    session_timeout INT NOT NULL DEFAULT 30,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 操作审计日志表
CREATE TABLE IF NOT EXISTS wms_audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT DEFAULT NULL,
    username VARCHAR(64) DEFAULT NULL,
    client_ip VARCHAR(64) DEFAULT NULL,
    http_method VARCHAR(8) DEFAULT NULL,
    uri VARCHAR(255) DEFAULT NULL,
    module VARCHAR(64) DEFAULT NULL,
    action VARCHAR(128) DEFAULT NULL,
    elapsed_ms INT DEFAULT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'success',
    error_msg VARCHAR(512) DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_created (created_at),
    INDEX idx_audit_module (module)
);

-- 仓库表
CREATE TABLE IF NOT EXISTS wms_warehouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    address VARCHAR(255) DEFAULT NULL,
    manager VARCHAR(64) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    area DECIMAL(10, 2) DEFAULT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'active',
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_warehouse_code (code),
    INDEX idx_warehouse_deleted (is_deleted)
);

-- 供应商表
CREATE TABLE IF NOT EXISTS wms_supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    contact VARCHAR(64) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    address VARCHAR(255) DEFAULT NULL,
    email VARCHAR(64) DEFAULT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'active',
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_supplier_code (code),
    INDEX idx_supplier_deleted (is_deleted)
);

-- 客户表
CREATE TABLE IF NOT EXISTS wms_customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    contact VARCHAR(64) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    address VARCHAR(255) DEFAULT NULL,
    email VARCHAR(64) DEFAULT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'active',
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_code (code),
    INDEX idx_customer_deleted (is_deleted)
);

-- 员工表
CREATE TABLE IF NOT EXISTS wms_employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    gender VARCHAR(8) NOT NULL DEFAULT 'male',
    phone VARCHAR(20) DEFAULT NULL,
    department VARCHAR(64) DEFAULT NULL,
    position VARCHAR(64) DEFAULT NULL,
    hire_date DATE DEFAULT NULL,
    salary DECIMAL(10, 2) DEFAULT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'active',
    remark VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_employee_code (code),
    INDEX idx_employee_department (department),
    INDEX idx_employee_deleted (is_deleted)
);

-- 报销表
CREATE TABLE IF NOT EXISTS wms_expense (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    expense_no VARCHAR(64) NOT NULL UNIQUE,
    type VARCHAR(32) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    applicant VARCHAR(64) NOT NULL,
    department VARCHAR(64) DEFAULT NULL,
    expense_date DATE DEFAULT NULL,
    invoice_no VARCHAR(64) DEFAULT NULL,
    reason VARCHAR(255) DEFAULT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'pending',
    approver VARCHAR(64) DEFAULT NULL,
    approve_time TIMESTAMP NULL DEFAULT NULL,
    reject_reason VARCHAR(255) DEFAULT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_expense_no (expense_no),
    INDEX idx_expense_status (status),
    INDEX idx_expense_type (type),
    INDEX idx_expense_deleted (is_deleted)
);

-- 入库单主表
CREATE TABLE IF NOT EXISTS wms_inbound (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    inbound_no VARCHAR(64) NOT NULL UNIQUE,
    type VARCHAR(32) NOT NULL,
    product_count INT NOT NULL DEFAULT 0,
    total_quantity INT NOT NULL DEFAULT 0,
    operator VARCHAR(64) DEFAULT NULL,
    inbound_time DATETIME DEFAULT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'pending',
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_inbound_no (inbound_no),
    INDEX idx_inbound_time (inbound_time),
    INDEX idx_inbound_deleted (is_deleted)
);

-- 入库单明细表
CREATE TABLE IF NOT EXISTS wms_inbound_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    inbound_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(128) DEFAULT NULL,
    sku VARCHAR(64) DEFAULT NULL,
    quantity INT NOT NULL DEFAULT 0,
    price DECIMAL(10, 2) DEFAULT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_inbound_item_inbound (inbound_id),
    INDEX idx_inbound_item_product (product_id),
    INDEX idx_inbound_item_deleted (is_deleted),
    CONSTRAINT fk_wms_inbound_item_inbound FOREIGN KEY (inbound_id) REFERENCES wms_inbound (id)
);

-- 出库单主表
CREATE TABLE IF NOT EXISTS wms_outbound (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    outbound_no VARCHAR(64) NOT NULL UNIQUE,
    type VARCHAR(32) NOT NULL,
    product_count INT NOT NULL DEFAULT 0,
    total_quantity INT NOT NULL DEFAULT 0,
    operator VARCHAR(64) DEFAULT NULL,
    outbound_time DATETIME DEFAULT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'pending',
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_outbound_no (outbound_no),
    INDEX idx_outbound_time (outbound_time),
    INDEX idx_outbound_deleted (is_deleted)
);

-- 出库单明细表
CREATE TABLE IF NOT EXISTS wms_outbound_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    outbound_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(128) DEFAULT NULL,
    sku VARCHAR(64) DEFAULT NULL,
    quantity INT NOT NULL DEFAULT 0,
    price DECIMAL(10, 2) DEFAULT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    INDEX idx_outbound_item_outbound (outbound_id),
    INDEX idx_outbound_item_product (product_id),
    INDEX idx_outbound_item_deleted (is_deleted),
    CONSTRAINT fk_wms_outbound_item_outbound FOREIGN KEY (outbound_id) REFERENCES wms_outbound (id)
);

-- ============================================
-- 初始数据
-- ============================================

-- 初始管理员用户 (密码: admin123, BCrypt 加密)
INSERT IGNORE INTO wms_user (username, password, real_name, phone, email, role_id, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '系统管理员', '13600136001', 'admin@wms.com', 1, 'active');

-- 初始角色
INSERT IGNORE INTO wms_role (code, name, description) VALUES
('admin', '管理员', '系统管理员，拥有所有权限'),
('warehouse', '仓库管理员', '仓库管理操作权限'),
('operator', '操作员', '基础操作权限');

-- 初始菜单/权限
INSERT IGNORE INTO wms_menu (id, parent_id, name, path, component, icon, sort, type, permission) VALUES
(1, 0, '首页', '/dashboard', NULL, 'HomeFilled', 1, 'menu', 'dashboard'),
(2, 0, '商品管理', '/product', NULL, 'GoodsFilled', 2, 'menu', 'product'),
(3, 2, '商品列表', 'list', NULL, NULL, 1, 'menu', 'product:list'),
(4, 2, '商品详情', 'detail', NULL, NULL, 2, 'menu', 'product:detail'),
(5, 0, '库存管理', '/inventory', NULL, 'Box', 3, 'menu', 'inventory'),
(6, 5, '库存查询', 'stock', NULL, NULL, 1, 'menu', 'inventory:stock'),
(7, 5, '入库管理', 'inbound', NULL, NULL, 2, 'menu', 'inventory:inbound'),
(8, 5, '出库管理', 'outbound', NULL, NULL, 3, 'menu', 'inventory:outbound'),
(9, 0, '仓库管理', '/warehouse', NULL, 'OfficeBuilding', 4, 'menu', 'warehouse'),
(10, 0, '供应商管理', '/supplier', NULL, 'Van', 5, 'menu', 'supplier'),
(11, 0, '客户管理', '/customer', NULL, 'UserFilled', 6, 'menu', 'customer'),
(12, 0, '员工管理', '/employee', NULL, 'Avatar', 7, 'menu', 'employee'),
(13, 0, '报销管理', '/expense', NULL, 'Money', 8, 'menu', 'expense'),
(14, 0, '系统管理', '/system', NULL, 'Setting', 9, 'menu', 'system'),
(15, 14, '用户管理', 'user', NULL, NULL, 1, 'menu', 'system:user'),
(16, 14, '角色管理', 'role', NULL, NULL, 2, 'menu', 'system:role'),
(17, 14, '系统设置', 'setting', NULL, NULL, 3, 'menu', 'system:setting');

-- 管理员角色拥有所有菜单权限
INSERT IGNORE INTO wms_role_menu (role_id, menu_id)
SELECT 1, id FROM wms_menu;

-- 初始系统配置
INSERT IGNORE INTO wms_system_setting (system_name, password_min_length, login_max_retry, session_timeout) VALUES
('WMS管理系统', 6, 5, 30);
