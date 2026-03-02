package com.advertising.entity;

import java.io.Serializable;

/**
 * 框架媒体实体类
 * 对应数据库表：frame
 */
public class Frame implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private String frameNo;
    
    private Integer communityId;
    
    private String building;
    
    private String unit;
    
    private String elevator;
    
    private Integer innerPosition;
    
    // 关联对象
    private Community community;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(String frameNo) {
        this.frameNo = frameNo;
    }

    public Integer getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Integer communityId) {
        this.communityId = communityId;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getElevator() {
        return elevator;
    }

    public void setElevator(String elevator) {
        this.elevator = elevator;
    }

    public Integer getInnerPosition() {
        return innerPosition;
    }

    public void setInnerPosition(Integer innerPosition) {
        this.innerPosition = innerPosition;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }
}
