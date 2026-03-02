package com.advertising.agent.entity;

import java.time.LocalDateTime;

/**
 * AI Attachment 附件实体
 */
public class AiAttachment {

    private String id;
    private String conversationId;
    private String messageId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String filePath;
    private String extractedText;
    private String summary;
    private Integer status;
    private LocalDateTime createdAt;

    // 状态常量
    public static final int STATUS_UPLOADING = 0;
    public static final int STATUS_PROCESSING = 1;
    public static final int STATUS_READY = 2;
    public static final int STATUS_ERROR = 3;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getExtractedText() { return extractedText; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
