package com.advertising.agent.dsl;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * SQL模板引擎
 * 基于DSL生成SQL查询
 */
@Component
public class SQLTemplateEngine {
    
    /**
     * SQL模板库
     */
    private final Map<String, SQLTemplate> templateLibrary = new HashMap<>();
    
    public SQLTemplateEngine() {
        initializeTemplates();
    }
    
    /**
     * 初始化SQL模板库
     */
    private void initializeTemplates() {
        // 模板1: 查询空闲道闸（按社区分配，每社区N个）
        templateLibrary.put("BARRIER_SELECT_WITH_DISTRIBUTION", new SQLTemplate(
            "SELECT_AVAILABLE_BARRIERS_WITH_DISTRIBUTION",
            """
            WITH available_barriers AS (
                SELECT 
                    bg.id as barrier_gate_id,
                    bg.gate_no,
                    bg.community_id,
                    c.building_name as community_name,
                    bg.device_no,
                    bg.door_location,
                    bg.device_position,
                    ROW_NUMBER() OVER (PARTITION BY bg.community_id ORDER BY bg.device_position, bg.gate_no) as rn
                FROM barrier_gate bg
                INNER JOIN community c ON bg.community_id = c.id
                WHERE bg.status = 1
                AND (c.id IN (:communityIds) OR :communityIds IS NULL)
                AND (c.city = :city OR :city IS NULL)
                AND (c.district = :district OR :district IS NULL)
                AND (bg.device_position IN (:devicePositions) OR :devicePositions IS NULL)
                AND (bg.screen_position = :screenPosition OR :screenPosition IS NULL)
                AND (bg.lightbox_direction = :lightboxDirection OR :lightboxDirection IS NULL)
                AND bg.id NOT IN (
                    SELECT pb.barrier_gate_id 
                    FROM plan_barrier pb
                    WHERE pb.release_status IN (2, 3, 4)
                    AND NOT (pb.release_date_end < :beginDate OR pb.release_date_begin > :endDate)
                )
            ),
            selected_communities AS (
                SELECT community_id, community_name
                FROM available_barriers
                WHERE rn <= :perCommunityCount
                GROUP BY community_id, community_name
                HAVING COUNT(*) >= :minPerCommunity
                LIMIT :maxCommunities
            )
            SELECT 
                ab.barrier_gate_id,
                ab.gate_no,
                ab.community_id,
                ab.community_name,
                ab.device_no,
                ab.door_location,
                ab.device_position
            FROM available_barriers ab
            INNER JOIN selected_communities sc ON ab.community_id = sc.community_id
            WHERE ab.rn <= :perCommunityCount
            ORDER BY ab.community_id, ab.rn
            LIMIT :totalLimit
            """
        ));
        
        // 模板2: 查询空闲框架（按社区分配，每社区N个）
        templateLibrary.put("FRAME_SELECT_WITH_DISTRIBUTION", new SQLTemplate(
            "SELECT_AVAILABLE_FRAMES_WITH_DISTRIBUTION",
            """
            WITH available_frames AS (
                SELECT 
                    f.id as frame_id,
                    f.frame_no,
                    f.community_id,
                    c.building_name as community_name,
                    f.building,
                    f.unit,
                    f.elevator,
                    f.inner_position,
                    ROW_NUMBER() OVER (PARTITION BY f.community_id ORDER BY f.building, f.unit, f.elevator) as rn
                FROM frame f
                INNER JOIN community c ON f.community_id = c.id
                WHERE f.status = 1
                AND (c.id IN (:communityIds) OR :communityIds IS NULL)
                AND (c.city = :city OR :city IS NULL)
                AND (c.district = :district OR :district IS NULL)
                AND (f.inner_position = :innerPosition OR :innerPosition IS NULL)
                AND f.id NOT IN (
                    SELECT pf.frame_id 
                    FROM plan_frame pf
                    WHERE pf.release_status IN (2, 3, 4)
                    AND NOT (pf.release_date_end < :beginDate OR pf.release_date_begin > :endDate)
                )
            ),
            selected_communities AS (
                SELECT community_id, community_name
                FROM available_frames
                WHERE rn <= :perCommunityCount
                GROUP BY community_id, community_name
                HAVING COUNT(*) >= :minPerCommunity
                LIMIT :maxCommunities
            )
            SELECT 
                af.frame_id,
                af.frame_no,
                af.community_id,
                af.community_name,
                af.building,
                af.unit,
                af.elevator,
                af.inner_position
            FROM available_frames af
            INNER JOIN selected_communities sc ON af.community_id = sc.community_id
            WHERE af.rn <= :perCommunityCount
            ORDER BY af.community_id, af.rn
            LIMIT :totalLimit
            """
        ));
        
        // 模板3: 简单查询空闲道闸（不限制每社区数量）
        templateLibrary.put("BARRIER_SELECT_SIMPLE", new SQLTemplate(
            "SELECT_AVAILABLE_BARRIERS_SIMPLE",
            """
            SELECT 
                bg.id as barrier_gate_id,
                bg.gate_no,
                bg.community_id,
                c.building_name as community_name,
                bg.device_no,
                bg.door_location,
                bg.device_position
            FROM barrier_gate bg
            INNER JOIN community c ON bg.community_id = c.id
            WHERE bg.status = 1
            AND (c.id IN (:communityIds) OR :communityIds IS NULL)
            AND (c.city = :city OR :city IS NULL)
            AND (bg.device_position IN (:devicePositions) OR :devicePositions IS NULL)
            AND bg.id NOT IN (
                SELECT pb.barrier_gate_id 
                FROM plan_barrier pb
                WHERE pb.release_status IN (2, 3, 4)
                AND NOT (pb.release_date_end < :beginDate OR pb.release_date_begin > :endDate)
            )
            ORDER BY c.building_name, bg.gate_no
            LIMIT :limit
            """
        ));
        
        // 模板4: 查询社区可用点位统计
        templateLibrary.put("COMMUNITY_AVAILABILITY_STATS", new SQLTemplate(
            "SELECT_COMMUNITY_AVAILABILITY",
            """
            SELECT 
                c.id as community_id,
                c.building_name as community_name,
                c.city,
                c.district,
                COUNT(DISTINCT bg.id) as available_barrier_count,
                COUNT(DISTINCT f.id) as available_frame_count
            FROM community c
            LEFT JOIN barrier_gate bg ON c.id = bg.community_id 
                AND bg.status = 1
                AND bg.id NOT IN (
                    SELECT pb.barrier_gate_id 
                    FROM plan_barrier pb
                    WHERE pb.release_status IN (2, 3, 4)
                    AND NOT (pb.release_date_end < :beginDate OR pb.release_date_begin > :endDate)
                )
            LEFT JOIN frame f ON c.id = f.community_id 
                AND f.status = 1
                AND f.id NOT IN (
                    SELECT pf.frame_id 
                    FROM plan_frame pf
                    WHERE pf.release_status IN (2, 3, 4)
                    AND NOT (pf.release_date_end < :beginDate OR pf.release_date_begin > :endDate)
                )
            WHERE (c.city = :city OR :city IS NULL)
            AND (c.district = :district OR :district IS NULL)
            GROUP BY c.id, c.building_name, c.city, c.district
            HAVING available_barrier_count > 0 OR available_frame_count > 0
            ORDER BY available_barrier_count + available_frame_count DESC
            LIMIT :limit
            """
        ));
    }
    
