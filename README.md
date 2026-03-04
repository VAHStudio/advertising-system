# 永达传媒 AI 广告投放管理系统

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-21-orange" alt="Java">
  <img src="https://img.shields.io/badge/React-19-blue" alt="React">
  <img src="https://img.shields.io/badge/TypeScript-5.7-blue" alt="TypeScript">
  <img src="https://img.shields.io/badge/Tailwind%20CSS-4.0-purple" alt="Tailwind CSS">
  <img src="https://img.shields.io/badge/MySQL-8.0-yellow" alt="MySQL">
</p>

## 🎯 项目概述

**永达传媒 AI 广告投放管理系统**是一个集成了 AI 智能助手的全栈广告投放管理平台。通过自然语言对话，用户可以快速创建广告方案、智能选择投放点位、管理项目全流程。

### 核心特性

- 🤖 **AI 智能对话** - 基于 Kimi AI 的自然语言交互，自动理解需求
- 🎙️ **语音输入** - 支持语音转文字，长按投小智图标即可语音输入
- 🧠 **智能领域匹配** - 自动识别问题领域（销售/媒介/工程/财务），匹配对应 AI 助理
- 📍 **智能选点** - 根据时间范围自动选择空闲道闸/框架点位
- 🎨 **个性化问候** - 根据时间、节日、生日显示文雅问候语
- 📱 **移动端优先** - 响应式设计，完美适配手机端

## 🏗️ 技术架构

### 后端技术栈
- **Spring Boot 4.0.3** - 核心框架
- **Java 21** - 编程语言
- **MyBatis** - 数据持久层
- **MySQL 8.0** - 数据库
- **Kimi AI** - 大语言模型 API
- **Maven** - 构建工具

### 前端技术栈

#### 移动端 (frontend/)
- **React 19** - UI 框架
- **TypeScript 5.7** - 类型安全
- **Vite 6** - 构建工具
- **Tailwind CSS 4** - 样式框架
- **Framer Motion** - 动画库
- **Lucide React** - 图标库

#### 桌面端 (frontend-desktop/)
- **React 19** - UI 框架
- **TypeScript 5.7** - 类型安全
- **Vite 6** - 构建工具
- **Tailwind CSS 4** - 样式框架
- **Material Icons** - 图标库
- **Recharts** - 图表库

## 🚀 快速开始

### 环境要求
- Java 21+
- Node.js 18+
- MySQL 8.0+

### 1. 克隆项目

```bash
git clone <repository-url>
cd advertising-system
```

### 2. 环境变量配置

⚠️ **重要**：所有敏感配置（数据库密码、API密钥）都已移至环境变量。

#### 方式一：使用环境变量（推荐用于生产环境）

```bash
# Linux/Mac
export DB_URL=jdbc:mysql://your_server_ip:3306/mvp?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
export DB_USERNAME=root
export DB_PASSWORD=your_password_here
export KIMI_API_KEY=your_kimi_api_key_here

# Windows CMD
set DB_URL=jdbc:mysql://your_server_ip:3306/mvp?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
set DB_USERNAME=root
set DB_PASSWORD=your_password_here
set KIMI_API_KEY=your_kimi_api_key_here
```

#### 方式二：使用 application-dev.yml（推荐用于开发环境）

已创建 `src/main/resources/application-dev.yml`（该文件已被 .gitignore 排除，不会提交到Git）：

```yaml
spring:
  datasource:
    url: jdbc:mysql://your_server_ip:3306/mvp?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password

kimi:
  api:
    key: your_kimi_api_key_here
```

📖 详细配置说明请参考 [ENVIRONMENT.md](./ENVIRONMENT.md)

### 4. 开发模式运行

**启动后端（方式一：使用脚本）：**

```bash
# Windows
start_backend.bat

# macOS/Linux
./start_backend.sh
```

**启动后端（方式二：使用Maven）：**

```bash
mvn spring-boot:run
```

**启动移动端前端（新终端）：**

```bash
cd frontend
npm install
npm run dev
```

**访问地址：**
- 移动端前端: http://localhost:3000
- 后端 API: http://localhost:16000/api

**启动桌面端前端（新终端）：**

```bash
# 方式一：使用脚本
# Windows
start_desktop.bat

# macOS/Linux
./start_desktop.sh

# 方式二：手动启动
cd frontend-desktop
npm install
npm run dev
```

**访问地址：**
- 桌面端前端: http://localhost:3001
- API 代理: http://localhost:16000

### 5. 生产模式部署

```bash
# 构建前端
cd frontend
npm install
npm run build
cd ..

# 打包后端（自动包含前端）
mvn clean package

# 运行
java -jar target/advertising-system-1.0.0.jar
```

**访问地址：** http://localhost:16000

---

## ⚙️ 配置说明

