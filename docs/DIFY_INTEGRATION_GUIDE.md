# OpenAPI Schema 与 Dify 集成指南

## 1. 访问 OpenAPI 文档

启动后端服务后，访问以下 URL：

### Swagger UI 界面
```
http://localhost:16000/swagger-ui.html
```

### OpenAPI JSON Schema
```
http://localhost:16000/v3/api-docs
```

### OpenAPI YAML (推荐用于导入 Dify)
```
http://localhost:16000/v3/api-docs.yaml
```

---

## 2. Dify 工具配置

### 2.1 导出 API Schema

1. 启动后端服务：`mvn spring-boot:run`
2. 访问 `http://localhost:16000/v3/api-docs.yaml`
3. 复制内容或下载文件

### 2.2 在 Dify 中创建工具

**步骤 1**: 进入 Dify → 工具 → 创建自定义工具

**步骤 2**: 导入 OpenAPI Schema
- 选择 "导入 OpenAPI"
- 粘贴 YAML 内容或上传文件

**步骤 3**: 配置认证
- 认证类型: **Bearer Token**
- Token: 从系统获取的 JWT Token

**步骤 4**: 配置 API 服务器
```
http://localhost:16000
```

---

## 3. 可用工具列表

### 社区管理 (Community)

| 工具 ID | 方法 | 端点 | 描述 |
|---------|------|------|------|
| `list_communities` | GET | /api/community/list | 查询所有社区 |
| `get_community` | GET | /api/community/{id} | 根据ID查询社区 |
| `create_community` | POST | /api/community | 创建社区 |
| `update_community` | PUT | /api/community | 更新社区 |
| `delete_community` | DELETE | /api/community/{id} | 删除社区 |
| `list_communities_page` | POST | /api/community/page | 分页查询社区 |
| `filter_communities` | POST | /api/community/filter/page | 条件分页查询 |

### 道闸管理 (Barrier Gate)

| 工具 ID | 方法 | 端点 | 描述 |
|---------|------|------|------|
| `list_barrier_gates` | GET | /api/barrier-gate/list | 查询所有道闸 |
| `get_barrier_gate` | GET | /api/barrier-gate/{id} | 根据ID查询道闸 |
| `create_barrier_gate` | POST | /api/barrier-gate | 创建道闸 |
| `update_barrier_gate` | PUT | /api/barrier-gate | 更新道闸 |
| `delete_barrier_gate` | DELETE | /api/barrier-gate/{id} | 删除道闸 |
| `get_barrier_gates_by_community` | GET | /api/barrier-gate/community/{id} | 根据社区查询道闸 |

### 框架管理 (Frame)

| 工具 ID | 方法 | 端点 | 描述 |
|---------|------|------|------|
| `list_frames` | GET | /api/frame/list | 查询所有框架 |
| `get_frame` | GET | /api/frame/{id} | 根据ID查询框架 |
| `create_frame` | POST | /api/frame | 创建框架 |
| `update_frame` | PUT | /api/frame | 更新框架 |
| `delete_frame` | DELETE | /api/frame/{id} | 删除框架 |
| `get_frames_by_community` | GET | /api/frame/community/{id} | 根据社区查询框架 |

### 方案管理 (Plan)

| 工具 ID | 方法 | 端点 | 描述 |
|---------|------|------|------|
| `list_plans` | GET | /api/plan/list | 查询所有方案 |
| `get_plan` | GET | /api/plan/{id} | 根据ID查询方案 |
| `create_plan` | POST | /api/plan | 创建方案 |
| `update_plan` | PUT | /api/plan | 更新方案 |
| `delete_plan` | DELETE | /api/plan/{id} | 删除方案 |
| `get_plans_by_customer` | GET | /api/plan/customer/{name} | 根据客户查询方案 |

### 方案关联管理

**方案-社区关联 (PlanCommunity)**
| 工具 ID | 方法 | 端点 | 描述 |
|---------|------|------|------|
| `list_plan_communities` | POST | /api/plan-community/filter/page | 查询方案社区关联 |
| `create_plan_community` | POST | /api/plan-community | 创建关联 |
| `delete_plan_community` | DELETE | /api/plan-community/{id} | 删除关联 |

**方案-道闸关联 (PlanBarrier)**
| 工具 ID | 方法 | 端点 | 描述 |
|---------|------|------|------|
| `list_plan_barriers` | POST | /api/plan-barrier/filter/page | 查询方案道闸关联 |
| `create_plan_barrier` | POST | /api/plan-barrier | 创建关联 |
| `delete_plan_barrier` | DELETE | /api/plan-barrier/{id} | 删除关联 |

**方案-框架关联 (PlanFrame)**
| 工具 ID | 方法 | 端点 | 描述 |
|---------|------|------|------|
| `list_plan_frames` | POST | /api/plan-frame/filter/page | 查询方案框架关联 |
| `create_plan_frame` | POST | /api/plan-frame | 创建关联 |
| `delete_plan_frame` | DELETE | /api/plan-frame/{id} | 删除关联 |

---

## 4. Dify Agent 系统提示词模板

