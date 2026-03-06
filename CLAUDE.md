# CLAUDE.md - Claude Code 插件使用指南

本项目已集成 Claude Code 插件，提供丰富的 AI 辅助开发能力。

---

## 可用命令

在 Claude Code 中使用 `/命令` 触发，或直接描述需求。

### 开发流程
- `/plan` - 创建实施计划
- `/tdd` - 测试驱动开发
- `/e2e` - 生成并运行 E2E 测试
- `/build-fix` - 修复构建错误
- `/code-review` - 代码审查
- `/refactor-clean` - 重构清理

### 项目管理
- `/multi-plan` - 多模块项目规划
- `/multi-execute` - 多文件并行执行
- `/multi-workflow` - 多步骤工作流
- `/sessions` - 会话管理

### 学习与优化
- `/learn` - 从会话中提取模式
- `/evolve` - 演进工作流
- `/skill-create` - 从 Git 历史创建技能

### 质量保障
- `/verify` - 验证实现
- `/quality-gate` - 质量门禁
- `/test-coverage` - 测试覆盖率检查

---

## 技能 (Skills)

自动加载的领域特定技能：

### 后端技能
- `springboot-patterns` - Spring Boot 架构模式
- `springboot-tdd` - Spring Boot TDD 开发
- `springboot-security` - Spring Security 最佳实践
- `springboot-verification` - Spring Boot 验证循环
- `jpa-patterns` - JPA/Hibernate 模式
- `java-coding-standards` - Java 编码规范
- `database-migrations` - 数据库迁移
- `api-design` - REST API 设计

### 前端技能
- `frontend-patterns` - React/前端开发模式
- `e2e-testing` - Playwright E2E 测试
- `coding-standards` - TypeScript/JavaScript 规范

### 通用技能
- `security-review` - 安全审查
- `deployment-patterns` - 部署工作流
- `docker-patterns` - Docker 配置
- `search-first` - 研究优先工作流
- `continuous-learning` - 持续学习
- `verification-loop` - 验证循环

---

## Agent

专用子代理：

- `planner` - 规划复杂任务
- `code-reviewer` - 代码审查
- `tdd-guide` - TDD 指导
- `e2e-runner` - E2E 测试运行
- `build-error-resolver` - 构建错误解决
- `security-reviewer` - 安全审查
- `database-reviewer` - 数据库审查
- `refactor-cleaner` - 重构清理

---

## Hooks

自动触发的钩子：

- 会话持久化
- 工具执行前后钩子
- 文件变更监听

---

## Rules

代码规范规则 (按语言分组)：

- `typescript/` - TypeScript 规范
- `python/` - Python 规范
- `golang/` - Go 规范
- `swift/` - Swift 规范
- `common/` - 通用规范

---

## 使用示例

```bash
# 修复构建错误
/build-fix

# 计划新功能
/plan 创建用户认证功能

# 运行测试驱动开发
/tdd

# 代码审查
/code-review

# 创建 E2E 测试
/e2e 测试登录流程
```

---

## 配置

- `.claude/` - Claude 配置
- `package-manager.json` - 包管理器配置 (npm)

---

更多信息请参考：https://claude.ai/code
