#!/bin/bash

# =======================================================
#      脚本配置 (Configuration)
# =======================================================
# 将可变的部分定义为变量，方便修改
APP_PORT=8080
JAR_PATH="/home/ubuntu/projects/UCSD-Registration-Trend/backend/web-backend-java-maven/target/web-backend-0.0.1-SNAPSHOT.jar"
LOG_FILE="/home/ubuntu/logs/app.log"
# 确保日志目录存在
mkdir -p "$(dirname "$LOG_FILE")"

# =======================================================
#      安全地加载数据库凭据 (Load Database Credentials)
# =======================================================
# export 命令会将这些变量设置为当前 shell 会话的环境变量
# 这样，后续的 java 命令就能读取到它们

export UCSD_REG_DB_URL="jdbc:mysql://database-ucsd-registration.cs9au8sqo1g1.us-east-1.rds.amazonaws.com:3306/ucsd_reg"
export UCSD_REG_DB_USER="admin"
export UCSD_REG_DB_PASSWORD="lirui67251377"

# =======================================================
#      基于端口杀掉旧的 Spring Boot 后台进程
# =======================================================
echo "Checking for existing process on port $APP_PORT..."

# 使用 lsof 命令查找监听指定端口的进程ID
# -t 选项让 lsof 只输出 PID，方便直接给 kill 命令使用
PID=$(lsof -t -i:$APP_PORT)

if [ -n "$PID" ]; then
  echo "Found existing process with PID: $PID. Sending TERM signal..."
  # 首先尝试优雅地关闭 (kill -15)
  kill "$PID"
  # 等待几秒钟
  sleep 5
  # 再次检查进程是否还存在
  if ps -p "$PID" > /dev/null; then
    echo "Process is still alive. Forcing kill (kill -9)..."
    kill -9 "$PID"
  fi
  echo "Old process killed."
else
  echo "No existing process found on port $APP_PORT."
fi


# =======================================================
#      运行 Spring Boot 应用 (Run The Application)
# =======================================================
echo "Starting Spring Boot application with credentials from environment..."

# 开发用：
/home/ubuntu/projects/UCSD-Registration-Trend/backend/web-backend-java-maven/mvnw spring-boot:run

# 实际运行：
# 使用 nohup 在后台运行，并将日志输出到 app.log 文件
# 确保将 'web-backend-0.0.1-SNAPSHOT.jar' 替换成你实际的 JAR 文件名
# nohup java -jar /home/ubuntu/projects/UCSD-Registration-Trend/backend/web-backend-java-maven/target/web-backend-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
# echo "Application started in the background."
# echo "You can check the log with: tail -f app.log"
