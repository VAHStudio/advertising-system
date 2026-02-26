package com.advertising.enums;

import lombok.Getter;

/**
 * 画面位置枚举
 * 用于道闸设备的画面位置字段
 */
@Getter
public enum ScreenPositionEnum {
    
    A(1, "A"),
    B(2, "B");
    
    private final Integer code;
    private final String desc;
    
    ScreenPositionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static ScreenPositionEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ScreenPositionEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
