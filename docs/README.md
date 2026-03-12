# Outdoor SaaS - 项目文档

## 项目概述

Outdoor SaaS 是一个全栈广告管理系统，用于管理户外广告资源（社区、道闸、电梯框架）和广告投放计划。

### 核心功能

1. **资源管理**
   - 社区管理：管理住宅社区信息
   - 道闸管理：停车场道闸广告设备
   - 框架管理：电梯框架广告

2. **投放计划**
   - 创建和管理广告活动
   - 关联资源到计划
   - 看板/列表双视图

3. **AI 助手**
   - 实时流式对话
   - 智能导航和操作
   - 工具调用功能

4. **数据看板**
   - 关键指标统计
   - 收入趋势分析
   - 资源利用率

---

## 技术架构

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 4.0.3 | 应用框架 |
| Java | 21 | 编程语言 |
| MyBatis | 3.x | ORM 框架 |
| MySQL | 8.0 | 数据库 |
| Flyway | 10.x | 数据库迁移 |
| Spring Security | 6.x | 安全认证 |
| JWT | 0.12.x | 令牌认证 |
| Dify AI | - | AI 服务集成 |
| Apache POI | 5.x | Excel 处理 |

### 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5.x | 前端框架 |
| TypeScript | 5.8 | 类型系统 |
| Vite | 6.x | 构建工具 |
| Tailwind CSS | 4.x | 样式框架 |
| Vue Router | 4.x | 路由管理 |
| Pinia | 2.x | 状态管理 |
| Lucide Vue | 0.x | 图标库 |

---

## 项目结构

```
advertising-system/
├── outdoor-saas-be/          # 后端项目
│   ├── src/main/java/        # Java 源代码
│   │   ├── controller/       # REST API 控制器
│   │   ├── service/          # 业务逻辑层
│   │   ├── mapper/           # MyBatis 数据访问
│   │   ├── entity/           # 实体类
│   │   ├── dto/              # 数据传输对象
│   │   ├── config/           # 配置类
│   │   ├── security/         # 安全组件
│   │   └── common/           # 通用工具
│   └── src/main/resources/
│       ├── db/migration/     # Flyway 迁移脚本
│       └── mapper/           # MyBatis XML
├── outdoor-saas-fe/          # 前端项目
│   ├── src/
│   │   ├── components/       # 可复用组件
│   │   ├── pages/            # 页面组件
│   │   ├── services/         # API 服务层
│   │   ├── stores/           # Pinia 状态管理
│   │   ├── router/           # 路由配置
│   │   ├── hooks/            # 自定义 Hooks
│   │   └── types/            # TypeScript 类型
│   └── public/               # 静态资源
└── docs/                     # 项目文档
```

---

## 快速开始

### 环境要求

- Java 21+
- Node.js 20+
- MySQL 8.0+
- Maven 3.9+

### 后端启动

```bash
cd outdoor-saas-be
mvn spring-boot:run
```

### 前端启动

```bash
cd outdoor-saas-fe
npm install
npm run dev
```

---

## 文档索引

- [API 文档](./api-documentation.md) - 完整的 API 接口文档
- [架构设计](./architecture.md) - 系统架构详细说明
- [数据库设计](./database.md) - 数据库表结构和关系
- [部署指南](./deployment.md) - 生产环境部署说明
- [开发指南](./development.md) - 开发规范和最佳实践
- [代码审查报告](./code-review.md) - 代码质量审查结果

---

## 版权信息

Copyright © 2024 Touhuwai. All rights reserved.
