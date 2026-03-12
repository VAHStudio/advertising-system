# 数据库设计文档

## 数据库概览

- **数据库名**: `outdoor_saas`
- **字符集**: `utf8mb4`
- **排序规则**: `utf8mb4_unicode_ci`
- **引擎**: `InnoDB`

---

## 核心表结构

### 1. 用户表 (users)

存储系统用户信息。

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL COMMENT '角色(ADMIN/SALES/MEDIA/ENGINEERING/FINANCE/USER)',
    status TINYINT DEFAULT 1 COMMENT '状态(1启用,0禁用)',
    last_login_time DATETIME COMMENT '最后登录时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB COMMENT='用户表';
```

**索引**:
- `uk_username`: 用户名唯一索引

---

### 2. 社区表 (community)

存储社区/小区信息。

```sql
CREATE TABLE community (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '社区名称',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区县',
    address VARCHAR(255) COMMENT '详细地址',
    households INT COMMENT '总户数',
    property_type VARCHAR(20) COMMENT '物业类型(住宅/商业/混合)',
    price_level VARCHAR(10) COMMENT '房价等级(高/中/低)',
    property_company VARCHAR(100) COMMENT '物业公司',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    remark TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_city (city),
    INDEX idx_district (district),
    INDEX idx_property_type (property_type),
    INDEX idx_price_level (price_level),
    FULLTEXT INDEX ft_name (name)
) ENGINE=InnoDB COMMENT='社区表';
```

**索引**:
- `idx_city`: 城市索引
- `idx_district`: 区县索引
- `idx_property_type`: 物业类型索引
- `idx_price_level`: 价格等级索引
- `ft_name`: 名称全文索引

---

### 3. 道闸表 (barrier_gate)

存储道闸设备信息。

```sql
CREATE TABLE barrier_gate (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_no VARCHAR(50) NOT NULL COMMENT '设备编号',
    community_id BIGINT NOT NULL COMMENT '所属社区ID',
    position VARCHAR(20) COMMENT '位置(入口/出口)',
    screen_size VARCHAR(20) COMMENT '屏幕尺寸',
    resolution VARCHAR(20) COMMENT '分辨率',
    status VARCHAR(20) DEFAULT '正常' COMMENT '状态',
    install_date DATE COMMENT '安装日期',
    remark TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_device_no (device_no),
    INDEX idx_community_id (community_id),
    INDEX idx_position (position),
    INDEX idx_status (status),
    FOREIGN KEY (community_id) REFERENCES community(id)
) ENGINE=InnoDB COMMENT='道闸表';
```

**索引**:
- `uk_device_no`: 设备编号唯一
- `idx_community_id`: 社区ID索引
- `idx_position`: 位置索引
- `idx_status`: 状态索引

---

### 4. 框架表 (frame)

存储电梯框架信息。

```sql
CREATE TABLE frame (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    frame_no VARCHAR(50) NOT NULL COMMENT '框架编号',
    community_id BIGINT NOT NULL COMMENT '所属社区ID',
    building_no VARCHAR(20) COMMENT '楼栋号',
    unit_no VARCHAR(20) COMMENT '单元号',
    elevator_no VARCHAR(20) COMMENT '电梯号',
    direction VARCHAR(10) COMMENT '朝向(东/南/西/北)',
    inner_position VARCHAR(20) COMMENT '内部位置(轿厢内/厅门)',
    size VARCHAR(30) COMMENT '尺寸',
    status VARCHAR(20) DEFAULT '正常' COMMENT '状态',
    install_date DATE COMMENT '安装日期',
    remark TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_frame_no (frame_no),
    INDEX idx_community_id (community_id),
    INDEX idx_direction (direction),
    INDEX idx_inner_position (inner_position),
    INDEX idx_status (status),
    FOREIGN KEY (community_id) REFERENCES community(id)
) ENGINE=InnoDB COMMENT='框架表';
```

**索引**:
- `uk_frame_no`: 框架编号唯一
- `idx_community_id`: 社区ID索引
- `idx_direction`: 朝向索引
- `idx_inner_position`: 内部位置索引

---

### 5. 投放计划表 (plan)

存储广告投放计划信息。

```sql
CREATE TABLE plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_no VARCHAR(50) NOT NULL COMMENT '计划编号',
    name VARCHAR(100) NOT NULL COMMENT '计划名称',
    customer_name VARCHAR(100) COMMENT '客户名称',
    sales_type VARCHAR(20) COMMENT '销售类型(直客/代理)',
    budget DECIMAL(15,2) COMMENT '预算金额',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    release_status VARCHAR(20) DEFAULT '未投放' COMMENT '投放状态',
    detail_release_status VARCHAR(20) COMMENT '明细投放状态',
    salesman VARCHAR(50) COMMENT '销售人员',
    designer VARCHAR(50) COMMENT '设计师',
    remark TEXT COMMENT '备注',
    created_by BIGINT COMMENT '创建人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_plan_no (plan_no),
    INDEX idx_customer_name (customer_name),
    INDEX idx_sales_type (sales_type),
    INDEX idx_release_status (release_status),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB COMMENT='投放计划表';
