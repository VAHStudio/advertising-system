package com.advertising.agent.controller;

import com.advertising.agent.dto.AgentChatRequest;
import com.advertising.agent.dto.AgentChatResponse;
import com.advertising.agent.dsl.PointSelectionDSL;
import com.advertising.agent.service.SmartPointSelectorV2;
import com.advertising.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * DSL Agent 控制器
 * 支持复杂点位选择的智能Agent
 */
@Slf4j
@RestController
@RequestMapping("/api/agent/v2")
@RequiredArgsConstructor
public class DSLAgentController {

    private final SmartPointSelectorV2 pointSelector;

    /**
     * 智能点位选择接口
     * 
     * 示例请求：
     * POST /api/agent/v2/select-points
     * {
     *     "mediaType": "barrier",
     *     "targetCount": 100,
     *     "dateRange": {
     *         "beginDate": "2025-03-01",
     *         "endDate": "2025-03-31"
     *     },
     *     "distributionStrategy": {
     *         "type": "per_community",
     *         "perCommunityCount": 2,
     *         "maxCommunities": 50
     *     },
     *     "filterConditions": {
     *         "devicePosition": "entrance",
     *         "onlyAvailable": true
     *     }
     * }
     */
    @PostMapping("/select-points")
    public Result<SmartPointSelectorV2.PointSelectionResult> selectPoints(
            @RequestBody PointSelectionDSL dsl) {
        
        log.info("收到点位选择请求: {}", dsl);
        SmartPointSelectorV2.PointSelectionResult result = pointSelector.selectPoints(dsl);
        
        if (result.isSuccess()) {
            return Result.success(result);
        } else {
            return Result.error(result.getErrorMessage());
        }
    }

    /**
     * 自然语言转DSL并执行
     * 
     * 示例请求：
     * POST /api/agent/v2/nlp-select
     * {
     *     "message": "选3月份档期内的100面道闸，每个楼盘2个，优先选进口，要空闲的",
     *     "sessionId": "optional"
     * }
     */
    @PostMapping("/nlp-select")
    public Result<SmartPointSelectorV2.PointSelectionResult> nlpSelectPoints(
            @RequestBody NlpSelectRequest request) {
        
        log.info("收到自然语言点位选择请求: {}", request.getMessage());
        
        // 1. 解析自然语言为DSL
        PointSelectionDSL dsl = parseNLPToDSL(request.getMessage());
        
        // 2. 执行点位选择
        SmartPointSelectorV2.PointSelectionResult result = pointSelector.selectPoints(dsl);
        
        if (result.isSuccess()) {
            return Result.success(result);
        } else {
            return Result.error(result.getErrorMessage());
        }
    }

    /**
     * 查询社区可用点位统计
     */
    @PostMapping("/community-availability")
    public Result<List<SmartPointSelectorV2.CommunityAvailability>> queryAvailability(
            @RequestBody AvailabilityQueryRequest request) {
        
        PointSelectionDSL dsl = PointSelectionDSL.builder()
            .dateRange(PointSelectionDSL.DateRange.builder()
                .beginDate(request.getBeginDate())
                .endDate(request.getEndDate())
                .build())
            .filterConditions(PointSelectionDSL.FilterConditions.builder()
                .city(request.getCity())
                .district(request.getDistrict())
                .build())
            .build();
        
        List<SmartPointSelectorV2.CommunityAvailability> availability = 
            pointSelector.queryCommunityAvailability(dsl);
        
        return Result.success(availability);
    }

    /**
     * 检查档期冲突
     */
    @PostMapping("/check-conflicts")
    public Result<List<SmartPointSelectorV2.ConflictInfo>> checkConflicts(
            @RequestBody ConflictCheckRequest request) {
        
        List<SmartPointSelectorV2.ConflictInfo> conflicts = pointSelector.checkConflicts(
            request.getPointIds(),
            request.getMediaType(),
            PointSelectionDSL.DateRange.builder()
                .beginDate(request.getBeginDate())
                .endDate(request.getEndDate())
                .build()
        );
        
        return Result.success(conflicts);
    }

    /**
     * 简单的NLP解析（实际项目中应使用LLM）
     */
    private PointSelectionDSL parseNLPToDSL(String message) {
        PointSelectionDSL.PointSelectionDSLBuilder builder = PointSelectionDSL.builder();
        
        // 默认参数
        builder.mediaType("barrier");
        builder.targetCount(10);
        builder.dateRange(PointSelectionDSL.DateRange.builder()
            .beginDate(LocalDate.now())
            .endDate(LocalDate.now().plusMonths(1))
            .build());
        
        // 解析媒体类型
        if (message.contains("道闸") || message.contains("barrier")) {
            builder.mediaType("barrier");
        } else if (message.contains("框架") || message.contains("frame")) {
            builder.mediaType("frame");
        }
        
        // 解析数量
        java.util.regex.Pattern countPattern = java.util.regex.Pattern.compile("(\\d+)");
        java.util.regex.Matcher matcher = countPattern.matcher(message);
        if (matcher.find()) {
            builder.targetCount(Integer.parseInt(matcher.group(1)));
        }
        
        // 解析每社区数量
        java.util.regex.Pattern perCommunityPattern = java.util.regex.Pattern.compile("每个?楼盘?(\\d+)");
        java.util.regex.Matcher perMatcher = perCommunityPattern.matcher(message);
        int perCommunity = 2;
        if (perMatcher.find()) {
            perCommunity = Integer.parseInt(perMatcher.group(1));
        }
        
        builder.distributionStrategy(PointSelectionDSL.DistributionStrategy.builder()
            .type("per_community")
            .perCommunityCount(perCommunity)
            .maxCommunities(100)
            .build());
        
        // 解析设备位置
        String devicePosition = "both";
        if (message.contains("进口") || message.contains("入口")) {
            devicePosition = "entrance";
        } else if (message.contains("出口")) {
            devicePosition = "exit";
        }
        
        builder.filterConditions(PointSelectionDSL.FilterConditions.builder()
            .devicePosition(devicePosition)
            .onlyAvailable(true)
            .excludeOccupied(true)
            .build());
        
        return builder.build();
    }

    // ============ 请求对象定义 ============
    
    @lombok.Data
    public static class NlpSelectRequest {
        private String message;
        private String sessionId;
    }
    
    @lombok.Data
    public static class AvailabilityQueryRequest {
        private LocalDate beginDate;
        private LocalDate endDate;
        private String city;
        private String district;
    }
    
    @lombok.Data
    public static class ConflictCheckRequest {
        private List<Integer> pointIds;
        private String mediaType;
        private LocalDate beginDate;
        private LocalDate endDate;
    }
}