# 本地开发

## 环境准备

- JDK 25
- Maven 3.9+
- MySQL 8+
- Redis
- Nacos
- Kafka（审计事件消费启用时需要）

建议使用独立的本地数据库、Nacos namespace 和测试凭证，不要复用生产环境配置。

## 构建与测试

在仓库根目录执行：

```bash
mvn clean verify
```

只验证业务模块及其依赖：

```bash
mvn -pl g2rain-basis-biz -am test
```

## 启动

```bash
mvn -pl g2rain-basis-startup -am spring-boot:run
```

默认端口 `8080`，默认 profile `dev`。常用覆盖方式：

```powershell
$env:SERVER_PORT = '8080'
$env:SPRING_PROFILES_ACTIVE = 'dev'
$env:NACOS_SERVER_ADDR = '127.0.0.1:8848'
mvn -pl g2rain-basis-startup -am spring-boot:run
```

## 提交前检查

```bash
mvn clean verify
```

如果修改了 Maven 模块、包边界、运行命令、配置、代码生成或长期设计，应在同一提交更新对应文档或 ADR。Agent 在生成或审查文档时应结合源码、POM 和 Git Diff 检查一致性。

## 调试入口

- 健康检查：`GET /actuator/health`
- OpenAPI：由 `g2rain-starter-spring-doc` 提供，实际地址受平台配置影响。
- 服务注册：检查 Nacos 中 `g2rain-basis`、group `g2rain` 和当前 namespace。
- 数据访问：MyBatis mapper 位于 `g2rain-basis-biz/src/main/resources/mybatis/mapper`。