### 数据库配置

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://your_server_ip:3306/mvp?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
    hikari:
      minimum-idle: 5              # 最小空闲连接数
      maximum-pool-size: 20        # 最大连接池大小
      connection-timeout: 60000    # 连接超时时间（毫秒）
      idle-timeout: 300000         # 空闲连接超时时间（毫秒）
      max-lifetime: 1800000        # 连接最大生命周期（毫秒）
      connection-test-query: SELECT 1  # 连接测试查询
      pool-name: HikariPool
```

**数据库连接参数说明：**

| 参数 | 说明 | 示例值 |
|------|------|--------|
| `url` | 数据库连接URL | `jdbc:mysql://your_server_ip:3306/mvp` |
| `username` | 数据库用户名 | `your_username` |
| `password` | 数据库密码 | `your_password` |
| `minimum-idle` | 最小空闲连接数 | `5` |
| `maximum-pool-size` | 最大连接池大小 | `20` |
| `connection-timeout` | 连接超时时间（毫秒） | `60000` |
| `idle-timeout` | 空闲连接超时时间（毫秒） | `300000` |
| `max-lifetime` | 连接最大生命周期（毫秒） | `1800000` |

### Kimi AI 大模型配置

```yaml
# Kimi AI 配置 (Kimi K2.5)
kimi:
  api:
    key: your_kimi_api_key  # API密钥
    endpoint: https://api.moonshot.ai/v1/chat/completions      # API端点
    model: kimi-k2.5                                           # 模型名称
    thinking: disabled                                          # 思考模式：enabled/disabled
```

**Kimi API 参数说明：**

| 参数 | 说明 | 可选值 | 默认值 |
|------|------|--------|--------|
| `key` | Kimi API密钥 | - | - |
| `endpoint` | API请求地址 | `https://api.moonshot.ai/v1/chat/completions` | - |
| `model` | 模型名称 | `kimi-k2.5` | `kimi-k2.5` |
| `thinking` | 思考模式 | `enabled`, `disabled` | `disabled` |

**思考模式说明：**
- `enabled`: 开启思考模式，模型会展示思考过程，适合需要详细解释的场景
- `disabled`: 关闭思考模式，直接返回答案，响应速度更快（推荐用于生产环境）

**API请求格式示例：**

```json
{
  "model": "kimi-k2.5",
  "messages": [
    {"role": "system", "content": "你是一个广告投放管理系统的AI助手..."},
    {"role": "user", "content": "帮我创建一个可口可乐的广告方案"}
  ],
  "temperature": 0.7,
  "max_tokens": 4096,
  "extra_body": {
    "thinking": {
      "type": "disabled"
    }
  }
}
```

## 📁 项目结构

```
advertising-system/
├── frontend/                          # 移动端前端 React 应用
│   ├── src/
│   │   ├── App.tsx                   # 主应用组件
│   │   ├── components/
│   │   │   ├── AgentChatView.tsx    # AI 对话界面
│   │   │   └── VoiceInputModal.tsx  # 语音输入弹窗
│   │   ├── services/
│   │   │   ├── agentService.ts      # Agent API 服务
│   │   │   └── speechService.ts     # 语音识别服务
│   │   ├── types.ts                 # TypeScript 类型
│   │   └── index.css                # 全局样式
│   └── package.json
├── frontend-desktop/                  # 桌面端前端 React 应用
│   ├── src/
│   │   ├── App.tsx                   # 主应用组件
│   │   ├── components/
│   │   │   ├── Sidebar.tsx          # 侧边栏导航
│   │   │   ├── Header.tsx           # 页面头部
│   │   │   └── AIAssistant.tsx      # AI 助手悬浮按钮
│   │   ├── pages/
│   │   │   ├── Dashboard.tsx        # 智能工作台
│   │   │   ├── Resources.tsx        # 资源管理
│   │   │   ├── Plans.tsx            # 方案管理
│   │   │   ├── Customers.tsx        # 客户管理
│   │   │   └── ...                  # 其他页面
│   │   ├── services/
│   │   │   ├── api.ts               # API 基础服务
│   │   │   ├── communityService.ts  # 社区资源服务
│   │   │   ├── planService.ts       # 方案服务
│   │   │   └── dashboardService.ts  # 统计数据服务
│   │   └── index.css                # 全局样式
│   └── package.json
├── src/
│   ├── main/
│   │   ├── java/com/advertising/
│   │   │   ├── AdvertisingSystemApplication.java
│   │   │   ├── agent/                 # AI Agent 模块
│   │   │   │   ├── controller/
│   │   │   │   │   └── AgentController.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── KimiAgentService.java
│   │   │   │   │   ├── AgentOrchestratorService.java
│   │   │   │   │   └── SmartPointSelector.java
│   │   │   │   └── dto/
│   │   │   ├── controller/            # REST API 控制器
│   │   │   ├── entity/                # 实体类
│   │   │   ├── enums/                 # 枚举类
│   │   │   ├── mapper/                # MyBatis 接口
│   │   │   └── service/               # 业务逻辑层
│   │   └── resources/
│   │       ├── mapper/                # MyBatis XML
│   │       ├── static/                # 前端构建产物
│   │       └── application.yml        # 配置文件
│   └── test/
├── pom.xml
└── README.md
```

