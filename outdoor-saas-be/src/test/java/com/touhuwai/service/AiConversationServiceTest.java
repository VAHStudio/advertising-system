package com.touhuwai.service;

import com.touhuwai.entity.AiConversation;
import com.touhuwai.entity.AiConversationMessage;
import com.touhuwai.mapper.AiConversationMapper;
import com.touhuwai.mapper.AiConversationMessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AiConversationService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class AiConversationServiceTest {

    @Mock
    private AiConversationMapper conversationMapper;

    @Mock
    private AiConversationMessageMapper messageMapper;

    @InjectMocks
    private AiConversationService conversationService;

    private static final String TEST_USER_ID = "user123";
    private static final String TEST_CONV_ID = "conv456";

    @Test
    @DisplayName("应该能按模式分页获取会话列表")
    void testGetUserConversationsByMode() {
        // Given
        AiConversation conv1 = new AiConversation();
        conv1.setConversationId("conv1");
        conv1.setMode("CUSTOM");
        
        AiConversation conv2 = new AiConversation();
        conv2.setConversationId("conv2");
        conv2.setMode("CUSTOM");
        
        when(conversationMapper.selectByUserAndMode(TEST_USER_ID, "CUSTOM", 0, 10))
            .thenReturn(Arrays.asList(conv1, conv2));

        // When
        List<AiConversation> result = conversationService
            .getUserConversations(TEST_USER_ID, "CUSTOM", 0);

        // Then
        assertEquals(2, result.size());
        verify(conversationMapper).selectByUserAndMode(TEST_USER_ID, "CUSTOM", 0, 10);
    }

    @Test
    @DisplayName("应该能创建指定模式的新会话")
    void testCreateNewConversationWithMode() {
        // When
        String conversationId = conversationService
            .createNewConversation(TEST_USER_ID, "CUSTOM");

        // Then
        assertNotNull(conversationId);
        assertTrue(conversationId.startsWith("custom-"));
        verify(conversationMapper).insert(any(AiConversation.class));
    }

    @Test
    @DisplayName("应该能保存消息并更新会话统计")
    void testSaveMessage() {
        // Given
        String content = "测试消息内容";
        String thinking = "思考过程";

        // When
        conversationService.saveMessage(TEST_CONV_ID, "user", content, thinking, null);

        // Then
        verify(messageMapper).insert(any(AiConversationMessage.class));
        verify(conversationMapper).updateMessageStats(eq(TEST_CONV_ID), anyString());
    }

    @Test
    @DisplayName("应该能分页获取消息历史")
    void testGetConversationMessages() {
        // Given
        AiConversationMessage msg1 = new AiConversationMessage();
        msg1.setId(1L);
        msg1.setContent("消息1");
        
        AiConversationMessage msg2 = new AiConversationMessage();
        msg2.setId(2L);
        msg2.setContent("消息2");
        
        when(messageMapper.selectByConversation(TEST_CONV_ID, 10, 0))
            .thenReturn(Arrays.asList(msg1, msg2));

        // When
        List<AiConversationMessage> result = conversationService
            .getConversationMessages(TEST_CONV_ID, 0);

        // Then
        assertEquals(2, result.size());
        verify(messageMapper).selectByConversation(TEST_CONV_ID, 10, 0);
    }

    @Test
    @DisplayName("应该能更新会话标题")
    void testUpdateTitle() {
        // When
        conversationService.updateTitle(TEST_USER_ID, TEST_CONV_ID, "新标题");

        // Then
        verify(conversationMapper).updateTitle(TEST_CONV_ID, "新标题");
    }

    @Test
    @DisplayName("应该能删除会话及其消息")
    void testDeleteConversation() {
        // Given
        AiConversation conv = new AiConversation();
        conv.setConversationId(TEST_CONV_ID);
        conv.setMode("CUSTOM");
        when(conversationMapper.selectByConversationId(TEST_CONV_ID)).thenReturn(conv);

        // When
        conversationService.deleteConversation(TEST_USER_ID, TEST_CONV_ID);

        // Then
        verify(messageMapper).deleteByConversation(TEST_CONV_ID);
        verify(conversationMapper).deleteByConversationId(TEST_CONV_ID);
    }

    @Test
    @DisplayName("CUSTOM模式会话ID应该以custom-开头")
    void testCustomConversationIdFormat() {
        // When
        String conversationId = conversationService
            .createNewConversation(TEST_USER_ID, "CUSTOM");

        // Then
        assertTrue(conversationId.startsWith("custom-"));
        assertTrue(conversationId.length() > "custom-".length());
    }

    @Test
    @DisplayName("DIFY模式会话ID不应该以custom-开头")
    void testDifyConversationIdFormat() {
        // When
        String conversationId = conversationService
            .createNewConversation(TEST_USER_ID, "DIFY");

        // Then
        assertFalse(conversationId.startsWith("custom-"));
    }
}
