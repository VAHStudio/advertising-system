package com.touhuwai.dto.dify;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Dify 聊天请求
 * 参考：https://docs.dify.ai/guides/application-prompt
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DifyChatRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户输入/问题（必需）
     */
    @JsonProperty("query")
    private String query;
    
    /**
     * 输入参数，用于填充提示词变量
     */
    @JsonProperty("inputs")
    private Map<String, Object> inputs;
    
    /**
     * 响应模式: streaming 或 blocking（必需）
     */
    @JsonProperty("response_mode")
    private String responseMode;
    
    /**
     * 会话 ID（可选，用于继续对话）
     */
    @JsonProperty("conversation_id")
    private String conversationId;
    
    /**
     * 用户标识（必需，用于区分不同用户）
     */
    @JsonProperty("user")
    private String user;
    
    /**
     * 文件上传（可选）
     */
    @JsonProperty("files")
    private Object files;
}
