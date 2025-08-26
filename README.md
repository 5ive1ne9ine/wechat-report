# 微信聊天分析器

## 项目简介

微信聊天分析器是一个基于Spring Boot的Web应用系统，通过集成AI服务和Chatlog服务，实现对微信聊天数据的智能分析和可视化展示。

## 核心功能

- **聊天数据获取**：集成Chatlog服务获取微信聊天数据
- **智能分析**：调用AI服务进行聊天内容智能分析
- **结构化报告**：生成详细的结构化分析报告
- **历史管理**：查看和管理历史分析记录
- **配置管理**：灵活配置AI服务和Chatlog服务参数

## 技术栈

- **后端框架**：Spring Boot 3.x
- **Java版本**：Java 8 (兼容语法)
- **构建工具**：Maven
- **数据库**：MySQL 8.0
- **ORM框架**：MyBatis-Plus
- **缓存**：Redis
- **消息队列**：RabbitMQ
- **前端模板**：Thymeleaf
- **容器化**：Docker
- **AI组件**：OpenAI SDK

## 系统架构

```
前端层 (Thymeleaf模板)
    ↓
应用层 (Controller + Service)
    ↓
业务处理层 (ChatlogClient + AIClient + DataProcessor)
    ↓
数据层 (MySQL + Redis + RabbitMQ)
    ↓
外部服务 (Chatlog API + AI API)
```

## 快速开始

### 环境要求

- JDK 8+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.8+
- Maven 3.6+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd wechat-report
   ```

2. **配置数据库**
   ```sql
   # 创建数据库
   CREATE DATABASE wechat_report CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   
   # 执行初始化脚本
   mysql -u root -p wechat_report < src/main/resources/sql/init.sql
   ```

3. **修改配置文件**
   ```yaml
   # src/main/resources/application.yml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/wechat_report?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
       username: your_username
       password: your_password
   ```

4. **编译运行**
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

5. **访问应用**
   ```
   http://localhost:8080
   ```

## 使用说明

### 初始配置

1. **访问配置页面**：`http://localhost:8080/config/`

2. **配置AI服务**：
   - 模型：选择AI模型（如GPT-3.5 Turbo）
   - 服务地址：AI服务API地址
   - API密钥：有效的API密钥
   - 其他参数：温度、最大令牌数等

3. **配置Chatlog服务**：
   - 服务地址：Chatlog服务地址（如 http://127.0.0.1:5030）
   - 超时时间：请求超时设置

### 分析聊天

1. **选择聊天对象**：从下拉列表中选择要分析的聊天会话

2. **选择日期**：选择要分析的具体日期

3. **开始分析**：点击"开始分析"按钮

4. **查看报告**：系统将自动跳转到分析报告页面

### 查看历史

- 访问 `http://localhost:8080/chat-analysis/history` 查看所有历史分析记录
- 支持分页浏览和状态筛选

## API接口

### 聊天分析接口

- `GET /chat-analysis/` - 分析页面
- `POST /chat-analysis/analyze` - 执行分析
- `GET /chat-analysis/report/{reportId}` - 查看报告
- `GET /chat-analysis/history` - 分析历史

### 配置管理接口

- `GET /config/` - 配置页面
- `POST /config/ai-service` - 更新AI配置
- `POST /config/chatlog-service` - 更新Chatlog配置

## 数据库设计

### 主要表结构

- `chat_sessions` - 聊天会话表
- `chat_messages` - 聊天消息表
- `analysis_reports` - 分析报告表
- `service_configs` - 服务配置表

## 部署说明

### Docker部署

1. **构建镜像**
   ```bash
   mvn clean package
   docker build -t wechat-report:latest .
   ```

2. **运行容器**
   ```bash
   docker run -d \
     --name wechat-report \
     -p 8080:8080 \
     -e SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/wechat_report \
     -e SPRING_DATASOURCE_USERNAME=username \
     -e SPRING_DATASOURCE_PASSWORD=password \
     wechat-report:latest
   ```

### 生产环境配置

1. **修改配置文件**：使用生产环境的数据库、Redis等配置
2. **优化JVM参数**：根据服务器配置调整内存参数
3. **配置日志**：设置合适的日志级别和输出位置
4. **监控告警**：配置应用监控和告警机制

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查数据库服务是否启动
   - 验证连接参数是否正确
   - 确认防火墙设置

2. **AI服务调用失败**
   - 检查API密钥是否有效
   - 验证网络连接是否正常
   - 确认服务地址是否正确

3. **Chatlog服务连接失败**
   - 确认Chatlog服务是否运行
   - 检查服务地址配置
   - 验证网络连通性

### 日志查看

```bash
# 查看应用日志
tail -f logs/wechat-report.log

# 查看Spring Boot启动日志
docker logs wechat-report
```

## 开发指南

### 项目结构

```
src/
├── main/
│   ├── java/com/wechat/dailyreport/
│   │   ├── client/          # 外部服务客户端
│   │   ├── config/          # 配置类
│   │   ├── constant/        # 常量定义
│   │   ├── controller/      # 控制器
│   │   ├── dto/             # 数据传输对象
│   │   ├── entity/          # 实体类
│   │   ├── exception/       # 异常处理
│   │   ├── listener/        # 事件监听器
│   │   ├── mapper/          # 数据访问接口
│   │   └── service/         # 业务服务
│   └── resources/
│       ├── sql/             # 数据库脚本
│       ├── templates/       # 前端模板
│       └── application.yml  # 配置文件
```

### 代码规范

- 使用Java 8兼容语法
- 遵循阿里巴巴Java开发规范
- 所有公共方法需要添加注释
- 异常处理要完善

### 测试

```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify
```

## 版本历史

- **v1.0.0** - 初始版本，实现基础功能

## 许可证

MIT License

## 联系方式

如有问题或建议，请联系项目维护者。