package com.advertising.agent.intent;

/**
 * Agent 意图类型定义
 * 基于业务场景清单设计
 */
public enum IntentType {
    
    // ========== 媒介域 ==========
    
    // 资源查询类 (M001-M010)
    RESOURCE_AVAILABILITY_QUERY,    // 查询某楼盘有哪些空闲点位
    OCCUPANCY_QUERY,                // 查询某档期哪些点位被占用了
    POINT_HISTORY_QUERY,            // 查询某点位的历史占用情况
    BUILDING_LIST_QUERY,            // 查询某区域的楼盘列表
    BUILDING_DETAIL_QUERY,          // 查询某楼盘的详细信息
    PRICE_QUERY,                    // 查询某点位的刊例价
    SPEC_QUERY,                     // 查询某点位的规格参数
    POINT_CAMPAIGN_HISTORY,         // 查询某点位的上刊历史
    MAINTENANCE_QUERY,              // 查询某点位的维护记录
    POINT_PHOTO_QUERY,              // 查询某点位的照片
    
    // 资源分配类 (M011-M020)
    RESOURCE_ALLOCATION_BY_QUANTITY,       // 按数量分配点位
    RESOURCE_ALLOCATION_BY_BUDGET,         // 按预算分配点位
    RESOURCE_ALLOCATION_EVEN_DISTRIBUTION, // 按楼盘均匀分配
    RESOURCE_ALLOCATION_BY_PRIORITY,       // 按优先级分配点位
    RESOURCE_ALLOCATION_AUTO_FILL,         // 智能补量
    RESOURCE_ALLOCATION_FORCE_ASSIGN,      // 指定点位强制分配
    RESOURCE_ALLOCATION_EXCLUDE_BUILDING,  // 排除特定楼盘分配
    RESOURCE_ALLOCATION_BY_POSITION,       // 指定位置类型分配
    RESOURCE_ALLOCATION_MIXED_RESOURCE,    // 跨资源类型混合分配
    RESOURCE_ALLOCATION_RESERVE,           // 预留点位
    
    // 方案管理类 (M021-M030)
    PLAN_CREATE,           // 创建新方案
    PLAN_DUPLICATE,        // 复制已有方案
    PLAN_MODIFY_DATE,      // 修改方案档期
    PLAN_MODIFY_POINTS,    // 修改方案点位
    PLAN_DELETE,           // 删除方案
    PLAN_ARCHIVE,          // 方案归档
    PLAN_MERGE,            // 合并多个方案
    PLAN_SPLIT,            // 拆分方案
    PLAN_VERSION_COMPARE,  // 方案版本对比
    PLAN_ROLLBACK,         // 方案回滚
    
    // 档期管理类 (M031-M040)
    PERIOD_OCCUPANCY_QUERY,       // 查询某档期占用情况
    PERIOD_AVAILABILITY_QUERY,    // 查询某档期空闲资源
    PERIOD_CONFLICT_CHECK,        // 档期冲突检测
    PERIOD_ADJUSTMENT_SUGGEST,    // 档期调整建议
    BATCH_PLAN_POSTPONE,          // 批量延期方案
    BATCH_PLAN_ADVANCE,           // 批量提前方案
    PERIOD_LOCK,                  // 档期锁定
    PERIOD_UNLOCK,                // 档期解锁
    PERIOD_OCCUPANCY_RATE,        // 档期占用率统计
    PERIOD_REVENUE_FORECAST,      // 档期收入预测
    
    // 监播管理类 (M041-M050)
    PUBLISH_CONFIRM,              // 上刊确认
    UNPUBLISH_CONFIRM,            // 下刊确认
    MONITORING_ISSUE_REPORT,      // 监播异常上报
    MONITORING_ISSUE_RESOLVE,     // 监播异常处理
    MONITORING_PHOTO_UPLOAD,      // 监播照片上传
    MONITORING_REPORT_GENERATE,   // 监播报告生成
    MONITORING_TASK_ASSIGN,       // 监播任务分配
    MONITORING_PROGRESS_QUERY,    // 监播进度查询
    MONITORING_QUALITY_CHECK,     // 监播质量检查
    MONITORING_DATA_STATISTICS,   // 监播数据统计
    
    // 数据分析类 (M051-M060)
    POINT_UTILIZATION_ANALYSIS,      // 点位利用率分析
    BUILDING_POPULARITY_ANALYSIS,    // 楼盘热度分析
    REGION_DISTRIBUTION_ANALYSIS,    // 区域分布分析
    RESOURCE_TYPE_RATIO_ANALYSIS,    // 资源类型占比分析
    PERIOD_DISTRIBUTION_ANALYSIS,    // 档期分布分析
    CLIENT_INDUSTRY_ANALYSIS,        // 客户行业分布
    PRICE_TREND_ANALYSIS,            // 价格趋势分析
    OCCUPANCY_TREND_ANALYSIS,        // 占用率趋势分析
    COMPETITOR_ANALYSIS,             // 竞品投放分析
    RESOURCE_GAP_ANALYSIS,           // 资源缺口分析
    
