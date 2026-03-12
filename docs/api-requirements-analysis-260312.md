# 投小智场景API工具需求清单与缺口分析

**文档说明**: 本文档列出投小智AI智能体所有场景需要的API工具，并对比现有`openapi-dify.yaml`的实现状态，标注缺失项以便后续开发。

**生成日期**: 2024年3月  
**对应场景文档**: `dify-agent-scenarios.md`

---

## 总体统计

| 类别 | 已提供 | 缺失 | 总计 |
|------|--------|------|------|
| 客户管理 | 0 | 4 | 4 |
| 方案管理 | 5 | 4 | 9 |
| 资源查询 | 8 | 2 | 10 |
| 工程执行 | 0 | 7 | 7 |
| 财务对账 | 0 | 3 | 3 |
| 数据报表 | 0 | 2 | 2 |
| 合同管理 | 0 | 4 | 4 |
| 业绩分析 | 0 | 4 | 4 |
| 运维管理 | 0 | 3 | 3 |
| **总计** | **13** | **33** | **46** |

---

## 一、客户管理场景（4个API缺失）

### 1.1 客户查询
- **API名称**: `filter_customers`
- **方法**: POST
- **路径**: `/api/customer/filter/page`
- **状态**: ❌ **缺失**
- **功能**: 根据关键词、行业、城市等筛选客户
- **优先级**: 🔴 高（核心场景）
- **请求参数**:
  ```json
  {
    "keyword": "string",
    "industry": "string",
    "city": "string",
    "pageNum": 1,
    "pageSize": 10
  }
  ```

### 1.2 创建客户
- **API名称**: `create_customer`
- **方法**: POST
- **路径**: `/api/customer`
- **状态**: ❌ **缺失**
- **功能**: 创建新客户记录
- **优先级**: 🔴 高（核心场景）
- **请求参数**:
  ```json
  {
    "customerName": "string",
    "industry": "string",
    "city": "string",
    "address": "string",
    "contacts": [
      {
        "name": "string",
        "phone": "string",
        "title": "string"
      }
    ]
  }
  ```

### 1.3 添加联系人
- **API名称**: `add_contact`
- **方法**: POST
- **路径**: `/api/customer/{customerId}/contact`
- **状态**: ❌ **缺失**
- **功能**: 为已有客户添加对接人
- **优先级**: 🟡 中
- **请求参数**:
  ```json
  {
    "name": "string",
    "phone": "string",
    "title": "string",
    "isPrimary": true
  }
  ```

### 1.4 创建跟进记录
- **API名称**: `create_followup`
- **方法**: POST
- **路径**: `/api/customer/{customerId}/followup`
- **状态**: ❌ **缺失**
- **功能**: 记录客户联系和跟进
- **优先级**: 🔴 高（核心场景）
- **请求参数**:
  ```json
  {
    "contactId": 1,
    "contactType": "phone/visit/wechat",
    "content": "string",
    "nextFollowupDate": "2024-03-20",
    "reminder": true
  }
  ```

---

## 二、方案管理场景（4个API缺失）

### 2.1 ✅ 已提供API

| API名称 | 方法 | 路径 | 功能 |
|---------|------|------|------|
| `list_plans` | GET | `/api/plan/list` | 查询所有方案 |
| `get_plan` | GET | `/api/plan/{id}` | 获取方案详情 |
| `create_plan` | POST | `/api/plan` | 创建方案 |
| `update_plan` | PUT | `/api/plan` | 更新方案 |
| `delete_plan` | DELETE | `/api/plan/{id}` | 删除方案 |

### 2.2 ❌ 缺失API

#### 复制方案
- **API名称**: `copy_plan`
- **方法**: POST
- **路径**: `/api/plan/{id}/copy`
- **状态**: ❌ **缺失**
- **功能**: 复制已有方案生成新方案
- **优先级**: 🟡 中
- **请求参数**:
  ```json
  {
    "newPlanName": "string",
    "adjustDates": true,
    "dateOffset": 30
  }
  ```

#### 归档方案
- **API名称**: `archive_plan`
- **方法**: POST
- **路径**: `/api/plan/{id}/archive`
- **状态**: ❌ **缺失**
- **功能**: 将方案归档（非删除）
- **优先级**: 🟢 低
- **请求参数**:
  ```json
  {
    "archiveReason": "string"
  }
  ```

