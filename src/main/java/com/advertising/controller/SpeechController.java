package com.advertising.controller;

import com.advertising.common.Result;
import com.advertising.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 语音交互控制器
 * 提供阿里云语音识别token生成
 */
@RestController
@RequestMapping("/api/speech")
public class SpeechController {

    private final SpeechService speechService;

    @Autowired
    public SpeechController(SpeechService speechService) {
        this.speechService = speechService;
    }

    /**
     * 获取阿里云语音识别的Token和配置信息
     */
    @GetMapping("/token")
    public Result<Map<String, Object>> getSpeechToken() {
        try {
            Map<String, Object> tokenInfo = speechService.generateSpeechToken();
            return Result.success(tokenInfo);
        } catch (Exception e) {
            return Result.error("获取语音token失败: " + e.getMessage());
        }
    }

    /**
     * 获取语音识别配置信息（不包含敏感信息）
     */
    @GetMapping("/config")
    public Result<Map<String, Object>> getSpeechConfig() {
        try {
            Map<String, Object> config = speechService.getSpeechConfig();
            return Result.success(config);
        } catch (Exception e) {
            return Result.error("获取语音配置失败: " + e.getMessage());
        }
    }
}
