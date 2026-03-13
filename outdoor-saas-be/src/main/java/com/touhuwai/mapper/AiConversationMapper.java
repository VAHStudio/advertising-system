package com.touhuwai.mapper;

import com.touhuwai.entity.AiConversation;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI 对话会话数据访问层
 */
@Mapper
public interface AiConversationMapper {
    
    /**
     * 插入新会话
     */
    @Insert("INSERT INTO ai_conversation (user_id, conversation_id, title, status, last_message_at) " +
            "VALUES (#{userId}, #{conversationId}, #{title}, #{status}, #{lastMessageAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AiConversation conversation);
    
    /**
     * 根据用户ID和Dify会话ID查询
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} AND conversation_id = #{conversationId}")
    AiConversation selectByUserAndConversation(@Param("userId") String userId, @Param("conversationId") String conversationId);
    
    /**
     * 获取用户活跃的会话
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} AND status = 1 " +
            "ORDER BY last_message_at DESC LIMIT 1")
    AiConversation selectActiveByUserId(String userId);
    
    /**
     * 获取用户的所有会话
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} ORDER BY last_message_at DESC")
    List<AiConversation> selectByUserId(String userId);
    
    /**
     * 更新最后消息时间
     */
    @Update("UPDATE ai_conversation SET last_message_at = NOW() WHERE id = #{id}")
    int updateLastMessageTime(Long id);
    
    /**
     * 归档会话
     */
    @Update("UPDATE ai_conversation SET status = 0 WHERE user_id = #{userId} AND conversation_id = #{conversationId}")
    int archiveConversation(@Param("userId") String userId, @Param("conversationId") String conversationId);
    
    /**
     * 删除会话
     */
    @Delete("DELETE FROM ai_conversation WHERE user_id = #{userId} AND conversation_id = #{conversationId}")
    int deleteByUserAndConversation(@Param("userId") String userId, @Param("conversationId") String conversationId);
    
    /**
     * 根据用户ID和模式查询会话列表（分页）
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} AND mode = #{mode} " +
            "ORDER BY last_message_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<AiConversation> selectByUserAndMode(@Param("userId") String userId, 
                                              @Param("mode") String mode,
                                              @Param("offset") int offset, 
                                              @Param("limit") int limit);
    
    /**
     * 根据会话ID查询
     */
    @Select("SELECT * FROM ai_conversation WHERE conversation_id = #{conversationId}")
    AiConversation selectByConversationId(String conversationId);
    
    /**
     * 更新消息统计信息
     */
    @Update("UPDATE ai_conversation SET " +
            "message_count = message_count + 1, " +
            "last_message_preview = #{preview}, " +
            "last_message_at = NOW() " +
            "WHERE conversation_id = #{conversationId}")
    int updateMessageStats(@Param("conversationId") String conversationId, @Param("preview") String preview);
    
    /**
     * 更新会话标题
     */
    @Update("UPDATE ai_conversation SET title = #{title} WHERE conversation_id = #{conversationId}")
    int updateTitle(@Param("conversationId") String conversationId, @Param("title") String title);
    
    /**
     * 根据会话ID删除
     */
    @Delete("DELETE FROM ai_conversation WHERE conversation_id = #{conversationId}")
    int deleteByConversationId(String conversationId);
}
