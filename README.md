# Outdoor SaaS - 户外广告投放管理系统

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-21-orange" alt="Java">
  <img src="https://img.shields.io/badge/Vue-3-green" alt="Vue">
  <img src="https://img.shields.io/badge/TypeScript-5.8-blue" alt="TypeScript">
  <img src="https://img.shields.io/badge/Tailwind%20CSS-4.0-purple" alt="Tailwind CSS">
  <img src="https://img.shields.io/badge/MySQL-8.0-yellow" alt="MySQL">
</p>

全栈户外广告投放管理系统，集成 AI 智能助手。

## 项目结构

```
outdoor-saas/
├── outdoor-saas-be/    # 后端 (be)
└── outdoor-saas-fe/    # 前端 (fe)
```

## 技术栈

### 后端 (be)
- **Spring Boot 4.0.3** - 核心框架
- **Java 21** - 编程语言
- **MyBatis** - ORM 框架
- **MySQL 8.0** - 数据库
- **Flyway** - 数据库迁移
- **Spring Security + JWT** - 认证授权
- **Dify** - AI 平台集成
- **Apache POI** - Excel 处理

### 前端 (fe)
- **Vue 3** - UI 框架
- **TypeScript 5.8** - 类型安全
- **Vite 6** - 构建工具
- **Tailwind CSS 4** - 样式框架
- **Vue Router 4** - 路由管理
- **Pinia** - 状态管理
- **Lucide Vue** - 图标库

## 快速开始

### 环境要求
- Java 21+
- Node.js 18+
- MySQL 8.0+
- Maven 3.9+

### 1. 克隆项目

```bash
git clone <repository-url>
cd outdoor-saas
```

### 2. 配置环境变量

**后端 (outdoor-saas-be/.env)**：

```bash
DB_URL=jdbc:mysql://localhost:3306/mvp?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=your_password
DIFY_API_KEY=your_dify_api_key
DIFY_BASE_URL=https://api.dify.ai/v1
```

### 3. 启动开发服务器

**后端**：

```bash
cd outdoor-saas-be
mvn spring-boot:run
```

后端服务运行在 http://localhost:16000

**前端**：

```bash
cd outdoor-saas-fe
npm install
npm run dev
```

前端服务运行在 http://localhost:3000

## 常用命令

### 后端 (be)

```bash
cd outdoor-saas-be

# 开发模式
mvn spring-boot:run

# 构建 JAR
mvn clean package -DskipTests

# 运行单个测试类
mvn test -Dtest=ClassNameTest

# 运行单个测试方法
mvn test -Dtest=ClassNameTest#methodName

# 运行所有测试
mvn test

# 代码检查
mvn checkstyle:check
```

### 前端 (fe)

```bash
cd outdoor-saas-fe

# 开发模式
npm run dev

# 生产构建
npm run build

# 预览生产构建
npm run preview

# 类型检查
npm run lint
```

## 功能特性

- 🤖 AI 智能助手 - 基于 Dify 的自然语言交互
- 📊 投放方案管理 - 完整的广告项目管理流程
- 🏘️ 基础数据管理 - 社区、框架媒体、道闸设备管理
- 📈 Dashboard 统计 - 营收趋势、资源利用率、排行榜
- 🎨 现代化 UI - 基于 Tailwind CSS 的响应式设计，支持暗黑模式
- 🔐 JWT 认证 - 安全的用户认证机制
- 🔍 高级查询 - 支持复杂条件筛选和分页查询

## 开发指南

请参考 [AGENTS.md](./AGENTS.md) 了解代码规范和开发指南。

本项目已集成 Claude Code 插件，支持：
- `/plan` - 创建实施计划
- `/tdd` - 测试驱动开发
- `/code-review` - 代码审查
- `/e2e` - E2E 测试生成

更多信息请参考 [CLAUDE.md](./CLAUDE.md)。

## 数据库

- MySQL 8.0
- Flyway 自动迁移
- 迁移文件位于 `outdoor-saas-be/src/main/resources/db/migration/`

## 目录结构

```
outdoor-saas-be/src/main/java/com/touhuwai/
├── controller/    # REST API 端点
├── service/       # 业务逻辑
├── mapper/        # MyBatis 数据访问
├── entity/        # 实体类
├── dto/           # 数据传输对象
├── config/        # 配置类
├── security/      # 安全相关
└── common/        # 工具类

outdoor-saas-fe/src/
├── pages/         # 页面组件 (13个页面)
├── components/    # 公共组件
├── services/      # API 服务
├── stores/        # Pinia 状态管理
├── router/        # 路由配置
├── composables/   # 组合式函数
├── hooks/         # 自定义 Hooks
└── types/         # TypeScript 类型
```

## 前端页面

| 页面 | 路径 | 说明 |
|------|------|------|
| 登录 | `/login` | 用户登录/注册 |
| 方案管理 | `/plans` | 投放方案列表 |
| 方案详情 | `/plans/:id` | 方案详细信息 |
| 社区管理 | `/communities` | 社区数据列表 |
| 社区详情 | `/communities/:id` | 社区详细信息 |
| 道闸管理 | `/barrier-gates` | 道闸设备列表 |
| 道闸详情 | `/barrier-gates/:id` | 道闸详细信息 |
| 框架管理 | `/frames` | 框架媒体列表 |
| 框架详情 | `/frames/:id` | 框架详细信息 |
| 方案社区 | `/plan-communities` | 方案社区关联 |
| 方案道闸 | `/plan-barriers` | 方案道闸明细 |
| 方案框架 | `/plan-frames` | 方案框架明细 |
| AI助手 | `/ai-assistant` | 智能对话助手 |

## API 文档

后端 API 前缀：`/api`

响应格式：
```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 主要接口

| 模块 | 接口 | 说明 |
|------|------|------|
| 认证 | `POST /auth/login` | 用户登录 |
| 认证 | `POST /auth/register` | 用户注册 |
| AI助手 | `GET /ai-assistant/stream` | 流式对话 (SSE) |
| 社区 | `GET/POST/PUT/DELETE /community` | CRUD 操作 |
| 社区 | `POST /community/filter` | 条件查询 |
| 社区 | `POST /community/filter/page` | 分页条件查询 |
| 框架 | `GET/POST/PUT/DELETE /frame` | CRUD 操作 |
| 框架 | `POST /frame/filter` | 条件查询 |
| 道闸 | `GET/POST/PUT/DELETE /barrier-gate` | CRUD 操作 |
| 道闸 | `POST /barrier-gate/filter` | 条件查询 |
| 方案 | `GET/POST/PUT/DELETE /plan` | CRUD 操作 |
| 方案 | `POST /plan/filter` | 条件查询 |
| Dashboard | `GET /dashboard/*` | 统计数据 |

### 查询接口说明

所有基础数据模块支持两种查询方式：
- `POST /{module}/filter` - 不分页条件查询
- `POST /{module}/filter/page` - 分页条件查询

请求体示例：
```json
{
  "name": "关键词",
  "status": 1,
  "startTime": "2024-01-01",
  "endTime": "2024-12-31"
}
```

## 贡献

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/xxx`)
3. 提交更改 (`git commit -m 'Add xxx'`)
4. 推送到分支 (`git push origin feature/xxx`)
5. 创建 Pull Request

