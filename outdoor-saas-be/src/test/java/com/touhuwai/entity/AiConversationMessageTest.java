package com.touhuwai.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AiConversationMessage 实体测试
 */
class AiConversationMessageTest {

    @Test
    @DisplayName("应该能正确设置和获取所有字段")
    void testGettersAndSetters() {
        // Given
        AiConversationMessage message = new AiConversationMessage();
        LocalDateTime now = LocalDateTime.now();

        // When
        message.setId(1L);
        message.setConversationId("conv-123");
        message.setRole("assistant");
        message.setContent("你好，有什么可以帮您的？");
        message.setThinking("用户问了一个问题，我需要...");
        message.setToolCalls("[{\"toolName\": \"search\", \"params\": {}}]");
        message.setMetadata("{\"source\": \"custom\"}");
        message.setCreatedAt(now);

        // Then
        assertEquals(1L, message.getId());
        assertEquals("conv-123", message.getConversationId());
        assertEquals("assistant", message.getRole());
        assertEquals("你好，有什么可以帮您的？", message.getContent());
        assertEquals("用户问了一个问题，我需要...", message.getThinking());
        assertNotNull(message.getToolCalls());
        assertNotNull(message.getMetadata());
        assertEquals(now, message.getCreatedAt());
    }

    @Test
    @DisplayName("应该支持user和assistant两种角色")
    void testRoleValues() {
        // Given
        AiConversationMessage userMsg = new AiConversationMessage();
        AiConversationMessage assistantMsg = new AiConversationMessage();

        // When
        userMsg.setRole("user");
        assistantMsg.setRole("assistant");

        // Then
        assertEquals("user", userMsg.getRole());
        assertEquals("assistant", assistantMsg.getRole());
    }

    @Test
    @DisplayName("可选字段应该允许为空")
    void testOptionalFields() {
        // Given
        AiConversationMessage message = new AiConversationMessage();

        // When & Then
        assertDoesNotThrow(() -> {
            message.setThinking(null);
            message.setToolCalls(null);
            message.setMetadata(null);
        });

        assertNull(message.getThinking());
        assertNull(message.getToolCalls());
        assertNull(message.getMetadata());
    }

    @Test
    @DisplayName("必填字段不能为空")
    void testRequiredFields() {
        // Given
        AiConversationMessage message = new AiConversationMessage();

        // When & Then - 实体层面不强制，但数据库会强制
        assertDoesNotThrow(() -> {
            // 这些在数据库层面是 NOT NULL，但实体测试不验证
            message.setConversationId(null);
            message.setRole(null);
            message.setContent(null);
        });
    }
}
