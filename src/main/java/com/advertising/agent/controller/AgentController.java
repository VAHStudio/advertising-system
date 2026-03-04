package com.advertising.agent.controller;

import com.advertising.agent.dto.AgentChatRequest;
import com.advertising.agent.dto.AgentChatResponse;
import com.advertising.agent.dto.SmartPlanRequest;
import com.advertising.agent.dto.SmartPlanResult;
import com.advertising.agent.entity.AiConversation;
import com.advertising.agent.entity.AiMessage;
import com.advertising.agent.mapper.AiConversationMapper;
import com.advertising.agent.service.AgentOrchestratorService;
import com.advertising.agent.service.AiAgentService;
import com.advertising.common.Result;
import com.advertising.service.CommunityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent 控制器
 */
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {
    
    private final AgentOrchestratorService orchestratorService;
    private final CommunityService communityService;
    private final AiAgentService aiAgentService;
    private final AiConversationMapper conversationMapper;
    private final ObjectMapper objectMapper;
    
    /**
     * Agent 对话接口
     */
    @PostMapping("/chat")
    public Result<AgentChatResponse> chat(@RequestBody AgentChatRequest request) {
        // 获取对话ID
        String conversationId = request.getSessionId();
        
        // 保存用户消息（如果有conversationId）
        if (conversationId != null && !conversationId.isEmpty()) {
            try {
                String userMessage = request.getMessage();
                // 如果用户选择了按钮，构建更有意义的消息
                if (request.getSelectedValue() != null && !request.getSelectedValue().isEmpty()) {
                    if (userMessage == null || userMessage.isEmpty()) {
                        userMessage = "选择了: " + request.getSelectedValue();
                    }
                }
                aiAgentService.saveMessage(conversationId, AiMessage.ROLE_USER, userMessage, null);
            } catch (Exception e) {
                // 忽略保存失败的错误，不影响主流程
            }
        }
        
        // 调用服务处理对话
        Result<AgentChatResponse> result = orchestratorService.processChat(request);
        
        // 保存AI回复消息
        if (result.getCode() == 200 && result.getData() != null && conversationId != null && !conversationId.isEmpty()) {
            try {
                AgentChatResponse response = result.getData();
                String metadata = null;
                if (response.getActions() != null || response.getData() != null) {
                    Map<String, Object> metaMap = new HashMap<>();
                    if (response.getActions() != null) {
                        metaMap.put("actions", response.getActions());
                    }
                    if (response.getData() != null) {
                        metaMap.put("data", response.getData());
                    }
                    metadata = objectMapper.writeValueAsString(metaMap);
                }
                aiAgentService.saveMessage(conversationId, AiMessage.ROLE_ASSISTANT, response.getMessage(), metadata);
                
                // 更新对话的lastMessageAt
                AiConversation conversation = conversationMapper.selectById(conversationId);
                if (conversation != null) {
                    conversation.setLastMessageAt(java.time.LocalDateTime.now());
                    conversationMapper.update(conversation);
                }
            } catch (Exception e) {
                // 忽略保存失败的错误，不影响主流程
            }
        }
        
        return result;
    }
    
    /**
     * 获取可用城市列表
     */
    @GetMapping("/cities")
    public Result<List<String>> getCities() {
        // 从社区服务获取所有城市
        // TODO: 需要添加 getAllCities 方法到 CommunityService
        return Result.success(List.of("南京"));
    }
    
    /**
     * 智能创建方案（一键创建）
     */
    @PostMapping("/plan/create-smart")
    public Result<SmartPlanResult> createSmartPlan(@RequestBody SmartPlanRequest request) {
        // TODO: 实现智能创建方案
        return Result.error("功能开发中");
    }
}
