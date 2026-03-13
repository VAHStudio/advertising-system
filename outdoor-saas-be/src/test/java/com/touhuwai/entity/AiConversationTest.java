package com.touhuwai.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AiConversation 实体测试
 * 验证实体类结构、Getter/Setter、Builder模式
 */
class AiConversationTest {

    @Test
    @DisplayName("应该能正确设置和获取所有字段")
    void testGettersAndSetters() {
        // Given
        AiConversation conversation = new AiConversation();
        LocalDateTime now = LocalDateTime.now();

        // When
        conversation.setId(1L);
        conversation.setUserId("user123");
        conversation.setConversationId("conv-456");
        conversation.setMode("CUSTOM");
        conversation.setTitle("测试会话");
        conversation.setStatus(1);
        conversation.setMessageCount(10);
        conversation.setLastMessagePreview("这是最后一条消息...");
        conversation.setCreatedAt(now);
        conversation.setUpdatedAt(now);
        conversation.setLastMessageAt(now);

        // Then
        assertEquals(1L, conversation.getId());
        assertEquals("user123", conversation.getUserId());
        assertEquals("conv-456", conversation.getConversationId());
        assertEquals("CUSTOM", conversation.getMode());
        assertEquals("测试会话", conversation.getTitle());
        assertEquals(1, conversation.getStatus());
        assertEquals(10, conversation.getMessageCount());
        assertEquals("这是最后一条消息...", conversation.getLastMessagePreview());
        assertEquals(now, conversation.getCreatedAt());
        assertEquals(now, conversation.getUpdatedAt());
        assertEquals(now, conversation.getLastMessageAt());
    }

    @Test
    @DisplayName("新创建的实体应该具有默认值")
    void testDefaultValues() {
        // Given & When
        AiConversation conversation = new AiConversation();

        // Then
        assertNull(conversation.getId());
        assertNull(conversation.getMode());  // 注意：数据库有默认值，但实体没有
        assertNull(conversation.getMessageCount());
        assertNull(conversation.getLastMessagePreview());
    }

    @Test
    @DisplayName("应该支持DIFY和CUSTOM两种模式")
    void testModeValues() {
        // Given
        AiConversation difyConv = new AiConversation();
        AiConversation customConv = new AiConversation();

        // When
        difyConv.setMode("DIFY");
        customConv.setMode("CUSTOM");

        // Then
        assertEquals("DIFY", difyConv.getMode());
        assertEquals("CUSTOM", customConv.getMode());
    }

    @Test
    @DisplayName("应该能正确处理空值")
    void testNullValues() {
        // Given
        AiConversation conversation = new AiConversation();

        // When & Then - 不应该抛出异常
        assertDoesNotThrow(() -> {
            conversation.setTitle(null);
            conversation.setLastMessagePreview(null);
            conversation.setMode(null);
        });

        assertNull(conversation.getTitle());
        assertNull(conversation.getLastMessagePreview());
        assertNull(conversation.getMode());
    }
}