## ✨ 核心功能

### 1. AI 智能对话

用户可以通过自然语言与 AI 助手对话：

```
用户: "帮我建个可口可乐的3月份广告方案，选择10个空闲道闸广告位"

AI: 1. 理解意图 → 2. 询问日期 → 3. 智能选点 → 4. 创建方案 → 5. 自动关联点位
```

**支持的对话类型：**
- 智能创建广告方案
- 查询空闲点位
- 管理项目流程
- 数据统计分析

### 2. 语音输入

- **长按投小智图标** 0.5秒触发语音输入
- 支持**阿里云语音识别**（需配置）
- 自动降级到 **Web Speech API**
- 识别结果自动带入对话

### 3. 智能领域匹配

系统根据问题关键词自动匹配专业 AI 助理：

| 领域 | 关键词 | AI 助理 |
|------|--------|---------|
| 销售 | 方案、客户、投放、广告、合同 | 销售 AI 助理 |
| 媒介 | 点位、库存、资源、位置、社区 | 媒介 AI 助理 |
| 工程 | 维护、验收、安装、设备、维修 | 工程 AI 助理 |
| 财务 | 预算、发票、付款、报销、成本 | 财务 AI 助理 |

### 4. 智能选点

- **时间冲突检测** - 自动排除已被占用的点位
- **均匀分布算法** - 点位均匀分布在不同社区
- **混合模式** - 系统自动选择 + 用户确认调整

### 5. 文雅问候语

根据时间、节日、员工生日显示个性化问候：

```
早晨 (5-11点): "晨光熹微，惠风和畅，愿今日诸事顺遂"
中午 (11-14点): "午时将至，日正当中，愿前程似锦"
下午 (14-18点): "午后时光，宁静致远，愿身体康泰"
晚上 (18-24点): "暮色四合，华灯初上，愿阖家欢乐"

生日: "生辰吉乐，福寿康宁，愿步步高升"
```

## 📱 功能模块

### 投小智（首页）
- 智能输入框（支持打字/语音）
- AI 主动业务资讯
- 永达传媒 AI 团队（销售/媒介/工程/财务）
- 长按投小智图标语音输入

### AI 对话
- 多轮对话上下文
- 方案创建流程引导
- 日期选择交互
- 点位确认展示

### 工作台
- 常用应用快捷入口
- 待办事项提醒
- 最近访问记录

### 方案管理
- 方案列表查看
- 方案详情（点位地图）
- 方案创建与编辑

### 任务管理
- 工程任务列表
- 任务详情与验收
- 拍照上传

## 🔌 API 接口

### Agent 对话接口

**发送消息**
```http
POST /api/agent/chat
Content-Type: application/json

{
  "message": "帮我建个可口可乐的广告方案",
  "sessionId": "optional-session-id"
}
```

**响应示例**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "type": "date_selection",
    "message": "请确认投放日期范围",
    "sessionId": "abc123",
    "step": "date",
    "actions": [
      { "label": "3月1日-3月31日", "value": "2025-03-01,2025-03-31" }
    ]
  }
}
```

### 其他 REST API

详见下方的 [完整API文档](#完整api文档) 部分

---

## 🧠 DSL Agent 架构（智能点位选择 V2）

### Agent业务场景覆盖

系统支持 **170个** 业务场景，覆盖媒介、销售、系统三大领域：

| 领域 | 场景数量 | 主要场景类型 |
|------|---------|-------------|
| **媒介域** | 80个 | 资源查询、分配、方案管理、档期管理、监播、数据分析、维护、报表 |
| **销售域** | 80个 | 客户管理、商机、报价、合同、收款、销售分析、客户服务、市场洞察 |
| **通用域** | 30个 | 系统管理、数据智能、自然语言转SQL |

**核心场景示例：**
- ✅ **资源分配**："选100面道闸，每楼盘2个，优先选进口，要空闲的"
- ✅ **档期查询**："查询3月份哪些点位被占用了"
- ✅ **客户分析**："查询可口可乐的历史投放记录"
- ✅ **销售统计**："统计上个月的销售业绩"
- ✅ **智能推荐**："推荐适合汽车客户的空闲点位"

### 架构设计

基于 **DSL (Domain Specific Language) + SQL模板引擎** 实现复杂点位筛选：

```
用户交互层 (Controller)
    ↓
