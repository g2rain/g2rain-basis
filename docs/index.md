# g2rain-basis 文档

这里是 `g2rain-basis` 的项目级技术文档，也是根 README、架构验证和后续文档站点的事实来源。

项目当前按组织级 [`java-domain-service 1.0.0`](https://github.com/g2rain/g2rain/tree/architecture-v1.0.0/docs/architecture/profiles/java-domain-service) 进行接入准备，状态为 `planned`。中央 Profile 管理同类服务公共规则，本目录维护 Basis 的领域设计、当前实现和[架构差异](architecture/deviations.md)。

## 项目定位

`g2rain-basis` 是 g2rain 平台的核心主数据与权限治理服务。它维护组织、用户、通行证、应用、资源、角色、权限和外部身份关系，并向 IAM、网关、管理端及其他平台服务提供领域能力。

## 阅读路径

### 了解架构

- [架构总览](architecture/overview.md)
- [模块职责](architecture/modules.md)
- [依赖边界](architecture/dependencies.md)
- [核心运行流程](architecture/runtime-flows.md)
- [架构差异与接入项](architecture/deviations.md)

### 开发与验证

- [本地开发](development/local-development.md)
- [代码规范](development/code-conventions.md)
- [API 设计规范](development/api-conventions.md)
- [数据库与数据模型](development/database-conventions.md)
- [测试策略](development/testing.md)
- [完成定义](development/definition-of-done.md)
- [CRUD 代码生成](development/code-generation.md)
- [需求设计与验收模板](requirements/README.md)
- [架构决策记录](decisions/README.md)

### 运行与维护

- [配置说明](operations/configuration.md)
- [构建与部署](operations/deployment.md)
- [故障排查](operations/troubleshooting.md)
- [可观测性](operations/observability.md)
- [安全、凭证与租户边界](security/security-boundaries.md)

### 专题设计

- [企业微信三方应用授权与扫码登录](design/wechat-work-authorization.md)
- [受信服务 API 与跨服务协作](design/trusted-service-collaboration.md)

### 社区

- [社区、贡献、联系方式与许可证](community.md)

## 文档维护约定

- README 只保留项目定位、快速开始、架构摘要和文档导航。
- `docs/project.yaml` 保存机器可读取的项目职责、模块和命令声明。
- 架构的“为什么”通过 ADR 或专题设计文档记录。
- 代码正确性由项目测试保障；文档与架构声明的一致性由 Agent 结合源码、POM 和 Git Diff 动态检查。
- 修改模块、依赖方向、启动命令、生成流程或运行配置时，应在同一提交中更新相关文档。