    // 维护管理类 (M061-M070)
    MAINTENANCE_REPORT,              // 点位维修上报
    MAINTENANCE_ASSIGN,              // 维修任务分配
    MAINTENANCE_PROGRESS_QUERY,      // 维修进度查询
    MAINTENANCE_COMPLETE,            // 维修完成确认
    INSPECTION_PLAN_CREATE,          // 点位巡检计划
    INSPECTION_TASK_EXECUTE,         // 巡检任务执行
    EQUIPMENT_REPLACE_RECORD,        // 设备更换记录
    POINT_SCRAP_PROCESS,             // 点位报废处理
    POINT_ADD,                       // 新增点位录入
    POINT_INFO_MODIFY,               // 点位信息修改
    
    // 报表生成类 (M071-M080)
    DAILY_REPORT_GENERATE,           // 日报生成
    WEEKLY_REPORT_GENERATE,          // 周报生成
    MONTHLY_REPORT_GENERATE,         // 月报生成
    RESOURCE_INVENTORY_REPORT,       // 资源盘点表
    OCCUPANCY_STATUS_REPORT,         // 占用情况表
    REVENUE_FORECAST_REPORT,         // 收入预测表
    CLIENT_CAMPAIGN_SUMMARY,         // 客户投放汇总
    BUILDING_PERFORMANCE_RANKING,    // 楼盘业绩排名
    MEDIA_PERFORMANCE_STATISTICS,    // 媒介业绩统计
    EXCEPTION_SUMMARY_REPORT,        // 异常汇总报表
    
    // ========== 销售域 ==========
    
    // 客户管理类 (S001-S010)
    CLIENT_INFO_QUERY,               // 查询客户信息
    CLIENT_HISTORY_QUERY,            // 查询客户历史方案
    CLIENT_PREFERENCE_QUERY,         // 查询客户投放偏好
    CLIENT_CONTACT_HISTORY,          // 查询客户联系记录
    CLIENT_ADD,                      // 新增客户录入
    CLIENT_MODIFY,                   // 修改客户信息
    CLIENT_LEVEL_ADJUST,             // 客户分级调整
    CLIENT_OWNER_CHANGE,             // 客户归属变更
    CLIENT_MERGE,                    // 客户合并
    CLIENT_CHURN_ALERT,              // 客户流失预警
    
    // 商机管理类 (S011-S020)
    OPPORTUNITY_LIST_QUERY,          // 查询商机列表
    OPPORTUNITY_DETAIL_QUERY,        // 查询商机详情
    OPPORTUNITY_ADD,                 // 新增商机录入
    OPPORTUNITY_MODIFY,              // 修改商机信息
    OPPORTUNITY_STAGE_ADVANCE,       // 商机阶段推进
    OPPORTUNITY_CLOSE,               // 商机关闭
    OPPORTUNITY_ASSIGN,              // 商机分配
    OPPORTUNITY_CONVERT_PLAN,        // 商机转方案
    OPPORTUNITY_FOLLOWUP_REMIND,     // 商机跟进提醒
    OPPORTUNITY_FUNNEL_ANALYSIS,     // 商机漏斗分析
    
    // 报价管理类 (S021-S030)
    QUICK_QUOTE_GENERATE,            // 快速报价生成
    MULTI_QUOTE_COMPARE,             // 多方案报价对比
    QUOTE_HISTORY_QUERY,             // 历史报价查询
    QUOTE_MODIFY,                    // 报价修改
    QUOTE_APPROVAL_SUBMIT,           // 报价审批提交
    QUOTE_APPROVAL_PROCESS,          // 报价审批处理
    QUOTE_CONVERT_CONTRACT,          // 报价转合同
    DISCOUNT_APPLY,                  // 折扣申请
    DISCOUNT_APPROVE,                // 折扣审批
    PRICE_SIMULATION,                // 价格模拟计算
    
    // 合同管理类 (S031-S040)
    CONTRACT_LIST_QUERY,             // 查询合同列表
    CONTRACT_DETAIL_QUERY,           // 查询合同详情
    CONTRACT_GENERATE,               // 合同生成
    CONTRACT_MODIFY,                 // 合同修改
    CONTRACT_APPROVAL,               // 合同审批
    CONTRACT_SIGN_CONFIRM,           // 合同签署确认
    CONTRACT_TERMINATE,              // 合同终止
    CONTRACT_RENEWAL,                // 合同续约
    CONTRACT_AMENDMENT,              // 合同变更
    CONTRACT_EXPIRY_REMIND,          // 合同到期提醒
    
