package com.advertising.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 道闸设备实体类
 * 对应数据库表：barrier_gate
 */
@Data
@Schema(description = "道闸设备")
public class BarrierGate implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "自增主键")
    private Integer id;
    
    @Schema(description = "道闸编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String gateNo;
    
    @Schema(description = "社区id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer communityId;
    
    @Schema(description = "设备编号")
    private String deviceNo;
    
    @Schema(description = "门的位置")
    private String doorLocation;
    
    @Schema(description = "设备位置：1-进口，2-出口，3-进出口")
    private Integer devicePosition;
    
    @Schema(description = "画面位置：1-A，2-B")
    private Integer screenPosition;
    
    @Schema(description = "灯箱朝向：1-朝外，2-朝内，3-临街面")
    private Integer lightboxDirection;
    
    // 关联对象
    @Schema(description = "所属社区信息")
    private Community community;
}
