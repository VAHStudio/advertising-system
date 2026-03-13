package com.touhuwai.config;

import io.agentscope.core.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AgentScope模型配置
 */
@Slf4j
@Configuration
public class AgentScopeModelConfig {
    
    @Value("${ai.custom.model.provider:openai}")
    private String provider;
    
    @Value("${ai.custom.model.api-key:}")
    private String apiKey;
    
    @Value("${ai.custom.model.model-name:qwen3-max}")
    private String modelName;
    
    @Value("${ai.custom.model.base-url:}")
    private String baseUrl;

    @Bean
    public ChatModelBase chatModel() {
        log.info("[AgentScope] Initializing {} model: {}", provider, modelName);

        return switch (provider.toLowerCase()) {
            case "dashscope" -> createDashScopeModel();
            case "openai" -> createOpenAIModel();
            case "ollama" -> createOllamaModel();
            case "anthropic" -> createAnthropicModel();
            default -> throw new IllegalArgumentException("Unknown model provider: " + provider);
        };
    }

    private ChatModelBase createDashScopeModel() {
        var builder = DashScopeChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName);

        if (baseUrl != null && !baseUrl.isEmpty()) {
            builder.baseUrl(baseUrl);
        }

        return builder.build();
    }

    private ChatModelBase createOpenAIModel() {
        var builder = OpenAIChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName);

        if (baseUrl != null && !baseUrl.isEmpty()) {
            builder.baseUrl(baseUrl);
        }

        return builder.build();
    }

    private ChatModelBase createOllamaModel() {
        String ollamaBaseUrl = baseUrl.isEmpty() ? "http://localhost:11434" : baseUrl;

        return OllamaChatModel.builder()
                .modelName(modelName)
                .baseUrl(ollamaBaseUrl)
                .build();
    }

    private ChatModelBase createAnthropicModel() {
        var builder = AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName.isEmpty() ? "claude-sonnet-4-5-20250929" : modelName);

        if (baseUrl != null && !baseUrl.isEmpty()) {
            builder.baseUrl(baseUrl);
        }
        return builder.build();
    }


}
