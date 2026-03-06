package com.touhuwai.dto.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 道闸设备查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BarrierGateQueryParam extends BaseQueryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 道闸编号（精确匹配）
     */
    private String gateNo;

    /**
     * 道闸编号模糊搜索
     */
    private String gateNoLike;

    /**
     * 社区ID（精确匹配）
     */
    private Integer communityId;

    /**
     * 社区ID列表（IN查询）
     */
    private List<Integer> communityIdList;

    /**
     * 设备编号（精确匹配）
     */
    private String deviceNo;

    /**
     * 设备编号模糊搜索
     */
    private String deviceNoLike;

    /**
     * 门岗位置（精确匹配）
     */
    private String doorLocation;

    /**
     * 门岗位置模糊搜索
     */
    private String doorLocationLike;

    /**
     * 设备位置（精确匹配）
     */
    private Integer devicePosition;

    /**
     * 设备位置列表（IN查询）
     */
    private List<Integer> devicePositionList;

    /**
     * 屏幕位置（精确匹配）
     */
    private Integer screenPosition;

    /**
     * 屏幕位置列表（IN查询）
     */
    private List<Integer> screenPositionList;

    /**
     * 灯箱方向（精确匹配）
     */
    private Integer lightboxDirection;

    /**
     * 灯箱方向列表（IN查询）
     */
    private List<Integer> lightboxDirectionList;

    /**
     * 城市（关联社区表查询）
     */
    private String city;

    /**
     * 楼栋名称模糊搜索（关联社区表）
     */
    private String buildingNameLike;
}
