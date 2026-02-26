package com.advertising.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 框架媒体实体类
 * 对应数据库表：frame
 */
@Data
@Schema(description = "框架媒体")
public class Frame implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "自增主键")
    private Integer id;
    
    @Schema(description = "框架编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String frameNo;
    
    @Schema(description = "社区id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer communityId;
    
    @Schema(description = "楼座")
    private String building;
    
    @Schema(description = "单元")
    private String unit;
    
    @Schema(description = "电梯")
    private String elevator;
    
    @Schema(description = "梯内位置：1-左，2-中，3-右")
    private Integer innerPosition;
    
    // 关联对象
    @Schema(description = "所属社区信息")
    private Community community;
}
