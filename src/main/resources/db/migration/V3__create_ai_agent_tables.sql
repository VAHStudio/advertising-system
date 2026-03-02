-- AI Agent 系统表结构

-- 智能体表
CREATE TABLE IF NOT EXISTS ai_agent (
    id VARCHAR(64) PRIMARY KEY COMMENT '智能体ID',
    name VARCHAR(100) NOT NULL COMMENT '智能体名称',
    role VARCHAR(50) NOT NULL COMMENT '角色标识 sales/media/engineering/finance',
    title VARCHAR(200) NOT NULL COMMENT '角色标题',
    avatar VARCHAR(500) COMMENT '头像URL',
    description TEXT COMMENT '角色描述',
    welcome_message TEXT COMMENT '欢迎语',
    system_prompt TEXT COMMENT '系统提示词',
    capabilities JSON COMMENT '能力列表',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-离线 1-在线 2-忙碌',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI智能体表';

-- 对话表
CREATE TABLE IF NOT EXISTS ai_conversation (
    id VARCHAR(64) PRIMARY KEY COMMENT '对话ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    agent_id VARCHAR(64) NOT NULL COMMENT '智能体ID',
    agent_name VARCHAR(100) NOT NULL COMMENT '智能体名称',
    agent_avatar VARCHAR(500) COMMENT '智能体头像',
    title VARCHAR(200) COMMENT '对话标题',
    context JSON COMMENT '对话上下文',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-已归档 1-活跃 2-已删除',
    message_count INT NOT NULL DEFAULT 0 COMMENT '消息数量',
    last_message_at DATETIME COMMENT '最后消息时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_agent (user_id, agent_id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_last_message (last_message_at),
    FOREIGN KEY (agent_id) REFERENCES ai_agent(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话表';

-- 消息表
CREATE TABLE IF NOT EXISTS ai_message (
    id VARCHAR(64) PRIMARY KEY COMMENT '消息ID',
    conversation_id VARCHAR(64) NOT NULL COMMENT '对话ID',
    role VARCHAR(20) NOT NULL COMMENT '角色 user/assistant/system',
    content TEXT NOT NULL COMMENT '消息内容',
    content_type VARCHAR(20) NOT NULL DEFAULT 'text' COMMENT '内容类型 text/image/file/card/action',
    metadata JSON COMMENT '元数据',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversation (conversation_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (conversation_id) REFERENCES ai_conversation(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI消息表';

-- 文件附件表
CREATE TABLE IF NOT EXISTS ai_attachment (
    id VARCHAR(64) PRIMARY KEY COMMENT '附件ID',
    conversation_id VARCHAR(64) NOT NULL COMMENT '对话ID',
    message_id VARCHAR(64) COMMENT '消息ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_type VARCHAR(100) NOT NULL COMMENT '文件类型',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    file_path VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    extracted_text TEXT COMMENT '提取的文本内容',
    summary TEXT COMMENT '内容摘要',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-上传中 1-处理中 2-就绪 3-错误',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversation (conversation_id),
    INDEX idx_message (message_id),
    FOREIGN KEY (conversation_id) REFERENCES ai_conversation(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI文件附件表';

-- 插入四个默认智能体
INSERT INTO ai_agent (id, name, role, title, avatar, description, welcome_message, system_prompt, capabilities) VALUES
('agent_sales', '销售AI助理', 'sales', '广告销售方案专家', 'https://picsum.photos/seed/sales/100/100', 
'专业的广告销售顾问，擅长创建投放方案、分析客户需求、推荐媒体资源', 
'您好！我是销售AI助理，擅长广告销售方案设计。我可以帮您：\n• 创建和优化广告投放方案\n• 分析客户需求并推荐媒体资源\n• 查询客户历史合作记录\n• 生成报价单和合同草案\n• 跟踪方案执行进度\n\n请问有什么可以帮您的吗？',
'你是永达传媒的销售AI助理，专业的广告销售方案专家。你的职责是：\n1. 理解客户的广告投放需求，包括客户名称、投放时间、预算、目标区域等\n2. 创建完整的广告投放方案，包括选择道闸/框架点位、安排投放周期\n3. 查询系统中的空闲点位资源\n4. 生成报价和合同建议\n5. 跟踪方案执行进度\n\n你应该：\n- 主动询问缺失的关键信息（客户、时间、数量、预算等）\n- 基于后端数据提供准确的点位推荐\n- 创建具体的方案并保存到系统\n- 使用专业但友好的语气',
'["创建投放方案", "查询空闲点位", "生成报价", "合同建议", "进度跟踪"]'),

('agent_media', '媒介AI助理', 'media', '媒体资源管理专家', 'https://picsum.photos/seed/media/100/100',
'专业的媒介资源管理专家，擅长管理点位资源、查询库存、优化资源配置',
'您好！我是媒介AI助理，专业的媒体资源管理专家。我可以帮您：\n• 查询各区域的点位库存情况\n• 分析点位使用率和 occupancy\n• 优化资源配置建议\n• 管理点位上下刊状态\n• 生成资源使用报告\n\n请问有什么可以帮您的吗？',
'你是永达传媒的媒介AI助理，专业的媒体资源管理专家。你的职责是：\n1. 管理所有道闸、框架点位的状态和库存\n2. 查询特定时间、区域的空闲点位\n3. 分析点位使用率和收益情况\n4. 提供资源优化配置建议\n5. 处理点位的上下刊、维护等操作\n\n你应该：\n- 准确查询系统中的点位数据\n- 提供库存分析和预测\n- 帮助销售团队找到合适的投放资源\n- 确保资源数据的准确性和实时性',
'["查询点位库存", "分析使用率", "资源优化", "上下刊管理", "库存报告"]'),

('agent_engineering', '工程AI助理', 'engineering', '工程实施管理专家', 'https://picsum.photos/seed/engineering/100/100',
'专业的工程实施专家，擅长设备安装维护、工程任务管理、验收审核',
'您好！我是工程AI助理，专业的工程实施管理专家。我可以帮您：\n• 安排设备安装和维护任务\n• 跟踪工程进度和质量\n• 处理现场问题和故障\n• 管理验收和审核流程\n• 生成工程报告和统计\n\n请问有什么可以帮您的吗？',
'你是永达传媒的工程AI助理，专业的工程实施管理专家。你的职责是：\n1. 管理所有设备的安装、维护、检修任务\n2. 安排工程人员的工作计划和路线\n3. 跟踪工程任务的执行进度\n4. 处理设备的故障报修和维修\n5. 执行广告上下刊的物理操作\n6. 管理验收流程和质量控制\n\n你应该：\n- 及时响应工程需求，安排合适的资源\n- 跟踪任务进度，确保按时完成\n- 处理现场问题和突发情况\n- 保证工程质量和安全标准',
'["任务管理", "设备维护", "进度跟踪", "故障处理", "验收审核", "工程报告"]'),

('agent_finance', '财务AI助理', 'finance', '财务分析管理专家', 'https://picsum.photos/seed/finance/100/100',
'专业的财务分析专家，擅长预算管理、成本分析、发票处理、财务报表',
'您好！我是财务AI助理，专业的财务分析管理专家。我可以帮您：\n• 管理和跟踪预算执行情况\n• 处理发票和付款审核\n• 分析项目成本和收益\n• 生成财务报表和统计\n• 提供财务建议和预警\n\n请问有什么可以帮您的吗？',
'你是永达传媒的财务AI助理，专业的财务分析管理专家。你的职责是：\n1. 管理公司的预算编制和执行跟踪\n2. 处理各类发票的审核和报销\n3. 分析广告投放项目的成本和收益\n4. 管理客户付款和应收账款\n5. 生成财务报表和管理报表\n6. 提供财务分析和决策支持\n\n你应该：\n- 确保财务数据的准确性和合规性\n- 及时响应财务相关咨询和需求\n- 提供清晰的财务分析和建议\n- 帮助管理层做出明智的财务决策',
'["预算管理", "发票处理", "成本分析", "财务报表", "付款跟踪", "收益分析"]');
