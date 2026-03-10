package com.touhuwai.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.touhuwai.config.DifyConfig;
import com.touhuwai.dto.dify.DifyChatRequest;
import com.touhuwai.dto.dify.DifyStreamEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Dify 流式客户端
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DifyStreamingClient {
    
    private final DifyConfig difyConfig;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    /**
     * 流式发送消息到 Dify Agent
     * 
     * @param query 用户查询
     * @param inputs 上下文输入
     * @param consumer 流式数据消费者
     */
    public void streamChat(String query, Map<String, Object> inputs, 
                          Consumer<DifyStreamEvent> consumer) {
        
        // 确保 user 字段有值（Dify 必需）
        // Todo 从token获取用户ID
        String user = inputs != null && inputs.get("userId") != null
                ? (String) inputs.get("userId")
                : "user_" + 1;
        
        // 获取会话 ID
        String conversationId = inputs != null 
            ? (String) inputs.get("conversationId") 
            : null;
        
        DifyChatRequest request = DifyChatRequest.builder()
            .query(query)
            .inputs(inputs != null ? inputs : Map.of())
            .responseMode("streaming")
            .user(user)
            .conversationId(conversationId)
            .build();
        
        log.info("Sending request to Dify: query={}, user={}, conversationId={}", 
            query, user, conversationId);
        log.debug("Request body: {}", request);
        
        String apiUrl = difyConfig.getBaseUrl() + "/chat-messages";
        log.info("Dify API URL: {}", apiUrl);

        // Todo 发送消息时携带用户token, 用于Dify Agent 使用工具执行接口时携带token
        webClient.post()
            .uri(apiUrl)
            .header("Authorization", "Bearer " + difyConfig.getApiKey())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.TEXT_EVENT_STREAM)
            .bodyValue(request)
            .retrieve()
            .onStatus(
                status -> status.isError(),
                response -> response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        log.error("Dify API error: {} - {}", response.statusCode(), errorBody);
                        return Mono.error(new RuntimeException(
                            "Dify API error: " + response.statusCode() + " - " + errorBody));
                    })
            )
            .bodyToFlux(String.class)
            .filter(line -> line != null && !line.isEmpty())
            .flatMap(line -> {
                try {
                    DifyStreamEvent event = parseEvent(line);
                    if (event != null) {
                        return Mono.just(event);
                    } else {
                        return Mono.empty();
                    }
                } catch (Exception e) {
                    log.warn("Error parsing line: {}", line, e);
                    return Mono.empty();
                }
            })
            .onErrorResume(e -> {
                log.error("Stream error, continuing...", e);
                return Mono.empty();
            })
            .subscribe(
                consumer,
                error -> log.error("Dify streaming fatal error", error),
                () -> log.debug("Dify streaming completed")
            );
    }
    
    /**
     * 同步发送消息（用于测试）
     * 
     * @param query 用户查询
     * @param inputs 上下文输入
     * @return Dify 响应字符串
     */
    public String sendMessageSync(String query, Map<String, Object> inputs) {
        String user = inputs != null && inputs.get("userId") != null 
            ? (String) inputs.get("userId") 
            : "user_" + 1;
        
        String conversationId = inputs != null 
            ? (String) inputs.get("conversationId") 
            : null;
        
        DifyChatRequest request = DifyChatRequest.builder()
            .query(query)
            .inputs(inputs != null ? inputs : Map.of())
            .responseMode("blocking")
            .user(user)
            .conversationId(conversationId)
            .build();
        
        String apiUrl = difyConfig.getBaseUrl() + "/chat-messages";
        log.info("Sending sync request to Dify: URL={}, query={}", apiUrl, query);
        
        try {
            String response = webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + difyConfig.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            log.info("Dify sync response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Dify sync request failed", e);
            throw e;
        }
    }

    /**
     * 解析 SSE 事件
     * 处理多种 SSE 格式：
     * - data: {...}
     * - event: message
     * - id: xxx
     * - 空行
     */
    private DifyStreamEvent parseEvent(String line) {
        try {
            if (line == null || line.isEmpty()) {
                return null;
            }

            // 处理 SSE data: 格式
            if (line.startsWith("data: ")) {
                String json = line.substring(6).trim();

                // 忽略 [DONE] 标记
                if ("[DONE]".equals(json)) {
                    log.debug("Received [DONE] marker");
                    return null;
                }

                if (!json.isEmpty()) {
                    DifyStreamEvent event = objectMapper.readValue(json, DifyStreamEvent.class);
                    log.debug("Parsed event: type={}, id={}", event.getEvent(), event.getMessageId());
                    return event;
                }
            }

            // 处理其他 SSE 字段（event, id, retry 等）
            if (line.startsWith("event:") || line.startsWith("id:") ||
                line.startsWith("retry:") || line.startsWith(":")) {
                log.debug("Skipping SSE control line: {}", line);
                return null;
            }

            // 尝试直接解析 JSON（某些情况下可能没有 data: 前缀）
            if (line.startsWith("{")) {
                DifyStreamEvent event = objectMapper.readValue(line, DifyStreamEvent.class);
                log.debug("Parsed JSON event: type={}", event.getEvent());
                return event;
            }

            return null;
        } catch (Exception e) {
            log.warn("Failed to parse event: {}", line, e);
            return null;
        }
    }
}
