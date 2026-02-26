package com.advertising.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装类
 * @param <T> 数据类型
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "当前页码")
    private Integer pageNum;
    
    @Schema(description = "每页大小")
    private Integer pageSize;
    
    @Schema(description = "总记录数")
    private Long total;
    
    @Schema(description = "总页数")
    private Integer pages;
    
    @Schema(description = "数据列表")
    private List<T> list;
    
    public PageResult() {}
    
    public PageResult(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }
    
    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> build(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        return new PageResult<>(pageNum, pageSize, total, list);
    }
}