DSL编排层 (DSL + SQL模板引擎)
    - DSL验证 → 规则匹配 → SQL模板选择 → 参数填充 → SQL生成
    ↓
执行引擎层 (JdbcTemplate)
    - SQL安全校验 → 沙箱执行 → 结果格式化
    ↓
配置管理层
    - DSL Schema定义 / SQL模板库 / 规则版本管理
```

### 核心组件

#### 1. PointSelectionDSL - 领域特定语言

```java
PointSelectionDSL.builder()
    .mediaType("barrier")                                    // 媒体类型
    .targetCount(100)                                        // 目标数量
    .dateRange(DateRange.builder()                          // 投放日期
        .beginDate(LocalDate.of(2025, 3, 1))
        .endDate(LocalDate.of(2025, 3, 31))
        .build())
    .distributionStrategy(DistributionStrategy.builder()    // 分配策略
        .type("per_community")                               // 每社区分配
        .perCommunityCount(2)                               // 每社区2个
        .maxCommunities(50)                                 // 最多50个社区
        .build())
    .filterConditions(FilterConditions.builder()            // 筛选条件
        .devicePosition("entrance")                         // 优先进口
        .onlyAvailable(true)                                // 只选空闲
        .city("南京市")                                     // 城市筛选
        .build())
    .sortRules(List.of(                                     // 排序规则
        SortRule.builder().field("priority").direction("desc").build()
    ))
    .build();
```

#### 2. SQL模板引擎

预定义模板支持复杂场景：

- **BARRIER_SELECT_WITH_DISTRIBUTION**: 道闸按社区分配
- **FRAME_SELECT_WITH_DISTRIBUTION**: 框架按社区分配
- **BARRIER_SELECT_SIMPLE**: 简单道闸查询
- **COMMUNITY_AVAILABILITY_STATS**: 社区可用性统计

**示例生成的SQL**（选100面道闸，每楼盘2个，优先进口）：

```sql
WITH available_barriers AS (
    SELECT 
        bg.id as barrier_gate_id,
        bg.gate_no,
        bg.community_id,
        c.building_name as community_name,
        ROW_NUMBER() OVER (PARTITION BY bg.community_id 
                          ORDER BY bg.device_position, bg.gate_no) as rn
    FROM barrier_gate bg
    INNER JOIN community c ON bg.community_id = c.id
    WHERE bg.status = 1
    AND (bg.device_position IN (1))  -- 进口优先
    AND bg.id NOT IN (
        -- 排除档期冲突的点位
        SELECT pb.barrier_gate_id 
        FROM plan_barrier pb
        WHERE pb.release_status IN (2, 3, 4)
        AND NOT (pb.release_date_end < '2025-03-01' 
                 OR pb.release_date_begin > '2025-03-31')
    )
),
selected_communities AS (
    SELECT community_id
    FROM available_barriers
    WHERE rn <= 2  -- 每社区2个
    GROUP BY community_id
    HAVING COUNT(*) >= 0
    LIMIT 50       -- 最多50个社区
)
SELECT * FROM available_barriers ab
INNER JOIN selected_communities sc ON ab.community_id = sc.community_id
WHERE ab.rn <= 2
ORDER BY ab.community_id, ab.rn
LIMIT 100;
```

### 使用场景

#### 场景1：选档期内的道闸，每楼盘分配
```json
POST /api/agent/v2/select-points
{
    "mediaType": "barrier",
    "targetCount": 100,
    "dateRange": {
        "beginDate": "2025-03-01",
        "endDate": "2025-03-31"
    },
    "distributionStrategy": {
        "type": "per_community",
        "perCommunityCount": 2,
        "maxCommunities": 50
    },
    "filterConditions": {
        "devicePosition": "entrance",
        "onlyAvailable": true,
        "city": "南京市"
    }
}
```

#### 场景2：自然语言智能选择
```json
POST /api/agent/v2/nlp-select
{
    "message": "选3月份档期内的100面道闸，每个楼盘2个，优先选进口，要空闲的"
}
```

---

## 📚 完整API文档

### 通用响应格式

所有API返回统一格式：

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {},
    "timestamp": 1704067200000
}
```

**状态码说明：**

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 一、社区管理 (Community)

**基础路径**: `/api/community`

#### 1. 根据ID查询社区
- **请求方式**: GET
- **请求路径**: `/api/community/{id}`
- **参数说明**:
  - `id` (路径参数): 社区ID
