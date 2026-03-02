package com.advertising.agent.mapper;

import com.advertising.agent.entity.AiAgent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI Agent Mapper
 */
@Mapper
public interface AiAgentMapper {
    
    /**
     * 查询所有智能体
     */
    @Select("SELECT agent_id as id, name, role, name as title, null as avatar, description, welcome_message as welcomeMessage, system_prompt as systemPrompt, null as capabilities, 1 as status, created_at as createdAt, updated_at as updatedAt FROM ai_agent WHERE status = 'active' ORDER BY created_at")
    List<AiAgent> selectAll();
    
    /**
     * 根据ID查询
     */
    @Select("SELECT agent_id as id, name, role, name as title, null as avatar, description, welcome_message as welcomeMessage, system_prompt as systemPrompt, null as capabilities, 1 as status, created_at as createdAt, updated_at as updatedAt FROM ai_agent WHERE agent_id = #{id}")
    AiAgent selectById(@Param("id") String id);
    
    /**
     * 根据角色查询
     */
    @Select("SELECT agent_id as id, name, role, name as title, null as avatar, description, welcome_message as welcomeMessage, system_prompt as systemPrompt, null as capabilities, 1 as status, created_at as createdAt, updated_at as updatedAt FROM ai_agent WHERE role = #{role} AND status = 'active'")
    AiAgent selectByRole(@Param("role") String role);
}
