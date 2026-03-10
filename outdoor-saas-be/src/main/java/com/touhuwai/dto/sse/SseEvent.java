package com.touhuwai.dto.sse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.touhuwai.dto.NavigationAction;
import lombok.Data;

import java.io.Serializable;

/**
 * SSE 事件封装
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SseEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 事件类型: agent_message, tool_call, tool_response, navigation, error, end
     */
    private String type;
    
    /**
     * 文本内容
     */
    private String content;
    
    /**
     * 是否为增量文本
     */
    private Boolean delta;
    
    /**
     * 状态: calling_tool, tool_completed, completed, error
     */
    private String status;
    
    /**
     * 工具调用信息
     */
    private ToolCallInfo toolCall;
    
    /**
     * 工具响应信息
     */
    private ToolResponseInfo toolResponse;
    
    /**
     * 导航信息
     */
    private NavigationAction navigation;
    
    /**
     * 错误信息
     */
    private String error;
    
    /**
     * 工具调用信息
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ToolCallInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private String id;
        private String toolName;
    }
    
    /**
     * 工具响应信息
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ToolResponseInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private String toolCallId;
        private String toolName;
        private Boolean success;
    }
}
