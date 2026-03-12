# API 文档

## 基础信息

- **Base URL**: `http://localhost:16000/api`
- **认证方式**: JWT Token (Bearer)
- **内容类型**: `application/json`

### 认证头

```http
Authorization: Bearer <jwt_token>
```

---

## 响应格式

所有 API 返回统一格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 状态码

| Code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

---

## 认证模块

### 1. 用户登录

**POST** `/auth/login`

#### 请求体

```json
{
  "username": "admin",
  "password": "password123"
}
```

#### 响应

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "role": "ADMIN"
    }
  }
}
```

### 2. 用户注册

**POST** `/auth/register`

#### 请求体

```json
{
  "username": "newuser",
  "password": "password123",
  "realName": "新用户"
}
```

### 3. 获取当前用户

**GET** `/auth/me`

#### 响应

```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "管理员",
    "role": "ADMIN"
  }
}
```

---

## 社区管理

### 1. 获取社区列表

**GET** `/community`

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认 1 |
| size | int | 否 | 每页条数，默认 10 |
| keyword | string | 否 | 搜索关键词 |
| city | string | 否 | 城市筛选 |
| district | string | 否 | 区县筛选 |

#### 响应

```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "name": "阳光花园",
        "city": "北京市",
        "district": "朝阳区",
        "address": "xxx街道",
        "households": 1200,
        "propertyType": "住宅",
        "priceLevel": "高",
        "createdAt": "2024-01-01 10:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

### 2. 获取社区详情

**GET** `/community/{id}`

#### 路径参数

| 参数 | 类型 | 说明 |
|------|------|------|
| id | int | 社区 ID |

### 3. 创建社区

**POST** `/community`

#### 请求体

```json
{
  "name": "新社区",
  "city": "上海市",
  "district": "浦东新区",
  "address": "xxx路xxx号",
  "households": 800,
  "propertyType": "住宅",
  "priceLevel": "中"
}
```

### 4. 更新社区

**PUT** `/community/{id}`

### 5. 删除社区

**DELETE** `/community/{id}`

### 6. 高级筛选

**POST** `/community/filter`

#### 请求体

```json
{
  "cities": ["北京市", "上海市"],
  "districts": ["朝阳区", "浦东新区"],
  "propertyTypes": ["住宅", "商业"],
  "priceLevels": ["高", "中"],
  "minHouseholds": 500,
  "maxHouseholds": 2000
}
```

---

## 道闸管理

### 1. 获取道闸列表

**GET** `/barrier-gate`

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码 |
| size | int | 否 | 每页条数 |
| keyword | string | 否 | 搜索关键词 |
| position | string | 否 | 位置(入口/出口) |
| status | string | 否 | 状态 |

### 2. 获取道闸详情

**GET** `/barrier-gate/{id}`

### 3. 创建道闸

**POST** `/barrier-gate`

#### 请求体

```json
{
  "deviceNo": "BZ001",
  "communityId": 1,
  "position": "入口",
  "screenSize": "32寸",
  "resolution": "1920x1080",
  "status": "正常",
  "installDate": "2024-01-01"
}
```

### 4. 更新道闸

**PUT** `/barrier-gate/{id}`

### 5. 删除道闸

**DELETE** `/barrier-gate/{id}`

### 6. 高级筛选

**POST** `/barrier-gate/filter`

---

## 框架管理

### 1. 获取框架列表

**GET** `/frame`

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码 |
| size | int | 否 | 每页条数 |
| keyword | string | 否 | 搜索关键词 |
| direction | string | 否 | 朝向 |
| innerPosition | string | 否 | 内部位置 |

### 2. 获取框架详情

**GET** `/frame/{id}`

### 3. 创建框架

**POST** `/frame`

#### 请求体

```json
{
  "frameNo": "KJ001",
  "communityId": 1,
  "buildingNo": "1号楼",
  "unitNo": "1单元",
  "elevatorNo": "电梯A",
  "direction": "东",
  "innerPosition": "轿厢内",
  "size": "60x80cm",
  "status": "正常"
}
```

### 4. 更新框架

**PUT** `/frame/{id}`

### 5. 删除框架

**DELETE** `/frame/{id}`

### 6. 高级筛选

**POST** `/frame/filter`

---

## 投放计划管理

### 1. 获取计划列表

**GET** `/plan`

#### 查询参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码 |
| size | int | 否 | 每页条数 |
| keyword | string | 否 | 搜索关键词 |
| status | string | 否 | 状态 |
| salesType | string | 否 | 销售类型 |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |

#### 响应

```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "planNo": "PLAN2024001",
        "name": "春季推广活动",
        "customerName": "某某公司",
        "salesType": "直客",
        "budget": 500000,
        "startDate": "2024-03-01",
        "endDate": "2024-05-31",
        "releaseStatus": "投放中",
        "detailReleaseStatus": "部分投放",
        "mediaInfo": {
          "barrierCount": 50,
          "frameCount": 100
        }
      }
    ],
    "total": 50
  }
}
```

### 2. 获取计划详情

**GET** `/plan/{id}`

### 3. 创建计划

**POST** `/plan`

#### 请求体

