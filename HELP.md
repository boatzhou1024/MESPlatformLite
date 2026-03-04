# HELP

本文件用于提供项目的快速使用入口。完整说明请优先查看 `README.md`。

## 1. 快速命令

### 本地运行

```bash
mvn spring-boot:run
```

### 编译检查

```bash
mvn clean compile
```

### 运行测试

```bash
mvn test
```

### 打包

```bash
mvn clean package -DskipTests
```

### 运行 Jar

```bash
java -jar target/mes-0.0.1-SNAPSHOT.jar
```

## 2. 关键访问地址

- 服务地址：`http://localhost:8080`
- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

## 3. 默认测试账号

- 管理员：`admin / 123456`
- 运维员：`operator / 123456`

## 4. 常用文档

- 项目主文档：`README.md`
- 学习与面试讲解：`项目学习与面试讲解手册.md`
- Vue 对接手册：`Vue对接接口与前端页面提示词手册.md`

## 5. 官方参考

- Spring Boot: <https://docs.spring.io/spring-boot/docs/3.0.2/reference/htmlsingle/>
- Spring Security: <https://docs.spring.io/spring-security/reference/>
- MyBatis-Plus: <https://baomidou.com/>
- Spring Data Redis: <https://docs.spring.io/spring-data/redis/docs/current/reference/html/>
- Spring AMQP: <https://docs.spring.io/spring-amqp/reference/>