    // 收款管理类 (S041-S050)
    RECEIVABLE_QUERY,                // 查询应收款
    PAYMENT_RECEIVED_QUERY,          // 查询收款记录
    PAYMENT_RECORD_ADD,              // 录入收款
    PAYMENT_SCHEDULE_GENERATE,       // 收款计划生成
    PAYMENT_REMIND,                  // 收款提醒
    OVERDUE_PAYMENT_STATISTICS,      // 逾期款统计
    INVOICE_APPLY,                   // 发票申请
    INVOICE_ISSUE,                   // 发票开具
    INVOICE_CANCEL,                  // 发票作废
    CLIENT_STATEMENT_GENERATE,       // 客户对账单
    
    // 销售分析类 (S051-S060)
    SALES_PERFORMANCE_STATISTICS,    // 销售业绩统计
    SALES_TARGET_ACHIEVEMENT,        // 销售目标达成
    CLIENT_GROWTH_ANALYSIS,          // 客户增长分析
    CLIENT_VALUE_ANALYSIS,           // 客户价值分析
    WIN_RATE_ANALYSIS,               // 赢单率分析
    AVG_DEAL_SIZE_ANALYSIS,          // 平均客单价
    SALES_CYCLE_ANALYSIS,            // 销售周期分析
    CLIENT_RETENTION_ANALYSIS,       // 客户留存率
    REGION_SALES_COMPARISON,         // 区域销售对比
    PRODUCT_SALES_ANALYSIS,          // 产品销售分析
    
    // 客户服务类 (S061-S070)
    COMPLAINT_RECORD,                // 客户投诉记录
    COMPLAINT_HANDLE,                // 投诉处理跟进
    SATISFACTION_SURVEY,             // 客户满意度调查
    CALLBACK_PLAN_CREATE,            // 客户回访计划
    CALLBACK_RECORD,                 // 回访记录
    CLIENT_SELF_SERVICE_QUERY,       // 客户自助查询
    CLIENT_REPORT_PUSH,              // 客户报告推送
    CLIENT_QA,                       // 客户问题解答
    CLIENT_TRAINING_SCHEDULE,        // 客户培训安排
    CLIENT_EVENT_INVITE,             // 客户活动邀请
    
    // 市场洞察类 (S071-S080)
    COMPETITOR_MONITORING,           // 竞品投放监测
    INDUSTRY_TREND_ANALYSIS,         // 行业趋势分析
    HOTSPOT_AREA_IDENTIFY,           // 热点区域识别
    PRICE_SENSITIVITY_ANALYSIS,      // 价格敏感度分析
    CLIENT_DEMAND_FORECAST,          // 客户需求预测
    POTENTIAL_CLIENT_MINING,         // 潜在客户挖掘
    CROSS_SELL_RECOMMENDATION,       // 交叉销售推荐
    UPSELL_OPPORTUNITY_IDENTIFY,     // upsell机会识别
    CAMPAIGN_ROI_ANALYSIS,           // 市场活动ROI
    BRAND_HEALTH_MONITORING,         // 品牌健康度监测
    
    // ========== 通用域 ==========
    
    // 系统管理类 (G001-G010)
    USER_PERMISSION_QUERY,           // 用户权限查询
    OPERATION_LOG_QUERY,             // 操作日志查询
    DATA_EXPORT,                     // 数据导出
    DATA_IMPORT,                     // 数据导入
    SYSTEM_CONFIG_QUERY,             // 系统配置查询
    SYSTEM_CONFIG_MODIFY,            // 系统配置修改
    NOTIFICATION_QUERY,              // 通知消息查询
    TODO_LIST_QUERY,                 // 待办事项查询
    APPROVAL_FLOW_QUERY,             // 审批流程查询
    APPROVAL_PROCESS,                // 审批处理
    
    // 数据智能类 (G011-G020)
    NL_TO_SQL,                       // 自然语言转查询
    SMART_RECOMMENDATION,            // 智能推荐
    ANOMALY_DETECTION,               // 异常检测
    TREND_FORECAST,                  // 趋势预测
    SMART_COMPLETION,                // 智能补全
    SIMILAR_CASE_SEARCH,             // 相似案例查找
    AUTO_CLASSIFICATION,             // 自动分类
    SENTIMENT_ANALYSIS,              // 情感分析
    DATA_VISUALIZATION,              // 数据可视化
    REPORT_AUTO_GENERATE,            // 报告自动生成
    
    // ========== 未知 ==========
    UNKNOWN                          // 无法识别的意图
}