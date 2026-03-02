package com.advertising.agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Kimi AI 服务
 * 调用Kimi K2.5模型API
 */
@Service
public class KimiService {
    
    private static final Logger log = LoggerFactory.getLogger(KimiService.class);
    
    @Value("${kimi.api.key}")
    private String apiKey;
    
    @Value("${kimi.api.endpoint}")
    private String apiEndpoint;
    
    @Value("${kimi.api.model}")
    private String model;
    
    @Value("${kimi.api.thinking:enabled}")
    private String thinkingMode;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public KimiService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }
    
    /**
     * 发送消息到Kimi并获取回复
     * 
     * @param systemPrompt 系统提示词
     * @param messages 历史消息列表
     * @return AI的回复内容
     */
    public String chat(String systemPrompt, List<Map<String, String>> messages) {
        try {
            // 构建请求体
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            
            // 添加系统消息
            ArrayNode messageArray = requestBody.putArray("messages");
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                ObjectNode systemMessage = messageArray.addObject();
                systemMessage.put("role", "system");
                systemMessage.put("content", systemPrompt);
            }
            
            // 添加历史消息
            for (Map<String, String> msg : messages) {
                ObjectNode messageNode = messageArray.addObject();
                messageNode.put("role", msg.get("role"));
                messageNode.put("content", msg.get("content"));
            }
            
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 4096);
            
            // 添加 thinking 控制参数 (Kimi K2.5 支持)
            ObjectNode extraBody = requestBody.putObject("extra_body");
            ObjectNode thinking = extraBody.putObject("thinking");
            thinking.put("type", "disabled".equals(thinkingMode) ? "disabled" : "enabled");
            
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            // 发送请求
            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            log.info("Sending request to Kimi API: {}", apiEndpoint);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                apiEndpoint, 
                request, 
                String.class
            );
            
            // 解析响应
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                JsonNode choices = responseBody.get("choices");
                if (choices != null && choices.isArray() && choices.size() > 0) {
                    JsonNode message = choices.get(0).get("message");
                    if (message != null) {
                        String content = message.get("content").asText();
                        log.info("Received response from Kimi API, length: {}", content.length());
                        return content;
                    }
                }
                log.error("Invalid response format from Kimi API: {}", response.getBody());
                return "抱歉，AI服务返回了无效的响应格式。";
            } else {
                log.error("Kimi API error: {} - {}", response.getStatusCode(), response.getBody());
                return "抱歉，AI服务暂时不可用，请稍后再试。";
            }
            
        } catch (Exception e) {
            log.error("Error calling Kimi API", e);
            return "抱歉，调用AI服务时出错: " + e.getMessage();
        }
    }
    
    /**
     * 发送单条消息（无历史上下文）
     */
    public String chat(String systemPrompt, String userMessage) {
        List<Map<String, String>> messages = List.of(
            Map.of("role", "user", "content", userMessage)
        );
        return chat(systemPrompt, messages);
    }
}
