# Flyway 迁移脚本备份说明

**备份时间**: 2026-03-13  
**迁移工具**: Liquibase  
**备份原因**: 历史记录保留

---

## 说明

本目录包含 Flyway 迁移工具的历史脚本（V1 ~ V10），用于数据库结构的初始化。

从 **2026-03-13** 起，数据库迁移工具已从 **Flyway** 切换到 **Liquibase**。

---

## 当前状态

- **这些脚本不再自动执行**
- **保留作为历史记录和参考**
- **新变更请使用 Liquibase 格式**

---

## 脚本清单

| 版本 | 文件 | 执行日期 | 说明 |
|------|------|----------|------|
| V1 | V1__init.sql | - | 初始化数据库 |
| V2 | V2__create_base_tables.sql | - | 创建基础表 |
| V3 | V3__placeholder.sql | - | 占位符 |
| V4 | V4__placeholder.sql | - | 占位符 |
| V5 | V5__create_user_table.sql | - | 创建用户表 |
| V6 | V6__add_filter_indexes.sql | - | 添加过滤索引 |
| V7 | V7__add_missing_columns_to_relation_tables.sql | - | 添加关联表缺失字段 |
| V1.9 | V1.9__create_ai_conversation_table.sql | - | 创建 AI 会话表 |
| V8 | V8__add_plan_timestamp_columns_if_missing.sql | - | 添加计划时间戳字段 |
| V9 | V9__enhance_ai_conversation.sql | - | 增强 AI 会话表 |
| V10 | V10__create_conversation_message.sql | - | 创建 AI 对话消息表 |

---

## 新迁移工具

**工具**: Liquibase 4.27.0  
**位置**: `src/main/resources/db/changelog/`  
**格式**: YAML  
**指南**: 参见 `docs/LIQUIBASE_MIGRATION_GUIDE.md`

---

## 如需手动执行

**警告**: 仅在全新数据库环境中使用！

```bash
# 手动执行所有 Flyway 脚本（仅在无数据的新环境中）
cd src/main/resources/db/migration
for f in V*.sql; do
    echo "执行: $f"
    mysql -u root -p mvp < $f
done
```

---

**注意**: 生产环境请勿手动执行这些脚本，以免破坏现有数据。
