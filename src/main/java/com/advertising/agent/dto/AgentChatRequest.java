package com.advertising.agent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Agent 聊天请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentChatRequest {
    
    /**
     * 用户消息
     */
    private String message;
    
    /**
     * 会话ID（首次为空，后续携带）
     */
    @JsonProperty("conversation_id")
    private String sessionId;
    
    /**
     * 智能体ID
     */
    @JsonProperty("agent_id")
    private String agentId;
    
    /**
     * 用户选择的选项值
     */
    @JsonProperty("selected_value")
    private String selectedValue;
    
    /**
     * 额外的上下文数据
     */
    private Map<String, Object> context;
}
