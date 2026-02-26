package com.advertising.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 投放方案实体类
 * 对应数据库表：plan
 */
@Data
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
}
