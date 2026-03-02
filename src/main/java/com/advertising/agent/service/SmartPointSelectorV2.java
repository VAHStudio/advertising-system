package com.advertising.agent.service;

import com.advertising.agent.dsl.PointSelectionDSL;
import com.advertising.agent.dsl.SQLTemplateEngine;
import com.advertising.entity.BarrierGate;
import com.advertising.entity.Frame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 智能点位选择服务 V2
 * 基于DSL + SQL模板引擎实现复杂点位筛选
 * 
 * 支持场景：
 * 1. 选某个档期内100面道闸，每个楼盘2个，优先选进口
 * 2. 在选定的楼盘范围内选，默认选空闲的
 * 3. 复杂的联表查询和筛选
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmartPointSelectorV2 {

    private final SQLTemplateEngine sqlEngine;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 执行智能点位选择
     * 
     * 示例DSL：
     * PointSelectionDSL.builder()
     *     .mediaType("barrier")
     *     .targetCount(100)
     *     .dateRange(DateRange.builder()
     *         .beginDate(LocalDate.of(2025, 3, 1))
     *         .endDate(LocalDate.of(2025, 3, 31))
     *         .build())
     *     .distributionStrategy(DistributionStrategy.builder()
     *         .type("per_community")
     *         .perCommunityCount(2)
     *         .maxCommunities(50)
     *         .build())
     *     .filterConditions(FilterConditions.builder()
     *         .devicePosition("entrance")
     *         .onlyAvailable(true)
     *         .build())
     *     .build()
     */
    public PointSelectionResult selectPoints(PointSelectionDSL dsl) {
        try {
            log.info("开始智能点位选择, DSL: {}", dsl);
            
            // 1. 生成SQL
            SQLTemplateEngine.SQLGenerationResult sqlResult = sqlEngine.generateSQL(dsl);
            if (!sqlResult.isSuccess()) {
                return PointSelectionResult.error(sqlResult.getErrorMessage());
            }
            
            // 2. 执行SQL查询
            String sql = sqlResult.getSql();
            log.debug("生成的SQL: {}", sql);
            
            List<Map<String, Object>> rawResults;
            if ("barrier".equalsIgnoreCase(dsl.getMediaType())) {
                rawResults = jdbcTemplate.queryForList(sql);
            } else if ("frame".equalsIgnoreCase(dsl.getMediaType())) {
                rawResults = jdbcTemplate.queryForList(sql);
            } else {
                return PointSelectionResult.error("不支持的媒体类型: " + dsl.getMediaType());
            }
            
            // 3. 转换为结果对象
            List<SelectedPoint> points = convertToSelectedPoints(rawResults, dsl.getMediaType());
            
            // 4. 验证结果
            PointSelectionResult result = validateAndPackage(dsl, points);
            
            log.info("点位选择完成，共选择 {} 个点位，覆盖 {} 个社区", 
                result.getPoints().size(), 
                result.getCommunityCount());
            
            return result;
            
        } catch (Exception e) {
            log.error("智能点位选择失败", e);
            return PointSelectionResult.error("选择点位时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 查询社区可用点位统计
     */
    public List<CommunityAvailability> queryCommunityAvailability(PointSelectionDSL dsl) {
        SQLTemplateEngine.SQLGenerationResult sqlResult = sqlEngine.generateSQL(
            PointSelectionDSL.builder()
                .intentType(PointSelectionDSL.IntentType.QUERY_AVAILABILITY)
                .mediaType("all")
                .dateRange(dsl.getDateRange())
                .filterConditions(dsl.getFilterConditions())
                .limitConditions(PointSelectionDSL.LimitConditions.builder()
                    .maxResults(1000)
                    .build())
                .build()
        );
        
        if (!sqlResult.isSuccess()) {
            log.error("生成查询SQL失败: {}", sqlResult.getErrorMessage());
            return Collections.emptyList();
        }
        
        String sql = sqlResult.getSql();
        return jdbcTemplate.query(sql, (rs, rowNum) -> CommunityAvailability.builder()
            .communityId(rs.getInt("community_id"))
            .communityName(rs.getString("community_name"))
            .city(rs.getString("city"))
            .district(rs.getString("district"))
            .availableBarrierCount(rs.getInt("available_barrier_count"))
            .availableFrameCount(rs.getInt("available_frame_count"))
            .build());
    }
    
    /**
     * 检查档期冲突
     */
    public List<ConflictInfo> checkConflicts(List<Integer> pointIds, String mediaType, 
                                              PointSelectionDSL.DateRange dateRange) {
        String table = "barrier".equalsIgnoreCase(mediaType) ? "plan_barrier" : "plan_frame";
        String idColumn = "barrier".equalsIgnoreCase(mediaType) ? "barrier_gate_id" : "frame_id";
        
        String sql = String.format("""
            SELECT 
                pc.id as plan_id,
                pc.plan_name,
                p.customer,
                pc.release_date_begin,
                pc.release_date_end,
                t.%s as point_id,
                t.release_status
            FROM %s t
            JOIN plan p ON t.plan_id = p.id
            WHERE t.%s IN (%s)
            AND t.release_status IN (2, 3, 4)
            AND NOT (t.release_date_end < ? OR t.release_date_begin > ?)
            """, idColumn, table, idColumn, String.join(",", pointIds.stream().map(String::valueOf).toList()));
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> ConflictInfo.builder()
            .planId(rs.getInt("plan_id"))
            .planName(rs.getString("plan_name"))
            .customer(rs.getString("customer"))
            .conflictBeginDate(rs.getDate("release_date_begin").toLocalDate())
            .conflictEndDate(rs.getDate("release_date_end").toLocalDate())
            .pointId(rs.getInt("point_id"))
            .releaseStatus(rs.getInt("release_status"))
            .build(), 
            dateRange.getBeginDate(), dateRange.getEndDate());
    }
    
    /**
     * 将查询结果转换为SelectedPoint对象
     */
    private List<SelectedPoint> convertToSelectedPoints(List<Map<String, Object>> rawResults, String mediaType) {
        List<SelectedPoint> points = new ArrayList<>();
        
        for (Map<String, Object> row : rawResults) {
            SelectedPoint point = SelectedPoint.builder()
                .pointId(getIntValue(row, "barrier_gate_id", "frame_id"))
                .pointNo(getStringValue(row, "gate_no", "frame_no"))
                .communityId(getIntValue(row, "community_id"))
                .communityName(getStringValue(row, "community_name"))
                .mediaType(mediaType)
                .details(extractDetails(row, mediaType))
                .build();
            points.add(point);
        }
        
        return points;
    }
    
    /**
     * 提取详情信息
     */
    private Map<String, String> extractDetails(Map<String, Object> row, String mediaType) {
        Map<String, String> details = new HashMap<>();
        
        if ("barrier".equalsIgnoreCase(mediaType)) {
            details.put("deviceNo", getStringValue(row, "device_no"));
            details.put("doorLocation", getStringValue(row, "door_location"));
            details.put("devicePosition", getStringValue(row, "device_position"));
        } else {
            details.put("building", getStringValue(row, "building"));
            details.put("unit", getStringValue(row, "unit"));
            details.put("elevator", getStringValue(row, "elevator"));
            details.put("innerPosition", getStringValue(row, "inner_position"));
        }
        
        return details;
    }
    
    /**
     * 验证结果并封装
     */
    private PointSelectionResult validateAndPackage(PointSelectionDSL dsl, List<SelectedPoint> points) {
        int targetCount = dsl.getTargetCount() != null ? dsl.getTargetCount() : 0;
        
        // 按社区分组统计
        Map<Integer, List<SelectedPoint>> byCommunity = new HashMap<>();
        for (SelectedPoint point : points) {
            byCommunity.computeIfAbsent(point.getCommunityId(), k -> new ArrayList<>()).add(point);
        }
        
        // 构建结果
        return PointSelectionResult.builder()
            .success(true)
            .points(points)
            .totalCount(points.size())
            .targetCount(targetCount)
            .communityCount(byCommunity.size())
            .pointsByCommunity(byCommunity)
            .isComplete(points.size() >= targetCount)
            .message(generateMessage(points.size(), targetCount, byCommunity.size()))
            .build();
    }
    
    /**
     * 生成结果消息
     */
    private String generateMessage(int actualCount, int targetCount, int communityCount) {
        if (actualCount >= targetCount) {
            return String.format("成功选择 %d 个点位（目标 %d 个），覆盖 %d 个社区", 
                actualCount, targetCount, communityCount);
        } else {
            return String.format("仅找到 %d 个可用点位（目标 %d 个），覆盖 %d 个社区，建议放宽筛选条件", 
                actualCount, targetCount, communityCount);
        }
    }
    
    // 辅助方法
    private Integer getIntValue(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return ((Number) value).intValue();
            }
        }
        return null;
    }
    
    private String getStringValue(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }
    
    // ============ 结果对象定义 ============
    
    @lombok.Data
    @lombok.Builder
    public static class PointSelectionResult {
        private boolean success;
        private String errorMessage;
        private List<SelectedPoint> points;
        private int totalCount;
        private int targetCount;
        private int communityCount;
        private Map<Integer, List<SelectedPoint>> pointsByCommunity;
        private boolean isComplete;
        private String message;
        
        public static PointSelectionResult error(String message) {
            return PointSelectionResult.builder()
                .success(false)
                .errorMessage(message)
                .points(Collections.emptyList())
                .totalCount(0)
                .communityCount(0)
                .build();
        }
    }
    
    @lombok.Data
    @lombok.Builder
    public static class SelectedPoint {
        private Integer pointId;
        private String pointNo;
        private Integer communityId;
        private String communityName;
        private String mediaType;
        private Map<String, String> details;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class CommunityAvailability {
        private Integer communityId;
        private String communityName;
        private String city;
        private String district;
        private Integer availableBarrierCount;
        private Integer availableFrameCount;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class ConflictInfo {
        private Integer planId;
        private String planName;
        private String customer;
        private java.time.LocalDate conflictBeginDate;
        private java.time.LocalDate conflictEndDate;
        private Integer pointId;
        private Integer releaseStatus;
    }
}