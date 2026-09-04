# 测试策略

| 层级 | 重点 |
| --- | --- |
| Domain/工具 | 密码哈希、状态转换、IdP 快照安全阈值和输入边界 |
| Service | 租户开通、组织隔离、幂等、事务、角色权限、IdP 绑定/同步和凭证脱敏 |
| DAO/Mapper | SQL、分页、逻辑删除、函数索引、乐观锁和数据隔离 |
| API/Controller | 参数校验、路由、结果、受信服务身份和越权拒绝 |
| 集成 | Basis→IAM、Basis→Department、缓存失效、Kafka 审计和失败补偿 |
| Startup | Bean、配置、Mapper 扫描、Profile、端口和健康检查 |

高风险流程最低覆盖：重复租户开通、跨机构请求、同步空/残缺快照、删除比例阈值、重复 IdP 主体、企业授权撤销、静态令牌只存摘要、密码更新、跨服务超时与部分成功。

```bash
mvn clean verify
mvn -pl g2rain-basis-biz -am test
```

现有测试以 Mockito 单元测试为主。它们不能证明真实 MySQL 约束、MyBatis 映射、数据隔离 AOP、跨服务超时、Kafka/Redis 交互或 Spring Boot 完整组装行为。
