-- AI Agent 对话历史表结构
-- 在 MySQL 中执行此脚本创建对话和消息表

-- 对话表
CREATE TABLE IF NOT EXISTS ai_conversations (
    id VARCHAR(36) PRIMARY KEY COMMENT '对话ID',
    user_id VARCHAR(50) NOT NULL COMMENT '用户ID',
    agent_id VARCHAR(50) NOT NULL COMMENT '智能体ID',
    agent_name VARCHAR(100) NOT NULL COMMENT '智能体名称',
    agent_avatar VARCHAR(500) COMMENT '智能体头像URL',
    title VARCHAR(200) NOT NULL COMMENT '对话标题',
    context JSON COMMENT '对话上下文（意图、步骤等）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-归档，1-活跃，2-删除',
    message_count INT DEFAULT 0 COMMENT '消息数量',
    last_message_at DATETIME COMMENT '最后消息时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_agent (user_id, agent_id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_last_message (last_message_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI对话表';

-- 消息表
CREATE TABLE IF NOT EXISTS ai_messages (
    id VARCHAR(36) PRIMARY KEY COMMENT '消息ID',
    conversation_id VARCHAR(36) NOT NULL COMMENT '对话ID',
    role VARCHAR(20) NOT NULL COMMENT '角色：user/assistant/system',
    content TEXT NOT NULL COMMENT '消息内容',
    content_type VARCHAR(50) DEFAULT 'text' COMMENT '内容类型：text/image/file/card',
    metadata JSON COMMENT '元数据（如操作按钮、附件等）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (conversation_id) REFERENCES ai_conversations(id) ON DELETE CASCADE,
    INDEX idx_conversation (conversation_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI消息表';

-- 附件表（可选）
CREATE TABLE IF NOT EXISTS ai_attachments (
    id VARCHAR(36) PRIMARY KEY COMMENT '附件ID',
    conversation_id VARCHAR(36) NOT NULL COMMENT '对话ID',
    message_id VARCHAR(36) COMMENT '消息ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_type VARCHAR(100) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小（字节）',
    file_path VARCHAR(500) COMMENT '文件存储路径',
    extracted_text TEXT COMMENT '提取的文本内容',
    status TINYINT DEFAULT 0 COMMENT '状态：0-上传中，1-处理中，2-就绪，3-错误',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (conversation_id) REFERENCES ai_conversations(id) ON DELETE CASCADE,
    INDEX idx_conversation (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI附件表';
