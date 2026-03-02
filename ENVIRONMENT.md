# 环境变量配置说明

本项目使用环境变量来管理敏感信息和配置。请不要将包含真实密码和密钥的配置文件提交到Git仓库。

## 快速开始

### 方法1：使用环境变量（推荐）

在启动应用前设置环境变量：

```bash
# Linux/Mac
export DB_URL=jdbc:mysql://121.40.166.153:3306/mvp?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
export DB_USERNAME=root
export DB_PASSWORD=your_password_here
export KIMI_API_KEY=your_kimi_api_key_here

# Windows CMD
set DB_URL=jdbc:mysql://121.40.166.153:3306/mvp?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
set DB_USERNAME=root
set DB_PASSWORD=your_password_here
set KIMI_API_KEY=your_kimi_api_key_here

# Windows PowerShell
$env:DB_URL="jdbc:mysql://121.40.166.153:3306/mvp?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password_here"
$env:KIMI_API_KEY="your_kimi_api_key_here"
```

### 方法2：使用 application-dev.yml 文件

1. 复制 `application.yml.template` 为 `application-dev.yml`：
   ```bash
   cp src/main/resources/application.yml.template src/main/resources/application-dev.yml
   ```

2. 编辑 `application-dev.yml`，填写真实的配置值

3. 确保 `application-dev.yml` 已在 `.gitignore` 中，不会被提交

## 必需的环境变量

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `DB_URL` | 数据库连接URL | `jdbc:mysql://localhost:3306/mvp?useUnicode=true&characterEncoding=utf-8...` |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | `your_password` |
| `KIMI_API_KEY` | Kimi AI API密钥 | `sk-xxxxx` |

## 可选的环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `SERVER_PORT` | 服务端口 | `16000` |
| `SPRING_PROFILES_ACTIVE` | Spring激活的配置文件 | `dev` |
| `KIMI_ENDPOINT` | Kimi API端点 | `https://api.moonshot.ai/v1/chat/completions` |
| `KIMI_MODEL` | Kimi模型名称 | `kimi-k2.5` |
| `KIMI_THINKING` | 是否启用思考模式 | `disabled` |

## 配置文件优先级

Spring Boot 会按以下顺序加载配置（后加载的覆盖先加载的）：

1. `application.yml`（基础配置，已提交到Git）
2. `application-dev.yml`（开发环境配置，**不提交到Git**）
3. 环境变量（最高优先级）

## 安全提示

- ⚠️ **永远不要将包含密码和密钥的文件提交到Git**
- ⚠️ `application-dev.yml` 已在 `.gitignore` 中，不会被提交
- ⚠️ 生产环境请使用更安全的方式管理密钥（如KMS、Vault等）
- ⚠️ 定期更换API密钥和数据库密码

## 生产环境建议

在生产环境中，建议使用：
- Kubernetes Secrets
- AWS Secrets Manager
- HashiCorp Vault
- 或云平台提供的密钥管理服务
