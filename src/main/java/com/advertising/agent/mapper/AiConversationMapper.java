package com.advertising.agent.mapper;

import com.advertising.agent.entity.AiConversation;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI Conversation Mapper
 */
@Mapper
public interface AiConversationMapper {
    
    /**
     * 插入对话
     */
    @Insert("INSERT INTO ai_conversation (id, user_id, agent_id, agent_name, agent_avatar, title, context, status, message_count, created_at, updated_at) " +
            "VALUES (#{id}, #{userId}, #{agentId}, #{agentName}, #{agentAvatar}, #{title}, #{context}, #{status}, #{messageCount}, NOW(), NOW())")
    int insert(AiConversation conversation);
    
    /**
     * 根据ID查询
     */
    @Select("SELECT * FROM ai_conversation WHERE id = #{id}")
    AiConversation selectById(@Param("id") String id);
    
    /**
     * 查询用户的所有对话
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} AND status = 1 ORDER BY last_message_at DESC")
    List<AiConversation> selectByUserId(@Param("userId") String userId);
    
    /**
     * 查询用户的特定智能体对话
     */
    @Select("SELECT * FROM ai_conversation WHERE user_id = #{userId} AND agent_id = #{agentId} AND status = 1 ORDER BY last_message_at DESC")
    List<AiConversation> selectByUserAndAgent(@Param("userId") String userId, @Param("agentId") String agentId);
    
    /**
     * 更新对话
     */
    @Update("UPDATE ai_conversation SET title = #{title}, context = #{context}, message_count = #{messageCount}, " +
            "last_message_at = #{lastMessageAt}, updated_at = NOW() WHERE id = #{id}")
    int update(AiConversation conversation);
    
    /**
     * 更新消息数
     */
    @Update("UPDATE ai_conversation SET message_count = message_count + 1, last_message_at = NOW(), updated_at = NOW() WHERE id = #{id}")
    int incrementMessageCount(@Param("id") String id);
    
    /**
     * 删除对话（软删除）
     */
    @Update("UPDATE ai_conversation SET status = 2, updated_at = NOW() WHERE id = #{id}")
    int deleteById(@Param("id") String id);
}
