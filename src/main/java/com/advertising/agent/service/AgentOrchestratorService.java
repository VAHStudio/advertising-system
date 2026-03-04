package com.advertising.agent.service;

import com.advertising.agent.dto.*;
import com.advertising.agent.util.ExcelExportUtil;
import com.advertising.common.Result;
import com.advertising.entity.*;
import com.advertising.entity.agent.AgentSession;
import com.advertising.mapper.AgentSessionMapper;
import com.advertising.mapper.BarrierGateMapper;
import com.advertising.mapper.CommunityMapper;
import com.advertising.service.PlanBarrierService;
import com.advertising.service.PlanCommunityService;
import com.advertising.service.PlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Agent 流程编排服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AgentOrchestratorService {
    
    private final KimiAgentService kimiAgentService;
    private final SmartPointSelector pointSelector;
    private final AgentSessionMapper sessionMapper;
    private final CommunityMapper communityMapper;
    private final BarrierGateMapper barrierGateMapper;
    private final PlanService planService;
    private final PlanCommunityService planCommunityService;
    private final PlanBarrierService planBarrierService;
    private final ObjectMapper objectMapper;
    private final ExcelExportUtil excelExportUtil;
    
    private static final long SESSION_EXPIRE_MINUTES = 30;
    
    @Transactional
    public Result<AgentChatResponse> processChat(AgentChatRequest request) {
        try {
            AgentSession session = getOrCreateSession(request.getSessionId());
            
            // 处理导出Excel请求
            if ("export_excel".equals(request.getSelectedValue()) && "query_inventory".equals(session.getCurrentStep())) {
                return exportInventoryToExcel(session, request);
            }
            
            switch (session.getCurrentStep()) {
                case "intent":
                    return processIntentStep(session, request);
                case "city":
                    return processCityStep(session, request);
                case "date":
                    return processDateStep(session, request);
                case "confirmation":
                    return processConfirmationStep(session, request);
                case "query_inventory":
                    return processInventoryQuery(session, request);
                default:
                    resetSession(session);
                    return processIntentStep(session, request);
            }
        } catch (Exception e) {
            log.error("处理对话失败: {}", e.getMessage(), e);
            return Result.error("处理请求失败: " + e.getMessage());
        }
    }
    
    private Result<AgentChatResponse> processIntentStep(AgentSession session, AgentChatRequest request) throws Exception {
        AgentIntent intent = kimiAgentService.parseIntent(request.getMessage());
        
        // 处理销控/库存查询请求
        if ("QUERY_INVENTORY".equals(intent.getAction())) {
            return processInventoryQueryIntent(session, intent);
        }
        
        if (!"CREATE_PLAN".equals(intent.getAction())) {
            return Result.success(AgentChatResponse.builder()
                    .type(AgentChatResponse.TYPE_TEXT)
                    .message("我还不太理解您的需求。请尝试说：\n• 帮我建个可口可乐的广告方案\n• 创建10个道闸点位的投放方案\n• 查看4月的销控表，找出100个空闲道闸点位")
                    .sessionId(session.getId())
                    .step(AgentChatResponse.STEP_INTENT)
                    .build());
        }
        
        session.setIntentJson(toJson(intent));
        session.setCurrentStep("city");
        updateSession(session);
        
        List<String> cities = getAvailableCities();
        
        if (cities.size() == 1) {
            intent.setConfirmedCity(cities.get(0));
            session.setIntentJson(toJson(intent));
            session.setCurrentStep("date");
            updateSession(session);
            
            return Result.success(AgentChatResponse.builder()
                    .type(AgentChatResponse.TYPE_DATE_SELECTION)
                    .message(kimiAgentService.generateConfirmation(intent) + 
                            "\n\n投放城市：" + cities.get(0) + "\n\n请确认投放日期范围：")
                    .sessionId(session.getId())
                    .step(AgentChatResponse.STEP_DATE)
                    .intent(intent)
                    .data(mapOf("cities", cities, "autoSelected", true))
                    .actions(Arrays.asList(
                            AgentChatResponse.Action.builder().label("3月1日-3月31日").value("2025-03-01,2025-03-31").type("primary").build(),
                            AgentChatResponse.Action.builder().label("3月15日-4月15日").value("2025-03-15,2025-04-15").type("secondary").build(),
                            AgentChatResponse.Action.builder().label("自定义日期").value("custom").type("secondary").build()
                    ))
                    .build());
        } else {
            List<AgentChatResponse.Action> cityActions = cities.stream()
                    .map(city -> AgentChatResponse.Action.builder()
                            .label(city)
                            .value(city)
                            .type("primary")
                            .build())
                    .collect(Collectors.toList());
            
            return Result.success(AgentChatResponse.builder()
                    .type(AgentChatResponse.TYPE_CITY_SELECTION)
                    .message(kimiAgentService.generateConfirmation(intent) + 
                            "\n\n检测到您在多个城市有资源，请选择投放城市：")
                    .sessionId(session.getId())
                    .step(AgentChatResponse.STEP_CITY)
                    .intent(intent)
                    .data(mapOf("cities", cities))
                    .actions(cityActions)
                    .build());
        }
    }
    
    /**
     * 处理销控查询意图 - 选择城市步骤
     */
    private Result<AgentChatResponse> processInventoryQueryIntent(AgentSession session, AgentIntent intent) throws Exception {
        session.setIntentJson(toJson(intent));
        session.setCurrentStep("query_inventory");
        updateSession(session);
        
        List<String> cities = getAvailableCities();
        
        StringBuilder message = new StringBuilder();
        message.append("📊 **销控查询**\n\n");
        message.append("查询条件：\n");
        if (intent.getTimeDescription() != null) {
            message.append("• 时间：").append(intent.getTimeDescription()).append("\n");
        }
        if (intent.getQuantity() != null) {
            message.append("• 数量：").append(intent.getQuantity()).append("个道闸\n");
        } else {
            message.append("• 数量：全部空闲点位\n");
        }
        message.append("\n请选择要查询的城市：");
        
        List<AgentChatResponse.Action> cityActions = cities.stream()
                .map(city -> AgentChatResponse.Action.builder()
                        .label(city)
                        .value(city)
                        .type("primary")
                        .build())
                .collect(Collectors.toList());
        
        return Result.success(AgentChatResponse.builder()
                .type(AgentChatResponse.TYPE_CITY_SELECTION)
                .message(message.toString())
                .sessionId(session.getId())
                .step("query_inventory")
                .intent(intent)
                .data(mapOf("cities", cities, "queryType", "inventory"))
                .actions(cityActions)
                .build());
    }
    
    /**
     * 处理销控查询 - 查询空闲点位并返回结果
     */
    private Result<AgentChatResponse> processInventoryQuery(AgentSession session, AgentChatRequest request) throws Exception {
        AgentIntent intent = fromJson(session.getIntentJson(), AgentIntent.class);
        
        // 获取城市选择
        String city = request.getSelectedValue();
        if (city == null || city.isEmpty()) {
            return Result.success(AgentChatResponse.builder()
                    .type(AgentChatResponse.TYPE_CITY_SELECTION)
                    .message("请选择要查询的城市：")
                    .sessionId(session.getId())
                    .step("query_inventory")
                    .intent(intent)
                    .actions(getAvailableCities().stream()
                            .map(c -> AgentChatResponse.Action.builder()
                                    .label(c)
                                    .value(c)
                                    .type("primary")
                                    .build())
                            .collect(Collectors.toList()))
                    .build());
        }
        
        // 解析日期范围
        LocalDate beginDate;
        LocalDate endDate;
        String timeDesc = intent.getTimeDescription();
        
        if (timeDesc != null && timeDesc.contains("4")) {
            // 4月份
            beginDate = LocalDate.of(2025, 4, 1);
            endDate = LocalDate.of(2025, 4, 30);
        } else if (timeDesc != null && timeDesc.contains("3")) {
            // 3月份
            beginDate = LocalDate.of(2025, 3, 1);
            endDate = LocalDate.of(2025, 3, 31);
        } else {
            // 默认查询当前月份
            beginDate = LocalDate.now().withDayOfMonth(1);
            endDate = beginDate.plusMonths(1).minusDays(1);
        }
        
        // 获取查询数量限制
        Integer limit = intent.getQuantity();
        if (limit == null || limit <= 0) {
            limit = 100; // 默认查询100个
        }
        
        // 查询空闲道闸点位
        List<BarrierGate> availableBarriers = barrierGateMapper.selectAvailableBarriers(
                city, null, beginDate, endDate, limit);
        
        // 构建返回结果
        StringBuilder message = new StringBuilder();
        message.append("📊 **").append(city).append(" ").append(timeDesc != null ? timeDesc : "当前月份").append(" 销控表**\n\n");
        message.append("找到 ").append(availableBarriers.size()).append(" 个空闲道闸点位\n\n");
        
        if (availableBarriers.isEmpty()) {
            message.append("⚠️ 该时间段内没有空闲道闸点位。\n");
        } else {
            message.append("**空闲点位列表：**\n");
            int count = 0;
            for (BarrierGate barrier : availableBarriers) {
                count++;
                String communityName = barrier.getCommunity() != null 
                        ? barrier.getCommunity().getBuildingName() 
                        : "未知社区";
                message.append(String.format("%d. %s - %s (%s)\n", 
                        count,
                        communityName,
                        barrier.getGateNo(),
                        barrier.getDoorLocation() != null ? barrier.getDoorLocation() : "未知位置"));
                
                if (count >= 20) {
                    message.append("... 还有 ").append(availableBarriers.size() - 20).append(" 个点位\n");
                    break;
                }
            }
        }
        
        // 准备导出数据
        List<Map<String, Object>> exportData = availableBarriers.stream()
                .map(b -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("序号", availableBarriers.indexOf(b) + 1);
                    map.put("社区名称", b.getCommunity() != null ? b.getCommunity().getBuildingName() : "未知");
                    map.put("道闸编号", b.getGateNo());
                    map.put("设备编号", b.getDeviceNo());
                    map.put("门岗位置", b.getDoorLocation());
                    map.put("城市", b.getCommunity() != null ? b.getCommunity().getCity() : city);
                    map.put("开始日期", beginDate.toString());
                    map.put("结束日期", endDate.toString());
                    return map;
                })
                .collect(Collectors.toList());
        
        // 存储查询结果到session context，供导出使用
        Map<String, Object> queryContext = new HashMap<>();
        queryContext.put("availableBarriers", availableBarriers);
        queryContext.put("city", city);
        queryContext.put("beginDate", beginDate.toString());
        queryContext.put("endDate", endDate.toString());
        queryContext.put("timeDescription", timeDesc);
        session.setContextJson(toJson(queryContext));
        updateSession(session);
        
        List<AgentChatResponse.Action> actions = new ArrayList<>();
        if (!availableBarriers.isEmpty()) {
            actions.add(AgentChatResponse.Action.builder()
                    .label("导出Excel")
                    .value("export_excel")
                    .type("primary")
                    .build());
        }
        actions.add(AgentChatResponse.Action.builder()
                .label("继续查询")
                .value("continue")
                .type("secondary")
                .build());
        
        return Result.success(AgentChatResponse.builder()
                .type(AgentChatResponse.TYPE_TEXT)
                .message(message.toString())
                .sessionId(session.getId())
                .step(AgentChatResponse.STEP_COMPLETED)
                .intent(intent)
                .data(mapOf(
                        "queryType", "inventory",
                        "city", city,
                        "beginDate", beginDate.toString(),
                        "endDate", endDate.toString(),
                        "totalCount", availableBarriers.size(),
                        "exportData", exportData,
                        "barriers", availableBarriers
                ))
                .actions(actions)
                .build());
    }
    
    /**
     * 导出销控查询结果为Excel文件
     */
    private Result<AgentChatResponse> exportInventoryToExcel(AgentSession session, AgentChatRequest request) throws Exception {
        // 从session context中获取查询结果
        String contextJson = session.getContextJson();
        if (contextJson == null || contextJson.isEmpty()) {
            return Result.error("导出失败：找不到查询数据");
        }
        
        Map<String, Object> context = fromJson(contextJson, HashMap.class);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> barrierMaps = (List<Map<String, Object>>) context.get("availableBarriers");
        String city = (String) context.get("city");
        String beginDateStr = (String) context.get("beginDate");
        String endDateStr = (String) context.get("endDate");
        
        if (barrierMaps == null || barrierMaps.isEmpty()) {
            return Result.success(AgentChatResponse.builder()
                    .type(AgentChatResponse.TYPE_TEXT)
                    .message("⚠️ 没有可导出的数据，请先进行销控查询。")
                    .sessionId(session.getId())
                    .step(AgentChatResponse.STEP_COMPLETED)
                    .actions(Arrays.asList(
                            AgentChatResponse.Action.builder().label("继续查询").value("continue").type("primary").build()
                    ))
                    .build());
        }
        
        // 将LinkedHashMap转换为BarrierGate对象
        List<BarrierGate> availableBarriers = barrierMaps.stream()
                .map(map -> {
                    BarrierGate gate = new BarrierGate();
                    gate.setId((Integer) map.get("id"));
                    gate.setGateNo((String) map.get("gateNo"));
                    gate.setCommunityId((Integer) map.get("communityId"));
                    gate.setDeviceNo((String) map.get("deviceNo"));
                    gate.setDoorLocation((String) map.get("doorLocation"));
                    gate.setDevicePosition((Integer) map.get("devicePosition"));
                    gate.setScreenPosition((Integer) map.get("screenPosition"));
                    gate.setLightboxDirection((Integer) map.get("lightboxDirection"));
                    // 转换Community对象
                    @SuppressWarnings("unchecked")
                    Map<String, Object> communityMap = (Map<String, Object>) map.get("community");
                    if (communityMap != null) {
                        Community community = new Community();
                        community.setId((Integer) communityMap.get("id"));
                        community.setBuildingName((String) communityMap.get("buildingName"));
                        community.setBuildingAddress((String) communityMap.get("buildingAddress"));
                        community.setCity((String) communityMap.get("city"));
                        gate.setCommunity(community);
                    }
                    return gate;
                })
                .collect(Collectors.toList());
        
        LocalDate beginDate = LocalDate.parse(beginDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);
        
        try {
            // 生成Excel文件并转为Base64
            String base64Excel = excelExportUtil.exportBarriersToBase64(availableBarriers, city, beginDate, endDate);
            
            // 生成文件名
            String fileName = String.format("%s_%s_销控表.xlsx", 
                    city, 
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            
            StringBuilder message = new StringBuilder();
            message.append("✅ Excel文件已生成！\n\n");
            message.append("📊 **导出信息**\n");
            message.append("• 文件名：").append(fileName).append("\n");
            message.append("• 包含点位：").append(availableBarriers.size()).append("个道闸\n");
            message.append("• 查询城市：").append(city).append("\n");
            message.append("• 时间范围：").append(beginDate).append(" 至 ").append(endDate).append("\n\n");
            message.append("文件已准备好，请点击下方按钮下载。");
            
            // 标记会话完成
            session.setStatus("completed");
            updateSession(session);
            
            return Result.success(AgentChatResponse.builder()
                    .type(AgentChatResponse.TYPE_TEXT)
                    .message(message.toString())
                    .sessionId(session.getId())
                    .step(AgentChatResponse.STEP_COMPLETED)
                    .data(mapOf(
                            "fileName", fileName,
                            "fileBase64", base64Excel,
                            "fileType", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            "totalCount", availableBarriers.size()
                    ))
                    .actions(Arrays.asList(
                            AgentChatResponse.Action.builder()
                                    .label("下载Excel")
                                    .value("download_excel")
                                    .type("primary")
                                    .build(),
                            AgentChatResponse.Action.builder()
                                    .label("继续查询")
                                    .value("continue")
                                    .type("secondary")
                                    .build()
                    ))
                    .build());
                    
        } catch (IOException e) {
            log.error("导出Excel失败: {}", e.getMessage(), e);
            return Result.error("导出Excel失败：" + e.getMessage());
        }
    }
    
    private Result<AgentChatResponse> processCityStep(AgentSession session, AgentChatRequest request) throws Exception {
        AgentIntent intent = fromJson(session.getIntentJson(), AgentIntent.class);
        
        if (request.getSelectedValue() == null || request.getSelectedValue().isEmpty()) {
            return Result.success(AgentChatResponse.builder()
                    .type(AgentChatResponse.TYPE_CITY_SELECTION)
                    .message("请选择一个城市：")
                    .sessionId(session.getId())
                    .step(AgentChatResponse.STEP_CITY)
                    .intent(intent)
                    .actions(getAvailableCities().stream()
                            .map(city -> AgentChatResponse.Action.builder()
                                    .label(city)
                                    .value(city)
                                    .type("primary")
                                    .build())
                            .collect(Collectors.toList()))
                    .build());
        }
        
        intent.setConfirmedCity(request.getSelectedValue());
        session.setIntentJson(toJson(intent));
        session.setCurrentStep("date");
        updateSession(session);
        
        return Result.success(AgentChatResponse.builder()
                .type(AgentChatResponse.TYPE_DATE_SELECTION)
                .message("已选择城市：" + request.getSelectedValue() + "\n\n请确认投放日期范围：")
                .sessionId(session.getId())
                .step(AgentChatResponse.STEP_DATE)
                .intent(intent)
                .actions(Arrays.asList(
                        AgentChatResponse.Action.builder().label("3月1日-3月31日").value("2025-03-01,2025-03-31").type("primary").build(),
                        AgentChatResponse.Action.builder().label("3月15日-4月15日").value("2025-03-15,2025-04-15").type("secondary").build(),
                        AgentChatResponse.Action.builder().label("自定义日期").value("custom").type("secondary").build()
                ))
                .build());
    }
    
    private Result<AgentChatResponse> processDateStep(AgentSession session, AgentChatRequest request) throws Exception {
        AgentIntent intent = fromJson(session.getIntentJson(), AgentIntent.class);
        
        if (request.getSelectedValue() == null || request.getSelectedValue().isEmpty()) {
            return Result.success(AgentChatResponse.builder()
                    .type(AgentChatResponse.TYPE_DATE_SELECTION)
                    .message("请选择投放日期范围：")
                    .sessionId(session.getId())
                    .step(AgentChatResponse.STEP_DATE)
                    .intent(intent)
                    .actions(Arrays.asList(
                            AgentChatResponse.Action.builder().label("3月1日-3月31日").value("2025-03-01,2025-03-31").type("primary").build(),
                            AgentChatResponse.Action.builder().label("3月15日-4月15日").value("2025-03-15,2025-04-15").type("secondary").build()
                    ))
                    .build());
        }
        
        String[] dates = request.getSelectedValue().split(",");
        LocalDate beginDate = LocalDate.parse(dates[0]);
        LocalDate endDate = LocalDate.parse(dates[1]);
        
        intent.setDateRange(AgentIntent.DateRange.builder()
                .beginDate(beginDate)
                .endDate(endDate)
                .build());
        
        PointSelectionResult selectionResult = pointSelector.selectBarriers(
                intent.getConfirmedCity(),
                beginDate,
                endDate,
                intent.getQuantity() != null ? intent.getQuantity() : 10
        );
        
        Map<String, Object> context = new HashMap<>();
        context.put("selectionResult", selectionResult);
        context.put("selectedBarrierIds", selectionResult.getAutoSelected().stream()
                .map(BarrierGate::getId)
                .collect(Collectors.toList()));
        session.setContextJson(toJson(context));
        session.setIntentJson(toJson(intent));
        session.setCurrentStep("confirmation");
        updateSession(session);
        
        StringBuilder message = new StringBuilder();
        message.append("已为您选择").append(selectionResult.getActualCount()).append("个空闲道闸点位：\n\n");
        
        for (PointSelectionResult.CommunityStat stat : selectionResult.getCommunityStats()) {
            message.append("• ").append(stat.getCommunityName())
                   .append("(").append(stat.getCity()).append(")")
                   .append(": ").append(stat.getSelectedCount()).append("个\n");
        }
        
        if (!selectionResult.getIsSatisfied()) {
            message.append("\n⚠️ ").append(selectionResult.getMessage());
        }
        
        message.append("\n\n请确认创建方案：");
        
        return Result.success(AgentChatResponse.builder()
                .type(AgentChatResponse.TYPE_POINT_SELECTION)
                .message(message.toString())
                .sessionId(session.getId())
                .step(AgentChatResponse.STEP_CONFIRMATION)
                .intent(intent)
                .data(mapOf(
                        "selectionResult", selectionResult,
                        "totalSelected", selectionResult.getActualCount(),
                        "totalAvailable", selectionResult.getTotalAvailable()
                ))
                .actions(Arrays.asList(
                        AgentChatResponse.Action.builder().label("确认创建").value("confirm").type("primary").build(),
                        AgentChatResponse.Action.builder().label("重新选择").value("reselect").type("secondary").build(),
                        AgentChatResponse.Action.builder().label("取消").value("cancel").type("danger").build()
                ))
                .build());
    }
    
    private Result<AgentChatResponse> processConfirmationStep(AgentSession session, AgentChatRequest request) throws Exception {
        if ("cancel".equals(request.getSelectedValue())) {
            session.setStatus("completed");
            updateSession(session);
            return Result.success(AgentChatResponse.builder()
                    .type(AgentChatResponse.TYPE_TEXT)
                    .message("已取消创建方案。如需帮助，请随时告诉我！")
                    .sessionId(session.getId())
                    .step(AgentChatResponse.STEP_COMPLETED)
                    .build());
        }
        
        if ("reselect".equals(request.getSelectedValue())) {
            session.setCurrentStep("date");
            updateSession(session);
            return Result.success(AgentChatResponse.builder()
                    .type(AgentChatResponse.TYPE_DATE_SELECTION)
                    .message("请重新选择投放日期范围：")
                    .sessionId(session.getId())
                    .step(AgentChatResponse.STEP_DATE)
                    .actions(Arrays.asList(
                            AgentChatResponse.Action.builder().label("3月1日-3月31日").value("2025-03-01,2025-03-31").type("primary").build(),
                            AgentChatResponse.Action.builder().label("3月15日-4月15日").value("2025-03-15,2025-04-15").type("secondary").build()
                    ))
                    .build());
        }
        
        AgentIntent intent = fromJson(session.getIntentJson(), AgentIntent.class);
        Map<String, Object> context = fromJson(session.getContextJson(), HashMap.class);
        
        // 从context中获取选中的点位ID列表（处理JSON反序列化后的类型转换）
        List<Integer> selectedBarrierIds = new ArrayList<>();
        Object barrierIdsObj = context.get("selectedBarrierIds");
        if (barrierIdsObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> idList = (List<Object>) barrierIdsObj;
            for (Object id : idList) {
                if (id instanceof Integer) {
                    selectedBarrierIds.add((Integer) id);
                } else if (id instanceof Number) {
                    selectedBarrierIds.add(((Number) id).intValue());
                }
            }
        }
        
        if (selectedBarrierIds == null || selectedBarrierIds.isEmpty()) {
            return Result.error("未找到选中的点位信息，请重新选择投放日期和点位。");
        }
        
        SmartPlanResult result = createSmartPlan(intent, selectedBarrierIds);
        
        session.setStatus("completed");
        updateSession(session);
        
        if (result.getSuccess()) {
            StringBuilder message = new StringBuilder();
            message.append("✅ 方案创建成功！\n\n");
            message.append("📋 **方案信息**\n");
            message.append("• 方案编号：").append(result.getPlan().getPlanNo()).append("\n");
            message.append("• 方案名称：").append(result.getPlan().getPlanName()).append("\n");
            message.append("• 客户：").append(result.getPlan().getCustomer()).append("\n");
            message.append("• 投放点位：").append(result.getBarrierCount()).append("个道闸\n");
            message.append("• 投放周期：").append(result.getPlan().getReleaseDateBegin())
                   .append(" 至 ").append(result.getPlan().getReleaseDateEnd()).append("\n");
            
            return Result.success(AgentChatResponse.builder()
                    .type(AgentChatResponse.TYPE_PLAN_CREATED)
                    .message(message.toString())
                    .sessionId(session.getId())
                    .step(AgentChatResponse.STEP_COMPLETED)
                    .data(mapOf(
                            "plan", result.getPlan(),
                            "barrierCount", result.getBarrierCount(),
                            "selectedBarriers", result.getSelectedBarriers()
                    ))
                    .actions(Arrays.asList(
                            AgentChatResponse.Action.builder().label("查看方案详情").value("/campaign-detail").type("primary").build(),
                            AgentChatResponse.Action.builder().label("继续对话").value("continue").type("secondary").build()
                    ))
                    .build());
        } else {
            return Result.error(result.getMessage());
        }
    }
    
    @Transactional
    public SmartPlanResult createSmartPlan(AgentIntent intent, List<Integer> barrierIds) {
        try {
            Plan plan = new Plan();
            plan.setPlanNo(generatePlanNo());
            plan.setPlanName(intent.getCustomer() + intent.getTimeDescription() + "投放方案");
            plan.setCustomer(intent.getCustomer());
            plan.setReleaseDateBegin(intent.getDateRange().getBeginDate());
            plan.setReleaseDateEnd(intent.getDateRange().getEndDate());
            plan.setReleaseStatus(1);
            plan.setSalesType(1);
            plan.setMediaRequirements(intent.getRequirements());
            
            planService.add(plan);
            
            List<BarrierGate> selectedBarriers = barrierIds.stream()
                    .map(id -> barrierGateMapper.selectById(id))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            
            Map<Integer, List<BarrierGate>> barriersByCommunity = selectedBarriers.stream()
                    .collect(Collectors.groupingBy(BarrierGate::getCommunityId));
            
            int communityCount = 0;
            for (Map.Entry<Integer, List<BarrierGate>> entry : barriersByCommunity.entrySet()) {
                Integer communityId = entry.getKey();
                List<BarrierGate> communityBarriers = entry.getValue();
                
                PlanCommunity planCommunity = new PlanCommunity();
                planCommunity.setPlanId(plan.getId());
                planCommunity.setCommunityId(communityId);
                planCommunity.setReleaseDateBegin(intent.getDateRange().getBeginDate());
                planCommunity.setReleaseDateEnd(intent.getDateRange().getEndDate());
                planCommunity.setBarrierRequiredQty(communityBarriers.size());
                planCommunity.setFrameRequiredQty(0);
                
                planCommunityService.add(planCommunity);
                communityCount++;
                
                for (BarrierGate barrier : communityBarriers) {
                    PlanBarrier planBarrier = new PlanBarrier();
                    planBarrier.setPlanId(plan.getId());
                    planBarrier.setBarrierGateId(barrier.getId());
                    planBarrier.setPlanCommunityId(planCommunity.getId());
                    planBarrier.setReleaseDateBegin(intent.getDateRange().getBeginDate());
                    planBarrier.setReleaseDateEnd(intent.getDateRange().getEndDate());
                    planBarrier.setReleaseStatus(1);
                    
                    planBarrierService.add(planBarrier);
                }
            }
            
            List<SmartPlanResult.SelectedBarrierInfo> barrierInfos = selectedBarriers.stream()
                    .map(b -> SmartPlanResult.SelectedBarrierInfo.builder()
                            .barrierId(b.getId())
                            .gateNo(b.getGateNo())
                            .communityName(b.getCommunity() != null ? b.getCommunity().getBuildingName() : "未知")
                            .doorLocation(b.getDoorLocation())
                            .city(b.getCommunity() != null ? b.getCommunity().getCity() : intent.getConfirmedCity())
                            .build())
                    .collect(Collectors.toList());
            
            return SmartPlanResult.builder()
                    .success(true)
                    .message("方案创建成功")
                    .plan(plan)
                    .communityCount(communityCount)
                    .barrierCount(selectedBarriers.size())
                    .selectedBarriers(barrierInfos)
                    .build();
                    
        } catch (Exception e) {
            log.error("创建智能方案失败: {}", e.getMessage(), e);
            return SmartPlanResult.builder()
                    .success(false)
                    .message("创建方案失败: " + e.getMessage())
                    .build();
        }
    }
    
    private AgentSession getOrCreateSession(String sessionId) throws Exception {
        if (sessionId != null && !sessionId.isEmpty()) {
            AgentSession session = sessionMapper.selectById(sessionId);
            if (session != null && session.getExpireAt().isAfter(LocalDateTime.now())) {
                return session;
            }
        }
        
        AgentSession newSession = new AgentSession();
        newSession.setId(UUID.randomUUID().toString().replace("-", ""));
        newSession.setCurrentStep("intent");
        newSession.setStatus("active");
        newSession.setCreatedAt(LocalDateTime.now());
        newSession.setUpdatedAt(LocalDateTime.now());
        newSession.setExpireAt(LocalDateTime.now().plusMinutes(SESSION_EXPIRE_MINUTES));
        newSession.setIntentJson("{}");
        newSession.setContextJson("{}");
        
        sessionMapper.insert(newSession);
        return newSession;
    }
    
    private void updateSession(AgentSession session) {
        session.setUpdatedAt(LocalDateTime.now());
        session.setExpireAt(LocalDateTime.now().plusMinutes(SESSION_EXPIRE_MINUTES));
        sessionMapper.update(session);
    }
    
    private void resetSession(AgentSession session) throws Exception {
        session.setCurrentStep("intent");
        session.setIntentJson("{}");
        session.setContextJson("{}");
        updateSession(session);
    }
    
    private List<String> getAvailableCities() {
        List<Community> communities = communityMapper.selectAll();
        return communities.stream()
                .map(Community::getCity)
                .filter(city -> city != null && !city.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    private String generatePlanNo() {
        return "PLAN" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
    
    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
    
    private <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
    
    private Map<String, Object> mapOf(Object... pairs) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((String) pairs[i], pairs[i + 1]);
        }
        return map;
    }
}
