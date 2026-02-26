package com.advertising.enums;

import lombok.Getter;

/**
 * 设备位置枚举
 * 用于道闸设备的设备位置字段
 */
@Getter
public enum DevicePositionEnum {
    
    ENTRANCE(1, "进口"),
    EXIT(2, "出口"),
    BOTH(3, "进出口");
    
    private final Integer code;
    private final String desc;
    
    DevicePositionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static DevicePositionEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (DevicePositionEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
