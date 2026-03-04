package com.advertising.agent.service;

import com.advertising.agent.entity.*;
import com.advertising.agent.mapper.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * AI Agent 管理服务
 */
@Service
public class AiAgentService {
    
    private static final Logger log = LoggerFactory.getLogger(AiAgentService.class);
    
    private final AiAgentMapper agentMapper;
    private final AiConversationMapper conversationMapper;
    private final AiMessageMapper messageMapper;
    private final AiAttachmentMapper attachmentMapper;
    private final ObjectMapper objectMapper;
    private final KimiService kimiService;

    @Autowired
    public AiAgentService(AiAgentMapper agentMapper, AiConversationMapper conversationMapper,
                         AiMessageMapper messageMapper, AiAttachmentMapper attachmentMapper,
                         ObjectMapper objectMapper, KimiService kimiService) {
        this.agentMapper = agentMapper;
        this.conversationMapper = conversationMapper;
        this.messageMapper = messageMapper;
        this.attachmentMapper = attachmentMapper;
        this.objectMapper = objectMapper;
        this.kimiService = kimiService;
    }
    
    // 文件上传路径
    private static final String UPLOAD_DIR = "uploads/ai-attachments/";
    
    /**
     * 获取所有智能体
     */
    public List<AiAgent> getAllAgents() {
        return agentMapper.selectAll();
    }
    
    /**
     * 根据ID获取智能体
     */
    public AiAgent getAgentById(String agentId) {
        return agentMapper.selectById(agentId);
    }
    
    /**
     * 根据角色获取智能体
     */
    public AiAgent getAgentByRole(String role) {
        return agentMapper.selectByRole(role);
    }
    
    /**
     * 开始新对话
     */
    @Transactional
    public Map<String, Object> startConversation(String userId, String agentId) {
        // 获取智能体信息
        AiAgent agent = agentMapper.selectById(agentId);
        if (agent == null) {
            throw new RuntimeException("智能体不存在");
        }
        
        // 创建对话
        AiConversation conversation = new AiConversation();
        conversation.setId(UUID.randomUUID().toString().replace("-", ""));
        conversation.setUserId(userId);
        conversation.setAgentId(agentId);
        conversation.setAgentName(agent.getName());
        conversation.setAgentAvatar(agent.getAvatar());
        conversation.setTitle("新对话");
        conversation.setStatus(AiConversation.STATUS_ACTIVE);
        conversation.setMessageCount(0);
        conversationMapper.insert(conversation);
        
        // 创建系统欢迎消息
        AiMessage welcomeMessage = new AiMessage();
        welcomeMessage.setId(UUID.randomUUID().toString().replace("-", ""));
        welcomeMessage.setConversationId(conversation.getId());
        welcomeMessage.setRole(AiMessage.ROLE_ASSISTANT);
        welcomeMessage.setContent(agent.getWelcomeMessage());
        welcomeMessage.setContentType(AiMessage.TYPE_TEXT);
        messageMapper.insert(welcomeMessage);
        
        // 更新消息数
        conversationMapper.incrementMessageCount(conversation.getId());
        
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("conversation", conversation);
        result.put("messages", Collections.singletonList(welcomeMessage));
        
        return result;
    }
    
    /**
     * 获取对话历史
     */
    public Map<String, Object> getConversationHistory(String conversationId) {
        AiConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }
        
        List<AiMessage> messages = messageMapper.selectByConversationId(conversationId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("conversation", conversation);
        result.put("messages", messages);
        
        return result;
    }
    
    /**
     * 获取用户的所有对话
     */
    public List<AiConversation> getUserConversations(String userId, String agentId) {
        if (agentId != null && !agentId.isEmpty()) {
            return conversationMapper.selectByUserAndAgent(userId, agentId);
        }
        return conversationMapper.selectByUserId(userId);
    }
    
    /**
     * 发送消息并调用Kimi AI获取回复
     */
    @Transactional
    public Map<String, Object> sendMessage(String conversationId, String content, List<String> attachmentIds) {
        // 获取对话信息
        AiConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }

