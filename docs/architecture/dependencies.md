# 模块与依赖边界

## 允许的方向

```text
startup → biz → api
```

| 来源 | 允许依赖 | 禁止依赖 |
| --- | --- | --- |
| `g2rain-basis-api` | 通用模型、Spring Web/Validation 契约 | `g2rain-basis-biz`、`g2rain-basis-startup`、实现包 |
| `g2rain-basis-biz` | `g2rain-basis-api`、持久化与平台 Starter、外部服务 API | `g2rain-basis-startup` |
| `g2rain-basis-startup` | `g2rain-basis-biz`、Spring Boot 运行组件 | 供下层模块反向依赖的领域实现 |

## 包级边界

```text
Controller → Service → DAO
                 └──→ Client
```

- Controller 负责 HTTP 协议、校验、权限入口和结果转换。
- Service 负责事务、幂等、状态迁移和领域安全约束。
- DAO 只负责持久化，不反向调用 Service 或 Controller。
- Converter 不发起数据库或网络调用。
- API 模块只发布稳定契约，不暴露 Biz 内部模型。

## Agent 检查清单

维护或生成项目文档时，Agent 应读取当前源码、POM、`docs/project.yaml` 和 Git Diff，动态检查：

- 根 POM 必须声明三个标准模块且不能出现未记录模块。
- API 不得依赖 Biz 或 Startup。
- Biz 必须依赖 API，且不得依赖 Startup。
- Startup 必须依赖 Biz。
- API 源码不得导入 Controller、Service、DAO、Config 或 Biz 实现包。
- 必需文档及相对链接必须有效。
- 新增模块、依赖或实现包是否已经同步到架构文档。

这些检查由 Agent 在任务期间执行并报告结果，不要求业务仓库维护专用验证脚本。

## 新模块规则

新增 Maven 模块时，同一提交必须更新 `docs/project.yaml`、本页和[模块职责](modules.md)。引入新的长期架构边界时，还应增加 ADR。
