package com.advertising.agent.dsl;

import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 扩展SQL模板库
 * 支持170个业务场景
 */
@Component
public class SQLTemplateLibrary {
    
    private final Map<String, SQLTemplateDef> templates = new HashMap<>();
    
    public SQLTemplateLibrary() {
        initializeTemplates();
    }
    
    private void initializeTemplates() {
        // ========== 媒介域 ==========
        
        // M001: 查询某楼盘有哪些空闲点位
        addTemplate("RESOURCE_AVAILABILITY_QUERY", 
            """
            SELECT 
                bg.id as point_id,
                bg.gate_no as point_no,
                'barrier' as media_type,
                c.building_name as community_name,
                bg.door_location,
                bg.device_position
            FROM barrier_gate bg
            JOIN community c ON bg.community_id = c.id
            WHERE c.id = :communityId
            AND bg.status = 1
            AND bg.id NOT IN (
                SELECT pb.barrier_gate_id
                FROM plan_barrier pb
                WHERE pb.release_status IN (2,3,4)
                AND NOT (pb.release_date_end < :beginDate OR pb.release_date_begin > :endDate)
            )
            UNION ALL
            SELECT 
                f.id,
                f.frame_no,
                'frame',
                c.building_name,
                CONCAT(f.building, '栋', f.unit, '单元'),
                f.inner_position
            FROM frame f
            JOIN community c ON f.community_id = c.id
            WHERE c.id = :communityId
            AND f.status = 1
            AND f.id NOT IN (
                SELECT pf.frame_id
                FROM plan_frame pf
                WHERE pf.release_status IN (2,3,4)
                AND NOT (pf.release_date_end < :beginDate OR pf.release_date_begin > :endDate)
            )
            """
        );
        
        // M002: 查询某档期哪些点位被占用了
        addTemplate("OCCUPANCY_QUERY",
            """
            SELECT 
                pb.id as plan_detail_id,
                pb.barrier_gate_id as point_id,
                'barrier' as media_type,
                bg.gate_no,
                c.building_name as community_name,
                p.plan_name,
                p.customer,
                pb.release_date_begin,
                pb.release_date_end,
                pb.release_status
            FROM plan_barrier pb
            JOIN plan p ON pb.plan_id = p.id
            JOIN barrier_gate bg ON pb.barrier_gate_id = bg.id
            JOIN community c ON bg.community_id = c.id
            WHERE pb.release_status IN (2,3,4)
            AND NOT (pb.release_date_end < :beginDate OR pb.release_date_begin > :endDate)
            AND (c.id = :communityId OR :communityId IS NULL)
            UNION ALL
            SELECT 
                pf.id,
                pf.frame_id,
                'frame',
                f.frame_no,
                c.building_name,
                p.plan_name,
                p.customer,
                pf.release_date_begin,
                pf.release_date_end,
                pf.release_status
            FROM plan_frame pf
            JOIN plan p ON pf.plan_id = p.id
            JOIN frame f ON pf.frame_id = f.id
            JOIN community c ON f.community_id = c.id
            WHERE pf.release_status IN (2,3,4)
            AND NOT (pf.release_date_end < :beginDate OR pf.release_date_begin > :endDate)
            AND (c.id = :communityId OR :communityId IS NULL)
            ORDER BY community_name, media_type, point_id
            """
        );
        
        // M005: 查询某楼盘的详细信息
        addTemplate("BUILDING_DETAIL_QUERY",
            """
            SELECT 
                c.id as community_id,
                c.community_no,
                c.building_name,
                c.building_address,
                c.city,
                c.district,
                COUNT(DISTINCT bg.id) as total_barriers,
                COUNT(DISTINCT f.id) as total_frames,
                SUM(CASE WHEN bg.status = 1 THEN 1 ELSE 0 END) as active_barriers,
                SUM(CASE WHEN f.status = 1 THEN 1 ELSE 0 END) as active_frames
            FROM community c
            LEFT JOIN barrier_gate bg ON c.id = bg.community_id
            LEFT JOIN frame f ON c.id = f.community_id
            WHERE c.id = :communityId
            GROUP BY c.id, c.community_no, c.building_name, c.building_address, c.city, c.district
            """
        );
        
        // M011: 按数量分配点位（支持每社区分配）
        addTemplate("RESOURCE_ALLOCATION_BY_QUANTITY",
            """
            WITH available_points AS (
                SELECT 
                    bg.id as point_id,
                    bg.gate_no as point_no,
                    bg.community_id,
                    c.building_name as community_name,
                    bg.device_position,
                    ROW_NUMBER() OVER (
                        PARTITION BY bg.community_id 
                        ORDER BY 
                            CASE WHEN bg.device_position = :preferredPosition THEN 0 ELSE 1 END,
                            bg.gate_no
                    ) as rn
                FROM barrier_gate bg
                JOIN community c ON bg.community_id = c.id
                WHERE bg.status = 1
                AND (c.id IN (:communityIds) OR :communityIds IS NULL)
                AND (c.city = :city OR :city IS NULL)
                AND (bg.device_position IN (:devicePositions) OR :devicePositions IS NULL)
                AND bg.id NOT IN (
                    SELECT pb.barrier_gate_id 
                    FROM plan_barrier pb
                    WHERE pb.release_status IN (2,3,4)
                    AND NOT (pb.release_date_end < :beginDate OR pb.release_date_begin > :endDate)
                )
            ),
            selected_communities AS (
                SELECT DISTINCT community_id, community_name
                FROM available_points
                WHERE rn <= :perCommunityCount
                LIMIT :maxCommunities
            )
            SELECT 
                ap.point_id,
                ap.point_no,
                ap.community_id,
                ap.community_name,
                ap.device_position
            FROM available_points ap
            INNER JOIN selected_communities sc ON ap.community_id = sc.community_id
            WHERE ap.rn <= :perCommunityCount
            ORDER BY ap.community_id, ap.rn
            LIMIT :totalCount
            """
        );
        
        // M031: 查询某档期占用情况
        addTemplate("PERIOD_OCCUPANCY_QUERY",
            """
            SELECT 
                DATE_FORMAT(date_range.date, '%Y-%m-%d') as date,
                COUNT(DISTINCT pb.id) as occupied_barriers,
                COUNT(DISTINCT pf.id) as occupied_frames,
                (SELECT COUNT(*) FROM barrier_gate WHERE status = 1) - COUNT(DISTINCT pb.id) as free_barriers,
                (SELECT COUNT(*) FROM frame WHERE status = 1) - COUNT(DISTINCT pf.id) as free_frames
            FROM (
                SELECT DATE_ADD(:beginDate, INTERVAL n DAY) as date
                FROM (SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 
                      UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) nums
                WHERE DATE_ADD(:beginDate, INTERVAL n DAY) <= :endDate
            ) date_range
            LEFT JOIN plan_barrier pb ON date_range.date BETWEEN pb.release_date_begin AND pb.release_date_end
                AND pb.release_status IN (2,3,4)
            LEFT JOIN plan_frame pf ON date_range.date BETWEEN pf.release_date_begin AND pf.release_date_end
                AND pf.release_status IN (2,3,4)
            GROUP BY date_range.date
            ORDER BY date_range.date
            """
        );
        
        // S001: 查询客户信息
        addTemplate("CLIENT_INFO_QUERY",
            """
            SELECT 
                p.id as client_id,
                p.customer as client_name,
                COUNT(DISTINCT pl.id) as total_plans,
                SUM(pl.budget) as total_budget,
                MAX(pl.created_at) as last_plan_date
            FROM plan p
            LEFT JOIN plan pl ON p.customer = pl.customer
            WHERE p.customer LIKE CONCAT('%', :clientName, '%')
            GROUP BY p.id, p.customer
            ORDER BY last_plan_date DESC
            LIMIT :limit
            """
        );
        
        // S051: 销售业绩统计
        addTemplate("SALES_PERFORMANCE_STATISTICS",
            """
            SELECT 
                DATE_FORMAT(p.created_at, '%Y-%m') as month,
                COUNT(DISTINCT p.id) as plan_count,
                COUNT(DISTINCT p.customer) as client_count,
                SUM(p.budget) as total_budget,
                AVG(p.budget) as avg_budget
            FROM plan p
            WHERE p.created_at BETWEEN :beginDate AND :endDate
            GROUP BY DATE_FORMAT(p.created_at, '%Y-%m')
            ORDER BY month DESC
            """
        );
        
        // G011: 自然语言转SQL（简单查询）
        addTemplate("NL_TO_SQL",
            """
            -- 此模板用于NL2SQL场景，实际SQL由LLM动态生成
            -- 基础查询框架：
            SELECT * FROM ${tableName}
            WHERE ${conditions}
            LIMIT ${limit}
            """
        );
    }
    
