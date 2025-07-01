#!/bin/bash

# =======================================================
#      安全地加载数据库凭据 (Load Database Credentials)
# =======================================================
# export 命令会将这些变量设置为当前 shell 会话的环境变量
# 这样，后续的 java 命令就能读取到它们

export UCSD_REG_DB_URL="jdbc:mysql://database-ucsd-registration.cs9au8sqo1g1.us-east-1.rds.amazonaws.com:3306/ucsd_reg"
export UCSD_REG_DB_USER="admin"
export UCSD_REG_DB_PASSWORD="lirui67251377"

# =======================================================
#      运行 Spring Boot 应用 (Run The Application)
# =======================================================
echo "Starting Spring Boot application with credentials from environment..."

# 开发用：
# /home/ubuntu/projects/UCSD-Registration-Trend/backend/web-backend-java-maven/mvnw spring-boot:run

# 实际运行：
# 使用 nohup 在后台运行，并将日志输出到 app.log 文件
# 确保将 'web-backend-0.0.1-SNAPSHOT.jar' 替换成你实际的 JAR 文件名
nohup java -jar /home/ubuntu/projects/UCSD-Registration-Trend/backend/web-backend-java-maven/target/web-backend-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
echo "Application started in the background."
echo "You can check the log with: tail -f app.log"