- **响应示例**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "communityNo": "COMM001",
        "buildingName": "阳光花园",
        "buildingAddress": "北京市朝阳区xxx街道",
        "coordLat": 39.9042,
        "coordLng": 116.4074,
        "city": "北京"
    }
}
```

#### 2. 根据编号查询社区
- **请求方式**: GET
- **请求路径**: `/api/community/no/{communityNo}`

#### 3. 查询所有社区
- **请求方式**: GET
- **请求路径**: `/api/community/list`

#### 4. 分页查询社区
- **请求方式**: POST
- **请求路径**: `/api/community/page?pageNum=1&pageSize=10`
- **请求体**:
```json
{
    "communityNo": "COMM",
    "buildingName": "阳光",
    "buildingAddress": "",
    "city": "北京"
}
```

#### 5. 新增社区
- **请求方式**: POST
- **请求路径**: `/api/community`
- **请求体**:
```json
{
    "communityNo": "COMM002",
    "buildingName": "翠湖小区",
    "buildingAddress": "上海市浦东新区xxx路",
    "coordLat": 31.2304,
    "coordLng": 121.4737,
    "city": "上海"
}
```

#### 6. 批量新增社区
- **请求方式**: POST
- **请求路径**: `/api/community/batch`

#### 7. 更新社区
- **请求方式**: PUT
- **请求路径**: `/api/community`

#### 8. 删除社区
- **请求方式**: DELETE
- **请求路径**: `/api/community/{id}`

#### 9. 根据城市查询社区
- **请求方式**: GET
- **请求路径**: `/api/community/city/{city}`

### 二、道闸管理 (Barrier Gate)

**基础路径**: `/api/barrier-gate`

#### 1. 根据ID查询道闸
- **请求方式**: GET
- **请求路径**: `/api/barrier-gate/{id}`

#### 2. 根据编号查询道闸
- **请求方式**: GET
- **请求路径**: `/api/barrier-gate/no/{gateNo}`

#### 3. 查询所有道闸
- **请求方式**: GET
- **请求路径**: `/api/barrier-gate/list`

#### 4. 根据社区查询道闸
- **请求方式**: GET
- **请求路径**: `/api/barrier-gate/community/{communityId}`

#### 5. 新增道闸
- **请求方式**: POST
- **请求路径**: `/api/barrier-gate`
- **请求体**:
```json
{
    "gateNo": "GATE001",
    "communityId": 1,
    "deviceNo": "DEV001",
    "doorLocation": "东门"
}
```

#### 6. 更新道闸
- **请求方式**: PUT
- **请求路径**: `/api/barrier-gate`

#### 7. 删除道闸
- **请求方式**: DELETE
- **请求路径**: `/api/barrier-gate/{id}`

### 三、框架管理 (Frame)

**基础路径**: `/api/frame`

#### 1. 根据ID查询框架
- **请求方式**: GET
- **请求路径**: `/api/frame/{id}`

#### 2. 根据编号查询框架
- **请求方式**: GET
- **请求路径**: `/api/frame/no/{frameNo}`

#### 3. 查询所有框架
- **请求方式**: GET
- **请求路径**: `/api/frame/list`

#### 4. 根据社区查询框架
- **请求方式**: GET
- **请求路径**: `/api/frame/community/{communityId}`

#### 5. 新增框架
- **请求方式**: POST
- **请求路径**: `/api/frame`
- **请求体**:
```json
{
    "frameNo": "FRAME001",
    "communityId": 1,
    "building": "A座",
    "unit": "1单元",
    "elevator": "1号电梯"
}
```

#### 6. 更新框架
- **请求方式**: PUT
- **请求路径**: `/api/frame`

#### 7. 删除框架
- **请求方式**: DELETE
- **请求路径**: `/api/frame/{id}`

### 四、方案管理 (Plan)

**基础路径**: `/api/plan`

#### 1. 根据ID查询方案
- **请求方式**: GET
- **请求路径**: `/api/plan/{id}`

#### 2. 根据编号查询方案
- **请求方式**: GET
- **请求路径**: `/api/plan/no/{planNo}`

#### 3. 查询所有方案
- **请求方式**: GET
- **请求路径**: `/api/plan/list`

#### 4. 分页查询方案
- **请求方式**: POST
- **请求路径**: `/api/plan/page?pageNum=1&pageSize=10`
- **请求体**:
```json
{
    "planNo": "PLAN",
    "planName": "春季推广",
    "customer": "某某公司",
    "releaseStatus": 3,
    "salesType": 1
}
```

#### 5. 根据客户查询方案
- **请求方式**: GET
- **请求路径**: `/api/plan/customer/{customer}`

#### 6. 根据状态查询方案
- **请求方式**: GET
- **请求路径**: `/api/plan/status/{releaseStatus}`

#### 7. 新增方案
- **请求方式**: POST
- **请求路径**: `/api/plan`
- **请求体**:
```json
{
    "planNo": "PLAN001",
    "planName": "春季品牌推广方案",
    "customer": "某某科技有限公司",
    "releaseDateBegin": "2024-03-01",
    "releaseDateEnd": "2024-05-31",
    "releaseStatus": 3,
    "salesType": 1,
    "mediaRequirements": "需要在高端社区投放"
}
```

#### 8. 更新方案
- **请求方式**: PUT
- **请求路径**: `/api/plan`

#### 9. 删除方案
- **请求方式**: DELETE
- **请求路径**: `/api/plan/{id}`

### 五、方案社区关联管理 (Plan Community)

**基础路径**: `/api/plan-community`

#### 1. 根据方案查询关联
- **请求方式**: GET
- **请求路径**: `/api/plan-community/plan/{planId}`

#### 2. 新增关联
- **请求方式**: POST
- **请求路径**: `/api/plan-community`
- **请求体**:
```json
{
    "planId": 1,
    "communityId": 1,
    "releaseDateBegin": "2024-03-01",
    "releaseDateEnd": "2024-05-31",
    "barrierRequiredQty": 2,
    "frameRequiredQty": 5
}
```

### 六、方案道闸明细管理 (Plan Barrier)

**基础路径**: `/api/plan-barrier`

#### 1. 根据方案查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-barrier/plan/{planId}`

