package com.advertising.agent.mapper;

import com.advertising.agent.entity.AiAttachment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI Attachment Mapper
 */
@Mapper
public interface AiAttachmentMapper {
    
    /**
     * 插入附件
     */
    @Insert("INSERT INTO ai_attachment (id, conversation_id, message_id, file_name, file_type, file_size, file_path, status, created_at) " +
            "VALUES (#{id}, #{conversationId}, #{messageId}, #{fileName}, #{fileType}, #{fileSize}, #{filePath}, #{status}, NOW())")
    int insert(AiAttachment attachment);
    
    /**
     * 更新附件状态和提取的文本
     */
    @Update("UPDATE ai_attachment SET status = #{status}, extracted_text = #{extractedText}, summary = #{summary} WHERE id = #{id}")
    int update(AiAttachment attachment);
    
    /**
     * 根据对话ID查询附件
     */
    @Select("SELECT * FROM ai_attachment WHERE conversation_id = #{conversationId} ORDER BY created_at DESC")
    List<AiAttachment> selectByConversationId(@Param("conversationId") String conversationId);
    
    /**
     * 根据消息ID查询附件
     */
    @Select("SELECT * FROM ai_attachment WHERE message_id = #{messageId}")
    AiAttachment selectByMessageId(@Param("messageId") String messageId);
    
    /**
     * 根据ID查询
     */
    @Select("SELECT * FROM ai_attachment WHERE id = #{id}")
    AiAttachment selectById(@Param("id") String id);
}
