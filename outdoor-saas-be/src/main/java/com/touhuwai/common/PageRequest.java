package com.touhuwai.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数基类
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码，从1开始
     */
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer pageNum = 1;

    /**
     * 每页大小，默认10，最大1000
     */
    @Min(value = 1, message = "每页大小必须大于等于1")
    @Max(value = 1000, message = "每页大小不能超过1000")
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方式：asc-升序，desc-降序
     */
    private String order = "desc";
}
