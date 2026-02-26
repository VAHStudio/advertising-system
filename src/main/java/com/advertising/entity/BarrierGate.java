package com.advertising.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 道闸设备实体类
 * 对应数据库表：barrier_gate
 */
@Data
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
    
    // 关联对象
    private Community community;
}
