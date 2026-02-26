package com.advertising.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 方案社区关联实体类
 * 对应数据库表：plan_community
 */
@Data
public class PlanCommunity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private Integer planId;
    
    private Integer communityId;
    
    private LocalDate releaseDateBegin;
    
    private LocalDate releaseDateEnd;
    
    private Integer barrierRequiredQty;
    
    private Integer frameRequiredQty;
    
    // 关联对象
    private Plan plan;
    
    private Community community;
}