```

**索引**:
- `uk_plan_no`: 计划编号唯一
- `idx_customer_name`: 客户名称索引
- `idx_sales_type`: 销售类型索引
- `idx_release_status`: 投放状态索引
- `idx_start_date/end_date`: 日期范围索引
- `idx_created_at`: 创建时间索引

---

### 6. 计划-社区关联表 (plan_community)

存储计划与社区的关联关系。

```sql
CREATE TABLE plan_community (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NOT NULL COMMENT '计划ID',
    community_id BIGINT NOT NULL COMMENT '社区ID',
    quantity INT DEFAULT 1 COMMENT '数量',
    price DECIMAL(10,2) COMMENT '单价',
    remark TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_plan_community (plan_id, community_id),
    INDEX idx_plan_id (plan_id),
    INDEX idx_community_id (community_id),
    FOREIGN KEY (plan_id) REFERENCES plan(id) ON DELETE CASCADE,
    FOREIGN KEY (community_id) REFERENCES community(id)
) ENGINE=InnoDB COMMENT='计划-社区关联表';
```

---

### 7. 计划-道闸关联表 (plan_barrier)

存储计划与道闸的关联关系。

```sql
CREATE TABLE plan_barrier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NOT NULL COMMENT '计划ID',
    barrier_id BIGINT NOT NULL COMMENT '道闸ID',
    quantity INT DEFAULT 1 COMMENT '数量',
    price DECIMAL(10,2) COMMENT '单价',
    release_date DATE COMMENT '投放日期',
    release_status VARCHAR(20) DEFAULT '未投放' COMMENT '投放状态',
    remark TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_plan_barrier (plan_id, barrier_id),
    INDEX idx_plan_id (plan_id),
    INDEX idx_barrier_id (barrier_id),
    FOREIGN KEY (plan_id) REFERENCES plan(id) ON DELETE CASCADE,
    FOREIGN KEY (barrier_id) REFERENCES barrier_gate(id)
) ENGINE=InnoDB COMMENT='计划-道闸关联表';
```

---

### 8. 计划-框架关联表 (plan_frame)

存储计划与框架的关联关系。

```sql
CREATE TABLE plan_frame (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    plan_id BIGINT NOT NULL COMMENT '计划ID',
    frame_id BIGINT NOT NULL COMMENT '框架ID',
    quantity INT DEFAULT 1 COMMENT '数量',
    price DECIMAL(10,2) COMMENT '单价',
    release_date DATE COMMENT '投放日期',
    release_status VARCHAR(20) DEFAULT '未投放' COMMENT '投放状态',
    remark TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_plan_frame (plan_id, frame_id),
    INDEX idx_plan_id (plan_id),
    INDEX idx_frame_id (frame_id),
    FOREIGN KEY (plan_id) REFERENCES plan(id) ON DELETE CASCADE,
    FOREIGN KEY (frame_id) REFERENCES frame(id)
) ENGINE=InnoDB COMMENT='计划-框架关联表';
```

---

### 9. AI 对话表 (ai_conversation)

存储 AI 对话历史。

```sql
CREATE TABLE ai_conversation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id VARCHAR(64) NOT NULL COMMENT '对话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) COMMENT '对话标题',
    messages JSON COMMENT '消息记录(JSON格式)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_conversation_id (conversation_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='AI对话表';
```

---

## ER 图

```
┌──────────────────────────────────────────────────────────────────┐
│                           用户模块                                │
│  ┌──────────────┐                                                │
│  │    users     │                                                │
│  ├──────────────┤                                                │
│  │ id (PK)      │                                                │
│  │ username     │                                                │
│  │ password     │                                                │
│  │ role         │                                                │
│  └──────┬───────┘                                                │
│         │ 1:N                                                    │
│         ▼                                                        │
│  ┌──────────────┐                                                │
│  │ai_conversation│                                               │
│  └──────────────┘                                                │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                          资源管理模块                             │
│                                                                  │
│  ┌──────────────┐       ┌──────────────┐       ┌──────────────┐ │
│  │  community   │◀──────┤barrier_gate  │       │    frame     │ │
│  │   (社区)     │       │   (道闸)     │       │   (框架)     │ │
│  ├──────────────┤       ├──────────────┤       ├──────────────┤ │
│  │ id (PK)      │       │ id (PK)      │       │ id (PK)      │ │
│  │ name         │       │ device_no    │       │ frame_no     │ │
│  │ city         │       │ community_id │◀──────┤ community_id │ │
│  │ district     │       └──────────────┘       └──────────────┘ │
│  │ households   │            ▲                      ▲          │
│  └──────────────┘            │                      │          │
│         ▲                    │                      │          │
└─────────┼────────────────────┼──────────────────────┼──────────┘
          │                    │                      │
          │            ┌───────┴──────────────────────┘
          │            │
