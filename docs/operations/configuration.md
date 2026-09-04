# 配置说明

运行配置入口为 `g2rain-basis-startup/src/main/resources/application.yml`。默认 profile 为 `dev`，并通过 Nacos 导入与应用同名的配置。

## 基础配置

| 配置 | 默认值 | 说明 |
| --- | --- | --- |
| `SERVER_PORT` | `8080` | HTTP 服务端口。 |
| `SPRING_PROFILES_ACTIVE` | `dev` | 当前 Spring profile。 |
| `NACOS_SERVER_ADDR` | `127.0.0.1:8848` | Nacos 服务地址。 |
| `SPRING_CLOUD_NACOS_DISCOVERY_USERNAME` / `PASSWORD` | 本地默认值 | 注册中心凭证；共享和生产环境必须覆盖。 |
| `SPRING_CLOUD_NACOS_CONFIG_USERNAME` / `PASSWORD` | 本地默认值 | 配置中心凭证；共享和生产环境必须覆盖。 |
| `SPRING_CLOUD_NACOS_DISCOVERY_NAMESPACE` | `dev` | 注册中心 namespace。 |
| `SPRING_CLOUD_NACOS_CONFIG_NAMESPACE` | `dev` | 配置中心 namespace。 |
| `REDIS_HOST` | `127.0.0.1` | Redis 地址。 |
| `REDIS_PORT` | `6379` | Redis 端口。 |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `127.0.0.1:9092` | Kafka 地址。 |

Nacos discovery group 固定为 `g2rain`，配置 group 默认为应用名 `g2rain-basis`。

## 审计事件

| 配置 | 默认值 | 说明 |
| --- | --- | --- |
| `g2rain.audit.event.enabled` | `false` | 是否消费网关审计事件。 |
| `g2rain.audit.event.topic` | `gateway.exchange.event` | 审计 Topic。 |
| `g2rain.audit.event.group-id` | `g2rain-basis-audit` | 消费组。 |

启用后必须保证 Kafka 可用，并确认 Topic、消息格式和消费组与网关配置一致。

## IdP 同步安全阈值

`g2rain.basis.idp-sync.safety` 控制成员删除比例、部门停用比例以及不完整/空快照的阻断策略。调整阈值属于高风险变更，应结合真实租户规模、同步测试和回滚方案审查。

## 敏感信息

- Nacos、数据库、Redis、Kafka 和 IdP 凭证应由环境变量、Secret 或配置中心提供。
- 不得把生产密码、PermanentCode、访问令牌或密钥写入仓库。
- `application-dev.yml` 和 `codegen.properties` 中的默认值只适用于隔离的本地开发环境；接入共享环境前必须覆盖。
- 当前仓库仍保留开发默认凭证，迁移要求见[架构差异](../architecture/deviations.md)。
- 日志、审计记录和 API 响应不得输出可用凭证。
