package com.advertising.agent.mapper;

import com.advertising.agent.entity.AiMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI Message Mapper
 */
@Mapper
public interface AiMessageMapper {
    
    /**
     * 插入消息
     */
    @Insert("INSERT INTO ai_messages (id, conversation_id, role, content, content_type, metadata, created_at) " +
            "VALUES (#{id}, #{conversationId}, #{role}, #{content}, #{contentType}, #{metadata}, NOW())")
    int insert(AiMessage message);

    /**
     * 根据对话ID查询消息列表（分页）
     */
    @Select("SELECT * FROM ai_messages WHERE conversation_id = #{conversationId} ORDER BY created_at ASC LIMIT #{limit} OFFSET #{offset}")
    List<AiMessage> selectByConversationIdWithPage(@Param("conversationId") String conversationId, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 根据对话ID查询消息列表（不分页）
     */
    @Select("SELECT * FROM ai_messages WHERE conversation_id = #{conversationId} ORDER BY created_at ASC")
    List<AiMessage> selectByConversationId(@Param("conversationId") String conversationId);

    /**
     * 统计对话消息总数
     */
    @Select("SELECT COUNT(*) FROM ai_messages WHERE conversation_id = #{conversationId}")
    int countByConversationId(@Param("conversationId") String conversationId);
    
    /**
     * 根据ID查询
     */
    @Select("SELECT * FROM ai_messages WHERE id = #{id}")
    AiMessage selectById(@Param("id") String id);

    /**
     * 删除对话的所有消息
     */
    @Delete("DELETE FROM ai_messages WHERE conversation_id = #{conversationId}")
    int deleteByConversationId(@Param("conversationId") String conversationId);
}
