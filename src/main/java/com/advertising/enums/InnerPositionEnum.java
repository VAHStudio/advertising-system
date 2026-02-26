package com.advertising.enums;

import lombok.Getter;

/**
 * 梯内位置枚举
 * 用于框架媒体的梯内位置字段
 */
@Getter
public enum InnerPositionEnum {
    
    LEFT(1, "左"),
    CENTER(2, "中"),
    RIGHT(3, "右");
    
    private final Integer code;
    private final String desc;
    
    InnerPositionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static InnerPositionEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (InnerPositionEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
