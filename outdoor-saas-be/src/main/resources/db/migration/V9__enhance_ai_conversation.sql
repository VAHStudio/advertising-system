-- 增强 AI 对话会话表，添加模式标识和统计字段
-- 创建时间: 2026-03-13

-- 添加 mode 字段（AI模式：DIFY 或 CUSTOM）
ALTER TABLE ai_conversation 
ADD COLUMN IF NOT EXISTS mode VARCHAR(20) NOT NULL DEFAULT 'DIFY' 
COMMENT 'AI模式: DIFY 或 CUSTOM'
AFTER conversation_id;

-- 添加消息数量统计字段
ALTER TABLE ai_conversation 
ADD COLUMN IF NOT EXISTS message_count INT DEFAULT 0 
COMMENT '消息数量';

-- 添加最后消息预览字段
ALTER TABLE ai_conversation 
ADD COLUMN IF NOT EXISTS last_message_preview VARCHAR(200) 
COMMENT '最后消息预览（前50字）';

-- 创建复合索引：用户 + 模式 + 最后消息时间（用于分页查询）
CREATE INDEX IF NOT EXISTS idx_user_mode_time 
ON ai_conversation(user_id, mode, last_message_at DESC);

-- 创建复合索引：会话ID + 模式（用于精确查询）
CREATE INDEX IF NOT EXISTS idx_conversation_lookup 
ON ai_conversation(conversation_id, mode);

-- 更新现有数据：根据 conversation_id 前缀判断 mode
UPDATE ai_conversation 
SET mode = 'CUSTOM' 
WHERE conversation_id LIKE 'custom-%';
