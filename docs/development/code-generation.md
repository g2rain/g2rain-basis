# CRUD 代码生成

根 POM 配置了 `g2rain-crafter` 的 `bootstrap` Goal，生成参数位于根目录 `codegen.properties`。

## 使用场景

当新增数据库表并需要生成标准 API、Controller、Service、DAO、DTO、VO、Converter 和 MyBatis Mapper 骨架时使用。生成结果只是基础 CRUD，幂等、状态机、权限、安全和跨聚合事务仍需在领域 Service 中实现。

## 执行步骤

1. 将建表 SQL 应用到生成器连接的本地 `g2rain_basis` 数据库。
2. 临时将 `database.tables` 收敛到需要生成的表。
3. 保持 `tables.overwrite=false`。
4. 在仓库根目录执行生成命令。
5. 检查 Git Diff，补充领域逻辑和测试。
6. 恢复完整表清单并将新表追加进去。

```bash
mvn g2rain-crafter:bootstrap
```

如果本机尚未注册 Maven 插件前缀：

```bash
mvn com.g2rain:g2rain-crafter:bootstrap
```

## 关键配置

| 配置 | 说明 |
| --- | --- |
| `archetype.package` | 生成代码的 Java 基础包。 |
| `database.url` | 用于读取元数据的本地数据库地址。 |
| `database.username` | 读取表结构的用户。 |
| `database.password` | 数据库凭证，不能使用或提交生产密码。 |
| `database.tables` | 逗号分隔的目标表。 |
| `tables.overwrite` | 是否覆盖同名文件，日常生成必须保持 `false`。 |

## 风险

- 不要为了处理单个文件跳过而对整个项目开启覆盖。
- 生成前应提交或暂存已有代码，便于准确审查变更。
- 生成器不会理解领域状态机、凭证脱敏、幂等和内部接口边界。
- `codegen.properties` 当前属于开发配置，任何真实共享密码都应改为本地注入且不得提交。

企业微信授权模型的生成和后续领域扩展要求见[专题设计](../design/wechat-work-authorization.md)。