        // 获取智能体信息
        AiAgent agent = agentMapper.selectById(conversation.getAgentId());
        if (agent == null) {
            throw new RuntimeException("智能体不存在");
        }

        // 保存用户消息
        AiMessage userMessage = new AiMessage();
        userMessage.setId(UUID.randomUUID().toString().replace("-", ""));
        userMessage.setConversationId(conversationId);
        userMessage.setRole(AiMessage.ROLE_USER);
        userMessage.setContent(content);
        userMessage.setContentType(AiMessage.TYPE_TEXT);
        messageMapper.insert(userMessage);

        // 更新消息数
        conversationMapper.incrementMessageCount(conversationId);

        // 获取历史消息作为上下文
        List<AiMessage> historyMessages = messageMapper.selectByConversationId(conversationId);
        List<Map<String, String>> contextMessages = new ArrayList<>();

        // 只保留最近20条消息作为上下文（避免token超限）
        int startIndex = Math.max(0, historyMessages.size() - 20);
        for (int i = startIndex; i < historyMessages.size() - 1; i++) {
            AiMessage msg = historyMessages.get(i);
            contextMessages.add(Map.of(
                "role", msg.getRole(),
                "content", msg.getContent()
            ));
        }

        // 添加当前用户消息
        contextMessages.add(Map.of("role", "user", "content", content));

        // 调用Kimi API获取回复
        String systemPrompt = agent.getSystemPrompt();
        String aiResponse = kimiService.chat(systemPrompt, contextMessages);

        // 保存AI回复
        AiMessage assistantMessage = new AiMessage();
        assistantMessage.setId(UUID.randomUUID().toString().replace("-", ""));
        assistantMessage.setConversationId(conversationId);
        assistantMessage.setRole(AiMessage.ROLE_ASSISTANT);
        assistantMessage.setContent(aiResponse);
        assistantMessage.setContentType(AiMessage.TYPE_TEXT);
        messageMapper.insert(assistantMessage);

        conversationMapper.incrementMessageCount(conversationId);

        Map<String, Object> result = new HashMap<>();
        result.put("message", assistantMessage);

        return result;
    }
    
    /**
     * 上传文件
     */
    @Transactional
    public AiAttachment uploadFile(String conversationId, MultipartFile file) throws IOException {
        // 创建上传目录
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 生成文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        
        // 保存文件
        Files.copy(file.getInputStream(), filePath);
        
        // 创建附件记录
        AiAttachment attachment = new AiAttachment();
        attachment.setId(UUID.randomUUID().toString().replace("-", ""));
        attachment.setConversationId(conversationId);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setFilePath(filePath.toString());
        attachment.setStatus(AiAttachment.STATUS_UPLOADING);
        attachmentMapper.insert(attachment);
        
        // TODO: 异步处理文件内容提取
        
        return attachment;
    }
    
    /**
     * 删除对话
     */
    @Transactional
    public void deleteConversation(String conversationId) {
        conversationMapper.deleteById(conversationId);
        messageMapper.deleteByConversationId(conversationId);
    }

    /**
     * 保存单条消息
     */
    @Transactional
    public AiMessage saveMessage(String conversationId, String role, String content, String metadata) {
        AiMessage message = new AiMessage();
        message.setId(UUID.randomUUID().toString().replace("-", ""));
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        message.setContentType(AiMessage.TYPE_TEXT);
        message.setMetadata(metadata);
        messageMapper.insert(message);

        // 更新对话消息数
        conversationMapper.incrementMessageCount(conversationId);

        return message;
    }

    /**
     * 分页查询消息
     */
    public Map<String, Object> getMessagesByConversation(String conversationId, int pageNum, int pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询消息列表
        List<AiMessage> messages = messageMapper.selectByConversationIdWithPage(conversationId, pageSize, offset);

        // 查询总数
        int total = messageMapper.countByConversationId(conversationId);

        // 计算总页数
        int totalPages = (total + pageSize - 1) / pageSize;

        Map<String, Object> result = new HashMap<>();
        result.put("messages", messages);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", totalPages);
        result.put("hasMore", pageNum < totalPages);

        return result;
    }
}
