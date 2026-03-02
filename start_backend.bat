@echo off
chcp 65001 >nul
echo 启动永达传媒AI广告投放管理系统后端服务...
echo 基于Spring Boot + Java 21 + Kimi K2.5
echo.

cd /d "%~dp0"

:: 检查Java环境
echo 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Java，请先安装Java 21或更高版本
    echo 下载地址: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

:: 检查Maven环境
echo 检查Maven环境...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo 警告: 未找到Maven，将使用项目自带的Maven Wrapper
    set MVN_CMD=mvnw.cmd
) else (
    set MVN_CMD=mvn
)

:: 检查JAR文件是否存在
if not exist "target\advertising-system-1.0.0.jar" (
    echo.
    echo JAR文件不存在，正在编译项目...
    echo.
    %MVN_CMD% clean package -DskipTests
    if errorlevel 1 (
        echo 错误: 编译失败，请检查代码错误
        pause
        exit /b 1
    )
)

echo.
echo ================================================
echo 启动Spring Boot服务...
echo 服务地址: http://localhost:16000
echo API文档: http://localhost:16000/swagger-ui.html
echo 前端地址: http://localhost:3000
echo ================================================
echo.
echo 按Ctrl+C停止服务
echo.

java -jar target\advertising-system-1.0.0.jar

pause