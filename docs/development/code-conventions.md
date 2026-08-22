# 代码规范

本文根据 `g2rain-basis` 当前工程结构、`.editorconfig`、代表性 API/Controller/Service/DAO、MapStruct Converter 和测试代码整理。

规范优先约束新增和修改代码，不要求仅为格式统一而批量改写无关历史代码。确需改变长期分层或安全边界时，应同步修改架构文档或增加 ADR。

## 1. 基础格式

- 所有文本使用 UTF-8。
- Java 使用 4 个空格缩进，YAML 使用 2 个空格，不使用 Tab。
- 单行不超过 120 个字符；长参数、链式调用和泛型按语义换行。
- 删除行尾空格，文件末尾保留换行。
- import 分组顺序保持“项目/第三方 → JDK”，静态 import 放在最后并单独成组。
- 以根目录 `.editorconfig` 为最终格式基线。

## 2. 命名

| 对象 | 规则 | 示例 |
| --- | --- | --- |
| 类、接口、枚举 | UpperCamelCase | `ApplicationAuthorizationService` |
| 方法、参数、局部变量、字段 | lowerCamelCase | `selectPage`、`applicationId` |
| 常量、枚举值 | UPPER_CASE_WITH_UNDERSCORES | `ACTIVE`、`MAX_RETRY_COUNT` |
| 包名 | 全小写，按职责分包 | `com.g2rain.basis.service.impl` |
| 测试类 | 被测类型 + `Test` | `TenantProvisionServiceImplTest` |
| 测试方法 | `方法_条件_期望`，使用 should 表达结果 | `save_shouldRejectCredentialWrite` |

类型后缀保持明确语义：

- `Api`：可被其他模块或服务复用的接口契约。
- `Controller`：HTTP 协议适配。
- `Service` / `ServiceImpl`：领域用例及实现。
- `Dao` / `Po`：数据访问和持久化对象。
- `Dto`：写入、命令或业务传输对象。
- `SelectDto`：查询条件。
- `Request`：具有独立语义的接口请求。
- `Vo`：接口返回对象。
- `Converter`：对象转换。
- `Config`：框架或集成配置。

避免使用 `Manager`、`Helper`、`Common`、`Util` 等无法说明职责的宽泛名称；已有公共工具除外。

## 3. 模块与包边界

依赖方向固定为：

```text
g2rain-basis-startup → g2rain-basis-biz → g2rain-basis-api
```

- API 模块只放 `api`、`dto`、`vo`、`enums`、`idp` 等稳定契约，不引用 Controller、Service、DAO、Config、Converter 或 PO。
- Biz 模块实现契约和领域规则，不反向依赖 Startup。
- Startup 只负责运行时组装、启动类和基础配置，不承载业务规则。
- Controller → Service → DAO/Client；DAO、Client 和 Converter 不得反向调用 Controller。
- Basis 与 Department 可按内部服务契约直接调用；前端 App 访问平台服务必须经过 Gateway。
- IAM 只能直连 Basis 明确开放的部分受信内部接口。

详细边界见[模块与依赖边界](../architecture/dependencies.md)。

## 4. API 与 Controller

- 可复用契约优先定义在 `g2rain-basis-api`，Controller 实现对应 `Api` 接口。
- Controller 负责路由、参数绑定、校验、权限入口和结果包装，不实现事务性领域规则。
- 请求体使用 `@RequestBody`，输入对象使用 Jakarta Validation 注解，并在入口启用 `@Validated`。
- 返回值统一使用 `Result<T>`；列表、分页和普通结果使用项目已有的 `Result.success`、`Result.successPage` 等工厂方法。
- 路由保持项目现有 snake_case 风格，例如 `/application_authorization`。
- OpenAPI 使用 `@Tag`、`@Operation` 和必要的 `@Parameter` 描述用途，不重复代码字面含义。
- 受信内部接口使用明确的 `/internal/...` 路径，并逐个设置 `@Operation(hidden = true)`；URL 前缀和 Swagger 隐藏不能替代真实访问控制。
- App 和普通外部调用方通过 Gateway 访问；IAM 直连例外只适用于明确公布的受信内部契约。

## 5. Service 与领域逻辑

- 业务校验、状态迁移、幂等、脱敏和跨 DAO 协调放在 Service。
- 涉及多个写操作或状态变化的方法使用 `@Transactional`；只读查询不随意扩大事务范围。
- 新增记录使用平台 `IdGenerator`，不要自行拼接或随机生成业务主键。
- 使用 `BusinessException`、`SystemErrorCode` 和 `Asserts` 表达可预期业务失败，不向接口层泄露底层异常细节。
- 字符串、时间和常用判断优先复用 `Strings`、`Moments` 等项目公共能力。
- 状态值优先由枚举集中校验和规范化，避免在多个 Service 中散落字符串比较。
- 幂等写入应明确业务唯一键；并发更新使用版本字段或现有乐观锁 DAO 方法。
- 方法应保持单一职责。复杂流程拆分为含义明确的私有方法或领域协作对象。

