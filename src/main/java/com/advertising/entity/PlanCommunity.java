package com.advertising.entity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 方案社区关联实体类
 * 对应数据库表：plan_community
 */
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

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Integer communityId) {
        this.communityId = communityId;
    }

    public LocalDate getReleaseDateBegin() {
        return releaseDateBegin;
    }

    public void setReleaseDateBegin(LocalDate releaseDateBegin) {
        this.releaseDateBegin = releaseDateBegin;
    }

    public LocalDate getReleaseDateEnd() {
        return releaseDateEnd;
    }

    public void setReleaseDateEnd(LocalDate releaseDateEnd) {
        this.releaseDateEnd = releaseDateEnd;
    }

    public Integer getBarrierRequiredQty() {
        return barrierRequiredQty;
    }

    public void setBarrierRequiredQty(Integer barrierRequiredQty) {
        this.barrierRequiredQty = barrierRequiredQty;
    }

    public Integer getFrameRequiredQty() {
        return frameRequiredQty;
    }

    public void setFrameRequiredQty(Integer frameRequiredQty) {
        this.frameRequiredQty = frameRequiredQty;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }
}
