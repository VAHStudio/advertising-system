package com.advertising.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数基类
 */
@Data
@Schema(description = "分页请求参数")
public class PageRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "当前页码，默认1", example = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页大小，默认10", example = "10")
    private Integer pageSize = 10;
    
    @Schema(description = "排序字段")
    private String orderBy;
    
    @Schema(description = "排序方式：asc升序，desc降序", example = "desc")
    private String order = "desc";
}