## 6. DAO、PO 与数据库

- DAO 使用 `@Mapper`，方法名表达数据库动作或查询意图。
- PO 放在 `dao.po`，继承项目公共持久化基类并与表字段保持可追踪映射。
- DAO 不处理权限决策、凭证脱敏或领域状态机。
- 使用 `@IgnoreIsolation` 必须有明确的受信场景，调用方负责补足租户、安全和数据范围检查。
- 删除、状态变更和乐观锁行为应与 SQL Mapper 保持一致，并由 Service 测试覆盖。
- 数据库结构变更同步更新 `scripts/g2rain-basis.sql`，涉及兼容或回滚风险时补充设计说明。

## 7. DTO、VO、PO 与转换

- DTO 表达输入和业务传输；VO 只表达允许对外返回的数据；PO 不直接作为接口响应。
- 必填、长度和格式等结构性约束使用 Jakarta Validation；跨字段和状态约束由 Service 校验。
- 常规字段转换使用 MapStruct `Converter`，时间等公共转换复用 `CommonConverter`。
- 凭证清理、权限过滤和其他安全逻辑不能只依赖自动映射，应在 Service 出口显式处理并测试。
- 不为复用一个字段而混用 DTO、VO 和 PO；跨服务契约变化应评估调用方兼容性。

## 8. 依赖注入与配置

- 普通 Spring Bean 沿用项目现有的明确命名注入方式，例如 `@Resource(name = "...")`。
- 可选依赖可以使用带限定名的 Setter 注入，并明确缺失时的失败行为。
- 不通过静态全局变量持有 Spring Bean。
- 环境差异通过环境变量、Nacos 或配置属性表达，不在 Java 代码中硬编码地址、密码和环境名。
- 新配置应有安全默认值、用途说明，并同步更新[配置文档](../operations/configuration.md)。

## 9. 日志与安全

- 使用统一日志框架，不使用 `System.out` 或 `printStackTrace`。
- 日志记录业务标识、状态、耗时和可定位错误的信息，不记录密码、Token、PermanentCode、密文、密钥或完整敏感载荷。
- 管理端列表、分页、详情和标准保存接口不得暴露或接受受限凭证字段。
- 受信内部接口采用最小暴露原则：只返回调用流程所需字段。
- 异常响应不得包含数据库结构、内部地址、凭证或完整第三方响应。

## 10. 注释与文档

- 注释解释“为什么这样做”、边界、兼容性和风险，不逐行翻译代码。
- 公共 API、复杂状态机、安全约束和不直观算法应提供必要 Javadoc 或设计文档。
- TODO 必须说明未完成事项和原因；不要保留无责任边界的占位 TODO。
- 修改模块、调用边界、生成流程、配置或长期设计时，同步更新 `docs`。

## 11. 测试

- 使用 JUnit 5；隔离依赖时使用 Mockito，优先测试 Service 的可观察行为。
- 测试方法采用 `method_shouldExpected` 或 `method_condition_shouldExpected`，同一测试类保持一致。
- 每个关键规则至少覆盖正常、拒绝和边界场景。
- 写操作重点覆盖幂等、重复请求、事务失败、乐观锁和状态迁移。
- 安全能力重点覆盖越权、跨组织数据、凭证写入、凭证返回和日志泄露风险。
- 修复缺陷时先增加能够复现问题的测试，再修改实现。
- 没有 Spring 容器需求时使用普通单元测试，避免不必要的完整上下文启动。

提交前运行：

```bash
mvn clean verify
```

## 12. 代码生成后的要求

- 使用 Crafter 生成标准 CRUD 时保持 `tables.overwrite=false`。
- 生成前提交或暂存现有改动，生成后逐文件审查 Git Diff。
- 生成代码必须继续遵守本文的分层、验证、事务、安全和测试要求。
- 领域状态机、幂等、权限、凭证脱敏和内部接口不能被塞入通用生成模板或简单 CRUD。

详细步骤见[CRUD 代码生成](code-generation.md)。

## 13. Agent 审查清单

Agent 在生成或审查代码时，应结合当前源码和 Git Diff 检查：

- 新文件是否放在正确模块和包中。
- 依赖方向、调用边界和受信接口范围是否被破坏。
- Controller 是否只做协议适配，领域规则是否位于 Service。
- DTO/VO/PO 是否混用，验证和转换是否位于正确层次。
- 写操作是否需要事务、幂等或乐观锁。
- 是否存在凭证、Token、个人信息或跨组织数据泄露。
- 新增行为是否有对应测试，相关文档是否同步更新。

Agent 直接执行上述动态检查并报告结果，不要求仓库新增专用验证脚本。
