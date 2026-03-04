-- ============================================================
-- 表：sys_users
-- 作用：存储登录账号与基础用户信息
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_users (
    -- 自增主键
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    -- 唯一登录名
    username VARCHAR(64) NOT NULL UNIQUE,
    -- 密码（建议加密后存储）
    password VARCHAR(255) NOT NULL,
    -- 昵称
    nickname VARCHAR(64),
    -- 用户状态：1 启用，0 禁用
    status TINYINT NOT NULL DEFAULT 1,
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 表：sys_roles
-- 作用：RBAC 角色字典
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    -- 后端鉴权使用的角色编码
    role_code VARCHAR(64) NOT NULL UNIQUE,
    -- 角色名称
    role_name VARCHAR(64) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 表：sys_user_roles
-- 作用：用户与角色的多对多关系表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    KEY idx_sur_role_id (role_id),
    CONSTRAINT fk_sur_user FOREIGN KEY (user_id) REFERENCES sys_users(id),
    CONSTRAINT fk_sur_role FOREIGN KEY (role_id) REFERENCES sys_roles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 表：devices
-- 作用：设备主数据
-- ============================================================
CREATE TABLE IF NOT EXISTS devices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    -- 设备业务唯一编码
    device_code VARCHAR(64) NOT NULL UNIQUE,
    device_name VARCHAR(128) NOT NULL,
    device_type VARCHAR(64) NOT NULL,
    -- 设备状态：ONLINE/OFFLINE/FAULT
    status VARCHAR(32) NOT NULL DEFAULT 'OFFLINE',
    location VARCHAR(255),
    description VARCHAR(512),
    -- 最近心跳时间
    last_heartbeat DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- 常用筛选字段索引
    KEY idx_device_type (device_type),
    KEY idx_device_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 表：work_orders
-- 作用：工单生命周期管理
-- ============================================================
CREATE TABLE IF NOT EXISTS work_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    -- 对外展示工单号
    order_no VARCHAR(64) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    -- 可选关联设备/处理人
    device_id BIGINT,
    assignee_id BIGINT,
    -- 优先级（1~5）
    priority TINYINT NOT NULL DEFAULT 3,
    -- 状态：PENDING/PROCESSING/COMPLETED
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    -- 进度：0~100
    progress TINYINT NOT NULL DEFAULT 0,
    -- 乐观锁版本号（并发更新保护）
    version INT NOT NULL DEFAULT 0,
    completed_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_work_order_status (status),
    KEY idx_work_order_device_id (device_id),
    KEY idx_work_order_assignee_id (assignee_id),
    CONSTRAINT fk_work_order_device FOREIGN KEY (device_id) REFERENCES devices(id),
    CONSTRAINT fk_work_order_assignee FOREIGN KEY (assignee_id) REFERENCES sys_users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