```markdown
# 户外广告管理系统 AI 助手

你是户外广告管理系统的 AI 助手，可以帮助用户管理社区、道闸、框架和方案信息。

## 可用工具

### 社区管理
- **list_communities**: 查询所有社区
- **get_community**: 根据ID获取社区详情
- **create_community**: 创建新社区
- **update_community**: 更新社区信息
- **delete_community**: 删除社区

### 道闸管理
- **list_barrier_gates**: 查询所有道闸
- **get_barrier_gate**: 根据ID获取道闸详情
- **create_barrier_gate**: 创建新道闸
- **update_barrier_gate**: 更新道闸信息
- **delete_barrier_gate**: 删除道闸

### 框架管理
- **list_frames**: 查询所有框架
- **get_frame**: 根据ID获取框架详情
- **create_frame**: 创建新框架
- **update_frame**: 更新框架信息
- **delete_frame**: 删除框架

### 方案管理
- **list_plans**: 查询所有方案
- **get_plan**: 根据ID获取方案详情
- **create_plan**: 创建新方案
- **update_plan**: 更新方案信息
- **delete_plan**: 删除方案

### 方案关联管理
- **list_plan_communities**: 查询方案社区关联
- **create_plan_community**: 将社区添加到方案
- **delete_plan_community**: 从方案移除社区
- **list_plan_barriers**: 查询方案道闸关联
- **create_plan_barrier**: 将道闸添加到方案
- **delete_plan_barrier**: 从方案移除道闸
- **list_plan_frames**: 查询方案框架关联
- **create_plan_frame**: 将框架添加到方案
- **delete_plan_frame**: 从方案移除框架

## 响应格式要求

所有工具调用完成后，必须在 tool_response 中包含 navigation 字段，指定页面跳转信息：

### 导航字段格式
```json
{
  "navigation": {
    "action": "navigate",
    "target": "/页面路径",
    "params": {
      "参数名": "参数值"
    },
    "message": "显示给用户的提示消息",
    "toast": {
      "type": "success|info|warning|error",
      "message": "Toast 消息内容",
      "duration": 3000
    }
  }
}
```

### 页面路由映射

**实体页面**:
- 社区列表: `/communities`
- 社区详情: `/communities/{id}`
- 道闸列表: `/barrier-gates`
- 道闸详情: `/barrier-gates/{id}`
- 框架列表: `/frames`
- 框架详情: `/frames/{id}`
- 方案列表: `/plans`
- 方案详情: `/plans/{id}`

**关联页面**:
- 方案社区关联: `/plan-communities?planId={id}`
- 方案道闸关联: `/plan-barriers?planId={id}`
- 方案框架关联: `/plan-frames?planId={id}`

### 导航策略

1. **查询操作**:
   - 单个结果 → 跳转到详情页
   - 多个结果 → 跳转到列表页

2. **创建操作**:
   - 成功后 → 跳转到新创建实体的详情页

3. **更新操作**:
   - 成功后 → 停留在详情页

4. **删除操作**:
   - 成功后 → 跳转到列表页

5. **关联操作**:
   - 添加/删除关联后 → 跳转到关联管理页面

## 交互规则

1. **确认重要操作**: 在执行创建、更新、删除操作前，向用户确认
2. **显示执行进度**: 调用工具时告知用户当前正在做什么
3. **清晰反馈**: 操作完成后给出明确的提示
4. **使用中文**: 所有回复使用中文

## 示例

用户: "帮我查询一下阳光小区的信息"
助手: "我来为您查询阳光小区的信息..."
[调用 filter_communities 工具]
助手: "找到阳光小区，正在跳转到详情页..."
[返回 navigation 到 /communities/123]
```

---

## 5. 工具响应示例

### 创建社区后跳转

```json
{
  "tool_response": {
    "tool_name": "create_community",
    "result": {
      "id": 123,
      "communityNo": "C2024001",
      "buildingName": "阳光小区"
    },
    "navigation": {
      "action": "navigate",
      "target": "/communities/123",
      "message": "社区 '阳光小区' 创建成功，正在跳转到详情页...",
      "toast": {
        "type": "success",
        "message": "社区创建成功",
        "duration": 3000
      }
    }
  }
}
```

### 删除社区后跳转

```json
{
  "tool_response": {
    "tool_name": "delete_community",
    "result": {
      "success": true
    },
    "navigation": {
      "action": "navigate",
      "target": "/communities",
      "message": "社区已删除",
      "toast": {
        "type": "success",
        "message": "删除成功",
        "duration": 2000
      }
    }
  }
}
```

### 查询后跳转

```json
{
  "tool_response": {
    "tool_name": "list_communities",
    "result": [
      {"id": 1, "buildingName": "小区1"},
      {"id": 2, "buildingName": "小区2"}
    ],
    "navigation": {
      "action": "navigate",
      "target": "/communities",
      "message": "找到 2 个社区，为您展示列表..."
    }
  }
}
```

---

## 6. 配置检查清单

### Dify Agent 配置
- [ ] 导入 OpenAPI Schema
- [ ] 配置 Bearer Token 认证
- [ ] 设置 API 服务器地址
- [ ] 配置系统提示词
- [ ] 测试每个工具调用

### 后端验证
- [ ] Swagger UI 可访问: `http://localhost:16000/swagger-ui.html`
- [ ] OpenAPI JSON 可下载: `http://localhost:16000/v3/api-docs`
- [ ] 所有 API 端点正常响应

### 前端验证
- [ ] AI 助手组件可正常显示
- [ ] 流式响应正常工作
- [ ] 页面跳转功能正常

---

## 7. 常见问题

**Q: EventSource 403 错误？**
A: JWT Token 需要通过 URL 参数传递，如 `/api/ai-assistant/stream?message=xxx&token=eyJhbG...`

**Q: 如何在 Dify 中测试工具？**
A: 在 Dify Agent 配置页面，使用 "预览" 功能测试工具调用

**Q: Schema 更新后如何同步到 Dify？**
A: 重新导出 OpenAPI Schema 并导入到 Dify 工具中

**Q: 是否支持流式响应？**
A: 是，SSE 流式响应已集成，前端通过 EventSource 接收
