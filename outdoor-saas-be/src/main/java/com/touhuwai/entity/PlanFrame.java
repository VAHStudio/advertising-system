package com.touhuwai.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 方案框架明细实体类
 * 对应数据库表：plan_frame
 */
public class PlanFrame implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer planId;
    private Integer frameId;
    private Integer planCommunityId;
    private LocalDate releaseDateBegin;
    private LocalDate releaseDateEnd;
    private Integer releaseStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联对象
    private Plan plan;
    private Frame frame;
    private PlanCommunity planCommunity;
    
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
    
    public Integer getFrameId() {
        return frameId;
    }
    
    public void setFrameId(Integer frameId) {
        this.frameId = frameId;
    }
    
    public Integer getPlanCommunityId() {
        return planCommunityId;
    }
    
    public void setPlanCommunityId(Integer planCommunityId) {
        this.planCommunityId = planCommunityId;
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
    
    public Integer getReleaseStatus() {
        return releaseStatus;
    }
    
    public void setReleaseStatus(Integer releaseStatus) {
        this.releaseStatus = releaseStatus;
    }
    
    public Plan getPlan() {
        return plan;
    }
    
    public void setPlan(Plan plan) {
        this.plan = plan;
    }
    
    public Frame getFrame() {
        return frame;
    }
    
    public void setFrame(Frame frame) {
        this.frame = frame;
    }
    
    public PlanCommunity getPlanCommunity() {
        return planCommunity;
    }

    public void setPlanCommunity(PlanCommunity planCommunity) {
        this.planCommunity = planCommunity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
