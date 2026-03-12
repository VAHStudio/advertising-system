# 部署指南

## 环境要求

### 服务器配置

| 组件 | 最低配置 | 推荐配置 |
|------|----------|----------|
| CPU | 2 核 | 4 核 |
| 内存 | 4 GB | 8 GB |
| 磁盘 | 50 GB | 100 GB SSD |
| 网络 | 5 Mbps | 10 Mbps |

### 软件环境

| 软件 | 版本 | 用途 |
|------|------|------|
| CentOS / Ubuntu | 7+ / 20.04+ | 操作系统 |
| Java | 21 | 运行后端服务 |
| MySQL | 8.0 | 数据库 |
| Nginx | 1.20+ | 反向代理 |
| Node.js | 20+ | 构建前端 |

---

## 部署架构

```
┌─────────────────────────────────────────────────────────┐
│                        用户                             │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                       Nginx                             │
│  - 端口: 80/443                                        │
│  - 静态资源服务                                         │
│  - API 反向代理                                         │
│  - SSL 终止                                             │
└─────────────┬───────────────────────────────┬───────────┘
              │                               │
    ┌─────────▼─────────┐         ┌───────────▼───────────┐
    │   Frontend        │         │    Backend            │
    │   (dist/)         │         │   (Spring Boot)       │
    │   端口: 80        │         │   端口: 16000         │
    └───────────────────┘         └───────────────────────┘
                                              │
                                  ┌───────────▼───────────┐
                                  │       MySQL           │
                                  │      端口: 3306       │
                                  └───────────────────────┘
```

---

## 部署步骤

### 1. 系统初始化

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y  # Ubuntu
sudo yum update -y                       # CentOS

# 安装基础工具
sudo apt install -y wget curl vim net-tools  # Ubuntu
sudo yum install -y wget curl vim net-tools  # CentOS

# 配置时区
timedatectl set-timezone Asia/Shanghai
```

### 2. 安装 Java 21

```bash
# 下载 JDK 21
wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz

# 解压
sudo mkdir -p /usr/lib/jvm
sudo tar -xzf jdk-21_linux-x64_bin.tar.gz -C /usr/lib/jvm/

# 配置环境变量
sudo tee /etc/profile.d/java.sh << 'EOF'
export JAVA_HOME=/usr/lib/jvm/jdk-21
export PATH=$PATH:$JAVA_HOME/bin
EOF

source /etc/profile.d/java.sh

# 验证
java -version
```

### 3. 安装 MySQL 8.0

```bash
# Ubuntu
sudo apt install -y mysql-server-8.0

# CentOS
sudo yum install -y https://dev.mysql.com/get/mysql80-community-release-el7-11.noarch.rpm
sudo yum install -y mysql-community-server

# 启动服务
sudo systemctl enable mysqld
sudo systemctl start mysqld

# 获取临时密码
sudo grep 'temporary password' /var/log/mysqld.log

# 安全配置
sudo mysql_secure_installation

# 登录并创建数据库
mysql -u root -p

CREATE DATABASE outdoor_saas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'outdoor_saas'@'localhost' IDENTIFIED BY 'YourStrongPassword123!';
GRANT ALL PRIVILEGES ON outdoor_saas.* TO 'outdoor_saas'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 4. 安装 Nginx

```bash
# Ubuntu
sudo apt install -y nginx

# CentOS
sudo yum install -y epel-release
sudo yum install -y nginx

# 启动服务
sudo systemctl enable nginx
sudo systemctl start nginx
```

### 5. 后端部署

```bash
# 创建应用目录
sudo mkdir -p /opt/outdoor-saas
sudo chown $USER:$USER /opt/outdoor-saas

# 复制 JAR 文件
cp outdoor-saas-be/target/outdoor-saas-*.jar /opt/outdoor-saas/app.jar

# 创建配置文件
cat > /opt/outdoor-saas/application.yml << 'EOF'
server:
  port: 16000

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/outdoor_saas?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: outdoor_saas
    password: YourStrongPassword123!
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

jwt:
  secret: YourVeryLongSecretKeyHereAtLeast32Characters
  expiration: 86400000

logging:
  level:
    com.touhuwai: INFO
  file:
    name: /opt/outdoor-saas/logs/app.log
EOF

# 创建启动脚本
cat > /opt/outdoor-saas/start.sh << 'EOF'
#!/bin/bash
APP_NAME=outdoor-saas
APP_JAR=/opt/outdoor-saas/app.jar
APP_CONFIG=/opt/outdoor-saas/application.yml
LOG_FILE=/opt/outdoor-saas/logs/app.log

mkdir -p /opt/outdoor-saas/logs

nohup java -jar \
  -Dspring.config.location=$APP_CONFIG \
  -Xms512m -Xmx1024m \
  $APP_JAR > $LOG_FILE 2>&1 &

echo "Application started. PID: $!"
EOF

chmod +x /opt/outdoor-saas/start.sh

# 创建 systemd 服务
sudo tee /etc/systemd/system/outdoor-saas.service << 'EOF'
[Unit]
Description=Outdoor SaaS Application
After=syslog.target network.target mysql.service

[Service]
User=outdoor-saas
Group=outdoor-saas
ExecStart=/usr/bin/java -jar -Xms512m -Xmx1024m /opt/outdoor-saas/app.jar --spring.config.location=/opt/outdoor-saas/application.yml
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# 创建用户
sudo useradd -r -s /bin/false outdoor-saas

# 设置权限
sudo chown -R outdoor-saas:outdoor-saas /opt/outdoor-saas

# 启动服务
sudo systemctl daemon-reload
sudo systemctl enable outdoor-saas
sudo systemctl start outdoor-saas

# 查看状态
sudo systemctl status outdoor-saas
```

### 6. 前端部署

