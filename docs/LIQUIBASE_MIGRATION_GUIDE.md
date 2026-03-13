# Liquibase 数据库迁移指南

**迁移日期**: 2026-03-13  
**迁移方式**: Baseline 方式（保留现有数据）  
**责任人**: AI Assistant

---

## 迁移概述

本次迁移将数据库迁移工具从 **Flyway** 切换到 **Liquibase**，采用 **Baseline 方式**保留现有数据库结构和数据。

---

## 迁移前状态

- **工具**: Flyway
- **脚本位置**: `src/main/resources/db/migration/`
- **脚本数量**: 10 个版本 (V1 ~ V10)
- **状态**: 已在生产环境执行

### Flyway 历史脚本清单

| 版本 | 文件 | 说明 |
|------|------|------|
| V1 | V1__init.sql | 初始化数据库 |
| V2 | V2__create_base_tables.sql | 创建基础表 |
| V3 | V3__placeholder.sql | 占位符 |
| V4 | V4__placeholder.sql | 占位符 |
| V5 | V5__create_user_table.sql | 创建用户表 |
| V6 | V6__add_filter_indexes.sql | 添加过滤索引 |
| V7 | V7__add_missing_columns_to_relation_tables.sql | 添加关联表缺失字段 |
| V1.9 | V1.9__create_ai_conversation_table.sql | 创建 AI 会话表 |
| V8 | V8__add_plan_timestamp_columns_if_missing.sql | 添加计划时间戳字段 |
| V9 | V9__enhance_ai_conversation.sql | 增强 AI 会话表 |
| V10 | V10__create_conversation_message.sql | 创建 AI 对话消息表 |

---

## 迁移后状态

- **工具**: Liquibase 4.27.0
- **脚本位置**: `src/main/resources/db/changelog/`
- **格式**: YAML
- **策略**: Baseline 方式

### 新目录结构

```
src/main/resources/db/
├── changelog/                          # Liquibase 变更日志
│   ├── db.changelog-master.yaml        # 主变更日志文件
│   └── changes/                        # 变更集目录
│       └── 001-baseline.yaml           # 基线变更集（标记当前状态）
│
└── migration/                          # 旧 Flyway 脚本（保留备份）
    ├── V1__init.sql
    ├── V2__create_base_tables.sql
    ├── ...
    └── V10__create_conversation_message.sql
```

---

## 配置说明

### application.yml

```yaml
spring:
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
    default-schema: ${DB_SCHEMA:}
    liquibase-schema: ${DB_SCHEMA:}
    drop-first: false  # 生产环境必须为 false
    contexts: ${SPRING_PROFILES_ACTIVE:dev}
```

### 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `DB_SCHEMA` | - | 数据库 schema |
| `SPRING_PROFILES_ACTIVE` | dev | 运行环境 |

---

## Baseline 变更集

### 001-baseline.yaml

此变更集是迁移的关键，它：

1. **不执行任何 DDL 操作** - 因为数据库结构已存在
2. **标记当前数据库状态** - 使用 `tagDatabase` 标记为 `baseline-flyway-v10`
3. **初始化 Liquibase 元数据表** - 创建 `DATABASECHANGELOG` 和 `DATABASECHANGELOGLOCK` 表
4. **使用 preConditions 检查** - 验证 `ai_conversation` 表已存在才执行

```yaml
databaseChangeLog:
  - changeSet:
      id: baseline-2026-03-13
      author: ai-assistant
      context: baseline
      preConditions:
        - onFail: MARK_RAN
        - sqlCheck:
            expectedResult: 1
            sql: SELECT COUNT(*) FROM ai_conversation LIMIT 1
      tagDatabase:
        tag: baseline-flyway-v10
      changes:
        - sql:
            sql: SELECT 1
            comment: 标记基线状态
```

---

## 使用说明

### 启动应用

Liquibase 会在应用启动时自动执行变更日志：

```bash
mvn spring-boot:run
```

### 查看变更历史

```sql
-- 查看已执行的变更集
SELECT id, author, filename, dateexecuted, tag, exectype
FROM DATABASECHANGELOG
ORDER BY dateexecuted DESC;
```

### 添加新变更

1. 在 `db/changelog/changes/` 目录创建新的 YAML 文件
2. 在 `db.changelog-master.yaml` 中添加 include

**示例**: 添加新表

**文件**: `db/changelog/changes/002-add-new-table.yaml`

```yaml
databaseChangeLog:
  - changeSet:
      id: 002-add-new-table
      author: your-name
      context: dev,test,prod
      comment: 添加新表
      changes:
        - createTable:
            tableName: new_table
            columns:
              - column:
                  name: id
                  type: BIGINT
                  autoIncrement: true
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: name
                  type: VARCHAR(100)
                  constraints:
                    nullable: false
```

**更新 master**:

```yaml
databaseChangeLog:
  - include:
      file: changes/001-baseline.yaml
      relativeToChangelogFile: true
  
  # 添加新变更集
  - include:
      file: changes/002-add-new-table.yaml
      relativeToChangelogFile: true
```

---

## 回滚操作

### 开发环境回滚

```bash
# 回滚到最后一个 tag
mvn liquibase:rollback -Dliquibase.rollbackTag=baseline-flyway-v10
```

### 生产环境回滚

**警告**: 生产环境回滚需要谨慎，建议在执行前备份数据库。

```bash
# 回滚指定数量的变更集
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

---

## 验证清单

### 迁移后验证

- [ ] 应用正常启动无错误
- [ ] `DATABASECHANGELOG` 表已创建
- [ ] `DATABASECHANGELOGLOCK` 表已创建
- [ ] Baseline 变更集已执行（ID: baseline-2026-03-13）
- [ ] 所有表结构完整
- [ ] 所有数据完整
- [ ] 应用功能正常

### 查看 Baseline 状态

```sql
-- 验证 baseline 变更集已记录
SELECT * FROM DATABASECHANGELOG 
WHERE id = 'baseline-2026-03-13';

-- 预期结果：
-- id: baseline-2026-03-13
-- author: ai-assistant
-- filename: db/changelog/changes/001-baseline.yaml
-- tag: baseline-flyway-v10
-- exectype: EXECUTED
```

---

## 常见问题

### Q: 为什么采用 Baseline 方式？

**A**: 因为生产环境已有数据和 Flyway 历史，重置数据库会导致数据丢失。Baseline 方式可以无缝接管现有数据库。

### Q: 旧的 Flyway 脚本还能用吗？

**A**: 可以，但不再推荐。保留在 `db/migration/` 目录作为备份和历史记录。

### Q: 如何查看数据库当前版本？

**A**: 
```sql
SELECT tag FROM DATABASECHANGELOG 
WHERE tag IS NOT NULL 
ORDER BY dateexecuted DESC 
LIMIT 1;
```

### Q: 新环境如何初始化？

**A**: 新环境需要：
1. 执行旧的 Flyway V1~V10 脚本创建基础表
2. 然后启动应用执行 Liquibase baseline

或者使用 `drop-first: true`（仅开发环境）：
```yaml
spring:
  liquibase:
    drop-first: true  # 开发环境可用
```

---

## 参考文档

- [Liquibase 官方文档](https://docs.liquibase.com/)
- [Liquibase YAML 格式](https://docs.liquibase.com/concepts/changelogs/yaml-format.html)
- [Spring Boot Liquibase 配置](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.liquibase)

---

**最后更新**: 2026-03-13
