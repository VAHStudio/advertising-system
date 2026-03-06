-- 为社区、道闸、框架、方案表添加过滤查询索引
-- 创建时间: 2025-03-06

-- ==================== community 表索引 ====================
ALTER TABLE community ADD INDEX idx_community_city (city);
ALTER TABLE community ADD INDEX idx_community_building_name (building_name);
ALTER TABLE community ADD INDEX idx_community_community_no (community_no);
ALTER TABLE community ADD INDEX idx_community_coord (coord_lat, coord_lng);
ALTER TABLE community ADD INDEX idx_community_created_at (created_at);
ALTER TABLE community ADD INDEX idx_community_updated_at (updated_at);

-- ==================== barrier_gate 表索引 ====================
ALTER TABLE barrier_gate ADD INDEX idx_barrier_community_id (community_id);
ALTER TABLE barrier_gate ADD INDEX idx_barrier_gate_no (gate_no);
ALTER TABLE barrier_gate ADD INDEX idx_barrier_device_no (device_no);
ALTER TABLE barrier_gate ADD INDEX idx_barrier_device_position (device_position);
ALTER TABLE barrier_gate ADD INDEX idx_barrier_screen_position (screen_position);
ALTER TABLE barrier_gate ADD INDEX idx_barrier_lightbox_direction (lightbox_direction);

-- ==================== frame 表索引 ====================
ALTER TABLE frame ADD INDEX idx_frame_community_id (community_id);
ALTER TABLE frame ADD INDEX idx_frame_frame_no (frame_no);
ALTER TABLE frame ADD INDEX idx_frame_building (building);
ALTER TABLE frame ADD INDEX idx_frame_inner_position (inner_position);

-- ==================== plan 表索引 ====================
ALTER TABLE plan ADD INDEX idx_plan_plan_no (plan_no);
ALTER TABLE plan ADD INDEX idx_plan_release_status (release_status);
ALTER TABLE plan ADD INDEX idx_plan_sales_type (sales_type);
ALTER TABLE plan ADD INDEX idx_plan_media_type (media_type);
ALTER TABLE plan ADD INDEX idx_plan_release_date_begin (release_date_begin);
ALTER TABLE plan ADD INDEX idx_plan_release_date_end (release_date_end);
ALTER TABLE plan ADD INDEX idx_plan_customer (customer);
ALTER TABLE plan ADD INDEX idx_plan_sales_person (sales_person);
ALTER TABLE plan ADD INDEX idx_plan_created_at (created_at);
ALTER TABLE plan ADD INDEX idx_plan_updated_at (updated_at);

-- 复合索引（用于组合查询优化）
ALTER TABLE plan ADD INDEX idx_plan_status_type (release_status, media_type);
ALTER TABLE plan ADD INDEX idx_plan_status_date (release_status, release_date_begin);

-- ==================== plan_community 关联表索引 ====================
ALTER TABLE plan_community ADD INDEX idx_pc_plan_id (plan_id);
ALTER TABLE plan_community ADD INDEX idx_pc_community_id (community_id);

-- ==================== plan_barrier 关联表索引 ====================
ALTER TABLE plan_barrier ADD INDEX idx_pb_plan_id (plan_id);
ALTER TABLE plan_barrier ADD INDEX idx_pb_barrier_id (barrier_gate_id);
ALTER TABLE plan_barrier ADD INDEX idx_pb_status_date (release_status, release_date_begin, release_date_end);

-- ==================== plan_frame 关联表索引 ====================
ALTER TABLE plan_frame ADD INDEX idx_pf_plan_id (plan_id);
ALTER TABLE plan_frame ADD INDEX idx_pf_frame_id (frame_id);
ALTER TABLE plan_frame ADD INDEX idx_pf_status_date (release_status, release_date_begin, release_date_end);
