package com.advertising.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 投放方案实体类
 * 对应数据库表：plan
 */
public class Plan implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String planNo;
    private String planName;
    private String customer;
    private LocalDate releaseDateBegin;
    private LocalDate releaseDateEnd;
    private Integer releaseStatus;
    private Integer salesType;
    private String mediaRequirements;

    // 时间字段
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 媒体类型：1-道闸，2-框架，3-社区（全社区选择）
    private Integer mediaType;

    // 媒体选位数量（非持久化字段）
    private Integer mediaCount;

    // 扩展字段
    private String salesPerson;
    private String sampleImage;
    private Long budget;
    private Integer estimatedReach;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getPlanNo() {
        return planNo;
    }
    
    public void setPlanNo(String planNo) {
        this.planNo = planNo;
    }
    
    public String getPlanName() {
        return planName;
    }
    
    public void setPlanName(String planName) {
        this.planName = planName;
    }
    
    public String getCustomer() {
        return customer;
    }
    
    public void setCustomer(String customer) {
        this.customer = customer;
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
    
    public Integer getSalesType() {
        return salesType;
    }
    
    public void setSalesType(Integer salesType) {
        this.salesType = salesType;
    }
    
    public String getMediaRequirements() {
        return mediaRequirements;
    }
    
    public void setMediaRequirements(String mediaRequirements) {
        this.mediaRequirements = mediaRequirements;
    }
    
    public Integer getMediaType() {
        return mediaType;
    }

    public void setMediaType(Integer mediaType) {
        this.mediaType = mediaType;
    }

    public Integer getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(Integer mediaCount) {
        this.mediaCount = mediaCount;
    }

    public String getSalesPerson() {
        return salesPerson;
    }
    
    public void setSalesPerson(String salesPerson) {
        this.salesPerson = salesPerson;
    }
    
    public String getSampleImage() {
        return sampleImage;
    }
    
    public void setSampleImage(String sampleImage) {
        this.sampleImage = sampleImage;
    }
    
    public Long getBudget() {
        return budget;
    }
    
    public void setBudget(Long budget) {
        this.budget = budget;
    }
    
    public Integer getEstimatedReach() {
        return estimatedReach;
    }
    
    public void setEstimatedReach(Integer estimatedReach) {
        this.estimatedReach = estimatedReach;
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
