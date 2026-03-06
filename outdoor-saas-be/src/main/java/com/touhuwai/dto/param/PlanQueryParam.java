package com.touhuwai.dto.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 投放方案查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PlanQueryParam extends BaseQueryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 方案编号（精确匹配）
     */
    private String planNo;

    /**
     * 方案编号模糊搜索
     */
    private String planNoLike;

    /**
     * 方案名称（精确匹配）
     */
    private String planName;

    /**
     * 方案名称模糊搜索
     */
    private String planNameLike;

    /**
     * 客户名称（精确匹配）
     */
    private String customer;

    /**
     * 客户名称模糊搜索
     */
    private String customerLike;

    /**
     * 投放状态（精确匹配）
     */
    private Integer releaseStatus;

    /**
     * 投放状态列表（IN查询）
     */
    private List<Integer> releaseStatusList;

    /**
     * 销售类型（精确匹配）
     */
    private Integer salesType;

    /**
     * 销售类型列表（IN查询）
     */
    private List<Integer> salesTypeList;

    /**
     * 媒体类型（精确匹配）
     */
    private Integer mediaType;

    /**
     * 媒体类型列表（IN查询）
     */
    private List<Integer> mediaTypeList;

    /**
     * 投放开始日期（大于等于）
     */
    private LocalDate releaseDateBeginFrom;

    /**
     * 投放开始日期（小于等于）
     */
    private LocalDate releaseDateBeginTo;

    /**
     * 投放结束日期（大于等于）
     */
    private LocalDate releaseDateEndFrom;

    /**
     * 投放结束日期（小于等于）
     */
    private LocalDate releaseDateEndTo;

    /**
     * 销售人员（精确匹配）
     */
    private String salesPerson;

    /**
     * 销售人员模糊搜索
     */
    private String salesPersonLike;

    /**
     * 预算最小值
     */
    private Long budgetMin;

    /**
     * 预算最大值
     */
    private Long budgetMax;

    /**
     * 预计触达人数最小值
     */
    private Integer estimatedReachMin;

    /**
     * 预计触达人数最大值
     */
    private Integer estimatedReachMax;
}
