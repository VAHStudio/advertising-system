package com.touhuwai.dto.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 查询参数基类
 * 包含分页、排序、时间范围、关键词搜索等通用参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseQueryParam extends com.touhuwai.common.PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关键词搜索（用于名称、编号等字段的模糊搜索）
     */
    private String keyword;

    /**
     * 开始时间（用于时间范围查询）
     */
    private LocalDateTime startTime;

    /**
     * 结束时间（用于时间范围查询）
     */
    private LocalDateTime endTime;

    /**
     * 创建时间开始
     */
    private LocalDateTime createdAtBegin;

    /**
     * 创建时间结束
     */
    private LocalDateTime createdAtEnd;

    /**
     * 更新时间开始
     */
    private LocalDateTime updatedAtBegin;

    /**
     * 更新时间结束
     */
    private LocalDateTime updatedAtEnd;

    /**
     * 状态列表（用于IN查询）
     */
    private String statusList;

    /**
     * ID列表（用于IN查询）
     */
    private String idList;
}
