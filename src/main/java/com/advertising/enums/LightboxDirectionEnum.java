package com.advertising.enums;

import lombok.Getter;

/**
 * 灯箱朝向枚举
 * 用于道闸设备的灯箱朝向字段
 */
@Getter
public enum LightboxDirectionEnum {
    
    OUTWARD(1, "朝外"),
    INWARD(2, "朝内"),
    STREET_FACING(3, "临街面");
    
    private final Integer code;
    private final String desc;
    
    LightboxDirectionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static LightboxDirectionEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (LightboxDirectionEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
