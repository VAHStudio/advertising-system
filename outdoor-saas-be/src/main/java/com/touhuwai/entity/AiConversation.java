package com.touhuwai.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI 对话会话实体
 * 用于存储用户与 Dify 会话的映射关系
 */
@Data
public class AiConversation {
    
    /**
     * 自增ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * Dify 会话ID
     */
    private String conversationId;
    
    /**
     * 会话标题
     */
    private String title;
    
    /**
     * AI模式: DIFY 或 CUSTOM
     */
    private String mode;
    
    /**
     * 状态：1-活跃 0-已归档
     */
    private Integer status;
    
    /**
     * 消息数量统计
     */
    private Integer messageCount;
    
    /**
     * 最后消息预览
     */
    private String lastMessagePreview;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageAt;
}