#### 2. 根据方案社区查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-barrier/plan-community/{planCommunityId}`

#### 3. 新增明细
- **请求方式**: POST
- **请求路径**: `/api/plan-barrier`
- **请求体**:
```json
{
    "planId": 1,
    "barrierGateId": 1,
    "planCommunityId": 1,
    "releaseDateBegin": "2024-03-01",
    "releaseDateEnd": "2024-05-31",
    "releaseStatus": 4
}
```

#### 4. 删除明细
- **请求方式**: DELETE
- **请求路径**: `/api/plan-barrier/{id}`

### 七、方案框架明细管理 (Plan Frame)

#### 1. 根据方案查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-frame/plan/{planId}`

#### 2. 根据方案社区查询明细
- **请求方式**: GET
- **请求路径**: `/api/plan-frame/plan-community/{planCommunityId}`

#### 3. 新增明细
- **请求方式**: POST
- **请求路径**: `/api/plan-frame`
- **请求体**:
```json
{
    "planId": 1,
    "frameId": 1,
    "planCommunityId": 1,
    "releaseDateBegin": "2024-03-01",
    "releaseDateEnd": "2024-05-31",
    "releaseStatus": 4
}
```

#### 4. 删除明细
- **请求方式**: DELETE
- **请求路径**: `/api/plan-frame/{id}`

---

### 八、DSL Agent 智能点位选择 (V2)

**基础路径**: `/api/agent/v2`

