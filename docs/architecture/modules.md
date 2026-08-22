# 模块职责

## `g2rain-basis-api`

领域契约模块，供当前服务实现和其他 g2rain 服务共同依赖。

| 目录 | 职责 |
| --- | --- |
| `api` | 可复用的服务接口契约。 |
| `dto` | 请求和命令对象。 |
| `vo` | 对外返回对象。 |
| `enums` | 跨模块共享的领域枚举。 |
| `idp` | 身份提供方相关契约和模型。 |

约束：

- 不得依赖 `g2rain-basis-biz` 或 `g2rain-basis-startup`。
- 不得引用 Controller、Service、DAO、配置类或持久化对象。
- 契约变更应考虑 IAM、网关和其他调用方的兼容性。
- 受信内部接口仍须显式标注并隐藏，不以 `/internal` 路径代替安全边界。

## `g2rain-basis-biz`

领域实现模块，承载接口适配、领域服务、数据访问和外部服务协作。

| 目录 | 职责 |
| --- | --- |
| `controller` | HTTP 接口适配、参数校验和权限入口。 |
| `service` | 领域用例和业务规则。 |
| `dao` | MyBatis 数据访问和持久化对象。 |
| `converter` | DTO、VO 与持久化对象转换。 |
| `client` | 对其他平台服务的调用适配。 |
| `config` | 业务模块所需的框架与集成配置。 |
| `audit` | 审计事件消费和处理。 |
| `model` | 仅在业务实现内部使用的领域辅助模型。 |

约束：

- 依赖 `g2rain-basis-api` 实现契约，不得依赖 `g2rain-basis-startup`。
- Controller 负责协议适配，不应承载事务性领域规则。
- Service 不应依赖具体 Controller。
- 敏感凭证必须在 Service 出口脱敏，不能只依赖前端隐藏或 Swagger 配置。

## `g2rain-basis-startup`

运行时组装模块，生成最终可运行服务：

- 提供 `com.g2rain.basis.Application` 启动类。
- 引入 `g2rain-basis-biz` 并完成 Spring Boot 组件组装。
- 提供端口、profile、Nacos、Redis、Kafka、MyBatis 和观测配置。
- 通过 Jib 构建运行镜像。

该模块不新增供业务模块反向依赖的领域类型，也不承载业务规则。

## 根工程

根 `pom.xml` 统一维护 Java、Spring Boot、Spring Cloud、内部组件版本、公共插件和三个 Maven 模块。根目录 `codegen.properties` 仅用于代码生成，修改时应遵循[代码生成说明](../development/code-generation.md)。
