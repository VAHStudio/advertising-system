package com.advertising.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Agent 意图解析结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentIntent {
    
    /**
     * 动作类型
     */
    private String action;
    
    /**
     * 客户名称
     */
    private String customer;
    
    /**
     * 时间描述（原始文本）
     */
    private String timeDescription;
    
    /**
     * 解析后的日期范围
     */
    private DateRange dateRange;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 媒体类型：barrier(道闸)/frame(框架)
     */
    private String mediaType;
    
    /**
     * 提到的城市列表
     */
    private List<String> mentionedCities;
    
    /**
     * 确认的城市
     */
    private String confirmedCity;
    
    /**
     * 其他要求/备注
     */
    private String requirements;
    
    /**
     * 原始消息
     */
    private String originalMessage;
    
    /**
     * 置信度
     */
    private Double confidence;
    
    /**
     * 额外参数
     */
    private Map<String, Object> extraParams;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateRange {
        private LocalDate beginDate;
        private LocalDate endDate;
    }
    
    // 动作类型常量
    public static final String ACTION_CREATE_PLAN = "CREATE_PLAN";
    public static final String ACTION_QUERY_INVENTORY = "QUERY_INVENTORY";
    public static final String ACTION_QUERY = "QUERY";
    public static final String ACTION_MODIFY = "MODIFY";
    public static final String ACTION_UNKNOWN = "UNKNOWN";
}
