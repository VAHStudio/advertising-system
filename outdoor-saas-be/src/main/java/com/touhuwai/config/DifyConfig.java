package com.touhuwai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Dify API 配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "dify")
public class DifyConfig {
    
    /**
     * Dify API 密钥
     */
    private String apiKey;
    
    /**
     * Dify API 基础 URL
     */
    private String baseUrl = "https://api.dify.ai/v1";
    
    /**
     * Agent ID（可选）
     */
    private String agentId;
}
