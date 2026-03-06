package com.touhuwai.dto.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 框架媒体查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FrameQueryParam extends BaseQueryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 框架编号（精确匹配）
     */
    private String frameNo;

    /**
     * 框架编号模糊搜索
     */
    private String frameNoLike;

    /**
     * 社区ID（精确匹配）
     */
    private Integer communityId;

    /**
     * 社区ID列表（IN查询）
     */
    private List<Integer> communityIdList;

    /**
     * 楼栋（精确匹配）
     */
    private String building;

    /**
     * 楼栋模糊搜索
     */
    private String buildingLike;

    /**
     * 单元（精确匹配）
     */
    private String unit;

    /**
     * 单元模糊搜索
     */
    private String unitLike;

    /**
     * 电梯（精确匹配）
     */
    private String elevator;

    /**
     * 电梯模糊搜索
     */
    private String elevatorLike;

    /**
     * 内外位置（精确匹配）
     */
    private Integer innerPosition;

    /**
     * 内外位置列表（IN查询）
     */
    private List<Integer> innerPositionList;

    /**
     * 城市（关联社区表查询）
     */
    private String city;

    /**
     * 楼栋名称模糊搜索（关联社区表）
     */
    private String buildingNameLike;
}
