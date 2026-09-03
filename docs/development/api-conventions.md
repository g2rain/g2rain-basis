# API 设计规范

## 调用边界

- App 和普通外部调用方携带 IAM Token，经 Gateway 调用公开接口。
- IAM 可以直连明确发布的身份解析、登录、令牌上下文和企业应用授权等受信契约。
- Basis 可直连 IAM 的租户校验、IdP 快照能力，并通过 `g2rain-department-api` 调用 Department 的受信同步契约。
- `/internal`、内网部署和 OpenAPI `hidden = true` 都不能替代认证、服务身份和授权。

## 契约规则

- 可复用契约位于 `g2rain-basis-api`，Controller 实现接口，返回统一的 `Result<T>`。
- 路径沿用项目现有 snake_case；新增路径保持资源和动作语义一致。
- API 模块默认发布查询契约。同步写命令必须说明唯一调用方、权限、事务、幂等、重试、超时、兼容和回滚，并登记到[架构差异](../architecture/deviations.md)。
- Controller 只负责路由、绑定、校验、权限入口和结果包装；领域状态与跨 DAO 协调放在 Service。
- DTO 使用 Jakarta Validation 表达结构约束；跨字段、状态和租户规则由 Service 校验。
- VO 不返回密码、原始 Token、Token 哈希、可用 IdP 凭证或调用方不需要的个人信息。

修改公共契约时，必须同时评估 IAM、Gateway、Department、管理端以及其他依赖 `g2rain-basis-api` 的服务，并记录兼容和发布顺序。
