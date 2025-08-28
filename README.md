# 微信聊天分析器 (wechat-report)

一个基于Spring Boot的微信聊天数据智能分析系统，通过集成外部AI服务和Chatlog服务，实现对微信群聊和私聊数据的深度分析，生成结构化的HTML分析报告。

## 🚀 功能特性

### 核心功能
- **📊 聊天数据分析**：支持群聊和私聊的智能分析
- **📅 日期范围分析**：灵活选择分析时间范围（支持跨日期区间）
- **🤖 AI智能处理**：集成OpenAI等AI服务进行深度内容分析
- **📈 结构化报告**：自动生成详细的HTML格式分析报告
- **🔍 群聊搜索**：支持按群名搜索和选择群聊会话
- **📚 历史记录**：完整的分析历史管理和查看功能
- **⚙️ 配置管理**：灵活的AI服务和Chatlog服务配置界面

### 分析能力
- **消息统计**：消息数量、参与人数、活跃度分析
- **内容分析**：关键词提取、情感分析、话题识别
- **用户行为**：发言频率、互动模式、活跃时段分析
- **数据可视化**：图表展示和趋势分析

## 🛠 技术栈

### 后端技术
- **框架**：Spring Boot 2.7.18
- **语言**：Java 8+
- **构建工具**：Maven 3.6+
- **Web模板**：Thymeleaf
- **JSON处理**：Fastjson 2.0.58
- **AI集成**：OpenAI Java SDK 3.1.2
- **HTTP客户端**：RestTemplate

### 数据存储
- **报告存储**：内存存储（可扩展为数据库）
- **配置存储**：本地配置管理

### 外部服务依赖
- **Chatlog服务**：微信聊天数据源（默认端口: 5030）
- **AI服务**：OpenAI兼容的AI分析服务

## 🏢 系统架构

### 整体架构
```
用户界面 (Thymeleaf 模板)
         │
    └─── Web控制器层
         │
    └─── 业务服务层
         │
    ├─── 数据处理层 (DataProcessor)
    │
    ├─── 外部服务客户端层
    │    ├─── AI服务客户端
    │    └─── Chatlog服务客户端
    │
    └─── 外部服务
         ├─── Chatlog API (微信数据源)
         └─── AI API (OpenAI兼容)
```

### 核心组件
- **ChatAnalysisController**: 聊天分析Web控制器
- **ConfigController**: 系统配置管理控制器
- **ChatAnalysisService**: 核心分析业务服务
- **DataProcessorService**: 数据预处理服务
- **AIService**: AI服务集成封装
- **ChatlogService**: Chatlog服务集成封装

## 🚀 快速开始

### 环境要求
- **JDK**: 8+
- **Maven**: 3.6+
- **Chatlog服务**: 需要在本地或远程部署Chatlog服务
- **AI服务**: OpenAI API Key或兼容的AI服务,默认使用的文心一言,可自主修改配置

### Chatlog 服务配置
- **Chatlog 项目**：sjzar/chatlog - 聊天记录工具，轻松使用自己的聊天数据
- **安装方式**：go install github.com/sjzar/chatlog@latest 或从 Releases 下载
- **启动服务**：运行 chatlog 并选择"开启 HTTP 服务"，默认地址为 http://127.0.0.1:5030
- **基础 URL**：在应用中配置为 Chatlog 服务的访问地址（如：http://127.0.0.1:5030）
- **确保 Chatlog 服务正常运行并可访问**
- **推荐WX版本**: Windows 4.0.3.36, Mac 4.0.3.80 

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd wechat-report
   ```

2. **配置应用**
   编辑 `src/main/resources/application.yml`：
   ```yaml
   server:
     port: 8080
   
   spring:
     chatlog:
       default:
         base-url: http://127.0.0.1:5030  # 修改为Chatlog服务地址
         timeout: 30000
   ```

3. **编译运行**
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

4. **访问应用**
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

## 🚀 API接口

### 聊天分析接口
- `GET /chat-analysis/` - 分析页面
- `POST /chat-analysis/analyze` - 执行分析（支持群聊和私聊）
- `GET /chat-analysis/search-groups` - 搜索群聊会话
- `GET /chat-analysis/report/{reportId}` - 查看分析报告
- `GET /chat-analysis/history` - 分析历史记录

### 配置管理接口
- `GET /config/` - 系统配置页面
- `POST /config/ai-service` - 更新AI服务配置
- `POST /config/chatlog-service` - 更新Chatlog服务配置

### 首页接口
- `GET /` - 系统首页

## 🛠 开发指南

### 项目结构

```
src/
├── main/
│   ├── java/com/wechat/dailyreport/
│   │   ├── client/                 # 外部服务客户端
│   │   │   ├── AIServiceClient.java   # AI服务客户端
│   │   │   └── ChatlogClient.java     # Chatlog服务客户端
│   │   ├── config/                 # 配置类
│   │   │   ├── AiConfig.java          # AI配置
│   │   │   └── WebConfig.java         # Web配置
│   │   ├── constant/               # 常量定义
│   │   │   └── AppConstants.java      # 应用常量
│   │   ├── controller/             # 控制器层
│   │   │   ├── ChatAnalysisController.java  # 聊天分析控制器
│   │   │   ├── ConfigController.java        # 配置管理控制器
│   │   │   └── HomeController.java          # 首页控制器
│   │   ├── dto/                    # 数据传输对象
│   │   │   ├── config/                # 配置相关DTO
│   │   │   ├── request/               # 请求DTO
│   │   │   └── response/              # 响应DTO
│   │   ├── exception/              # 异常处理
│   │   │   └── GlobalExceptionHandler.java  # 全局异常处理器
│   │   ├── listener/               # 事件监听器
│   │   │   └── ApplicationStartupListener.java  # 应用启动监听器
│   │   ├── service/                # 业务服务层
│   │   │   ├── AIConfigService.java          # AI配置服务
│   │   │   ├── AIService.java               # AI服务
│   │   │   ├── ChatAnalysisService.java     # 聊天分析服务
│   │   │   ├── ChatlogConfigService.java    # Chatlog配置服务
│   │   │   ├── ChatlogService.java          # Chatlog服务
│   │   │   └── DataProcessorService.java    # 数据处理服务
│   │   └── WechatDailyReportApplication.java  # 应用主类
│   └── resources/
│       ├── templates/              # 前端模板
│       │   ├── analysis.html          # 分析页面
│       │   ├── config.html            # 配置页面
│       │   ├── error.html             # 错误页面
│       │   ├── history.html           # 历史页面
│       │   ├── layout.html            # 布局模板
│       │   ├── processing.html        # 处理中页面
│       │   └── report.html            # 报告页面
│       └── application.yml         # 配置文件
└── test/
    ├── java/com/wechat/dailyreport/
    │   └── WechatDailyReportApplicationTests.java  # 主测试类
    └── resources/
        └── application-test.yml    # 测试配置
