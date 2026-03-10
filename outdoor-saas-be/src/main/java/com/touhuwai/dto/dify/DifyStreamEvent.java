package com.touhuwai.dto.dify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.touhuwai.dto.NavigationAction;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Dify 流式事件
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DifyStreamEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 事件类型: agent_message, message, tool_call, tool_response, end, error 等
     */
    @JsonProperty("event")
    private String event;
    
    /**
     * 会话 ID
     */
    @JsonProperty("conversation_id")
    private String conversationId;
    
    /**
     * 消息 ID
     */
    @JsonProperty("message_id")
    private String messageId;
    
    /**
     * 消息序号
     */
    @JsonProperty("message_index")
    private Integer messageIndex;
    
    /**
     * 文本内容（agent_message/message 事件）
     */
    @JsonProperty("answer")
    private String answer;
    
    /**
     * 工具调用信息
     */
    @JsonProperty("tool_calls")
    private ToolCall toolCall;
    
    /**
     * 工具响应信息
     */
    @JsonProperty("tool_response")
    private ToolResponse toolResponse;
    
    /**
     * 元数据
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
    
    /**
     * 创建时间
     */
    @JsonProperty("created_at")
    private Long createdAt;
    
    /**
     * 工作流运行 ID
     */
    @JsonProperty("workflow_run_id")
    private String workflowRunId;
    
    /**
     * 任务 ID
     */
    @JsonProperty("task_id")
    private String taskId;
    
    /**
     * ID（某些事件使用）
     */
    @JsonProperty("id")
    private String id;
    
    /**
     * 工具调用
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ToolCall implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String id;
        private String toolName;
        private Map<String, Object> toolInputs;
    }
    
    /**
     * 工具响应
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ToolResponse implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String toolCallId;
        private String toolName;
        private Object result;
        private Boolean success;
        private NavigationAction navigation;
    }
}
