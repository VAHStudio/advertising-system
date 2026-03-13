package com.touhuwai.mapper;

import com.touhuwai.entity.AiConversationMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI 对话消息数据访问层
 * 用于存储和查询 CUSTOM 模式的消息历史
 */
@Mapper
public interface AiConversationMessageMapper {
    
    /**
     * 插入新消息
     */
    @Insert("INSERT INTO ai_conversation_message " +
            "(conversation_id, role, content, thinking, tool_calls, metadata) " +
            "VALUES (#{conversationId}, #{role}, #{content}, #{thinking}, #{toolCalls}, #{metadata})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AiConversationMessage message);
    
    /**
     * 根据会话ID查询消息列表（分页，按时间倒序）
     */
    @Select("SELECT * FROM ai_conversation_message " +
            "WHERE conversation_id = #{conversationId} " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<AiConversationMessage> selectByConversation(@Param("conversationId") String conversationId,
                                                      @Param("limit") int limit,
                                                      @Param("offset") int offset);
    
    /**
     * 统计会话的消息数量
     */
    @Select("SELECT COUNT(*) FROM ai_conversation_message WHERE conversation_id = #{conversationId}")
    int countByConversation(String conversationId);
    
    /**
     * 删除会话的所有消息
     */
    @Delete("DELETE FROM ai_conversation_message WHERE conversation_id = #{conversationId}")
    int deleteByConversation(String conversationId);
    
    /**
     * 根据ID查询消息
     */
    @Select("SELECT * FROM ai_conversation_message WHERE id = #{id}")
    AiConversationMessage selectById(Long id);
}
