package com.touhuwai.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI 对话消息实体
 * 用于存储 CUSTOM 模式的对话消息历史
 */
@Data
public class AiConversationMessage {
    
    /**
     * 自增ID
     */
    private Long id;
    
    /**
     * 关联的会话ID
     */
    private String conversationId;
    
    /**
     * 角色: user | assistant
     */
    private String role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 思考过程（CUSTOM模式AI的思考内容）
     */
    private String thinking;
    
    /**
     * 工具调用信息（JSON格式）
     */
    private String toolCalls;
    
    /**
     * 额外元数据（JSON格式）
     */
    private String metadata;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
