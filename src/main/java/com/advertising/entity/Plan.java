package com.advertising.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 投放方案实体类
 * 对应数据库表：plan
 */
@Data
@Schema(description = "投放方案")
public class Plan implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "自增主键")
    private Integer id;
    
    @Schema(description = "方案编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String planNo;
    
    @Schema(description = "方案名称")
    private String planName;
    
    @Schema(description = "客户")
    private String customer;
    
    @Schema(description = "发布档期开始")
    private LocalDate releaseDateBegin;
    
    @Schema(description = "发布档期结束")
    private LocalDate releaseDateEnd;
    
    @Schema(description = "发布状态：1-意向，2-锁位，3-执行中，4-执行完毕，5-档")
    private Integer releaseStatus;
    
    @Schema(description = "销售类型：1-销售，2-公益，3-置换，4-赠送，5-余位，6-其他")
    private Integer salesType;
    
    @Schema(description = "媒体要求")
    private String mediaRequirements;
}
