package com.touhuwai.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 道闸设备实体类
 * 对应数据库表：barrier_gate
 */
public class BarrierGate implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private String gateNo;
    
    private Integer communityId;
    
    private String deviceNo;
    
    private String doorLocation;
    
    private Integer devicePosition;
    
    private Integer screenPosition;
    
    private Integer lightboxDirection;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // 关联对象
    private Community community;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGateNo() {
        return gateNo;
    }

    public void setGateNo(String gateNo) {
        this.gateNo = gateNo;
    }

    public Integer getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Integer communityId) {
        this.communityId = communityId;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getDoorLocation() {
        return doorLocation;
    }

    public void setDoorLocation(String doorLocation) {
        this.doorLocation = doorLocation;
    }

    public Integer getDevicePosition() {
        return devicePosition;
    }

    public void setDevicePosition(Integer devicePosition) {
        this.devicePosition = devicePosition;
    }

    public Integer getScreenPosition() {
        return screenPosition;
    }

    public void setScreenPosition(Integer screenPosition) {
        this.screenPosition = screenPosition;
    }

    public Integer getLightboxDirection() {
        return lightboxDirection;
    }

    public void setLightboxDirection(Integer lightboxDirection) {
        this.lightboxDirection = lightboxDirection;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
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
