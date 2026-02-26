package com.advertising.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 方案社区关联实体类
 * 对应数据库表：plan_community
 */
@Data
@Schema(description = "方案社区关联")
public class PlanCommunity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "自增主键")
    private Integer id;
    
    @Schema(description = "方案id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer planId;
    
    @Schema(description = "社区id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer communityId;
    
    @Schema(description = "发布档期开始")
    private LocalDate releaseDateBegin;
    
    @Schema(description = "发布档期结束")
    private LocalDate releaseDateEnd;
    
    @Schema(description = "道闸-需求数量")
    private Integer barrierRequiredQty;
    
    @Schema(description = "框架-需求数量")
    private Integer frameRequiredQty;
    
    // 关联对象
    @Schema(description = "方案信息")
    private Plan plan;
    
    @Schema(description = "社区信息")
    private Community community;
}
