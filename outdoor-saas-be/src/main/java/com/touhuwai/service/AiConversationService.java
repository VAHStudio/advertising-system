package com.touhuwai.service;

import com.touhuwai.entity.AiConversation;
import com.touhuwai.entity.AiConversationMessage;
import com.touhuwai.mapper.AiConversationMapper;
import com.touhuwai.mapper.AiConversationMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AI 对话会话服务
 * 管理用户与 Dify 会话的关联关系
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiConversationService {

    private final AiConversationMapper conversationMapper;
    private final AiConversationMessageMapper messageMapper;
    private final AiConversationCacheService cacheService;

    private static final int PAGE_SIZE = 10;
    
    /**
     * 获取或创建会话
     * 
     * @param userId 用户ID
     * @param conversationId 可选的 Dify 会话ID
     * @return Dify 会话ID，如果是新会话则返回 null（让 Dify 生成）
     */
    @Transactional
    public String getOrCreateConversation(String userId, String conversationId) {
        if (conversationId != null && !conversationId.isEmpty()) {
            // 验证会话是否属于当前用户
            AiConversation existing = conversationMapper.selectByUserAndConversation(userId, conversationId);
            if (existing != null) {
                // 更新最后消息时间
                conversationMapper.updateLastMessageTime(existing.getId());
                log.info("使用现有会话: userId={}, conversationId={}", userId, conversationId);
                return conversationId;
            }
            log.warn("会话ID {} 不属于用户 {}，将创建新会话", conversationId, userId);
        }
        
        // 尝试获取用户的活跃会话
        AiConversation activeConversation = conversationMapper.selectActiveByUserId(userId);
        if (activeConversation != null) {
            log.info("获取用户活跃会话: userId={}, conversationId={}", userId, activeConversation.getConversationId());
            conversationMapper.updateLastMessageTime(activeConversation.getId());
            return activeConversation.getConversationId();
        }
        
        // 新会话：返回 null，让 Dify 生成 conversation_id
        // 我们会在 Dify 返回后保存这个 ID
        log.info("创建新会话，等待 Dify 生成 conversation_id: userId={}", userId);
        return null;
    }
    
    /**
     * 创建新会话
     */
    @Transactional
    public String createNewConversation(String userId) {
        String newConversationId = generateConversationId();
        
        AiConversation conversation = new AiConversation();
        conversation.setUserId(userId);
        conversation.setConversationId(newConversationId);
        conversation.setTitle("新对话");
        conversation.setStatus(1);
        conversation.setLastMessageAt(LocalDateTime.now());
        
        conversationMapper.insert(conversation);
        
        log.info("创建新会话: userId={}, conversationId={}", userId, newConversationId);
        return newConversationId;
    }
    
    /**
     * 保存 Dify 返回的会话ID
     * Dify 可能会返回新的 conversation_id，需要更新映射
     */
    @Transactional
    public void saveConversationMapping(String userId, String conversationId) {
        if (conversationId == null || conversationId.isEmpty()) {
            return;
        }
        
        AiConversation existing = conversationMapper.selectByUserAndConversation(userId, conversationId);
        if (existing == null) {
            AiConversation conversation = new AiConversation();
            conversation.setUserId(userId);
            conversation.setConversationId(conversationId);
            conversation.setTitle("新对话");
            conversation.setStatus(1);
            conversation.setLastMessageAt(LocalDateTime.now());
            
            conversationMapper.insert(conversation);
            log.info("保存新会话映射: userId={}, conversationId={}", userId, conversationId);
        }
    }
    
    /**
     * 获取用户的所有会话
     */
    public List<AiConversation> getUserConversations(String userId) {
        return conversationMapper.selectByUserId(userId);
    }
    
    /**
     * 归档会话
     */
    @Transactional
    public void archiveConversation(String userId, String conversationId) {
        conversationMapper.archiveConversation(userId, conversationId);
        log.info("归档会话: userId={}, conversationId={}", userId, conversationId);
    }
    
    /**
     * 删除会话及其消息
     */
    @Transactional
    public void deleteConversation(String userId, String conversationId) {
        // 先查询会话信息
        AiConversation conv = conversationMapper.selectByConversationId(conversationId);
        if (conv == null) {
            log.warn("会话不存在: conversationId={}", conversationId);
            return;
        }

        String mode = conv.getMode();

        // 删除消息
        messageMapper.deleteByConversation(conversationId);

        // 删除会话
        conversationMapper.deleteByConversationId(conversationId);

        // 清除相关缓存
        cacheService.clearAllConversationCache(userId, mode, conversationId);

        log.info("删除会话: userId={}, conversationId={}", userId, conversationId);
    }
    
    /**
     * 按模式分页获取用户的会话列表（带缓存）
     */
    public List<AiConversation> getUserConversations(String userId, String mode, int page) {
        int offset = page * PAGE_SIZE;

        // 只有第一页使用缓存
        if (page == 0) {
            // 尝试从缓存获取
            List<AiConversation> cached = cacheService.getCachedConversationList(
                userId, mode, offset, PAGE_SIZE);
            if (cached != null && !cached.isEmpty()) {
                log.debug("Conversation list cache hit for user {} mode {}", userId, mode);
                return cached;
            }
        }

        // 从数据库查询
        List<AiConversation> conversations = conversationMapper.selectByUserAndMode(
            userId, mode, offset, PAGE_SIZE);

        // 缓存第一页
        if (page == 0 && !conversations.isEmpty()) {
            cacheService.cacheConversationList(userId, mode, conversations);
        }

        return conversations;
    }
    
    /**
     * 创建指定模式的新会话
     */
    @Transactional
    public String createNewConversation(String userId, String mode) {
        String newConversationId;

        if ("CUSTOM".equalsIgnoreCase(mode)) {
            newConversationId = "custom-" + UUID.randomUUID().toString().replace("-", "");
        } else {
            newConversationId = UUID.randomUUID().toString().replace("-", "");
        }

        AiConversation conversation = new AiConversation();
        conversation.setUserId(userId);
        conversation.setConversationId(newConversationId);
        conversation.setMode(mode);
        conversation.setTitle("新对话");
        conversation.setStatus(1);
        conversation.setMessageCount(0);
        conversation.setLastMessageAt(LocalDateTime.now());

        conversationMapper.insert(conversation);

        // 清除会话列表缓存
        cacheService.clearConversationListCache(userId, mode);

        // 设置活跃会话
        cacheService.setActiveConversation(userId, newConversationId);

        log.info("创建新 {} 会话: userId={}, conversationId={}", mode, userId, newConversationId);
        return newConversationId;
    }
    
    /**
     * 保存消息并更新会话统计
     */
    @Transactional
    public void saveMessage(String conversationId, String role,
            String content, String thinking, String toolCalls) {
        // 保存消息
        AiConversationMessage message = new AiConversationMessage();
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        message.setThinking(thinking);
        message.setToolCalls(toolCalls);

        messageMapper.insert(message);

        // 更新会话统计
        String preview = content.length() > 50
            ? content.substring(0, 50) + "..."
            : content;
        conversationMapper.updateMessageStats(conversationId, preview);

        // 添加到缓存
        cacheService.cacheMessage(conversationId, message);

        log.debug("保存消息: conversationId={}, role={}", conversationId, role);
    }
    
    /**
     * 分页获取会话消息历史（带缓存）
     */
    public List<AiConversationMessage> getConversationMessages(
            String conversationId, int page) {
        int offset = page * PAGE_SIZE;

        // 只有第一页使用缓存
        if (page == 0) {
            // 尝试从缓存获取
            List<AiConversationMessage> cached = cacheService.getCachedMessages(
                conversationId, offset, PAGE_SIZE);
            if (cached != null && !cached.isEmpty()) {
                log.debug("Message cache hit for conversation {}", conversationId);
                return cached;
            }
        }

        // 从数据库查询
        List<AiConversationMessage> messages = messageMapper.selectByConversation(
            conversationId, PAGE_SIZE, offset);

        // 缓存第一页
        if (page == 0 && !messages.isEmpty()) {
            // 批量缓存消息
            for (AiConversationMessage msg : messages) {
                cacheService.cacheMessage(conversationId, msg);
            }
        }

        return messages;
    }
    
    /**
     * 更新会话标题
     */
    @Transactional
    public void updateTitle(String userId, String conversationId, String title) {
        // 查询会话获取模式
        AiConversation conv = conversationMapper.selectByConversationId(conversationId);
        if (conv == null) {
            log.warn("会话不存在: conversationId={}", conversationId);
            return;
        }

        conversationMapper.updateTitle(conversationId, title);

        // 清除会话列表缓存
        cacheService.clearConversationListCache(userId, conv.getMode());

        log.info("更新会话标题: userId={}, conversationId={}, title={}",
            userId, conversationId, title);
    }
    
    /**
     * 获取单个会话详情
     */
    public AiConversation getConversation(String conversationId) {
        return conversationMapper.selectByConversationId(conversationId);
    }
    
    /**
     * 生成唯一的会话ID
     */
    private String generateConversationId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
