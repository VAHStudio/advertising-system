#!/bin/bash
# 后台启动后端服务

cd /Users/zongheng/Documents/GitHub/advertising-system

# 加载环境变量
export $(cat .env | grep -v '^#' | xargs)

echo "Starting backend on port 16000..."
echo "KIMI_API_KEY: ${KIMI_API_KEY:0:10}..."
echo "DB_URL: ${DB_URL}"

# 使用 nohup 在后台运行
nohup mvn spring-boot:run > backend.log 2>&1 &

# 等待启动
sleep 10

# 检查是否成功
if lsof -i:16000 > /dev/null 2>&1; then
    echo "✅ Backend started successfully on port 16000"
    echo "Log file: backend.log"
else
    echo "❌ Backend failed to start"
    echo "Check backend.log for errors"
    cat backend.log | tail -20
fi
