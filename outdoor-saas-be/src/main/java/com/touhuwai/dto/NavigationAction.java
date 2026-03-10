package com.touhuwai.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * AI 助手导航动作
 * 用于执行完成后跳转到指定页面
 */
@Data
public class NavigationAction implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 动作类型: navigate, refresh, open_modal, none
     */
    private String action = "none";
    
    /**
     * 目标页面 URL 或标识
     */
    private String target;
    
    /**
     * 路由参数
     */
    private Map<String, Object> params;
    
    /**
     * 显示给用户的提示消息
     */
    private String message;
    
    /**
     * Toast 消息配置
     */
    private ToastMessage toast;
    
    /**
     * Toast 消息
     */
    @Data
    public static class ToastMessage implements Serializable {
        private static final long serialVersionUID = 1L;
        
        /**
         * 类型: success, info, warning, error
         */
        private String type;
        
        /**
         * 消息内容
         */
        private String message;
        
        /**
         * 显示时长（毫秒）
         */
        private Integer duration = 3000;
    }
}
