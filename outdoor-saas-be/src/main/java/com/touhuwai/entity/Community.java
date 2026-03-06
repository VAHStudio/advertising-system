package com.touhuwai.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 社区信息实体类
 * 对应数据库表：community
 */
public class Community implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private String communityNo;
    
    private String buildingName;
    
    private String buildingAddress;
    
    private BigDecimal coordLat;
    
    private BigDecimal coordLng;
    
    private String city;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommunityNo() {
        return communityNo;
    }

    public void setCommunityNo(String communityNo) {
        this.communityNo = communityNo;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingAddress() {
        return buildingAddress;
    }

    public void setBuildingAddress(String buildingAddress) {
        this.buildingAddress = buildingAddress;
    }

    public BigDecimal getCoordLat() {
        return coordLat;
    }

    public void setCoordLat(BigDecimal coordLat) {
        this.coordLat = coordLat;
    }

    public BigDecimal getCoordLng() {
        return coordLng;
    }

    public void setCoordLng(BigDecimal coordLng) {
        this.coordLng = coordLng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