    /**
     * 根据DSL生成SQL
     */
    public SQLGenerationResult generateSQL(PointSelectionDSL dsl) {
        try {
            // 1. 选择模板
            SQLTemplate template = selectTemplate(dsl);
            
            // 2. 准备参数
            Map<String, Object> params = prepareParameters(dsl);
            
            // 3. 生成SQL
            String sql = fillTemplate(template.getSql(), params);
            
            // 4. SQL安全校验
            if (!validateSQL(sql)) {
                return SQLGenerationResult.error("SQL安全校验失败");
            }
            
            return SQLGenerationResult.success(sql, params);
            
        } catch (Exception e) {
            return SQLGenerationResult.error("SQL生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 选择SQL模板
     */
    private SQLTemplate selectTemplate(PointSelectionDSL dsl) {
        String mediaType = dsl.getMediaType();
        PointSelectionDSL.DistributionStrategy strategy = dsl.getDistributionStrategy();
        
        if ("barrier".equalsIgnoreCase(mediaType)) {
            if (strategy != null && "per_community".equals(strategy.getType())) {
                return templateLibrary.get("BARRIER_SELECT_WITH_DISTRIBUTION");
            }
            return templateLibrary.get("BARRIER_SELECT_SIMPLE");
        } else if ("frame".equalsIgnoreCase(mediaType)) {
            return templateLibrary.get("FRAME_SELECT_WITH_DISTRIBUTION");
        }
        
        throw new IllegalArgumentException("不支持的媒体类型: " + mediaType);
    }
    
    /**
     * 准备SQL参数
     */
    private Map<String, Object> prepareParameters(PointSelectionDSL dsl) {
        Map<String, Object> params = new HashMap<>();
        
        // 日期范围
        if (dsl.getDateRange() != null) {
            params.put("beginDate", dsl.getDateRange().getBeginDate());
            params.put("endDate", dsl.getDateRange().getEndDate());
        }
        
        // 筛选条件
        PointSelectionDSL.FilterConditions filters = dsl.getFilterConditions();
        if (filters != null) {
            params.put("communityIds", filters.getCommunityIds());
            params.put("city", filters.getCity());
            params.put("district", filters.getDistrict());
            params.put("screenPosition", filters.getScreenPosition());
            params.put("lightboxDirection", filters.getLightboxDirection());
            params.put("innerPosition", filters.getInnerPosition());
            
            // 设备位置转换
            String devicePosition = filters.getDevicePosition();
            if (devicePosition != null) {
                List<Integer> positions = convertDevicePosition(devicePosition);
                params.put("devicePositions", positions);
            }
        }
        
        // 分配策略参数
        PointSelectionDSL.DistributionStrategy strategy = dsl.getDistributionStrategy();
        if (strategy != null) {
            params.put("perCommunityCount", strategy.getPerCommunityCount() != null ? 
                strategy.getPerCommunityCount() : 2);
            params.put("maxCommunities", strategy.getMaxCommunities() != null ? 
                strategy.getMaxCommunities() : 100);
            params.put("minPerCommunity", strategy.getMinCommunities() != null ? 
                strategy.getMinCommunities() : 0);
        }
        
        // 限制条件
        PointSelectionDSL.LimitConditions limits = dsl.getLimitConditions();
        if (limits != null) {
            params.put("totalLimit", limits.getMaxResults());
            params.put("limit", limits.getMaxResults());
        } else if (dsl.getTargetCount() != null) {
            params.put("totalLimit", dsl.getTargetCount());
            params.put("limit", dsl.getTargetCount());
        }
        
        return params;
    }
    
    /**
     * 转换设备位置为数据库值
     */
    private List<Integer> convertDevicePosition(String devicePosition) {
        return switch (devicePosition.toLowerCase()) {
            case "entrance" -> List.of(1);  // 进口
            case "exit" -> List.of(2);      // 出口
            case "both" -> List.of(1, 2, 3); // 进出口
            default -> List.of(1, 2, 3);
        };
    }
    
    /**
     * 填充SQL模板
     */
    private String fillTemplate(String template, Map<String, Object> params) {
        String result = template;
        
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = ":" + entry.getKey();
            Object value = entry.getValue();
            
            String replacement;
            if (value == null) {
                replacement = "NULL";
            } else if (value instanceof List) {
                replacement = formatList((List<?>) value);
            } else if (value instanceof String) {
                replacement = "'" + escapeString((String) value) + "'";
            } else if (value instanceof java.time.LocalDate) {
                replacement = "'" + value.toString() + "'";
            } else {
                replacement = value.toString();
            }
            
            result = result.replace(key, replacement);
        }
        
        // 处理OR NULL条件（当参数为NULL时使整个条件为TRUE）
        result = result.replaceAll("OR NULL IS NULL", "");
        result = result.replaceAll("= NULL", "IS NULL");
        result = result.replaceAll("<> NULL", "IS NOT NULL");
        
        return result;
    }
    
    /**
     * 格式化列表参数
     */
    private String formatList(List<?> list) {
        if (list == null || list.isEmpty()) {
            return "NULL";
        }
        return list.stream()
            .map(item -> {
                if (item instanceof String) {
                    return "'" + escapeString((String) item) + "'";
                }
                return item.toString();
            })
            .collect(Collectors.joining(", "));
    }
    
    /**
     * 转义SQL字符串 - 增强版，防止SQL注入
     */
    private String escapeString(String str) {
        if (str == null) {
            return "";
        }
        return str
            .replace("'", "''")          // 转义单引号
            .replace("\\", "\\\\")      // 转义反斜杠
            .replace("\n", "\\n")       // 转义换行符
            .replace("\r", "\\r")       // 转义回车符
            .replace("\t", "\\t")       // 转义制表符
            .replace("\b", "\\b")       // 转义退格符
            .replace("\f", "\\f")       // 转义换页符
            .replace("\0", "");          // 移除null字符
    }
    
    /**
     * SQL安全校验
     */
    private boolean validateSQL(String sql) {
        // 检查危险操作
        String lowerSQL = sql.toLowerCase();
        String[] forbiddenKeywords = {"drop", "truncate", "delete", "update", "insert"};
        
        for (String keyword : forbiddenKeywords) {
            if (lowerSQL.contains(keyword)) {
                return false;
            }
        }
        
        // 确保是查询语句
        if (!lowerSQL.trim().startsWith("select") && !lowerSQL.trim().startsWith("with")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * SQL模板定义
     */
    public static class SQLTemplate {
        private final String name;
        private final String sql;
        
        public SQLTemplate(String name, String sql) {
            this.name = name;
            this.sql = sql;
        }
        
        public String getName() { return name; }
        public String getSql() { return sql; }
    }
    
    /**
     * SQL生成结果
     */
    public static class SQLGenerationResult {
        private final boolean success;
        private final String sql;
        private final Map<String, Object> params;
        private final String errorMessage;
        
        private SQLGenerationResult(boolean success, String sql, Map<String, Object> params, String errorMessage) {
            this.success = success;
            this.sql = sql;
            this.params = params;
            this.errorMessage = errorMessage;
        }
        
        public static SQLGenerationResult success(String sql, Map<String, Object> params) {
            return new SQLGenerationResult(true, sql, params, null);
        }
        
        public static SQLGenerationResult error(String message) {
            return new SQLGenerationResult(false, null, null, message);
        }
        
        public boolean isSuccess() { return success; }
        public String getSql() { return sql; }
        public Map<String, Object> getParams() { return params; }
        public String getErrorMessage() { return errorMessage; }
    }
}