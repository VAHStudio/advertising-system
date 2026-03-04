package com.advertising.agent.service;

import com.advertising.agent.dto.AgentIntent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Kimi AI 服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KimiAgentService {
    
    @Value("${kimi.api.key}")
    private String apiKey;
    
    @Value("${kimi.api.endpoint:https://api.moonshot.cn/v1/chat/completions}")
    private String apiEndpoint;
    
    @Value("${kimi.api.model:kimi-latest}")
    private String model;
    
    @Value("${kimi.api.thinking:enabled}")
    private String thinkingMode;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 解析用户意图
     */
    public AgentIntent parseIntent(String userMessage) {
        try {
            String prompt = buildIntentPrompt(userMessage);
            String response = callKimiApi(prompt);
            
            // 解析 JSON 响应
            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            
            // Kimi 可能返回 Markdown 代码块，需要提取 JSON
            String jsonStr = extractJsonFromMarkdown(content);
            
            JsonNode intentNode = objectMapper.readTree(jsonStr);
            
            return AgentIntent.builder()
                    .action(intentNode.path("action").asText("UNKNOWN"))
                    .customer(intentNode.path("customer").asText(null))
                    .timeDescription(intentNode.path("timeDescription").asText(null))
                    .quantity(intentNode.path("quantity").isNull() ? null : intentNode.path("quantity").asInt())
                    .mediaType(intentNode.path("mediaType").asText(null))
                    .requirements(intentNode.path("requirements").asText(null))
                    .originalMessage(userMessage)
                    .confidence(intentNode.path("confidence").isNull() ? 0.0 : intentNode.path("confidence").asDouble())
                    .build();
                    
        } catch (Exception e) {
            log.error("解析意图失败: {}", e.getMessage(), e);
            return AgentIntent.builder()
                    .action("UNKNOWN")
                    .originalMessage(userMessage)
                    .confidence(0.0)
                    .build();
        }
    }
    
    /**
     * 生成确认消息
     */
    public String generateConfirmation(AgentIntent intent) {
        StringBuilder sb = new StringBuilder();
        sb.append("我将为您创建广告方案：\n\n");
        sb.append("📋 **方案概要**\n");
        
        if (intent.getCustomer() != null) {
            sb.append("• 客户：").append(intent.getCustomer()).append("\n");
        }
        if (intent.getTimeDescription() != null) {
            sb.append("• 时间：").append(intent.getTimeDescription()).append("\n");
        }
        if (intent.getQuantity() != null) {
            sb.append("• 数量：").append(intent.getQuantity()).append("个");
            if (intent.getMediaType() != null) {
                sb.append(intent.getMediaType().equals("barrier") ? "道闸" : "框架");
            }
            sb.append("\n");
        }
        if (intent.getRequirements() != null) {
            sb.append("• 要求：").append(intent.getRequirements()).append("\n");
        }
        
        sb.append("\n请确认是否正确？");
        
        return sb.toString();
    }
    
    /**
     * 调用 Kimi API (K2.5)
     */
    private String callKimiApi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        
        // 构建请求体，支持 thinking 模式控制
        Map<String, Object> thinkingConfig = Map.of("type", "disabled".equals(thinkingMode) ? "disabled" : "enabled");
        Map<String, Object> extraBody = Map.of("thinking", thinkingConfig);
        
        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", List.of(
                Map.of("role", "system", "content", "你是一个广告投放管理系统的AI助手，帮助用户创建广告方案。请用JSON格式返回结果。"),
                Map.of("role", "user", "content", prompt)
            ),
            "temperature", 1.0,
            "response_format", Map.of("type", "json_object"),
            "extra_body", extraBody
        );
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            return restTemplate.postForObject(apiEndpoint, request, String.class);
        } catch (Exception e) {
            log.error("调用 Kimi API 失败: {}", e.getMessage());
            throw new RuntimeException("AI 服务暂时不可用", e);
        }
    }
    
    /**
     * 构建意图识别 Prompt
     */
    private String buildIntentPrompt(String userMessage) {
        return """
            分析用户的广告方案创建需求，提取以下信息并以JSON格式返回：
            
            用户消息：%s
            
            需要提取的字段：
            1. action: 动作类型，可选值：
               - CREATE_PLAN: 创建方案
               - QUERY_INVENTORY: 查询销控/库存/空闲点位（关键词：销控、库存、空闲点位、空位、可用点位、查空位等）
               - QUERY: 查询
               - MODIFY: 修改
               - UNKNOWN: 无法识别
            2. customer: 客户名称（提取公司名称或品牌名）
            3. timeDescription: 时间描述（如"3月份"、"下个月"、"2025年春节"、"4月"等）
            4. quantity: 数量（整数，如果没有明确数字返回null）
            5. mediaType: 媒体类型，可选值：
               - barrier: 道闸
               - frame: 框架/电梯海报
               - null: 未明确指定
            6. requirements: 其他特殊要求或备注
            7. confidence: 置信度（0-1之间的小数）
            
            返回JSON格式示例：
            {
              "action": "CREATE_PLAN",
              "customer": "可口可乐",
              "timeDescription": "3月份",
              "quantity": 10,
              "mediaType": "barrier",
              "requirements": "选择空闲点位",
              "confidence": 0.95
            }
            
            销控查询示例：
            {
              "action": "QUERY_INVENTORY",
              "customer": null,
              "timeDescription": "4月份",
              "quantity": 100,
              "mediaType": "barrier",
              "requirements": "导出成表",
              "confidence": 0.95
            }
            
            只返回JSON，不要有其他说明文字。
            """.formatted(userMessage);
    }
    
    /**
     * 从 Markdown 代码块中提取 JSON
     */
    private String extractJsonFromMarkdown(String content) {
        if (content == null || content.isEmpty()) {
            return "{}";
        }
        
        // 移除 Markdown 代码块标记
        String json = content.trim();
        if (json.startsWith("```json")) {
            json = json.substring(7);
        } else if (json.startsWith("```")) {
            json = json.substring(3);
        }
        if (json.endsWith("```")) {
            json = json.substring(0, json.length() - 3);
        }
        
        return json.trim();
    }
}
