package com.advertising.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 框架媒体实体类
 * 对应数据库表：frame
 */
@Data
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
}