#### 锁定方案资源
- **API名称**: `lock_plan_resources`
- **方法**: POST
- **路径**: `/api/plan/{id}/lock`
- **状态**: ❌ **缺失**
- **功能**: 锁定方案关联的所有点位资源
- **优先级**: 🔴 高（核心场景）
- **请求参数**:
  ```json
  {
    "lockDuration": 7,
    "resourceTypes": ["barrier", "frame"]
  }
  ```

#### 解锁方案资源
- **API名称**: `unlock_plan_resources`
- **方法**: POST
- **路径**: `/api/plan/{id}/unlock`
- **状态**: ❌ **缺失**
- **功能**: 解锁方案关联的点位资源
- **优先级**: 🔴 高（核心场景）
- **说明**: 与锁定成对出现

---

## 三、资源查询场景（2个API缺失）

### 3.1 ✅ 已提供API

| API名称 | 方法 | 路径 | 功能 |
|---------|------|------|------|
| `list_communities` | GET | `/api/community/list` | 查询所有社区 |
| `get_community` | GET | `/api/community/{id}` | 获取社区详情 |
| `filter_communities` | POST | `/api/community/filter/page` | 分页查询社区 |
| `list_barrier_gates` | GET | `/api/barrier-gate/list` | 查询所有道闸 |
| `get_barrier_gate` | GET | `/api/barrier-gate/{id}` | 获取道闸详情 |
| `get_barrier_gates_by_community` | GET | `/api/barrier-gate/community/{communityId}` | 根据社区查道闸 |
| `list_frames` | GET | `/api/frame/list` | 查询所有框架 |
| `get_frame` | GET | `/api/frame/{id}` | 获取框架详情 |

### 3.2 ❌ 缺失API

#### 检查资源档期
- **API名称**: `check_resource_availability`
- **方法**: POST
- **路径**: `/api/resource/check-availability`
- **状态**: ❌ **缺失**
- **功能**: 检查指定时间段内资源是否可用
- **优先级**: 🔴 高（核心场景）
- **请求参数**:
  ```json
  {
    "resourceType": "barrier/frame",
    "resourceIds": [1, 2, 3],
    "startDate": "2024-05-01",
    "endDate": "2024-05-31"
  }
  ```
- **响应示例**:
  ```json
  {
    "available": [1, 3],
    "occupied": [
      {
        "resourceId": 2,
        "occupiedBy": "PLAN-2024-XXX",
        "period": "2024-05-01 to 2024-05-15"
      }
    ]
  }
  ```

#### 获取空位列表
- **API名称**: `get_empty_slots`
- **方法**: POST
- **路径**: `/api/resource/empty-slots`
- **状态**: ❌ **缺失**
- **功能**: 获取指定城市/区域在指定时间的空位资源
- **优先级**: 🔴 高（核心场景）
- **请求参数**:
  ```json
  {
    "city": "杭州",
    "district": "滨江区",
    "resourceType": "barrier",
    "startDate": "2024-05-01",
    "endDate": "2024-05-31",
    "minCount": 10
  }
  ```

---

## 四、工程执行场景（7个API缺失）

### 4.1 任务管理

#### 创建任务
- **API名称**: `create_task`
- **方法**: POST
- **路径**: `/api/task`
- **状态**: ❌ **缺失**
- **功能**: 创建上刊/巡查/下刊任务
- **优先级**: 🔴 高（核心场景）
- **请求参数**:
  ```json
  {
    "taskType": "upload/monitor/inspect/remove",
    "planId": 1,
    "resourceIds": [1, 2, 3],
    "scheduledDate": "2024-04-30",
    "priority": "high/normal/low",
    "notes": "string"
  }
  ```

#### 查询工程人员
- **API名称**: `list_engineers`
- **方法**: GET
- **路径**: `/api/engineer/list`
- **状态**: ❌ **缺失**
- **功能**: 获取可用工程人员列表
- **优先级**: 🟡 中
- **响应包含**: 人员姓名、负责区域、当前任务量

#### 路线优化
- **API名称**: `optimize_route`
- **方法**: POST
- **路径**: `/api/task/optimize-route`
- **状态**: ❌ **缺失**
- **功能**: 根据点位地理位置优化执行路线
- **优先级**: 🟡 中
- **请求参数**:
  ```json
  {
    "resourceIds": [1, 2, 3, 4, 5],
    "startPoint": "公司地址"
  }
  ```

#### 指派任务
- **API名称**: `assign_task`
- **方法**: POST
- **路径**: `/api/task/{taskId}/assign`
- **状态**: ❌ **缺失**
- **功能**: 将任务指派给具体执行人
- **优先级**: 🔴 高（核心场景）
- **请求参数**:
  ```json
  {
    "engineerId": 1,
    "autoNotify": true
  }
  ```

