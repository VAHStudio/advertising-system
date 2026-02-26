package com.advertising.enums;

import lombok.Getter;

/**
 * 方案发布状态枚举
 * 用于方案表的发布状态字段
 */
@Getter
public enum PlanReleaseStatusEnum {
    
    INTENTION(1, "意向"),
    LOCKED(2, "锁位"),
    EXECUTING(3, "执行中"),
    COMPLETED(4, "执行完毕"),
    EXPIRED(5, "档");
    
    private final Integer code;
    private final String desc;
    
    PlanReleaseStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static PlanReleaseStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PlanReleaseStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
