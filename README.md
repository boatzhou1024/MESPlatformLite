# MES Platform（Spring Boot 教学项目）

## 1. 项目介绍

本项目是一个面向制造执行场景的 MES 后端示例系统，基于 Spring Boot 3 构建，覆盖了常见企业业务能力：

- 用户登录认证（JWT）
- 设备管理（增删改查 + 条件筛选）
- 工单管理（创建、分配、进度更新、状态流转）
- 报表统计（按日期与设备类型聚合）
- AI 辅助诊断（支持缓存与 Mock 模式）

该项目同时集成了 Redis 缓存、RabbitMQ 异步通知、Swagger 接口文档，适合作为学习与面试演示工程。

## 2. 技术栈与依赖

- Java 17
- Spring Boot 3.0.2
- Spring Security + JWT（jjwt 0.12.3）
- MyBatis-Plus 3.5.5
- MySQL 8.x
- Redis
- RabbitMQ
- Springdoc OpenAPI（Swagger UI）
- Maven 3.9+

## 3. 核心模块说明

- `Auth`：登录认证、Token 签发与权限识别
- `Device`：设备主数据管理，支持按状态/类型/关键字查询
- `WorkOrder`：工单生命周期管理，含乐观锁并发控制（`version`）
- `Report`：工单统计报表查询
- `AI Diagnosis`：设备异常诊断，支持缓存与外部 AI 调用
- `MQ`：工单事件异步投递与消费

## 4. 项目结构

```text
src/main/java/com/boatzhou/mes
├─ common      # 统一返回体、错误码、异常处理
├─ config      # 安全、Swagger、MyBatis、MQ、AI配置
├─ controller  # 接口层
├─ dto         # 请求/响应数据传输对象
├─ entity      # 实体模型
├─ mapper      # MyBatis-Plus 数据访问层
├─ mq          # 消息生产与消费
├─ security    # JWT 与 Security 组件
├─ service     # 业务接口与实现
└─ MesApplication.java

src/main/resources
├─ application.yml  # 核心配置
├─ schema.sql       # 建表脚本
└─ data.sql         # 初始化数据
```

## 5. 项目环境准备

启动项目前请准备以下环境：

- JDK：17（`java -version`）
- Maven：3.9+（`mvn -v`）
- MySQL：8.x（建议字符集 `utf8mb4`）
- Redis：6.x/7.x（默认端口 6379）
- RabbitMQ：3.x（默认端口 5672）

## 6. 项目搭建过程

### 步骤 1：获取代码

```bash
git clone <your-repo-url>
cd mes
```

### 步骤 2：创建数据库

```sql
CREATE DATABASE IF NOT EXISTS mes_platform
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;
```

### 步骤 3：导入表结构和初始化数据

```bash
mysql -uroot -p mes_platform < src/main/resources/schema.sql
mysql -uroot -p mes_platform < src/main/resources/data.sql
```

### 步骤 4：修改本地配置

编辑 `src/main/resources/application.yml`，按本机实际环境调整：

- MySQL：`spring.datasource.url/username/password`
- Redis：`spring.data.redis.host/port/password`
- RabbitMQ：`spring.rabbitmq.host/port/username/password`
- JWT：`jwt.secret`（建议替换为你自己的安全密钥）
- AI：`ai.endpoint`、`ai.api-key`、`ai.mock-enabled`

### 步骤 5：启动中间件

- 确保 MySQL、Redis、RabbitMQ 都处于可连接状态

## 7. 如何运行项目

### 方式 A：IDE 运行

- 打开 `MesApplication`
- 直接运行 `main` 方法

### 方式 B：Maven 命令运行

```bash
mvn spring-boot:run
```

启动成功后访问：

- 应用首页：`http://localhost:8080`
- Swagger 文档：`http://localhost:8080/swagger-ui.html`

默认测试账号：

- 管理员：`admin / 123456`
- 运维员：`operator / 123456`

## 8. 项目构建（Build）

### 仅编译

```bash
mvn clean compile
```

### 运行测试

```bash
mvn test
```

### 打包（跳过测试）

```bash
mvn clean package -DskipTests
```

产物位置：

- `target/mes-0.0.1-SNAPSHOT.jar`

运行打包文件：

```bash
java -jar target/mes-0.0.1-SNAPSHOT.jar
```

## 9. 接口文档与联调建议

- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI：`http://localhost:8080/v3/api-docs`

建议联调顺序：

1. 登录获取 Token
2. 设备模块 CRUD
3. 工单模块（重点验证 `version` 冲突场景）
4. 报表模块
5. AI 诊断模块

## 10. 常见问题

### 1) 启动后数据库无表

本项目默认建议手动执行 `schema.sql` 与 `data.sql`。如果希望自动初始化，可按需补充 `spring.sql.init.mode=always` 等配置。

### 2) 工单更新返回 409

这是乐观锁冲突（`version` 不一致），属于并发保护机制。前端应先刷新详情再重新提交。

### 3) 报表或设备接口访问被拒绝

- 未带 Token 会触发 `401`
- 非管理员访问管理员接口会触发 `403`

## 11. 补充文档

- 快速帮助：`HELP.md`
- 学习与面试讲解：`项目学习与面试讲解手册.md`
- Vue 对接手册：`Vue对接接口与前端页面提示词手册.md`
