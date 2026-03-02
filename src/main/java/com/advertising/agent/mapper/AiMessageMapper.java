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
    @Insert("INSERT INTO ai_message (id, conversation_id, role, content, content_type, metadata, created_at) " +
            "VALUES (#{id}, #{conversationId}, #{role}, #{content}, #{contentType}, #{metadata}, NOW())")
    int insert(AiMessage message);
    
    /**
     * 根据对话ID查询消息列表
     */
    @Select("SELECT * FROM ai_message WHERE conversation_id = #{conversationId} ORDER BY created_at ASC")
    List<AiMessage> selectByConversationId(@Param("conversationId") String conversationId);
    
    /**
     * 根据ID查询
     */
    @Select("SELECT * FROM ai_message WHERE id = #{id}")
    AiMessage selectById(@Param("id") String id);
    
    /**
     * 删除对话的所有消息
     */
    @Delete("DELETE FROM ai_message WHERE conversation_id = #{conversationId}")
    int deleteByConversationId(@Param("conversationId") String conversationId);
}
