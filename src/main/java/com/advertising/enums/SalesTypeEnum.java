package com.advertising.enums;

import lombok.Getter;

/**
 * 销售类型枚举
 * 用于方案表的销售类型字段
 */
@Getter
public enum SalesTypeEnum {
    
    SALES(1, "销售"),
    PUBLIC_WELFARE(2, "公益"),
    EXCHANGE(3, "置换"),
    GIFT(4, "赠送"),
    REMAINING(5, "余位"),
    OTHER(6, "其他");
    
    private final Integer code;
    private final String desc;
    
    SalesTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举
     */
    public static SalesTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SalesTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