#### 发送任务通知
- **API名称**: `send_task_notification`
- **方法**: POST
- **路径**: `/api/task/{taskId}/notify`
- **状态**: ❌ **缺失**
- **功能**: 向工程人员推送任务通知（短信/APP推送）
- **优先级**: 🟡 中

### 4.2 执行状态

#### 获取执行状态
- **API名称**: `get_execution_status`
- **方法**: GET
- **路径**: `/api/plan/{planId}/execution-status`
- **状态**: ❌ **缺失**
- **功能**: 查询方案的执行进度和各点位状态
- **优先级**: 🔴 高（核心场景）
- **响应示例**:
  ```json
  {
    "planId": 1,
    "totalResources": 20,
    "completed": 18,
    "pending": 2,
    "failed": 0,
    "completionRate": 90,
    "details": [
      {
        "resourceId": 1,
        "status": "completed",
        "completedAt": "2024-04-28",
        "photos": ["url1", "url2"]
      }
    ]
  }
  ```

#### 获取上刊率统计
- **API名称**: `get_upload_rate`
- **方法**: GET
- **路径**: `/api/plan/{planId}/upload-rate`
- **状态**: ❌ **缺失**
- **功能**: 获取方案上刊完成率统计
- **优先级**: 🟡 中

---

## 五、财务对账场景（3个API缺失）

### 5.1 应收款查询
- **API名称**: `get_receivable`
- **方法**: GET
- **路径**: `/api/finance/receivable`
- **状态**: ❌ **缺失**
- **功能**: 查询应收款列表（支持按客户、时间、逾期状态筛选）
- **优先级**: 🟡 中
- **查询参数**:
  ```
  ?customerId=1&startDate=2024-03-01&endDate=2024-03-31&overdueOnly=false
  ```

### 5.2 付款状态查询
- **API名称**: `get_payment_status`
- **方法**: GET
- **路径**: `/api/finance/payment-status/{contractId}`
- **状态**: ❌ **缺失**
- **功能**: 查询合同的付款进度
- **优先级**: 🟡 中

### 5.3 导出财务报表
- **API名称**: `export_financial_report`
- **方法**: POST
- **路径**: `/api/finance/export`
- **状态**: ❌ **缺失**
- **功能**: 导出财务对账报表
- **优先级**: 🟢 低
- **请求参数**:
  ```json
  {
    "reportType": "receivable/payment/profit",
    "startDate": "2024-01-01",
    "endDate": "2024-03-31",
    "format": "excel/pdf"
  }
  ```

---

## 六、数据报表场景（2个API缺失）

### 6.1 生成PPT
- **API名称**: `generate_ppt`
- **方法**: POST
- **路径**: `/api/report/generate-ppt`
- **状态**: ❌ **缺失**
- **功能**: 生成方案展示PPT
- **优先级**: 🟡 中
- **请求参数**:
  ```json
  {
    "planId": 1,
    "templateId": "standard/luxury/simple",
    "includePhotos": true,
    "brandColor": "#FF0000"
  }
  ```

### 6.2 导出Excel
- **API名称**: `export_excel`
- **方法**: POST
- **路径**: `/api/report/export-excel`
- **状态**: ❌ **缺失**
- **功能**: 导出各类业务数据Excel
- **优先级**: 🟢 低
- **请求参数**:
  ```json
  {
    "dataType": "communities/barriers/frames/plans",
    "filter": {},
    "columns": ["col1", "col2"]
  }
  ```

---

## 七、合同管理场景（4个API缺失）

### 7.1 上传合同PDF
- **API名称**: `upload_contract_pdf`
- **方法**: POST
- **路径**: `/api/contract/upload`
- **状态**: ❌ **缺失**
- **功能**: 上传合同PDF文件
- **优先级**: 🟡 中
- **Content-Type**: `multipart/form-data`

### 7.2 识别合同信息
- **API名称**: `recognize_contract`
- **方法**: POST
- **路径**: `/api/contract/recognize`
- **状态**: ❌ **缺失**
- **功能**: OCR识别合同PDF中的关键信息
- **优先级**: 🟢 低（高级功能，可人工录入替代）
- **请求参数**:
  ```json
  {
    "fileUrl": "string"
  }
  ```
- **响应包含**: 合同金额、时间、付款条款、签章检测

