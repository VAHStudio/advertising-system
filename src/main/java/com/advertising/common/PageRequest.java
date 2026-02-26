package com.advertising.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数基类
 */
@Data
public class PageRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;
    
    private String orderBy;
    
    private String order = "desc";
}
