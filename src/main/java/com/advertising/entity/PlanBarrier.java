package com.advertising.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 方案道闸明细实体类
 * 对应数据库表：plan_barrier
 */
@Data
@Schema(description = "方案道闸明细")
public class PlanBarrier implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "自增主键")
    private Integer id;
    
    @Schema(description = "方案id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer planId;
    
    @Schema(description = "道闸id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer barrierGateId;
    
    @Schema(description = "方案X社区id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer planCommunityId;
    
    @Schema(description = "发布档期开始")
    private LocalDate releaseDateBegin;
    
    @Schema(description = "发布档期结束")
    private LocalDate releaseDateEnd;
    
    @Schema(description = "发布状态：1-意向，2-锁位，3-待刊发，4-刊发中，5-可调，6-到期，7-已下刊")
    private Integer releaseStatus;
    
    // 关联对象
    @Schema(description = "方案信息")
    private Plan plan;
    
    @Schema(description = "道闸信息")
    private BarrierGate barrierGate;
    
    @Schema(description = "方案社区关联信息")
    private PlanCommunity planCommunity;
}
