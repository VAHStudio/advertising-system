package com.advertising.enums;

import lombok.Getter;

/**
 * 明细发布状态枚举
 * 用于方案道闸明细和方案框架明细的发布状态字段
 */
@Getter
public enum DetailReleaseStatusEnum {
    
    INTENTION(1, "意向"),
    LOCKED(2, "锁位"),
    PENDING_PUBLISH(3, "待刊发"),
    PUBLISHING(4, "刊发中"),
    ADJUSTABLE(5, "可调"),
    EXPIRED(6, "到期"),
    REMOVED(7, "已下刊");
    
    private final Integer code;
    private final String desc;
    
    DetailReleaseStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static DetailReleaseStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (DetailReleaseStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
