package com.advertising.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 语音交互服务
 * 提供阿里云语音识别token生成
 */
@Service
public class SpeechService {

    @Value("${aliyun.speech.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.speech.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.speech.app-key}")
    private String appKey;

    @Value("${aliyun.speech.endpoint}")
    private String endpoint;

    // 语音参数配置
    private static final int SAMPLE_RATE = 16000;
    private static final String FORMAT = "pcm";

    /**
     * 生成阿里云语音识别Token
     */
    public Map<String, Object> generateSpeechToken() throws Exception {
        // 生成时间戳
        Instant now = Instant.now();
        long timestamp = now.getEpochSecond();
        String dateStr = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
                .format(now.atZone(java.time.ZoneOffset.UTC));

        // 构建Policy
        Map<String, Object> policy = new HashMap<>();
        policy.put("expiration", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .format(now.plusSeconds(3600).atZone(java.time.ZoneOffset.UTC)));
        
        Map<String, Object> condition1 = new HashMap<>();
        condition1.put("acs:access_key_id", accessKeyId);
        
        Map<String, Object> condition2 = new HashMap<>();
        condition2.put("date", dateStr);
        
        policy.put("conditions", new Object[]{condition1, condition2});

        // 将Policy转为JSON并Base64编码
        String policyJson = convertMapToJson(policy);
        String policyBase64 = urlSafeBase64(policyJson);

        // 生成签名
        String signature = hmacSha1(accessKeySecret, policyBase64);

        // 组合Token
        String token = accessKeyId + ":" + policyBase64 + ":" + signature;

        // 构建完整的WebSocket URL
        String wsUrl = endpoint + "?token=" + java.net.URLEncoder.encode(token, StandardCharsets.UTF_8);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("wsUrl", wsUrl);
        result.put("appKey", appKey);
        result.put("expiresAt", timestamp + 3600);
        result.put("sampleRate", SAMPLE_RATE);
        result.put("format", FORMAT);
        
        return result;
    }

    /**
     * 获取语音识别配置（不包含敏感信息）
     */
    public Map<String, Object> getSpeechConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("sampleRate", SAMPLE_RATE);
        config.put("format", FORMAT);
        config.put("endpoint", endpoint);
        config.put("appKey", appKey);
        return config;
    }

    /**
     * URL安全的Base64编码
     */
    private String urlSafeBase64(String input) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * HMAC-SHA1签名
     */
    private String hmacSha1(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        mac.init(secretKeySpec);
        byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    /**
     * 将Map转为JSON字符串（简化版）
     */
    private String convertMapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            
            sb.append("\"").append(entry.getKey()).append("\":");
            
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else if (value instanceof Object[]) {
                sb.append(convertArrayToJson((Object[]) value));
            } else {
                sb.append(value);
            }
        }
        
        sb.append("}");
        return sb.toString();
    }

    /**
     * 将数组转为JSON字符串
     */
    private String convertArrayToJson(Object[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        boolean first = true;
        for (Object item : array) {
            if (!first) sb.append(",");
            first = false;
            
            if (item instanceof Map) {
                sb.append(convertMapToJson((Map<String, Object>) item));
            } else if (item instanceof String) {
                sb.append("\"").append(item).append("\"");
            } else {
                sb.append(item);
            }
        }
        
        sb.append("]");
        return sb.toString();
    }
}
