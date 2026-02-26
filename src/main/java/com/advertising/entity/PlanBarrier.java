package com.advertising.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 方案道闸明细实体类
 * 对应数据库表：plan_barrier
 */
@Data
public class PlanBarrier implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private Integer planId;
    
    private Integer barrierGateId;
    
    private Integer planCommunityId;
    
    private LocalDate releaseDateBegin;
    
    private LocalDate releaseDateEnd;
    
    private Integer releaseStatus;
    
    // 关联对象
    private Plan plan;
    
    private BarrierGate barrierGate;
    
    private PlanCommunity planCommunity;
}
