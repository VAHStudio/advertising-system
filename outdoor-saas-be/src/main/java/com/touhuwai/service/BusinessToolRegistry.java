package com.touhuwai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务工具注册表
 */
@Component
@RequiredArgsConstructor
public class BusinessToolRegistry {
    

    
    public List<Object> getTools() {
        // todo 将api接口修改为业务工具
        return new ArrayList<>();
    }
}