```json
{
  "planNo": "PLAN2024001",
  "name": "春季推广活动",
  "customerName": "某某公司",
  "salesType": "直客",
  "budget": 500000,
  "startDate": "2024-03-01",
  "endDate": "2024-05-31",
  "remark": "备注信息"
}
```

### 4. 更新计划

**PUT** `/plan/{id}`

### 5. 删除计划

**DELETE** `/plan/{id}`

### 6. 批量创建

**POST** `/plan/batch`

#### 请求体

```json
[
  { "planNo": "PLAN001", "name": "计划1", ... },
  { "planNo": "PLAN002", "name": "计划2", ... }
]
```

### 7. 分页筛选

**POST** `/plan/filter/page`

#### 请求体

```json
{
  "page": 1,
  "size": 10,
  "salesTypes": ["直客", "代理"],
  "releaseStatuses": ["投放中", "已结束"],
  "detailReleaseStatuses": ["全部投放"],
  "startDateFrom": "2024-01-01",
  "startDateTo": "2024-12-31"
}
```

---

## 计划关联管理

### 计划-社区关联

#### 1. 获取关联列表

**GET** `/plan-community?planId={planId}`

#### 2. 添加关联

**POST** `/plan-community`

```json
{
  "planId": 1,
  "communityId": 1,
  "quantity": 10
}
```

#### 3. 批量添加

**POST** `/plan-community/batch`

#### 4. 删除关联

**DELETE** `/plan-community/{id}`

### 计划-道闸关联

- **GET** `/plan-barrier?planId={planId}`
- **POST** `/plan-barrier`
- **POST** `/plan-barrier/batch`
- **DELETE** `/plan-barrier/{id}`

### 计划-框架关联

- **GET** `/plan-frame?planId={planId}`
- **POST** `/plan-frame`
- **POST** `/plan-frame/batch`
- **DELETE** `/plan-frame/{id}`

---

## AI 助手

### 1. 流式对话

**GET** `/ai-assistant/stream?message={message}&conversationId={id}&token={jwt_token}`

使用 Server-Sent Events (SSE) 进行流式响应。

#### 响应

```
event: message
data: {"content": "你好", "isComplete": false}

event: message
data: {"content": "！", "isComplete": false}

event: navigation
data: {"action": "navigate", "path": "/plans"}

event: message
data: {"content": "", "isComplete": true}
```

### 2. 获取对话历史

**GET** `/ai-assistant/conversations`

#### 响应

```json
{
  "code": 200,
  "data": [
    {
      "id": "conv_001",
      "title": "关于计划管理的对话",
      "createdAt": "2024-01-01T10:00:00Z",
      "updatedAt": "2024-01-01T10:30:00Z"
    }
  ]
}
```

### 3. 获取单条对话

**GET** `/ai-assistant/conversations/{id}`

### 4. 删除对话

**DELETE** `/ai-assistant/conversations/{id}`

### 5. 获取快捷指令

**GET** `/ai-assistant/quick-commands`

#### 响应

```json
{
  "code": 200,
  "data": [
    { "id": "create_plan", "label": "创建投放计划", "prompt": "帮我创建一个新的投放计划" },
    { "id": "search_community", "label": "查找社区", "prompt": "帮我查找符合条件的社区" }
  ]
}
```

---

## 数据看板

### 1. 统计数据

**GET** `/dashboard/stats`

#### 响应

```json
{
  "code": 200,
  "data": {
    "totalCommunities": 150,
    "totalBarrierGates": 300,
    "totalFrames": 1200,
    "activePlans": 25,
    "totalRevenue": 5000000,
    "monthlyRevenue": 800000
  }
}
```

### 2. 收入趋势

**GET** `/dashboard/revenue-trend?months={months}`

#### 响应

```json
{
  "code": 200,
  "data": [
    { "month": "2024-01", "revenue": 500000 },
    { "month": "2024-02", "revenue": 600000 }
  ]
}
```

### 3. 资源利用率

**GET** `/dashboard/resource-utilization`

### 4. 最近计划

**GET** `/dashboard/recent-plans?limit={limit}`

### 5. 热门社区

**GET** `/dashboard/top-communities?limit={limit}`

---

## 错误处理

### 错误响应格式

```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null
}
```

### 常见错误

| HTTP 状态 | Code | Message | 说明 |
|-----------|------|---------|------|
| 400 | 400001 | 参数校验失败 | 请求参数不符合要求 |
| 401 | 401001 | 未提供认证信息 | 缺少 JWT Token |
| 401 | 401002 | Token 已过期 | JWT Token 过期 |
| 403 | 403001 | 无权限访问 | 用户权限不足 |
| 404 | 404001 | 资源不存在 | 请求的资源不存在 |
| 409 | 409001 | 数据已存在 | 唯一性约束冲突 |
| 500 | 500001 | 服务器内部错误 | 服务器异常 |

---

## 分页规范

所有列表接口默认使用分页：

- `page`: 页码，从 1 开始
- `size`: 每页条数，默认 10，最大 100

### 分页响应

```json
{
  "list": [...],
  "total": 100,
  "page": 1,
  "size": 10,
  "pages": 10
}
```

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0.0 | 2024-03-01 | 初始版本 |