```

### 技术特点
- **轻量级架构**: 去除了数据库、缓存、消息队列等依赖，保持系统简单
- **内存存储**: 使用ConcurrentHashMap存储分析报告
- **RestTemplate**: 使用Spring原生HTTP客户端进行外部服务调用
- **异步处理**: 支持分析任务的异步执行和状态跟踪
### 代码规范
- 使用Java 8兼容语法
- 遵循阿里巴巴Java开发规范
- 所有公共方法需要添加注释
- 遵循RESTful API设计原则

## 🔧 故障排除

### 常见问题

1. **AI服务调用失败**
   - 检查API密钥是否有效
   - 验证网络连接是否正常
   - 确认服务地址是否正确
   - 检查请求参数是否符合API规范

2. **Chatlog服务连接失败**
   - 确认Chatlog服务是否运行在指定端口（默认5030）
   - 检查服务地址配置（application.yml）
   - 验证网络连通性和防火墙设置

3. **分析失败或无数据**
   - 检查选择的日期范围是否有聊天数据
   - 验证群聊名称或私聊对象是否正确
   - 检查Chatlog服务是否正常返回数据

### 日志查看
```bash
# 查看应用日志
tail -f logs/wechat-report.log

# 查看实时日志
mvn spring-boot:run

# Docker容器日志
docker logs -f wechat-report
```

## 🧪 测试

### 运行测试
```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify

# 生成测试报告
mvn test jacoco:report
```

### 功能测试
1. 访问 `http://localhost:8080` 检查首页
2. 访问 `http://localhost:8080/config/` 测试配置功能
3. 访问 `http://localhost:8080/chat-analysis/` 测试分析功能

## 📝 更新日志

### v1.0.0 (2024-08-28)
- ✅ 完成核心分析功能开发
- ✅ 集成AI服务和Chatlog服务
- ✅ 仅实现群聊分析
- ✅ 支持日期范围选择
- ✅ 完成Web界面和历史管理
- ✅ 实现配置管理功能

## 🔮 未来规划

### v1.1.0 (计划中)
- 🔄 数据库集成（支持持久化存储）
- 📈 添加数据可视化图表
- 🔔 添加定时分析任务
- 📊 增强分析统计功能

### v1.2.0 (计划中)
- 👥 用户管理和权限控制
- 📄 报告导出功能（PDF/Excel）
- 🔍 高级搜索和筛选功能
- ⚙️ 更多自定义配置选项

## 📜 参考文档

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Thymeleaf 模板引擎](https://www.thymeleaf.org/)
- [OpenAI API 文档](https://platform.openai.com/docs/api-reference)
- [Maven 构建工具](https://maven.apache.org/)

## 📞 联系方式

如有问题或建议，请通过以下方式联系项目维护者：

- 💮 **Issue 反馈**: 在GitHub上提交Issue
- 📧 **功能建议**: 提交Feature Request
- 🐛 **Bug报告**: 详细描述问题复现步骤

## 📜 许可证

MIT License - 详情见 [LICENSE](LICENSE) 文件

---

**⭐ 如果这个项目对您有帮助，请给我们一个 Star！**