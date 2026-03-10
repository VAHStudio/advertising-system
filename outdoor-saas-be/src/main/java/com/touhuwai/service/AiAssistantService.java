package com.touhuwai.service;

import com.touhuwai.dto.sse.SseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI 助手服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiAssistantService {
    
    // 存储活跃的 SSE 连接
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    
    /**
     * 注册 SSE Emitter
     */
    public void registerEmitter(String emitterId, SseEmitter emitter) {
        emitters.put(emitterId, emitter);
        log.debug("Registered SSE emitter: {}", emitterId);
    }
    
    /**
     * 移除 SSE Emitter
     */
    public void removeEmitter(String emitterId) {
        emitters.remove(emitterId);
        log.debug("Removed SSE emitter: {}", emitterId);
    }
    
    /**
     * 发送事件到指定 Emitter
     */
    public void sendEvent(String emitterId, SseEvent event) {
        SseEmitter emitter = emitters.get(emitterId);
        if (emitter == null) {
            return;
        }
        
        try {
            emitter.send(SseEmitter.event()
                .name(event.getType())
                .data(event));
        } catch (IOException e) {
            log.error("Failed to send SSE event to emitter: {}", emitterId, e);
            removeEmitter(emitterId);
        }
    }
    
    /**
     * 完成 Emitter
     */
    public void completeEmitter(String emitterId) {
        SseEmitter emitter = emitters.get(emitterId);
        if (emitter != null) {
            emitter.complete();
            removeEmitter(emitterId);
        }
    }
    
    /**
     * 完成 Emitter（带错误）
     */
    public void completeEmitterWithError(String emitterId, Throwable error) {
        SseEmitter emitter = emitters.get(emitterId);
        if (emitter != null) {
            emitter.completeWithError(error);
            removeEmitter(emitterId);
        }
    }
}
