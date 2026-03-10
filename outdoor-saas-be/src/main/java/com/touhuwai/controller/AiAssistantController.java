package com.touhuwai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.touhuwai.client.DifyStreamingClient;
import com.touhuwai.dto.dify.DifyStreamEvent;
import com.touhuwai.dto.sse.SseEvent;
import com.touhuwai.service.AiAssistantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AI 助手控制器
 * 提供流式对话接口
 */
@RestController
@RequestMapping("/api/ai-assistant")
@RequiredArgsConstructor
@Slf4j
public class AiAssistantController {

    private final DifyStreamingClient difyClient;
    private final AiAssistantService aiAssistantService;
    private final ObjectMapper objectMapper;
    
    /**
     * 流式对话接口
     * 前端通过 EventSource 连接
     * 
     * @param message 用户消息
     * @param conversationId 会话 ID（可选）
     * @return SSE Emitter
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(
            @RequestParam String message,
            @RequestParam(required = false) String conversationId) {
        
        // 创建 SSE Emitter，超时 5 分钟
        SseEmitter emitter = new SseEmitter(300_000L);
        String emitterId = UUID.randomUUID().toString();
        
        // 注册 emitter
        aiAssistantService.registerEmitter(emitterId, emitter);
        
        // 构建上下文
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("userId", getCurrentUserId());
        inputs.put("conversationId", conversationId);
        inputs.put("currentTime", LocalDateTime.now().toString());
        
        try {
            // 启动流式处理
            difyClient.streamChat(message, inputs, event -> {
                handleDifyEvent(emitterId, emitter, event);
            });
        } catch (Exception e) {
            log.error("Failed to start streaming", e);
            sendErrorEvent(emitter, e.getMessage());
            emitter.complete();
            aiAssistantService.removeEmitter(emitterId);
        }
        
        // 处理客户端断开
        emitter.onCompletion(() -> aiAssistantService.removeEmitter(emitterId));
        emitter.onTimeout(() -> aiAssistantService.removeEmitter(emitterId));
        emitter.onError((e) -> aiAssistantService.removeEmitter(emitterId));
        
        return emitter;
    }
    
    /**
     * 处理 Dify 事件
     */
    private void handleDifyEvent(String emitterId, SseEmitter emitter,
                                  DifyStreamEvent event) {
        try {
            log.info("[Dify] Received event: type={}, id={}, answer={}",
                event.getEvent(), event.getMessageId(),
                event.getAnswer() != null ? event.getAnswer().substring(0, Math.min(50, event.getAnswer().length())) + "..." : "null");

            SseEvent sseEvent = convertToSseEvent(event);

            // 将对象序列化为 JSON 字符串
            String jsonData = objectMapper.writeValueAsString(sseEvent);
            log.debug("[Dify] Sending JSON: {}", jsonData);

            // 使用 data() 发送字符串，而不是对象
            SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                .id(event.getMessageId())
                .name(event.getEvent())
                .data(jsonData, MediaType.APPLICATION_JSON);

            emitter.send(eventBuilder);
            log.debug("[Dify] Event sent to frontend");

            // 检查是否需要关闭连接
            if (shouldComplete(event)) {
                log.info("[Dify] Completing stream");
                emitter.complete();
                aiAssistantService.removeEmitter(emitterId);
            }
        } catch (IOException e) {
            log.error("[Dify] Failed to send SSE event", e);
            emitter.completeWithError(e);
            aiAssistantService.removeEmitter(emitterId);
        }
    }
    
    /**
     * 转换为 SSE 事件
     * 支持 Dify 多种事件类型
     */
    private SseEvent convertToSseEvent(DifyStreamEvent event) {
        SseEvent sse = new SseEvent();
        String eventType = event.getEvent();
        sse.setType(eventType);
        
        log.debug("[Dify] Converting event type: {}", eventType);
        
        // 处理消息内容（支持多种事件类型）
        if ("agent_message".equals(eventType) || "message".equals(eventType) || 
            "agent_thought".equals(eventType)) {
            sse.setContent(event.getAnswer());
            sse.setDelta(true);
        }
        else if ("tool_call".equals(eventType) || "tool_calls".equals(eventType)) {
            if (event.getToolCall() != null) {
                SseEvent.ToolCallInfo toolCallInfo = new SseEvent.ToolCallInfo();
                toolCallInfo.setId(event.getToolCall().getId());
                toolCallInfo.setToolName(event.getToolCall().getToolName());
                sse.setToolCall(toolCallInfo);
            }
            sse.setStatus("calling_tool");
        }
        else if ("tool_response".equals(eventType) || "tool_responses".equals(eventType)) {
            if (event.getToolResponse() != null) {
                SseEvent.ToolResponseInfo toolResponseInfo = 
                    new SseEvent.ToolResponseInfo();
                toolResponseInfo.setToolCallId(
                    event.getToolResponse().getToolCallId());
                toolResponseInfo.setToolName(
                    event.getToolResponse().getToolName());
                toolResponseInfo.setSuccess(
                    event.getToolResponse().getSuccess());
                sse.setToolResponse(toolResponseInfo);
                
                // 提取导航信息
                if (event.getToolResponse().getNavigation() != null) {
                    sse.setNavigation(event.getToolResponse().getNavigation());
                }
            }
            sse.setStatus("tool_completed");
        }
        else if ("error".equals(eventType)) {
            sse.setError(event.getMetadata() != null ? 
                (String) event.getMetadata().get("error") : "未知错误");
            sse.setStatus("error");
        }
        else if ("end".equals(eventType) || "workflow_finished".equals(eventType) || 
                 "message_end".equals(eventType)) {
            sse.setStatus("completed");
        }
        else {
            log.debug("[Dify] Unknown event type: {}", eventType);
        }
        
        return sse;
    }
    
    /**
     * 检查是否应该完成连接
     */
    private boolean shouldComplete(DifyStreamEvent event) {
        if ("end".equals(event.getEvent())) {
            return true;
        }
        if ("error".equals(event.getEvent())) {
            return true;
        }
        return false;
    }
    
    /**
     * 发送错误事件
     */
    private void sendErrorEvent(SseEmitter emitter, String error) {
        try {
            SseEvent sse = new SseEvent();
            sse.setType("error");
            sse.setError(error);
            sse.setStatus("error");

            String jsonData = objectMapper.writeValueAsString(sse);
            emitter.send(SseEmitter.event()
                .name("error")
                .data(jsonData, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            log.error("Failed to send error event", e);
        }
    }
    
    /**
     * 获取当前用户 ID（简化实现）
     */
    private String getCurrentUserId() {
        // 实际项目中应从 SecurityContext 获取
        // todo 从token中获取用户信息
        return "user_" + 1;
    }
}
