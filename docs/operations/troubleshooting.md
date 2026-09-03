# 故障排查

## 服务无法注册或读取配置

检查：

- `NACOS_SERVER_ADDR` 是否可达。
- discovery 与 config 的 username、password、namespace 是否一致。
- discovery group 是否为 `g2rain`。
- Nacos 是否存在 group 为 `g2rain-basis` 的 `g2rain-basis.yml`。

## 数据库连接或 Mapper 失败

- 确认数据源配置已由 dev profile 或 Nacos 注入。
- 确认数据库结构与 `scripts/g2rain-basis.sql` 及当前分支一致。
- Mapper 应位于 `classpath:/mybatis/mapper/*.xml` 可扫描路径。
- 代码生成后检查表名、字段类型和生成的 Mapper，而不是直接开启全局覆盖重新生成。

## Redis 相关能力失败

- 检查 `REDIS_HOST`、`REDIS_PORT` 和密码配置。
- 检查缓存同步、组织邀请等能力使用的 key 和环境是否隔离。
- 不要用清空共享 Redis 的方式处理单个环境问题。

## 审计事件没有入库

- 确认 `g2rain.audit.event.enabled=true`。
- 检查 Kafka 地址、`gateway.exchange.event` Topic 和消费组。
- 核对网关实际发送的消息结构与当前消费者兼容。

## IAM 调用内部接口失败

- 确认 IAM 使用的 `g2rain-basis-api` 版本与当前服务一致。
- 检查 Feign、网关或服务发现路径是否与 Controller 契约一致。
- 内部接口仍需要受信服务身份和明确安全策略，不能只依赖 `/internal` 前缀。

## 企业微信三方登录被拒绝

- 检查企业应用授权记录是否为 `ACTIVE`。
- 检查 SuiteID、企业标识和 AgentID 是否匹配。
- 已取消授权、未安装 Suite 或 AgentID 不一致均应拒绝登录。
- 详细边界见[企业微信授权设计](../design/wechat-work-authorization.md)。

## 文档验证失败

让 Agent 结合当前源码、POM、`docs/project.yaml` 和 Git Diff 检查缺失文件、失效链接、模块差异与非法依赖。有意调整架构时，应同步更新项目元数据、文档和 ADR，不能只修改 README 摘要。