### 7.3 创建合同
- **API名称**: `create_contract`
- **方法**: POST
- **路径**: `/api/contract`
- **状态**: ❌ **缺失**
- **功能**: 创建合同记录
- **优先级**: 🟡 中
- **请求参数**:
  ```json
  {
    "contractNo": "HT-2024-001",
    "contractType": "sales/development",
    "customerId": 1,
    "amount": 280000,
    "signDate": "2024-03-15",
    "startDate": "2024-05-01",
    "endDate": "2024-05-31",
    "paymentTerms": "30-40-30",
    "fileUrl": "string"
  }
  ```

### 7.4 关联方案
- **API名称**: `link_plan_to_contract`
- **方法**: POST
- **路径**: `/api/contract/{contractId}/link-plan`
- **状态**: ❌ **缺失**
- **功能**: 将方案关联到合同
- **优先级**: 🟡 中
- **请求参数**:
  ```json
  {
    "planId": 1
  }
  ```

### 7.5 保存合同文件
- **API名称**: `save_contract_file`
- **方法**: POST
- **路径**: `/api/contract/{contractId}/save-file`
- **状态**: ❌ **缺失**
- **功能**: 保存合同电子档
- **优先级**: 🟢 低

---

## 八、业绩分析场景（4个API缺失）

### 8.1 获取销售业绩
- **API名称**: `get_sales_performance`
- **方法**: GET
- **路径**: `/api/analytics/sales-performance`
- **状态**: ❌ **缺失**
- **功能**: 查询销售业绩统计
- **优先级**: 🟡 中
- **查询参数**: `?salespersonId=1&startDate=2024-01-01&endDate=2024-03-31`

### 8.2 获取团队业绩
- **API名称**: `get_team_performance`
- **方法**: GET
- **路径**: `/api/analytics/team-performance`
- **状态**: ❌ **缺失**
- **功能**: 查询团队整体业绩
- **优先级**: 🟢 低

### 8.3 获取客户贡献度
- **API名称**: `get_customer_contribution`
- **方法**: GET
- **路径**: `/api/analytics/customer-contribution`
- **状态**: ❌ **缺失**
- **功能**: 统计客户贡献度和排名
- **优先级**: 🟢 低

### 8.4 获取方案统计
- **API名称**: `get_plan_statistics`
- **方法**: GET
- **路径**: `/api/analytics/plan-statistics`
- **状态**: ❌ **缺失**
- **功能**: 统计方案成单率、平均金额等
- **优先级**: 🟢 低

### 8.5 资源盈亏分析
- **API名称**: `get_resource_profit_analysis`
- **方法**: GET
- **路径**: `/api/analytics/resource-profit`
- **状态**: ❌ **缺失**
- **功能**: 分析点位ROI和盈亏
- **优先级**: 🟡 中
- **查询参数**: `?city=杭州&startDate=2024-01-01&endDate=2024-03-31`

---

## 九、运维管理场景（3个API缺失）

### 9.1 创建报修工单
- **API名称**: `create_repair_ticket`
- **方法**: POST
- **路径**: `/api/repair`
- **状态**: ❌ **缺失**
- **功能**: 创建故障报修工单
- **优先级**: 🟡 中
- **请求参数**:
  ```json
  {
    "resourceId": 1,
    "resourceType": "barrier/frame",
    "issueType": "hardware/software/damage",
    "description": "string",
    "reporter": "string",
    "photos": ["url1", "url2"]
  }
  ```

### 9.2 查询保修状态
- **API名称**: `get_warranty_status`
- **方法**: GET
- **路径**: `/api/resource/{resourceId}/warranty`
- **状态**: ❌ **缺失**
- **功能**: 查询点位是否在保修期内
- **优先级**: 🟢 低

### 9.3 提交需求工单
- **API名称**: `submit_feature_request`
- **方法**: POST
- **路径**: `/api/feedback/feature-request`
- **状态**: ❌ **缺失**
- **功能**: 提交产品需求（"我还不会"场景）
- **优先级**: 🟢 低
- **请求参数**:
  ```json
  {
    "title": "string",
    "description": "string",
    "useCase": "string",
    "priority": "high/medium/low"
  }
  ```

---

## 十、缺失API优先级汇总

### 🔴 高优先级（必须实现）

