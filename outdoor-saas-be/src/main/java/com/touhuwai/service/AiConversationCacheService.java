package com.touhuwai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.touhuwai.entity.AiConversation;
import com.touhuwai.entity.AiConversationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * AI 对话缓存服务
 * 使用 Redis 缓存会话列表和消息历史
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiConversationCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // 缓存键前缀
    private static final String CONV_LIST_KEY_PREFIX = "ai:conv:";
    private static final String MSG_LIST_KEY_PREFIX = "ai:msg:";
    private static final String ACTIVE_CONV_KEY_PREFIX = "ai:active:";

    // 缓存过期时间
    private static final long CONV_LIST_TTL = 3600; // 1小时
    private static final long MSG_LIST_TTL = 1800;  // 30分钟
    private static final long ACTIVE_CONV_TTL = 86400; // 24小时

    /**
     * 构建会话列表缓存键
     */
    private String buildConvListKey(String userId, String mode) {
        return CONV_LIST_KEY_PREFIX + userId + ":" + mode;
    }

    /**
     * 构建消息列表缓存键
     */
    private String buildMsgListKey(String conversationId) {
        return MSG_LIST_KEY_PREFIX + conversationId;
    }

    /**
     * 构建活跃会话缓存键
     */
    private String buildActiveConvKey(String userId) {
        return ACTIVE_CONV_KEY_PREFIX + userId;
    }

    /**
     * 缓存会话列表（Sorted Set，按最后消息时间排序）
     */
    public void cacheConversationList(String userId, String mode, List<AiConversation> conversations) {
        String key = buildConvListKey(userId, mode);

        try {
            // 删除旧缓存
            redisTemplate.delete(key);

            // 添加新数据
            for (AiConversation conv : conversations) {
                String value = objectMapper.writeValueAsString(conv);
                double score = conv.getLastMessageAt().toEpochSecond(ZoneOffset.UTC);
                redisTemplate.opsForZSet().add(key, value, score);
            }

            // 设置过期时间
            redisTemplate.expire(key, CONV_LIST_TTL, TimeUnit.SECONDS);

            log.debug("Cached {} conversations for user {} mode {}",
                conversations.size(), userId, mode);
        } catch (Exception e) {
            log.error("Failed to cache conversation list", e);
        }
    }

    /**
     * 从缓存获取会话列表（分页）
     */
    public List<AiConversation> getCachedConversationList(
            String userId, String mode, int offset, int limit) {
        String key = buildConvListKey(userId, mode);

        try {
            Set<String> values = redisTemplate.opsForZSet()
                .reverseRange(key, offset, offset + limit - 1);

            if (values == null || values.isEmpty()) {
                return null; // 缓存未命中
            }

            return values.stream()
                .map(v -> {
                    try {
                        return objectMapper.readValue(v, AiConversation.class);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to parse conversation from cache", e);
                        return null;
                    }
                })
                .filter(conv -> conv != null)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get cached conversation list", e);
            return null;
        }
    }

    /**
     * 清除会话列表缓存
     */
    public void clearConversationListCache(String userId, String mode) {
        String key = buildConvListKey(userId, mode);
        try {
            redisTemplate.delete(key);
            log.debug("Cleared conversation list cache for user {} mode {}", userId, mode);
        } catch (Exception e) {
            log.error("Failed to clear conversation list cache", e);
        }
    }

    /**
     * 添加消息到缓存（List，LPUSH新消息）
     */
    public void cacheMessage(String conversationId, AiConversationMessage message) {
        String key = buildMsgListKey(conversationId);

        try {
            String value = objectMapper.writeValueAsString(message);
            redisTemplate.opsForList().leftPush(key, value);
            redisTemplate.expire(key, MSG_LIST_TTL, TimeUnit.SECONDS);
            log.debug("Cached message for conversation {}", conversationId);
        } catch (Exception e) {
            log.error("Failed to cache message", e);
        }
    }

    /**
     * 从缓存获取消息列表（分页）
     */
    public List<AiConversationMessage> getCachedMessages(
            String conversationId, int offset, int limit) {
        String key = buildMsgListKey(conversationId);

        try {
            List<String> values = redisTemplate.opsForList()
                .range(key, offset, offset + limit - 1);

            if (values == null || values.isEmpty()) {
                return null;
            }

            return values.stream()
                .map(v -> {
                    try {
                        return objectMapper.readValue(v, AiConversationMessage.class);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to parse message from cache", e);
                        return null;
                    }
                })
                .filter(msg -> msg != null)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get cached messages", e);
            return null;
        }
    }

    /**
     * 清除消息缓存
     */
    public void clearMessageCache(String conversationId) {
        String key = buildMsgListKey(conversationId);
        try {
            redisTemplate.delete(key);
            log.debug("Cleared message cache for conversation {}", conversationId);
        } catch (Exception e) {
            log.error("Failed to clear message cache", e);
        }
    }

    /**
     * 设置/获取当前活跃会话
     */
    public void setActiveConversation(String userId, String conversationId) {
        String key = buildActiveConvKey(userId);
        try {
            redisTemplate.opsForValue().set(key, conversationId, ACTIVE_CONV_TTL, TimeUnit.SECONDS);
            log.debug("Set active conversation for user {}: {}", userId, conversationId);
        } catch (Exception e) {
            log.error("Failed to set active conversation", e);
        }
    }

    /**
     * 获取当前活跃会话
     */
    public String getActiveConversation(String userId) {
        String key = buildActiveConvKey(userId);
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Failed to get active conversation", e);
            return null;
        }
    }

    /**
     * 清除活跃会话
     */
    public void clearActiveConversation(String userId) {
        String key = buildActiveConvKey(userId);
        try {
            redisTemplate.delete(key);
            log.debug("Cleared active conversation for user {}", userId);
        } catch (Exception e) {
            log.error("Failed to clear active conversation", e);
        }
    }

    /**
     * 清除与会话相关的所有缓存
     */
    public void clearAllConversationCache(String userId, String mode, String conversationId) {
        clearConversationListCache(userId, mode);
        clearMessageCache(conversationId);
        log.info("Cleared all cache for user {} mode {} conversation {}",
            userId, mode, conversationId);
    }
}