#### 1. DSL点位选择
- **请求方式**: POST
- **请求路径**: `/api/agent/v2/select-points`
- **功能**: 基于DSL执行复杂点位筛选
- **请求体**:
```json
{
    "mediaType": "barrier",
    "targetCount": 100,
    "dateRange": {
        "beginDate": "2025-03-01",
        "endDate": "2025-03-31"
    },
    "distributionStrategy": {
        "type": "per_community",
        "perCommunityCount": 2,
        "maxCommunities": 50,
        "minCommunities": 0
    },
    "filterConditions": {
        "communityIds": [1, 2, 3],
        "city": "南京市",
        "district": "鼓楼区",
        "devicePosition": "entrance",
        "onlyAvailable": true,
        "excludeOccupied": true
    },
    "limitConditions": {
        "maxResults": 100,
        "maxPerCommunity": 2
    }
}
```
- **响应**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "success": true,
        "totalCount": 100,
        "targetCount": 100,
        "communityCount": 50,
        "isComplete": true,
        "message": "成功选择 100 个点位（目标 100 个），覆盖 50 个社区",
        "points": [
            {
                "pointId": 1,
                "pointNo": "GATE001",
                "communityId": 1,
                "communityName": "万科翡翠公园",
                "mediaType": "barrier",
                "details": {
                    "deviceNo": "DEV001",
                    "doorLocation": "南门",
                    "devicePosition": "1"
                }
            }
        ],
        "pointsByCommunity": {
            "1": [...]
        }
    }
}
```

#### 2. 自然语言点位选择
- **请求方式**: POST
- **请求路径**: `/api/agent/v2/nlp-select`
- **功能**: 自然语言解析为DSL并执行点位选择
- **请求体**:
```json
{
    "message": "选3月份档期内的100面道闸，每个楼盘2个，优先选进口，要空闲的",
    "sessionId": "optional-session-id"
}
```

#### 3. 查询社区可用点位统计
- **请求方式**: POST
- **请求路径**: `/api/agent/v2/community-availability`
- **功能**: 查询各社区可用点位数量
- **请求体**:
```json
{
    "beginDate": "2025-03-01",
    "endDate": "2025-03-31",
    "city": "南京市",
    "district": "鼓楼区"
}
```
- **响应**:
```json
{
    "code": 200,
    "data": [
        {
            "communityId": 1,
            "communityName": "万科翡翠公园",
            "city": "南京市",
            "district": "鼓楼区",
            "availableBarrierCount": 5,
            "availableFrameCount": 10
        }
    ]
}
```

#### 4. 检查档期冲突
- **请求方式**: POST
- **请求路径**: `/api/agent/v2/check-conflicts`
- **功能**: 检查指定点位的档期冲突
- **请求体**:
```json
{
    "pointIds": [1, 2, 3],
    "mediaType": "barrier",
    "beginDate": "2025-03-01",
    "endDate": "2025-03-31"
}
```

---

## 📊 枚举值说明

### 设备位置 (devicePosition)
| 值 | 说明 |
|----|------|
| 1 | 进口 |
| 2 | 出口 |
| 3 | 进出口 |

### 画面位置 (screenPosition)
| 值 | 说明 |
|----|------|
| 1 | A |
| 2 | B |

### 灯箱朝向 (lightboxDirection)
| 值 | 说明 |
|----|------|
| 1 | 朝外 |
| 2 | 朝内 |
| 3 | 临街面 |

### 梯内位置 (innerPosition)
| 值 | 说明 |
|----|------|
| 1 | 左 |
| 2 | 中 |
| 3 | 右 |

### 方案发布状态 (releaseStatus)
| 值 | 说明 |
|----|------|
| 1 | 意向 |
| 2 | 锁位 |
| 3 | 执行中 |
| 4 | 执行完毕 |
| 5 | 档 |

### 销售类型 (salesType)
| 值 | 说明 |
|----|------|
| 1 | 销售 |
| 2 | 公益 |
| 3 | 置换 |
| 4 | 赠送 |
| 5 | 余位 |
| 6 | 其他 |

### 明细发布状态 (releaseStatus)
| 值 | 说明 |
|----|------|
| 1 | 意向 |
| 2 | 锁位 |
| 3 | 待刊发 |
| 4 | 刊发中 |
| 5 | 可调 |
| 6 | 到期 |
| 7 | 已下刊 |

---

## 🗄️ 数据库表

### 核心表

- `community` - 社区信息
- `barrier_gate` - 道闸信息
- `frame` - 框架信息
- `plan` - 广告方案
- `plan_community` - 方案-社区关联
- `plan_barrier` - 方案-道闸明细
- `plan_frame` - 方案-框架明细
- `agent_session` - AI 对话会话

## 🔧 其他配置

### MyBatis 配置

```yaml
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.advertising.entity
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
    lazy-loading-enabled: true
    multiple-result-sets-enabled: true
    use-column-label: true
    use-generated-keys: true
    default-executor-type: simple
    default-statement-timeout: 25000
```

### 日志配置

```yaml
logging:
  level:
    com.advertising.mapper: debug
    org.springframework.web: info
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

---

## 🔐 环境变量配置

所有敏感信息（API密钥、数据库密码）都通过环境变量管理，不会提交到Git。

### 配置文件

项目使用 `.env.template` 作为模板，你需要：

1. **复制模板文件**
   ```bash
   cp .env.template .env
   ```

2. **填写实际的API密钥和配置**

3. **加载环境变量并启动**
   
   **macOS/Linux:**
   ```bash
   # 方法1：使用 dotenv
   export $(cat .env | xargs)
   
   # 方法2：使用 source
   source .env
   
   # 启动应用
   mvn spring-boot:run
   ```
   
   **Windows (PowerShell):**
   ```powershell
   # 加载环境变量
   Get-Content .env | ForEach-Object {
     $name, $value = $_ -split '=', 2
     Set-Item -Path Env:$name -Value $value
   }
   
   # 启动应用
   mvn spring-boot:run
   ```

### 获取 API Key

1. **Kimi API Key** - 访问 https://platform.moonshot.cn/ 注册并获取
2. **阿里云语音** - 访问 https://nls-portal.console.aliyun.com/ 申请

⚠️ **注意**：`.env` 文件已被 `.gitignore` 排除，不会被提交到Git。请确保永远不要提交包含真实密钥的文件！

---

## 📁 项目结构

