package com.advertising.agent.dsl;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DSL (Domain Specific Language) 定义
 * 用于描述点位查询的意图和参数
 */
@Data
@Builder
public class PointSelectionDSL {
    
    /**
     * 查询意图类型
     */
    private IntentType intentType;
    
    /**
     * 媒体类型：barrier(道闸) / frame(框架)
     */
    private String mediaType;
    
    /**
     * 目标数量
     */
    private Integer targetCount;
    
    /**
     * 投放日期范围
     */
    private DateRange dateRange;
    
    /**
     * 分配策略
     */
    private DistributionStrategy distributionStrategy;
    
    /**
     * 筛选条件
     */
    private FilterConditions filterConditions;
    
    /**
     * 排序规则
     */
    private List<SortRule> sortRules;
    
    /**
     * 限制条件
     */
    private LimitConditions limitConditions;
    
    @Data
    @Builder
    public static class DateRange {
        private LocalDate beginDate;
        private LocalDate endDate;
    }
    
    @Data
    @Builder
    public static class DistributionStrategy {
        /**
         * 分配类型：per_community(每社区N个) / total(总数)
         */
        private String type;
        
        /**
         * 每社区数量（当type=per_community时使用）
         */
        private Integer perCommunityCount;
        
        /**
         * 最大社区数
         */
        private Integer maxCommunities;
        
        /**
         * 最小社区数
         */
        private Integer minCommunities;
    }
    
    @Data
    @Builder
    public static class FilterConditions {
        /**
         * 指定社区ID列表（可选）
         */
        private List<Integer> communityIds;
        
        /**
         * 城市筛选
         */
        private String city;
        
        /**
         * 区域筛选
         */
        private String district;
        
        /**
         * 设备位置：entrance(进口) / exit(出口) / both(进出口)
         */
        private String devicePosition;
        
        /**
         * 画面位置：A / B
         */
        private String screenPosition;
        
        /**
         * 灯箱朝向：outside(朝外) / inside(朝内) / street(临街)
         */
        private String lightboxDirection;
        
        /**
         * 梯内位置：left(左) / middle(中) / right(右)
         */
        private String innerPosition;
        
        /**
         * 是否只选空闲的
         */
        private Boolean onlyAvailable;
        
        /**
         * 排除已被占用的（档期冲突）
         */
        private Boolean excludeOccupied;
        
        /**
         * 额外的自定义条件
         */
        private Map<String, Object> customConditions;
    }
    
    @Data
    @Builder
    public static class SortRule {
        /**
         * 排序字段：priority(优先级) / random(随机) / community_id(社区ID)
         */
        private String field;
        
        /**
         * 排序方向：asc / desc
         */
        private String direction;
    }
    
    @Data
    @Builder
    public static class LimitConditions {
        /**
         * 最大结果数
         */
        private Integer maxResults;
        
        /**
         * 每社区最大数量
         */
        private Integer maxPerCommunity;
        
        /**
         * 每社区最小数量
         */
        private Integer minPerCommunity;
    }
    
    /**
     * 意图类型枚举
     */
    public enum IntentType {
        SELECT_POINTS,      // 选择点位
        QUERY_AVAILABILITY, // 查询可用性
        CHECK_CONFLICT,     // 检查冲突
        DISTRIBUTE_POINTS   // 分配点位
    }
}