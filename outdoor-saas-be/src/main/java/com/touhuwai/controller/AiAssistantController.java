package com.touhuwai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.touhuwai.client.DifyStreamingClient;
import com.touhuwai.common.Result;
import com.touhuwai.dto.dify.DifyStreamEvent;
import com.touhuwai.dto.sse.SseEvent;
import com.touhuwai.enums.AiMode;
import com.touhuwai.service.AiAssistantService;
import com.touhuwai.service.AiConversationService;
import com.touhuwai.service.CustomAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final CustomAgentService customAgentService;
    private final AiAssistantService aiAssistantService;
    private final AiConversationService conversationService;
    private final ObjectMapper objectMapper;

    @Value("${ai.default-mode:DIFY}")
    private String defaultMode;
    
    /**
     * 流式对话接口
     * 前端通过 EventSource 连接
     * 
     * @param message 用户消息
     * @param conversationId 会话 ID（可选，不传则自动关联用户当前活跃会话）
     * @param mode AI模式（可选，不传则使用默认值）
     * @param userId 用户ID（API Key鉴权场景）
     * @return SSE Emitter
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(
            @RequestParam String message,
            @RequestParam(required = false) String conversationId,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String userId) {
        
        // 解析AI模式
        AiMode aiMode = parseMode(mode);
        
        // 获取当前用户ID（优先使用传入的userId，用于API Key鉴权场景）
        String currentUserId = userId != null && !userId.isEmpty() 
            ? userId 
            : getCurrentUserId();
        
        log.info("[AI] Stream request: mode={}, user={}, message={}", aiMode, currentUserId, 
            message.length() > 50 ? message.substring(0, 50) + "..." : message);
        
        // 根据模式路由到不同服务
        return switch (aiMode) {
            case DIFY -> handleDifyChat(message, conversationId, currentUserId);
            case CUSTOM -> handleCustomChat(message, conversationId, currentUserId);
        };
    }

    /**
     * 处理Dify模式对话
     */
    private SseEmitter handleDifyChat(String message, String conversationId, String userId) {
        // 获取或创建会话（基于用户ID关联对话）
        // 如果是新会话，返回 null，让 Dify 生成 conversation_id
        String activeConversationId = conversationService.getOrCreateConversation(
            userId, conversationId);
        
        // 创建 SSE Emitter，超时 5 分钟
        SseEmitter emitter = new SseEmitter(300_000L);
        String emitterId = UUID.randomUUID().toString();
        
        // 注册 emitter
        aiAssistantService.registerEmitter(emitterId, emitter);
        
        // 构建上下文
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("userId", userId);
        // 只有存在有效会话ID时才传递给 Dify
        if (activeConversationId != null && !activeConversationId.isEmpty()) {
            inputs.put("conversationId", activeConversationId);
        }
        inputs.put("currentTime", LocalDateTime.now().toString());
        
        // 使用 AtomicReference 来在 lambda 中修改值
        final java.util.concurrent.atomic.AtomicReference<String> currentConversationId = 
            new java.util.concurrent.atomic.AtomicReference<>(activeConversationId);
        // 记录原始传入的会话ID，用于检测会话是否变更
        final String originalConversationId = activeConversationId;
        
        try {
            // 启动流式处理
            difyClient.streamChat(message, inputs, event -> {
                // 如果事件包含 conversation_id，检查是否需要更新
                if (event.getConversationId() != null 
                    && !event.getConversationId().isEmpty()) {
                    String difyConversationId = event.getConversationId();
                    String currentId = currentConversationId.get();
                    
                    // 情况1：之前没有会话ID，保存新的
                    if (currentId == null) {
                        currentConversationId.set(difyConversationId);
                        conversationService.saveConversationMapping(userId, difyConversationId);
                        log.info("保存 Dify 生成的会话ID: userId={}, conversationId={}", 
                            userId, difyConversationId);
                    }
                    // 情况2：会话ID发生了变化（原会话失效，Dify创建了新会话）
                    else if (!currentId.equals(difyConversationId)) {
                        log.warn("会话ID变更: old={}, new={}", currentId, difyConversationId);
                        // 删除旧的映射关系
                        conversationService.deleteConversation(userId, currentId);
                        // 保存新的映射关系
                        currentConversationId.set(difyConversationId);
                        conversationService.saveConversationMapping(userId, difyConversationId);
                        log.info("更新会话映射: userId={}, newConversationId={}", 
                            userId, difyConversationId);
                    }
                }
                handleDifyEvent(emitterId, emitter, event, currentConversationId.get());
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
                                  DifyStreamEvent event, String conversationId) {
        try {
            log.info("[Dify] Received event: type={}, id={}, answer={}",
                event.getEvent(), event.getMessageId(),
                event.getAnswer() != null ? event.getAnswer().substring(0, Math.min(50, event.getAnswer().length())) + "..." : "null");

            SseEvent sseEvent = convertToSseEvent(event);
            
            // 在事件中添加会话ID，让前端知道当前对话的conversation_id
            sseEvent.setConversationId(conversationId);

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
     * 从 SecurityContext 获取当前用户 ID
     * @return 当前用户名
     * @throws IllegalStateException 如果用户未认证
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("用户未认证，无法获取用户ID");
            throw new IllegalStateException("用户未认证，请先登录");
        }
        String userId = authentication.getName();
        log.debug("获取当前用户ID: {}", userId);
        return userId;
    }
    
    /**
     * 获取用户的所有会话
     */
    @GetMapping("/conversations")
    public Result<?> getConversations(@RequestParam(required = false) String userId) {
        String currentUserId = userId != null && !userId.isEmpty() 
            ? userId 
            : getCurrentUserId();
        return Result.success(conversationService.getUserConversations(currentUserId));
    }
    
    /**
     * 创建新会话
     */
    @PostMapping("/conversations")
    public Result<?> createConversation(@RequestParam(required = false) String userId) {
        String currentUserId = userId != null && !userId.isEmpty() 
            ? userId 
            : getCurrentUserId();
        String conversationId = conversationService.createNewConversation(currentUserId);
        Map<String, String> result = new HashMap<>();
        result.put("conversationId", conversationId);
        result.put("userId", currentUserId);
        return Result.success("创建成功", result);
    }
    
    /**
     * 删除会话
     */
    @DeleteMapping("/conversations/{conversationId}")
    public Result<?> deleteConversation(
            @PathVariable String conversationId,
            @RequestParam(required = false) String userId) {
        String currentUserId = userId != null && !userId.isEmpty() 
            ? userId 
            : getCurrentUserId();
        conversationService.deleteConversation(currentUserId, conversationId);

        // 如果是Custom Agent会话，也清除Agent缓存
        if (conversationId != null && conversationId.startsWith("custom-")) {
            customAgentService.clearSession(conversationId);
        }

        return Result.success("删除成功");
    }


    /**
     * 处理Custom Agent模式对话 - 真正的流式输出
     */
    private SseEmitter handleCustomChat(String message, String conversationId, String userId) {
        // 使用sessionId作为Custom Agent的会话标识
        String sessionId = conversationId != null && !conversationId.isEmpty()
            ? conversationId
            : "custom-" + userId + "-" + UUID.randomUUID().toString();

        // 创建 SSE Emitter
        SseEmitter emitter = new SseEmitter(300_000L);
        String emitterId = UUID.randomUUID().toString();
        aiAssistantService.registerEmitter(emitterId, emitter);

        // 使用流式输出（带持久化）
        customAgentService.streamChat(sessionId, userId, message, new CustomAgentService.StreamCallback() {
            private final StringBuilder fullContent = new StringBuilder();

            @Override
            public void onMessage(String content, boolean isDelta) {
                try {
                    if (content != null && !content.isEmpty()) {
                        fullContent.append(content);

                        SseEvent sse = new SseEvent();
                        sse.setType("message");
                        sse.setContent(content);
                        sse.setDelta(isDelta);
                        sse.setConversationId(sessionId);

                        String jsonData = objectMapper.writeValueAsString(sse);
                        emitter.send(SseEmitter.event()
                            .id(sessionId)
                            .name("message")
                            .data(jsonData, MediaType.APPLICATION_JSON));
                    }
                } catch (Exception e) {
                    log.error("[CustomAgent] Failed to send message", e);
                }
            }

            @Override
            public void onThinking(String thinking) {
                try {
                    SseEvent sse = new SseEvent();
                    sse.setType("agent_thought");
                    sse.setContent(thinking);
                    sse.setConversationId(sessionId);

                    String jsonData = objectMapper.writeValueAsString(sse);
                    emitter.send(SseEmitter.event()
                        .id(sessionId)
                        .name("agent_thought")
                        .data(jsonData, MediaType.APPLICATION_JSON));
                } catch (Exception e) {
                    log.error("[CustomAgent] Failed to send thinking", e);
                }
            }

            @Override
            public void onToolCall(String toolName, java.util.Map<String, Object> params) {
                try {
                    SseEvent sse = new SseEvent();
                    sse.setType("tool_call");
                    sse.setStatus("calling_tool");
                    SseEvent.ToolCallInfo toolCallInfo = new SseEvent.ToolCallInfo();
                    toolCallInfo.setToolName(toolName);
                    sse.setToolCall(toolCallInfo);
                    sse.setConversationId(sessionId);

                    String jsonData = objectMapper.writeValueAsString(sse);
                    emitter.send(SseEmitter.event()
                        .id(sessionId)
                        .name("tool_call")
                        .data(jsonData, MediaType.APPLICATION_JSON));
                } catch (Exception e) {
                    log.error("[CustomAgent] Failed to send tool call", e);
                }
            }

            @Override
            public void onComplete() {
                try {
                    SseEvent endEvent = new SseEvent();
                    endEvent.setType("end");
                    endEvent.setStatus("completed");
                    endEvent.setConversationId(sessionId);

                    String endJson = objectMapper.writeValueAsString(endEvent);
                    emitter.send(SseEmitter.event()
                        .id(sessionId)
                        .name("end")
                        .data(endJson, MediaType.APPLICATION_JSON));

                    emitter.complete();
                } catch (Exception e) {
                    log.error("[CustomAgent] Failed to send completion", e);
                    emitter.completeWithError(e);
                } finally {
                    aiAssistantService.removeEmitter(emitterId);
                }
            }

            @Override
            public void onError(String error) {
                log.error("[CustomAgent] Stream error: {}", error);
                sendErrorEvent(emitter, error);
                emitter.complete();
                aiAssistantService.removeEmitter(emitterId);
            }
        });

        emitter.onCompletion(() -> aiAssistantService.removeEmitter(emitterId));
        emitter.onTimeout(() -> aiAssistantService.removeEmitter(emitterId));
        emitter.onError((e) -> aiAssistantService.removeEmitter(emitterId));

        return emitter;
    }

    /**
     * 解析AI模式
     */
    private AiMode parseMode(String mode) {
        if (mode == null || mode.isEmpty()) {
            return AiMode.valueOf(defaultMode);
        }
        try {
            return AiMode.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown AI mode: {}, using default: {}", mode, defaultMode);
            return AiMode.valueOf(defaultMode);
        }
    }
}
