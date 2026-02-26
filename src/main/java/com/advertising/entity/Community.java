package com.advertising.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 社区信息实体类
 * 对应数据库表：community
 */
@Data
@Schema(description = "社区信息")
public class Community implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "自增主键")
    private Integer id;
    
    @Schema(description = "社区编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String communityNo;
    
    @Schema(description = "楼盘名称")
    private String buildingName;
    
    @Schema(description = "楼盘地址")
    private String buildingAddress;
    
    @Schema(description = "纬度")
    private BigDecimal coordLat;
    
    @Schema(description = "经度")
    private BigDecimal coordLng;
    
    @Schema(description = "城市")
    private String city;
}
