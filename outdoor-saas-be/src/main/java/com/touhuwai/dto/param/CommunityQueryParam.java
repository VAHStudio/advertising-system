package com.touhuwai.dto.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 社区信息查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunityQueryParam extends BaseQueryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 社区编号（精确匹配）
     */
    private String communityNo;

    /**
     * 社区编号模糊搜索
     */
    private String communityNoLike;

    /**
     * 楼栋名称（精确匹配）
     */
    private String buildingName;

    /**
     * 楼栋名称模糊搜索
     */
    private String buildingNameLike;

    /**
     * 楼栋地址（精确匹配）
     */
    private String buildingAddress;

    /**
     * 楼栋地址模糊搜索
     */
    private String buildingAddressLike;

    /**
     * 城市（精确匹配）
     */
    private String city;

    /**
     * 城市列表（IN查询）
     */
    private String cityList;

    /**
     * 纬度最小值
     */
    private BigDecimal coordLatMin;

    /**
     * 纬度最大值
     */
    private BigDecimal coordLatMax;

    /**
     * 经度最小值
     */
    private BigDecimal coordLngMin;

    /**
     * 经度最大值
     */
    private BigDecimal coordLngMax;
}
