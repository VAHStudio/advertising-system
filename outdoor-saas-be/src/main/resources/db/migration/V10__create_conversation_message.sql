-- 创建 AI 对话消息表
-- 用于存储 CUSTOM 模式的历史消息
-- 创建时间: 2026-03-13

CREATE TABLE IF NOT EXISTS ai_conversation_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增ID',
    
    -- 关联会话ID
    conversation_id VARCHAR(128) NOT NULL COMMENT '关联的会话ID',
    
    -- 消息角色
    role VARCHAR(20) NOT NULL COMMENT '角色: user | assistant',
    
    -- 消息内容
    content TEXT NOT NULL COMMENT '消息内容',
    
    -- 思考过程（CUSTOM模式AI的思考内容）
    thinking TEXT COMMENT '思考过程',
    
    -- 工具调用信息（JSON格式）
    tool_calls JSON COMMENT '工具调用信息 [{toolName, params, result}]',
    
    -- 额外元数据（JSON格式）
    metadata JSON COMMENT '额外元数据',
    
    -- 创建时间
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引
    INDEX idx_conversation_time (conversation_id, created_at DESC),
    INDEX idx_conversation_role (conversation_id, role)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='AI对话消息表 - 存储CUSTOM模式的完整对话历史';

-- 添加外键约束（可选，如果会话被删除则级联删除消息）
-- ALTER TABLE ai_conversation_message
-- ADD CONSTRAINT fk_conversation 
-- FOREIGN KEY (conversation_id) 
-- REFERENCES ai_conversation(conversation_id)
-- ON DELETE CASCADE;
