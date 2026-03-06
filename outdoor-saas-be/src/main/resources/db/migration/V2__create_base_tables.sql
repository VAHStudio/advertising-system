-- 创建基础业务表结构
-- 包含社区、道闸、框架、方案及关联表

-- ==================== community 表 ====================
CREATE TABLE IF NOT EXISTS community (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '社区ID',
    community_no VARCHAR(50) NOT NULL UNIQUE COMMENT '社区编号',
    building_name VARCHAR(100) COMMENT '楼栋名称',
    building_address VARCHAR(255) COMMENT '楼栋地址',
    coord_lat DECIMAL(10, 8) COMMENT '纬度',
    coord_lng DECIMAL(11, 8) COMMENT '经度',
    city VARCHAR(50) COMMENT '城市',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_community_city (city),
    INDEX idx_community_building_name (building_name),
    INDEX idx_community_community_no (community_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区信息表';

-- ==================== barrier_gate 表 ====================
CREATE TABLE IF NOT EXISTS barrier_gate (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '道闸ID',
    gate_no VARCHAR(50) NOT NULL UNIQUE COMMENT '道闸编号',
    community_id BIGINT NOT NULL COMMENT '社区ID',
    device_no VARCHAR(50) COMMENT '设备编号',
    door_location VARCHAR(100) COMMENT '门岗位置',
    device_position INT COMMENT '设备位置：1-入口，2-出口',
    screen_position INT COMMENT '屏幕位置：1-左侧，2-右侧',
    lightbox_direction INT COMMENT '灯箱方向：1-正面，2-反面',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (community_id) REFERENCES community(id) ON DELETE CASCADE,
    INDEX idx_barrier_community_id (community_id),
    INDEX idx_barrier_gate_no (gate_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='道闸设备表';

-- ==================== frame 表 ====================
CREATE TABLE IF NOT EXISTS frame (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '框架ID',
    frame_no VARCHAR(50) NOT NULL UNIQUE COMMENT '框架编号',
    community_id BIGINT NOT NULL COMMENT '社区ID',
    building VARCHAR(50) COMMENT '楼栋',
    unit VARCHAR(50) COMMENT '单元',
    elevator VARCHAR(50) COMMENT '电梯',
    inner_position INT COMMENT '内外位置：1-内，2-外',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (community_id) REFERENCES community(id) ON DELETE CASCADE,
    INDEX idx_frame_community_id (community_id),
    INDEX idx_frame_frame_no (frame_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='框架媒体表';

-- ==================== plan 表 ====================
CREATE TABLE IF NOT EXISTS plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '方案ID',
    plan_no VARCHAR(50) NOT NULL UNIQUE COMMENT '方案编号',
    plan_name VARCHAR(100) COMMENT '方案名称',
    customer VARCHAR(100) COMMENT '客户名称',
    release_date_begin DATE COMMENT '投放开始日期',
    release_date_end DATE COMMENT '投放结束日期',
    release_status INT DEFAULT 1 COMMENT '发布状态：1-未发布，2-已发布，3-执行中，4-已完成，5-已取消',
    sales_type INT COMMENT '销售类型：1-直销，2-代理',
    media_requirements TEXT COMMENT '媒体要求',
    media_type INT COMMENT '媒体类型：1-道闸，2-框架，3-社区',
    sales_person VARCHAR(50) COMMENT '销售人员',
    sample_image VARCHAR(500) COMMENT '样例图片URL',
    budget BIGINT COMMENT '预算（分）',
    estimated_reach INT COMMENT '预计触达人数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_plan_plan_no (plan_no),
    INDEX idx_plan_release_status (release_status),
    INDEX idx_plan_sales_type (sales_type),
    INDEX idx_plan_media_type (media_type),
    INDEX idx_plan_release_date_begin (release_date_begin),
    INDEX idx_plan_release_date_end (release_date_end),
    INDEX idx_plan_customer (customer),
    INDEX idx_plan_sales_person (sales_person)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投放方案表';

-- ==================== plan_community 关联表 ====================
CREATE TABLE IF NOT EXISTS plan_community (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    plan_id BIGINT NOT NULL COMMENT '方案ID',
    community_id BIGINT NOT NULL COMMENT '社区ID',
    release_date_begin DATE COMMENT '投放开始日期',
    release_date_end DATE COMMENT '投放结束日期',
    release_status INT DEFAULT 1 COMMENT '发布状态',
    barrier_required_qty INT DEFAULT 10 COMMENT '所需道闸数量',
    frame_required_qty INT DEFAULT 10 COMMENT '所需框架数量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (plan_id) REFERENCES plan(id) ON DELETE CASCADE,
    FOREIGN KEY (community_id) REFERENCES community(id) ON DELETE CASCADE,
    UNIQUE KEY uk_plan_community (plan_id, community_id),
    INDEX idx_pc_plan_id (plan_id),
    INDEX idx_pc_community_id (community_id),
    INDEX idx_pc_release_status (release_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='方案社区关联表';

-- ==================== plan_barrier 关联表 ====================
CREATE TABLE IF NOT EXISTS plan_barrier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    plan_id BIGINT NOT NULL COMMENT '方案ID',
    barrier_gate_id BIGINT NOT NULL COMMENT '道闸ID',
    plan_community_id BIGINT COMMENT '方案社区关联ID',
    release_date_begin DATE COMMENT '投放开始日期',
    release_date_end DATE COMMENT '投放结束日期',
    release_status INT DEFAULT 1 COMMENT '发布状态：1-待锁位，2-锁位，3-待刊发，4-刊发中，5-已刊发，6-已取消',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (plan_id) REFERENCES plan(id) ON DELETE CASCADE,
    FOREIGN KEY (barrier_gate_id) REFERENCES barrier_gate(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_community_id) REFERENCES plan_community(id) ON DELETE SET NULL,
    UNIQUE KEY uk_plan_barrier (plan_id, barrier_gate_id),
    INDEX idx_pb_plan_id (plan_id),
    INDEX idx_pb_barrier_id (barrier_gate_id),
    INDEX idx_pb_plan_community_id (plan_community_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='方案道闸关联表';

-- ==================== plan_frame 关联表 ====================
CREATE TABLE IF NOT EXISTS plan_frame (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    plan_id BIGINT NOT NULL COMMENT '方案ID',
    frame_id BIGINT NOT NULL COMMENT '框架ID',
    plan_community_id BIGINT COMMENT '方案社区关联ID',
    release_date_begin DATE COMMENT '投放开始日期',
    release_date_end DATE COMMENT '投放结束日期',
    release_status INT DEFAULT 1 COMMENT '发布状态：1-待锁位，2-锁位，3-待刊发，4-刊发中，5-已刊发，6-已取消',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (plan_id) REFERENCES plan(id) ON DELETE CASCADE,
    FOREIGN KEY (frame_id) REFERENCES frame(id) ON DELETE CASCADE,
    FOREIGN KEY (plan_community_id) REFERENCES plan_community(id) ON DELETE SET NULL,
    UNIQUE KEY uk_plan_frame (plan_id, frame_id),
    INDEX idx_pf_plan_id (plan_id),
    INDEX idx_pf_frame_id (frame_id),
    INDEX idx_pf_plan_community_id (plan_community_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='方案框架关联表';