    private void addTemplate(String intentType, String sql) {
        templates.put(intentType, new SQLTemplateDef(intentType, sql));
    }
    
    public SQLTemplateDef getTemplate(String intentType) {
        return templates.get(intentType);
    }
    
    public boolean hasTemplate(String intentType) {
        return templates.containsKey(intentType);
    }
    
    public Set<String> getAllTemplateNames() {
        return templates.keySet();
    }
    
    /**
     * 获取模板定义
     */
    public static class SQLTemplateDef {
        private final String name;
        private final String sql;
        private final List<String> requiredParams;
        private final List<String> optionalParams;
        
        public SQLTemplateDef(String name, String sql) {
            this.name = name;
            this.sql = sql;
            this.requiredParams = extractRequiredParams(sql);
            this.optionalParams = extractOptionalParams(sql);
        }
        
        private List<String> extractRequiredParams(String sql) {
            List<String> params = new ArrayList<>();
            // 提取 :paramName 格式的参数
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(":([a-zA-Z_][a-zA-Z0-9_]*)");
            java.util.regex.Matcher matcher = pattern.matcher(sql);
            while (matcher.find()) {
                params.add(matcher.group(1));
            }
            return params;
        }
        
        private List<String> extractOptionalParams(String sql) {
            // 参数以 OR :param IS NULL 形式出现时视为可选
            List<String> optional = new ArrayList<>();
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "\\(\\w+ = :([a-zA-Z_]+) OR :\\1 IS NULL\\)");
            java.util.regex.Matcher matcher = pattern.matcher(sql);
            while (matcher.find()) {
                optional.add(matcher.group(1));
            }
            return optional;
        }
        
        // Getters
        public String getName() { return name; }
        public String getSql() { return sql; }
        public List<String> getRequiredParams() { return requiredParams; }
        public List<String> getOptionalParams() { return optionalParams; }
    }
}