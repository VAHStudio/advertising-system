package com.touhuwai.service;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.model.ChatModelBase;
import io.agentscope.core.tool.Toolkit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom Agent服务
 * 基于AgentScope Java的ReAct Agent
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomAgentService {
    
    private final ChatModelBase chatModel;
    private final BusinessToolRegistry toolRegistry;
    
    @Value("${ai.custom.agent.name:投小智}")
    private String agentName;
    
    @Value("${ai.custom.agent.system-prompt:}")
    private String systemPrompt;
    
    @Value("${ai.custom.agent.max-iterations:10}")
    private int maxIterations;
    
    // Agent实例缓存（每个session一个）
    private final Map<String, ReActAgent> agentCache = new ConcurrentHashMap<>();
    
    /**
     * 同步对话
     */
    public String chat(String sessionId, String message) {
        log.info("[CustomAgent] Session: {}, Message: {}", sessionId, message);
        
        if (chatModel == null) {
            return "错误: AI模型未配置，请检查API Key设置";
        }
        
        ReActAgent agent = getOrCreateAgent(sessionId);
        
        Msg userMsg = Msg.builder()
            .name("user")
            .textContent(message)
            .build();
        
        try {
            Msg response = agent.call(userMsg).block();
            return response != null ? response.getTextContent() : "";
        } catch (Exception e) {
            log.error("[CustomAgent] Chat error: {}", e.getMessage());
            return "对话失败: " + e.getMessage();
        }
    }
    
    /**
     * 流式对话 - 模拟流式输出（分块发送）
     * 
     * @param sessionId 会话ID
     * @param message 用户消息
     * @param callback 流式回调
     */
    public void streamChat(String sessionId, String useId, String message, StreamCallback callback) {
        log.info("[CustomAgent] Stream chat - Session: {}, Message: {}", sessionId, 
            message.length() > 50 ? message.substring(0, 50) + "..." : message);
        
        if (chatModel == null) {
            callback.onError("AI模型未配置，请检查API Key设置");
            callback.onComplete();
            return;
        }
        
        // 检查是否是dummy key（用于测试）
        boolean isMockMode = systemPrompt != null && systemPrompt.contains("测试模式");
        if (isMockMode) {
            simulateMockStream(callback, "你好！这是模拟的流式响应。实际部署时请配置有效的API Key。");
            return;
        }
        
        // 在后台线程执行，模拟流式效果
        CompletableFuture.runAsync(() -> {
            try {
                ReActAgent agent = getOrCreateAgent(sessionId);
                
                Msg userMsg = Msg.builder()
                    .name("user")
                    .textContent(message)
                    .build();
                
                // 通知开始思考
                callback.onThinking("正在思考问题...");
                
                // 获取完整响应
                Msg response = agent.call(userMsg).block();
                
                if (response != null) {
                    String fullContent = response.getTextContent();
                    
                    // 模拟流式输出 - 按句子分割
                    String[] sentences = fullContent.split("(?<=[。！？.!?])");
                    
                    for (String sentence : sentences) {
                        if (!sentence.trim().isEmpty()) {
                            callback.onMessage(sentence.trim() + " ", true);
                            // 模拟打字延迟
                            Thread.sleep(100);
                        }
                    }
                }
                
                callback.onComplete();
            } catch (Exception e) {
                log.error("[CustomAgent] Stream error: {}", e.getMessage());
                callback.onError(e.getMessage());
                callback.onComplete();
            }
        });
    }
    
    /**
     * 获取会话历史
     * 
     * @param sessionId 会话ID
     * @return 消息历史列表
     */
    public List<Msg> getSessionHistory(String sessionId) {
        ReActAgent agent = agentCache.get(sessionId);
        if (agent == null) {
            log.debug("[CustomAgent] No agent found for session: {}", sessionId);
            return List.of();
        }
        
        try {
            // 从Agent的Memory中获取历史消息
            var memory = agent.getMemory();
            if (memory != null) {
                return memory.getMessages();
            }
        } catch (Exception e) {
            log.error("[CustomAgent] Failed to get session history: {}", e.getMessage());
        }
        
        return List.of();
    }
    
    /**
     * 清除会话
     * 
     * @param sessionId 会话ID
     */
    public void clearSession(String sessionId) {
        ReActAgent removed = agentCache.remove(sessionId);
        if (removed != null) {
            log.info("[CustomAgent] Cleared session: {}", sessionId);
        }
    }
    
    /**
     * 获取Agent实例（用于测试）
     * 
     * @param sessionId 会话ID
     * @return Agent实例或null
     */
    public ReActAgent getAgent(String sessionId) {
        return agentCache.get(sessionId);
    }
    
    /**
     * 模拟流式响应（用于测试或模型未配置时）
     */
    private void simulateMockStream(StreamCallback callback, String message) {
        CompletableFuture.runAsync(() -> {
            try {
                callback.onThinking("正在思考...");
                
                // 按字符发送，模拟打字效果
                String[] chars = message.split("");
                StringBuilder current = new StringBuilder();
                
                for (String ch : chars) {
                    current.append(ch);
                    callback.onMessage(ch, true);
                    Thread.sleep(50); // 50ms 延迟
                }
                
                callback.onComplete();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                callback.onError("Stream interrupted");
                callback.onComplete();
            }
        });
    }

    /**
     * 获取或创建Agent实例
     */
    private ReActAgent getOrCreateAgent(String sessionId) {
        return agentCache.computeIfAbsent(sessionId, id -> {
            log.info("[CustomAgent] Creating new agent for session: {}", id);
            
            Toolkit toolkit = new Toolkit();
            toolRegistry.getTools().forEach(toolkit::registerTool);
            
            return ReActAgent.builder()
                .name(agentName)
                .sysPrompt(systemPrompt)
                .model(chatModel)
                .toolkit(toolkit)
                .maxIters(maxIterations)
                .build();
        });
    }
    
    /**
     * 流式对话回调接口
     */
    public interface StreamCallback {
        void onMessage(String content, boolean isDelta);
        void onThinking(String thinking);
        void onToolCall(String toolName, Map<String, Object> params);
        void onComplete();
        void onError(String error);
    }
}