┌─────────┼────────────┼──────────────────────────────────────────┐
│         │            │          投放计划模块                      │
│         │       ┌────┴────┐                                     │
│         │       │  plan   │                                     │
│         │       │(投放计划)│                                     │
│         │       ├─────────┤                                     │
│         │       │ id (PK) │                                     │
│         │       │ plan_no │                                     │
│         │       │ name    │                                     │
│         │       │ budget  │                                     │
│         │       └────┬────┘                                     │
│         │            │ 1:N                                       │
│         │     ┌──────┴──────┐                                    │
│         │     │             │                                    │
│         │     ▼             ▼                                    │
│  ┌──────┴───────┐    ┌─────────────┐    ┌─────────────┐         │
│  │plan_community│    │ plan_barrier│    │ plan_frame  │         │
│  │(计划-社区)   │    │(计划-道闸)  │    │(计划-框架)  │         │
│  └──────────────┘    └─────────────┘    └─────────────┘         │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 数据字典

### 用户角色 (role)

| 值 | 说明 |
|----|------|
| ADMIN | 管理员 |
| SALES | 销售 |
| MEDIA | 媒介 |
| ENGINEERING | 工程 |
| FINANCE | 财务 |
| USER | 普通用户 |

### 销售类型 (sales_type)

| 值 | 说明 |
|----|------|
| 直客 | 直接客户 |
| 代理 | 代理商 |

### 投放状态 (release_status)

| 值 | 说明 |
|----|------|
| 未投放 | 尚未开始投放 |
| 投放中 | 正在投放 |
| 已暂停 | 暂停投放 |
| 已结束 | 投放结束 |

### 明细投放状态 (detail_release_status)

| 值 | 说明 |
|----|------|
| 未投放 | 无任何资源投放 |
| 部分投放 | 部分资源已投放 |
| 全部投放 | 所有资源已投放 |

### 物业类型 (property_type)

| 值 | 说明 |
|----|------|
| 住宅 | 住宅小区 |
| 商业 | 商业楼宇 |
| 混合 | 商住混合 |
| 别墅 | 别墅区 |
| 公寓 | 公寓楼 |

### 价格等级 (price_level)

| 值 | 说明 |
|----|------|
| 高 | 高端社区 |
| 中 | 中端社区 |
| 低 | 普通社区 |

### 道闸位置 (position)

| 值 | 说明 |
|----|------|
| 入口 | 停车场入口 |
| 出口 | 停车场出口 |

### 框架朝向 (direction)

| 值 | 说明 |
|----|------|
| 东 | 朝东 |
| 南 | 朝南 |
| 西 | 朝西 |
| 北 | 朝北 |

### 内部位置 (inner_position)

| 值 | 说明 |
|----|------|
| 轿厢内 | 电梯轿厢内部 |
| 厅门 | 电梯厅门 |
| 两者 | 轿厢和厅门都有 |

---

## 索引策略

### 1. 主键索引

所有表使用 `BIGINT` 自增主键，保证唯一性和查询效率。

### 2. 业务唯一索引

- `uk_username`: 用户名唯一
- `uk_device_no`: 设备编号唯一
- `uk_frame_no`: 框架编号唯一
- `uk_plan_no`: 计划编号唯一
- `uk_conversation_id`: 对话ID唯一

### 3. 外键索引

所有外键字段都建立索引，支持关联查询。

### 4. 查询索引

根据业务查询场景建立索引：
- 列表筛选字段
- 排序字段
- 全文搜索字段

### 5. 复合索引

- `uk_plan_community`: 计划+社区唯一
- `uk_plan_barrier`: 计划+道闸唯一
- `uk_plan_frame`: 计划+框架唯一

---

## 分区策略（未来）

对于大数据量表，建议采用分区：

```sql
-- 按日期范围分区
CREATE TABLE plan (
    ...
) PARTITION BY RANGE (YEAR(created_at)) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

---

## 数据归档（未来）

历史数据归档策略：

1. **归档周期**: 3年以上的历史数据
2. **归档表命名**: `{table_name}_archive_{year}`
3. **归档方式**: 定时任务迁移
4. **查询支持**: 联合查询归档表

---

## 数据库迁移

使用 Flyway 管理数据库版本：

```
db/migration/
├── V1__init.sql                          # 初始化
├── V2__create_base_tables.sql            # 核心表
├── V3__placeholder.sql
├── V4__placeholder.sql
├── V5__create_user_table.sql             # 用户表
├── V6__add_filter_indexes.sql            # 筛选索引
└── V7__add_missing_columns_to_relation_tables.sql  # 关联表补充
```

---

## 性能优化建议

### 1. 查询优化

- 使用 EXPLAIN 分析查询
- 避免 SELECT *
- 大表使用覆盖索引

### 2. 写入优化

- 批量插入代替单条
- 合理使用事务
- 避免长事务

### 3. 连接优化

- 使用连接池
- 合理设置连接数
- 及时释放连接

### 4. 监控指标

- 慢查询日志
- 连接数监控
- 锁等待监控

---

## 备份策略

### 1. 全量备份

- 频率: 每天凌晨 2:00
- 保留: 30 天
- 方式: mysqldump

### 2. 增量备份

- 频率: 每 6 小时
- 保留: 7 天
- 方式: binlog

### 3. 备份验证

- 每周恢复测试
- 备份文件校验