```bash
# 本地构建
cd outdoor-saas-fe
npm install
npm run build

# 上传到服务器
scp -r dist/* user@server:/var/www/outdoor-saas/

# 或在服务器上构建
ssh user@server
sudo apt install -y nodejs npm  # 安装 Node.js
cd /opt/outdoor-saas-fe
npm install
npm run build
sudo cp -r dist/* /var/www/outdoor-saas/
```

### 7. Nginx 配置

```bash
sudo tee /etc/nginx/conf.d/outdoor-saas.conf << 'EOF'
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态资源
    location / {
        root /var/www/outdoor-saas;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    # API 反向代理
    location /api {
        proxy_pass http://localhost:16000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        
        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
    
    # SSE 特殊配置
    location /api/ai-assistant/stream {
        proxy_pass http://localhost:16000;
        proxy_http_version 1.1;
        proxy_set_header Connection '';
        proxy_buffering off;
        proxy_cache off;
        proxy_read_timeout 3600s;
    }
    
    # 错误页面
    error_page 500 502 503 504 /50x.html;
    location = /50x.html {
        root /var/www/outdoor-saas;
    }
}
EOF

# 测试配置
sudo nginx -t

# 重载配置
sudo systemctl reload nginx
```

### 8. SSL 配置（推荐）

```bash
# 安装 Certbot
sudo apt install -y certbot python3-certbot-nginx

# 申请证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo systemctl enable certbot.timer
```

---

## Docker 部署（可选）

### 1. 创建 Dockerfile - 后端

```dockerfile
# outdoor-saas-be/Dockerfile
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 16000

ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx1024m", "app.jar"]
```

### 2. 创建 Dockerfile - 前端

```dockerfile
# outdoor-saas-fe/Dockerfile
FROM node:20-alpine as builder

WORKDIR /app
COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
```

### 3. Docker Compose

```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: outdoor_saas
      MYSQL_USER: outdoor_saas
      MYSQL_PASSWORD: userpassword
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  backend:
    build: ./outdoor-saas-be
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/outdoor_saas?useUnicode=true&characterEncoding=utf8
      SPRING_DATASOURCE_USERNAME: outdoor_saas
      SPRING_DATASOURCE_PASSWORD: userpassword
      JWT_SECRET: your-secret-key-here
    ports:
      - "16000:16000"
    depends_on:
      mysql:
        condition: service_healthy

  frontend:
    build: ./outdoor-saas-fe
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

### 4. 启动

```bash
docker-compose up -d
```

---

## 监控和维护

### 1. 日志查看

```bash
# 后端日志
sudo tail -f /opt/outdoor-saas/logs/app.log

# Nginx 日志
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log

# MySQL 日志
sudo tail -f /var/log/mysql/error.log
```

### 2. 服务管理

```bash
# 重启后端
sudo systemctl restart outdoor-saas

# 重启 Nginx
sudo systemctl restart nginx

# 重启 MySQL
sudo systemctl restart mysql
```

### 3. 备份脚本

```bash
#!/bin/bash
# backup.sh

BACKUP_DIR=/opt/backup
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME=outdoor_saas

mkdir -p $BACKUP_DIR

# 数据库备份
mysqldump -u outdoor_saas -p'YourStrongPassword123!' $DB_NAME > $BACKUP_DIR/db_$DATE.sql

# 应用备份
tar -czf $BACKUP_DIR/app_$DATE.tar.gz /opt/outdoor-saas

# 保留最近 30 天
find $BACKUP_DIR -name "*.sql" -mtime +30 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +30 -delete

echo "Backup completed: $DATE"
```

---

## 故障排查

### 1. 后端无法启动

```bash
# 检查端口占用
sudo netstat -tulpn | grep 16000

# 检查日志
sudo journalctl -u outdoor-saas -f

# 检查权限
ls -la /opt/outdoor-saas/
```

### 2. 数据库连接失败

```bash
# 检查 MySQL 状态
sudo systemctl status mysql

# 检查连接
mysql -u outdoor_saas -p -h localhost

# 检查防火墙
sudo ufw status
```

### 3. Nginx 502 错误

```bash
# 检查后端是否运行
curl http://localhost:16000/api/health

# 检查 Nginx 配置
sudo nginx -t

# 检查 SELinux (CentOS)
getenforce
```

---

## 安全加固

### 1. 防火墙配置

```bash
# Ubuntu UFW
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable

# CentOS Firewalld
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --permanent --add-service=ssh
sudo firewall-cmd --reload
```

### 2. 禁用 root 登录

```bash
# 创建普通用户
sudo useradd -m -s /bin/bash deploy
sudo usermod -aG sudo deploy

# 配置 SSH
sudo sed -i 's/PermitRootLogin yes/PermitRootLogin no/' /etc/ssh/sshd_config
sudo systemctl restart sshd
```

### 3. 定期更新

```bash
# 设置自动更新
sudo apt install -y unattended-upgrades  # Ubuntu
sudo yum install -y yum-cron             # CentOS
```

---

## 性能优化

### 1. JVM 调优

```bash
# /opt/outdoor-saas/start.sh
java -jar \
  -Xms1g -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/opt/outdoor-saas/logs/ \
  app.jar
```

### 2. MySQL 调优

```ini
# /etc/mysql/mysql.conf.d/mysqld.cnf
[mysqld]
innodb_buffer_pool_size = 2G
innodb_log_file_size = 512M
max_connections = 200
query_cache_size = 64M
query_cache_type = 1
```

### 3. Nginx 调优

```nginx
# /etc/nginx/nginx.conf
worker_processes auto;
worker_connections 4096;

gzip on;
gzip_types text/plain text/css application/json application/javascript;

client_max_body_size 50M;
proxy_buffer_size 4k;
proxy_buffers 8 4k;
```
