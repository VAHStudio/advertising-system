#!/bin/bash
# 启动脚本 - 加载环境变量并启动应用

# 获取脚本所在目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# 加载环境变量
if [ -f .env ]; then
    echo "Loading environment variables from .env..."
    export $(cat .env | grep -v '^#' | xargs)
    echo "Environment variables loaded successfully!"
else
    echo "Warning: .env file not found!"
fi

# 显示配置信息
echo ""
echo "=================================="
echo "Starting Advertising System"
echo "=================================="
echo "KIMI_API_KEY: ${KIMI_API_KEY:0:10}..."
echo "MYSQL_HOST: ${MYSQL_HOST_ENV}"
echo "MYSQL_PORT: ${MYSQL_PORT_ENV}"
echo "SERVER_PORT: ${SERVER_PORT:-16000}"
echo "=================================="
echo ""

# 编译并启动 Spring Boot 应用
echo "Building and starting Spring Boot application..."
mvn clean compile spring-boot:run
