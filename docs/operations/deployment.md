# 构建与部署

## 构建可执行产物

```bash
mvn clean verify
```

可执行 Spring Boot 产物由 `g2rain-basis-startup` 模块生成。

## 本地构建镜像

仓库通过 Jib 构建镜像，`build.sh` 会先安装全部模块，再在 Startup 模块执行 `jib:dockerBuild`：

```bash
./build.sh 1.0.0
```

未提供标签时默认使用 `latest`。目标镜像名为：

```text
g2rain/g2rain-basis:<tag>
```

基础镜像为 `eclipse-temurin:25-jre`，主类为 `com.g2rain.basis.Application`。

## 部署前检查

- MySQL 数据库结构已升级并与当前代码匹配。
- Nacos discovery/config namespace 和凭证正确。
- Redis 可达且数据隔离符合环境要求。
- 启用审计事件时 Kafka Topic 已建立。
- 所有敏感配置由部署环境注入。
- `mvn clean verify` 已通过，Agent 已核对架构声明、文档和当前源码的一致性。
- 企业微信授权等跨项目变更已按实施顺序升级 `g2rain-basis-api` 和调用方。

## 运行观测

Actuator 暴露 `health` 和 `info`。生产环境应通过网关、网络策略或管理端口限制访问范围，不要无条件公开更多管理端点。