| # | API名称 | 所属场景 | 影响范围 |
|---|---------|----------|----------|
| 1 | `filter_customers` | 客户管理 | 核心场景：查询客户 |
| 2 | `create_customer` | 客户管理 | 核心场景：创建客户 |
| 3 | `create_followup` | 客户管理 | 核心场景：记录联系 |
| 4 | `copy_plan` | 方案管理 | 常用功能：复制方案 |
| 5 | `lock_plan_resources` | 方案管理 | 核心场景：锁位 |
| 6 | `unlock_plan_resources` | 方案管理 | 核心场景：解锁 |
| 7 | `check_resource_availability` | 资源查询 | 核心场景：查档期 |
| 8 | `get_empty_slots` | 资源查询 | 核心场景：查空位 |
| 9 | `create_task` | 工程执行 | 核心场景：派任务 |
| 10 | `assign_task` | 工程执行 | 核心场景：指派 |
| 11 | `get_execution_status` | 工程执行 | 核心场景：查进度 |

### 🟡 中优先级（建议实现）

| # | API名称 | 所属场景 | 说明 |
|---|---------|----------|------|
| 12 | `add_contact` | 客户管理 | 完善客户信息 |
| 13 | `archive_plan` | 方案管理 | 方案生命周期 |
| 14 | `list_engineers` | 工程执行 | 人员管理 |
| 15 | `optimize_route` | 工程执行 | 效率优化 |
| 16 | `send_task_notification` | 工程执行 | 通知推送 |
| 17 | `get_upload_rate` | 工程执行 | 进度统计 |
| 18 | `get_receivable` | 财务对账 | 应收管理 |
| 19 | `get_payment_status` | 财务对账 | 回款跟踪 |
| 20 | `generate_ppt` | 数据报表 | 方案展示 |
| 21 | `upload_contract_pdf` | 合同管理 | 合同录入 |
| 22 | `create_contract` | 合同管理 | 合同管理 |
| 23 | `link_plan_to_contract` | 合同管理 | 关联方案 |
| 24 | `get_sales_performance` | 业绩分析 | 业绩统计 |
| 25 | `get_resource_profit_analysis` | 业绩分析 | ROI分析 |
| 26 | `create_repair_ticket` | 运维管理 | 故障处理 |

### 🟢 低优先级（可选实现）

| # | API名称 | 所属场景 | 说明 |
|---|---------|----------|------|
| 27 | `recognize_contract` | 合同管理 | OCR高级功能 |
| 28 | `save_contract_file` | 合同管理 | 文档管理 |
| 29 | `export_financial_report` | 财务对账 | 报表导出 |
| 30 | `export_excel` | 数据报表 | 数据导出 |
| 31 | `get_team_performance` | 业绩分析 | 团队统计 |
| 32 | `get_customer_contribution` | 业绩分析 | 客户分析 |
| 33 | `get_plan_statistics` | 业绩分析 | 方案统计 |
| 34 | `get_warranty_status` | 运维管理 | 保修查询 |
| 35 | `submit_feature_request` | 运维管理 | 需求反馈 |

---

## 十一、OpenAPI扩展建议

### 11.1 立即扩展（高优先级）

建议优先实现以下11个API，即可支撑80%的核心场景：

1. 客户查询与创建（3个）
2. 方案锁位/解锁（2个）
3. 资源档期检查（2个）
4. 任务创建与指派（3个）
5. 执行状态查询（1个）

### 11.2 字段补充建议

现有API需要补充的字段：

**Plan对象**:
```yaml
Plan:
  properties:
    # 现有字段
    # ...
    # 需要补充
    status:
      type: string
      enum: [draft, pending, locked, executing, completed, archived]
      description: 方案状态
    totalAmount:
      type: number
      description: 方案总金额
    salespersonId:
      type: integer
      description: 销售负责人ID
    customerId:
      type: integer
      description: 客户ID
```

**Community对象**:
```yaml
Community:
  properties:
    # 现有字段
    # ...
    # 需要补充
    propertyPrice:
      type: string
      description: 房价等级（高/中/低）
    propertyType:
      type: string
      description: 物业类型（住宅/商业/混合）
    householdCount:
      type: integer
      description: 总户数
```

---

## 十二、版本规划建议

### v1.1（2周内）- 核心功能
实现11个🔴高优先级API，支持基本业务流程。

### v1.2（1个月内）- 完善功能
实现14个🟡中优先级API，提升用户体验。

### v1.3（2个月内）- 高级功能
实现10个🟢低优先级API，完整覆盖所有场景。

---

**文档维护**: 投户外技术团队  
**更新频率**: 每迭代评审更新  
**关联文档**: `openapi-dify.yaml`, `dify-agent-scenarios.md`
