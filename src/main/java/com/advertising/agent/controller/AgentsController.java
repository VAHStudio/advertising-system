package com.advertising.agent.controller;

import com.advertising.agent.entity.AiAgent;
import com.advertising.agent.entity.AiAttachment;
import com.advertising.agent.entity.AiConversation;
import com.advertising.agent.service.AiAgentService;
import com.advertising.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI Agents 控制器
 * 支持四个独立智能体、对话管理、文件上传
 */
@RestController
@RequestMapping("/api/agents")
public class AgentsController {
    
    private static final Logger log = LoggerFactory.getLogger(AgentsController.class);
    
    private final AiAgentService agentService;
    
    @Autowired
    public AgentsController(AiAgentService agentService) {
        this.agentService = agentService;
    }
    
    /**
     * 获取所有智能体列表
     */
    @GetMapping("/list")
    public Result<List<AiAgent>> getAgents() {
        try {
            List<AiAgent> agents = agentService.getAllAgents();
            return Result.success(agents);
        } catch (Exception e) {
            log.error("获取智能体列表失败", e);
            return Result.error("获取智能体列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取单个智能体详情
     */
    @GetMapping("/{agentId}")
    public Result<AiAgent> getAgent(@PathVariable String agentId) {
        try {
            AiAgent agent = agentService.getAgentById(agentId);
            if (agent == null) {
                return Result.error("智能体不存在");
            }
            return Result.success(agent);
        } catch (Exception e) {
            log.error("获取智能体详情失败", e);
            return Result.error("获取智能体详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 开始新对话
     */
    @PostMapping("/conversations/start")
    public Result<Map<String, Object>> startConversation(
            @RequestBody Map<String, String> request) {
        try {
            String userId = request.getOrDefault("user_id", "user_001");
            String agentId = request.get("agent_id");
            
            if (agentId == null || agentId.isEmpty()) {
                return Result.error("请选择智能体");
            }
            
            Map<String, Object> result = agentService.startConversation(userId, agentId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("开始对话失败", e);
            return Result.error("开始对话失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取对话历史（支持分页）
     */
    @GetMapping("/conversations/{conversationId}")
    public Result<Map<String, Object>> getConversationHistory(
            @PathVariable String conversationId,
            @RequestParam String user_id,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            // 获取对话基本信息
            Map<String, Object> conversationData = agentService.getConversationHistory(conversationId);

            // 分页获取消息
            Map<String, Object> messagesData = agentService.getMessagesByConversation(conversationId, pageNum, pageSize);

            // 合并结果
            Map<String, Object> result = new HashMap<>();
            result.put("conversation", conversationData.get("conversation"));
            result.put("messages", messagesData.get("messages"));
            result.put("pagination", Map.of(
                "total", messagesData.get("total"),
                "pageNum", messagesData.get("pageNum"),
                "pageSize", messagesData.get("pageSize"),
                "totalPages", messagesData.get("totalPages"),
                "hasMore", messagesData.get("hasMore")
            ));

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取对话历史失败", e);
            return Result.error("获取对话历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的所有对话
     */
    @GetMapping("/conversations/user/{userId}")
    public Result<List<AiConversation>> getUserConversations(
            @PathVariable String userId,
            @RequestParam(required = false) String agent_id) {
        try {
            List<AiConversation> conversations = agentService.getUserConversations(userId, agent_id);
            return Result.success(conversations);
        } catch (Exception e) {
            log.error("获取用户对话列表失败", e);
            return Result.error("获取用户对话列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送消息
     */
    @PostMapping("/conversations/{conversationId}/messages")
    public Result<Map<String, Object>> sendMessage(
            @PathVariable String conversationId,
            @RequestBody Map<String, Object> request) {
        try {
            String content = (String) request.get("content");
            @SuppressWarnings("unchecked")
            List<String> attachmentIds = (List<String>) request.get("attachment_ids");
            
            if (content == null || content.isEmpty()) {
                return Result.error("消息内容不能为空");
            }
            
            Map<String, Object> result = agentService.sendMessage(conversationId, content, attachmentIds);
            return Result.success(result);
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return Result.error("发送消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传文件
     */
    @PostMapping("/conversations/{conversationId}/attachments")
    public Result<AiAttachment> uploadFile(
            @PathVariable String conversationId,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            AiAttachment attachment = agentService.uploadFile(conversationId, file);
            return Result.success(attachment);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return Result.error("上传文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除对话
     */
    @DeleteMapping("/conversations/{conversationId}")
    public Result<Void> deleteConversation(@PathVariable String conversationId) {
        try {
            agentService.deleteConversation(conversationId);
            return Result.success();
        } catch (Exception e) {
            log.error("删除对话失败", e);
            return Result.error("删除对话失败: " + e.getMessage());
        }
    }
}