```
advertising-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/advertising/
│   │   │       ├── AdvertisingSystemApplication.java  # 启动类
│   │   │       ├── common/                            # 通用类
│   │   │       │   ├── Result.java                    # 通用响应结果
│   │   │       │   ├── PageResult.java                # 分页结果
│   │   │       │   ├── PageRequest.java               # 分页请求
│   │   │       │   └── GlobalExceptionHandler.java    # 全局异常处理
│   │   │       ├── config/                            # 配置类
│   │   │       │   ├── CorsConfig.java                # 跨域配置
│   │   │       │   └── WebMvcConfig.java              # Web MVC配置
│   │   │       ├── controller/                        # 控制器层
│   │   │       │   ├── CommunityController.java
│   │   │       │   ├── BarrierGateController.java
│   │   │       │   ├── FrameController.java
│   │   │       │   ├── PlanController.java
│   │   │       │   ├── PlanCommunityController.java
│   │   │       │   ├── PlanBarrierController.java
│   │   │       │   └── PlanFrameController.java
│   │   │       ├── entity/                            # 实体类
│   │   │       │   ├── Community.java
│   │   │       │   ├── BarrierGate.java
│   │   │       │   ├── Frame.java
│   │   │       │   ├── Plan.java
│   │   │       │   ├── PlanCommunity.java
│   │   │       │   ├── PlanBarrier.java
│   │   │       │   └── PlanFrame.java
│   │   │       ├── enums/                             # 枚举类
│   │   │       │   ├── DevicePositionEnum.java
│   │   │       │   ├── ScreenPositionEnum.java
│   │   │       │   ├── LightboxDirectionEnum.java
│   │   │       │   ├── InnerPositionEnum.java
│   │   │       │   ├── PlanReleaseStatusEnum.java
│   │   │       │   ├── SalesTypeEnum.java
│   │   │       │   └── DetailReleaseStatusEnum.java
│   │   │       ├── mapper/                            # 数据访问层
│   │   │       │   ├── CommunityMapper.java
│   │   │       │   ├── BarrierGateMapper.java
│   │   │       │   ├── FrameMapper.java
│   │   │       │   ├── PlanMapper.java
│   │   │       │   ├── PlanCommunityMapper.java
│   │   │       │   ├── PlanBarrierMapper.java
│   │   │       │   └── PlanFrameMapper.java
│   │   │       └── service/                           # 业务逻辑层
│   │   │           ├── CommunityService.java
│   │   │           ├── BarrierGateService.java
│   │   │           ├── FrameService.java
│   │   │           ├── PlanService.java
│   │   │           ├── PlanCommunityService.java
│   │   │           ├── PlanBarrierService.java
│   │   │           ├── PlanFrameService.java
│   │   │           └── impl/                          # 实现类
│   │   │               ├── CommunityServiceImpl.java
│   │   │               ├── BarrierGateServiceImpl.java
│   │   │               ├── FrameServiceImpl.java
│   │   │               ├── PlanServiceImpl.java
│   │   │               ├── PlanCommunityServiceImpl.java
│   │   │               ├── PlanBarrierServiceImpl.java
│   │   │               └── PlanFrameServiceImpl.java
│   │   └── resources/
│   │       ├── mapper/                                # MyBatis映射文件
│   │       │   ├── CommunityMapper.xml
│   │       │   ├── BarrierGateMapper.xml
│   │       │   ├── FrameMapper.xml
│   │       │   ├── PlanMapper.xml
│   │       │   ├── PlanCommunityMapper.xml
│   │       │   ├── PlanBarrierMapper.xml
│   │       │   └── PlanFrameMapper.xml
│   │       └── application.yml                        # 配置文件
│   └── test/
├── pom.xml                                            # Maven配置
└── README.md                                         # API文档
```

## 🐛 常见问题

### 1. 前端构建失败

```bash
# 清除缓存重新安装
cd frontend
rm -rf node_modules package-lock.json
npm install
```

### 2. 数据库连接失败

- 检查 MySQL 服务是否启动
- 检查用户名密码是否正确
- 检查数据库是否存在
- 检查 `allowPublicKeyRetrieval=true` 是否配置

### 3. AI 对话无响应

- 检查 Kimi API Key 是否配置正确
- 检查网络连接
- 查看后端日志是否有错误

### 4. 语音识别失败

- 检查麦克风权限是否允许
- 阿里云语音识别需要正确配置密钥
- 会自动降级到浏览器 Web Speech API

## 📝 开发指南

### 添加新的 AI 意图

编辑 `AgentOrchestratorService.java`：

```java
private Result<AgentChatResponse> processIntentStep(...) {
    // 在 intent 解析后添加新的处理逻辑
    switch (intent.getAction()) {
        case "CREATE_PLAN":
            return processCreatePlan(session, intent);
        case "QUERY":
            return processQuery(session, intent);
        // 添加新的意图处理
    }
}
```

### 修改问候语

编辑 `App.tsx` 中的 `generateGreeting` 函数：

```typescript
const generateGreeting = () => {
  // 添加新的问候语或节日
  const festivalGreetings: Record<string, string> = {
    '1-1': '新年伊始，万象更新',
    // 添加新的节日
  };
};
```

## 📄 许可证

[Apache License 2.0](LICENSE)

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📞 联系

如有问题，请联系项目维护者。

---

**Made with ❤️ by 永达传媒技术团队**
