package com.advertising.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 社区信息实体类
 * 对应数据库表：community
 */
@Data
public class Community implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private String communityNo;
    
    private String buildingName;
    
    private String buildingAddress;
    
    private BigDecimal coordLat;
    
    private BigDecimal coordLng;
    
    private String city;
}
