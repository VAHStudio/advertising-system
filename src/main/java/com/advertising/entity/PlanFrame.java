package com.advertising.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 方案框架明细实体类
 * 对应数据库表：plan_frame
 */
@Data
public class PlanFrame implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private Integer planId;
    
    private Integer frameId;
    
    private Integer planCommunityId;
    
    private LocalDate releaseDateBegin;
    
    private LocalDate releaseDateEnd;
    
    private Integer releaseStatus;
    
    // 关联对象
    private Plan plan;
    
    private Frame frame;
    
    private PlanCommunity planCommunity;
}
