# 企业微信三方应用授权与扫码登录跨项目设计

## 1. 文档状态

- 状态：已审核（跨项目主文档）
- 涉及项目：`g2rain-basis`、`g2rain-iam`
- 身份源类型：`IdpType.WECHAT_WORK`
- 接入模式：`INTERNAL`、`THIRD_PARTY`
- 正式上线默认模式：`THIRD_PARTY`

### 1.1 文档分工（与 IAM 子文档去重）

| 文档 | 路径 | 职责 |
|---|---|---|
| **本文（主文档）** | `g2rain-basis/docs/design/wechat-work-authorization.md` | 跨项目边界：数据模型、Basis 内部 API、授权状态机、登录前置条件、凭证存储与脱敏、与钉钉对齐的产品规则 |
| **IAM 子文档** | `g2rain-iam/docs/design/wecom-qr-login.md` | IAM 实现细节：OAuth URL、Adapter、Redis Key、配置项、HTTP 接口、错误码、测试与验收 |

约定：

- 数据模型、Basis API 契约、登录与 Organ 映射规则**以本文为准**；IAM 子文档只引用，不重复展开。
- IAM 子文档变更若影响 Basis 契约，须同步回本文。
- 产品行为与现有钉钉 IdP **保持一致**；企业微信仅在「三方 Suite 安装授权」上额外增加 Basis 表校验，其余链路复用钉钉模式。

## 2. 设计结论

企业微信扫码登录保留两种模式：

| 模式 | `bindMode` | 使用场景 |
|---|---|---|
| 企业内部自建应用 | `INTERNAL` | 内部部署、私有化部署和联调 |
| 服务商三方应用 | `THIRD_PARTY` | 正式 SaaS、多企业租户 |

两种模式分别实现企业微信授权入口、凭证和换票接口，共用以下 IAM/Basis 链路：

```text
企业微信身份
  -> IdpPrincipal(WECHAT_WORK)
  -> passport_idp_binding
  -> passportId
  -> IAM Session
  -> OAuth consent
  -> authorization_code
  -> token
```

服务商三方模式还必须校验登录者所属企业是否已经安装并授权对应 Suite。

## 3. 与现有数据模型的边界

现有表职责保持不变：

| 表 | 职责 |
|---|---|
| `application_authorization` | G2Rain 机构获得 G2Rain 应用的使用授权 |
| `application_suite` | G2Rain 应用之间的归类关系 |
| `application_idp_provision` | IdP 应用标识与 G2Rain 应用的映射 |
| `passport_idp_binding` | Passport 与外部身份主体绑定 |
| `idp_enterprise_organ` | 外部企业与 G2Rain Organ 映射 |

`application_authorization` 不用于存储企业微信安装授权、SuiteID、PermanentCode 或企业微信取消授权状态。

需要新增独立、通用的外部 IdP 企业应用授权模型：

```text
idp_enterprise_application_authorization
```

该模型可以在后续复用于钉钉、飞书等完整三方企业应用授权场景。

## 4. 新表设计

建议表结构：

```sql
CREATE TABLE `idp_enterprise_application_authorization` (
    `id` BIGINT NOT NULL COMMENT
        '主键标识',
    `idp_type` VARCHAR(32) NOT NULL COMMENT
        '身份源类型，与 IdpType 枚举名一致',
    `bind_mode` VARCHAR(32) NOT NULL DEFAULT 'THIRD_PARTY' COMMENT
        '接入形态，与 IdpBindMode 枚举名一致',
    `idp_application_code` VARCHAR(128) NOT NULL COMMENT
        'IdP 应用标识；企业微信为 SuiteID',
    `enterprise_id` VARCHAR(128) NOT NULL COMMENT
        'IdP 返回的外部企业标识',
    `installed_application_id` VARCHAR(128) DEFAULT NULL COMMENT
        '企业安装后的应用标识；企业微信为 AgentID',
    `authorization_status` VARCHAR(32) NOT NULL COMMENT
        '授权状态[PENDING, ACTIVE, REVOKED, EXPIRED]',
    `credential_ciphertext` TEXT DEFAULT NULL COMMENT
        '加密后的永久授权凭证',
    `credential_key_id` VARCHAR(128) DEFAULT NULL COMMENT
        '凭证加密密钥版本标识',
    `authorized_at` TIMESTAMP NULL COMMENT
        '授权时间',
    `revoked_at` TIMESTAMP NULL COMMENT
        '取消授权时间',
    `credential_expire_at` TIMESTAMP NULL COMMENT
        '凭证过期时间；永久凭证可为空',
    `raw_authorization` JSON DEFAULT NULL COMMENT
        '脱敏后的授权元数据',
    `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT
        '创建时间',
    `update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `version` INT NOT NULL DEFAULT 0 COMMENT
        '乐观锁版本',
    `delete_flag` TINYINT NOT NULL DEFAULT 0 COMMENT
        '删除标识[0:未删除, 1:已删除]',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_idp_enterprise_application`
        (`idp_type`, `idp_application_code`, `enterprise_id`),
    KEY `idx_idp_authorization_status`
        (`idp_type`, `authorization_status`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='外部 IdP 企业应用授权记录';
```

为保证企业微信企业标识在授权记录、身份绑定和企业映射中的长度一致，本次同时扩展两个现有字段：

```sql
ALTER TABLE `passport_idp_binding`
    MODIFY COLUMN `corp_id` VARCHAR(128) DEFAULT NULL
        COMMENT '外部身份源企业标识';

ALTER TABLE `idp_enterprise_organ`
    MODIFY COLUMN `enterprise_id` VARCHAR(128) NOT NULL
        COMMENT '外部身份源企业标识';
```

数据库升级脚本必须先执行字段扩容，再写入长度超过 64 的企业标识；回滚脚本不得在未确认存量数据长度的情况下直接缩回 `VARCHAR(64)`。

企业微信字段映射：

| 字段 | 企业微信取值 |
|---|---|
| `idp_type` | `WECHAT_WORK` |
| `bind_mode` | `THIRD_PARTY` |
| `idp_application_code` | SuiteID |
| `enterprise_id` | 授权企业 CorpID 或服务商主体下企业标识 |
| `installed_application_id` | AgentID |
| `authorization_status` | 企业安装授权状态 |
| `credential_ciphertext` | 加密后的 PermanentCode |
| `raw_authorization` | 脱敏后的授权信息 |

`installed_application_id` 的生命周期约束如下：

- `PENDING` 状态允许为空，因为临时授权回调阶段可能尚未取得 AgentID。
- `ACTIVE` 状态必须为非空 AgentID；写入或恢复授权时不满足该约束应拒绝更新。
- 重新授权时允许更新 AgentID。
- 取消授权时保留 AgentID 供审计，但该记录不得再用于登录。
- 三方扫码登录取得企业微信登录信息后，必须校验返回的应用 AgentID 与授权记录一致。

授权状态建议定义为：

```java
public enum IdpApplicationAuthorizationStatus {
    PENDING,
    ACTIVE,
    REVOKED,
    EXPIRED
}
```

## 5. 凭证存储

PermanentCode 不得明文落库。

建议由 IAM 负责凭证加密和解密，Basis 只保存不透明密文：

```text
IAM
  -> 使用配置的密钥或 KMS 加密 PermanentCode
  -> 将 ciphertext + keyId 写入 Basis
```

普通登录校验接口不得返回：

- `credential_ciphertext`
- `credential_key_id`
- `raw_authorization` 中的敏感内容

只有后续明确需要调用企业接口的受信内部接口才可以读取密文。

### 5.1 管理端 CRUD 凭证脱敏（与钉钉一致）

钉钉的 AppSecret / 永久授权等凭证保存在 **IAM 配置**（`DingTalkIamProperties`），Basis **不存在**可列出凭证的管理端 CRUD，因此管理界面天然不会泄露 Secret。

企业微信三方 PermanentCode 因需按企业持久化，写入 Basis 新表；**安全边界必须与钉钉等价**——管理端可读接口不得暴露可用凭证：

| 接口 | 凭证字段处理 |
|---|---|
| `GET .../list`、`GET .../page` | `credentialCiphertext`、`credentialKeyId` **固定为 null**；`rawAuthorization` 仅保留已脱敏元数据或 null |
| `POST .../save`（管理端标准 CRUD） | **不接受**写入 `credentialCiphertext` / `credentialKeyId`；凭证仅由 IAM 经 internal `upsert` 写入 |
| `POST .../internal/.../upsert` | 允许写入密文；`hidden = true`，仅受信服务调用 |
| `POST .../internal/.../resolve` | 不返回任何凭证字段（已实现） |

实现方式（与项目内 `PersonalStaticAccessToken` 的 `maskedToken` 思路一致）：在 `IdpEnterpriseApplicationAuthorizationConverter.po2vo` 或 Service 的 `selectList` / `selectPage` 出口统一清空凭证字段，**不要**依赖 Swagger 隐藏或前端不展示。

日志、审计与对外错误响应同样不得包含密文或 PermanentCode 明文。

以下临时凭证保存在 IAM Redis，不写入 Basis：

| 数据 | 存储 |
|---|---|
| `suite_ticket` | IAM Redis |
| `suite_access_token` | IAM Redis |
| `provider_access_token` | IAM Redis |
| 内部应用 `access_token` | IAM Redis |
| OAuth State | IAM Redis |

以下数据由 Basis 持久化：

| 数据 | 存储 |
|---|---|
| PermanentCode 密文 | 新授权表 |
| 企业安装授权状态 | 新授权表 |
| SuiteID、AgentID | 新授权表 |
| 外部企业与 Organ 映射 | `idp_enterprise_organ` |
| 用户身份绑定 | `passport_idp_binding` |

## 6. Basis API

在 `g2rain-basis-api` 新增：

```text
IdpEnterpriseApplicationAuthorizationApi
IdpEnterpriseApplicationAuthorizationUpsertDto
IdpEnterpriseApplicationAuthorizationRevokeDto
IdpEnterpriseApplicationAuthorizationResolveRequest
IdpEnterpriseApplicationAuthorizationResolveVo
IdpApplicationAuthorizationStatus
```

内部接口建议：

```http
POST /internal/idp/enterprise-application-authorization/upsert
POST /internal/idp/enterprise-application-authorization/revoke
POST /internal/idp/enterprise-application-authorization/resolve
```

以上内部接口不出现在对外 Swagger 文档中。API 接口的每个内部方法必须显式设置：

```java
@Operation(
    summary = "内部接口说明",
    description = "仅供受信服务间调用",
    hidden = true
)
```

不能只依赖 URL 中的 `/internal` 前缀进行文档隔离，也不能遗漏某个方法的 `hidden = true`。生成器产生的标准 CRUD 接口遵循项目现有 Swagger 规范；后续增加的受信内部接口单独隐藏。

### 6.1 写入或恢复授权

```json
{
  "idpType": "WECHAT_WORK",
  "bindMode": "THIRD_PARTY",
  "idpApplicationCode": "suiteId",
  "enterpriseId": "corpId",
  "installedApplicationId": "agentId",
  "authorizationStatus": "ACTIVE",
  "credentialCiphertext": "...",
  "credentialKeyId": "key-version",
  "authorizedAt": "2026-07-24T00:00:00"
}
```

相同 `idpType + idpApplicationCode + enterpriseId` 必须幂等更新。已取消企业重新安装时，将记录恢复为 `ACTIVE` 并更新 AgentID 和凭证密文。

### 6.2 取消授权

```json
{
  "idpType": "WECHAT_WORK",
  "idpApplicationCode": "suiteId",
  "enterpriseId": "corpId",
  "revokedAt": "2026-07-24T00:00:00"
}
```

取消授权采用状态更新，不物理删除历史记录。更新内容至少包括：

```text
authorization_status = REVOKED
credential_ciphertext = NULL
credential_key_id = NULL
credential_expire_at = NULL
revoked_at = 回调时间
```

`installed_application_id` 保留用于审计。取消授权事务提交后，IAM 还必须清理该企业授权关联的凭证缓存。

### 6.3 登录授权查询

请求：

```json
{
  "idpType": "WECHAT_WORK",
  "bindMode": "THIRD_PARTY",
  "idpApplicationCode": "suiteId",
  "enterpriseId": "corpId"
}
```

响应：

```json
{
  "authorizationId": 123,
  "idpType": "WECHAT_WORK",
  "bindMode": "THIRD_PARTY",
  "idpApplicationCode": "suiteId",
  "enterpriseId": "corpId",
  "installedApplicationId": "agentId",
  "authorizationStatus": "ACTIVE"
}
```

该接口不返回任何凭证字段。

该查询使用 `@PostMapping`，通过 `@RequestBody @Validated IdpEnterpriseApplicationAuthorizationResolveRequest` 接收上述 JSON，避免 GET 请求体在网关、Feign 和客户端实现中的兼容性问题。

## 7. Basis 代码改动

### 7.1 `g2rain-basis-api`

- 继续使用已有 `IdpType.WECHAT_WORK`，不新增别名。
- 新增授权状态枚举、DTO、VO 和内部 API。
- 更新 `PassportIdpBindingApi`、DTO 中“当前 IAM 仅支持钉钉”的过期说明。
- 明确 `enterpriseId`、`idpApplicationCode` 的字段语义。

### 7.2 `g2rain-basis-biz`

建议新增：

```text
IdpEnterpriseApplicationAuthorizationPo
IdpEnterpriseApplicationAuthorizationDao
IdpEnterpriseApplicationAuthorizationMapper.xml
IdpEnterpriseApplicationAuthorizationService
IdpEnterpriseApplicationAuthorizationServiceImpl
IdpEnterpriseApplicationAuthorizationController
```

服务要求：

- 授权记录幂等写入。
- 重新授权后恢复 `ACTIVE`。
- 取消授权后更新为 `REVOKED`，并清空持久化凭证字段。
- 使用乐观锁处理并发回调。
- 支持无数据隔离的受信内部查询。
- 登录查询永不返回凭证密文。
- **管理端** `selectList` / `selectPage` 出口清空 `credentialCiphertext`、`credentialKeyId`；标准 `save` 不接受凭证写入（见 §5.1）。
- 业务日志只记录 ID、状态和脱敏企业标识。

### 7.3 数据库和接口注册

- 在 `scripts/g2rain-basis.sql` 增加新表。
- 使用 `g2rain-crafter` 引入的 `g2rain-generator-maven-plugin` 能力生成新表的标准 CRUD，不手写生成器能够产生的基础代码。
- 增加内部接口注册数据。
- 不修改 `application_authorization` 和 `application_suite` 的语义。

#### 7.3.1 CRUD 生成流程

1. 先将 `idp_enterprise_application_authorization` 建表 SQL 应用到代码生成所连接的本地 `g2rain_basis` 数据库。
2. 临时将根目录 `codegen.properties` 的 `database.tables` 收敛为：

   ```properties
   database.tables=idp_enterprise_application_authorization
   tables.overwrite=false
   ```

3. 在 `g2rain-basis` 根目录执行项目 POM 已配置的 Crafter 入口：

   ```shell
   mvn g2rain-crafter:bootstrap
   ```

   也可以使用完整 Maven 坐标，避免本机尚未注册插件前缀：

   ```shell
   mvn com.g2rain:g2rain-crafter:bootstrap
   ```

4. `g2rain-crafter` 负责装配并调用 `g2rain-generator-maven-plugin`，生成符合项目规范的基础 CRUD。
5. 生成完成后恢复 `codegen.properties` 的完整表清单，并将新表追加到清单末尾。
6. 检查生成结果和 Git Diff，确认未覆盖其他表的已有定制代码。

必须保持 `tables.overwrite=false`。如果生成器因为同名文件已存在而跳过文件，应单独处理新表生成结果，不能为了省事对整个项目启用覆盖。

#### 7.3.2 生成产物

以实际生成器输出为准，预期至少包含：

```text
g2rain-basis-api/
  api/IdpEnterpriseApplicationAuthorizationApi.java
  vo/IdpEnterpriseApplicationAuthorizationVo.java
  dto/IdpEnterpriseApplicationAuthorizationSelectDto.java

g2rain-basis-biz/
  controller/IdpEnterpriseApplicationAuthorizationController.java
  service/IdpEnterpriseApplicationAuthorizationService.java
  service/impl/IdpEnterpriseApplicationAuthorizationServiceImpl.java
  dao/IdpEnterpriseApplicationAuthorizationDao.java
  dao/po/IdpEnterpriseApplicationAuthorizationPo.java
  converter/IdpEnterpriseApplicationAuthorizationConverter.java
  dto/IdpEnterpriseApplicationAuthorizationDto.java
  resources/mybatis/mapper/IdpEnterpriseApplicationAuthorizationMapper.xml
```

生成器产生的 CRUD 是基础能力，生成后再补充以下领域逻辑，不能把它们塞进通用 `save` 中：

- 按 `idpType + idpApplicationCode + enterpriseId` 幂等授权。
- 取消授权。
- 取消后重新授权。
- 登录态只读解析。
- 凭证密文的受限读取。
- 乐观锁冲突处理。

这些能力通过独立 Service 方法和同一 API 契约中的隐藏服务间方法提供。标准 CRUD 继续遵循生成器的权限、数据隔离、分页和 Swagger 规范。

API 统一命名为 `IdpEnterpriseApplicationAuthorizationApi`，Controller 统一命名为 `IdpEnterpriseApplicationAuthorizationController`，均不增加 `Internal` 后缀。代码结构以生成器实际产物为准：

- API 接口保留生成器产生的列表、分页等可复用契约。
- 保存、删除、状态更新等生成器直接放在 Controller 的标准 CRUD 方法，不强行迁移到 API 接口。
- 仅将 IAM 需要通过 Feign 复用的 `upsert`、`revoke`、`resolve` 服务间方法加入同一个 API 接口。
- Controller 实现该 API，并为每个服务间扩展方法逐一设置 `@Operation(hidden = true)`。

### 7.4 现有 IdP 能力

Basis 已经具备：

- `IdpType.WECHAT_WORK`
- `passport_idp_binding`
- `idp_enterprise_organ`
- 通用 IdP Passport Resolve
- 通用 IdP 绑定保存

本次不重做上述能力，只增加 `WECHAT_WORK` 用例和文档修正。

当前 `TenantIdpSyncServiceImpl` 只允许 `DINGTALK`。本期只实现扫码登录时继续拒绝 `WECHAT_WORK` 通讯录同步，不能提前声明同步能力已支持。

## 8. IAM 代码改动

IAM 负责：

- `INTERNAL` 企业内部应用扫码。
- `THIRD_PARTY` 服务商扫码。
- Provider Token、Suite Token、内部应用 Token 缓存。
- OAuth State。
- 企业微信 Suite Ticket、安装授权和取消授权回调。
- PermanentCode 加密。
- 调用 Basis 写入、撤销和查询企业授权。
- 将企业微信主体转换为 `IdpPrincipal`。
- 查询或创建 Passport 绑定。
- 创建 IAM Session 并进入现有 OAuth 授权码链路。

IAM 新增的 Basis Feign 客户端依赖统一 API 契约中的隐藏服务间方法：

```text
IdpEnterpriseApplicationAuthorizationClient
```

## 9. 三方扫码登录流程

```text
1. 浏览器进入企业微信服务商 3rd_qrConnect
2. 企业微信回调 IAM，携带 auth_code + state
3. IAM 原子消费 State
4. IAM 获取 provider_access_token
5. IAM 调用 get_login_info
6. IAM 得到企业标识 + OpenUserID
7. IAM 调用 Basis 查询：
   WECHAT_WORK + SuiteID + 企业标识
8. Basis 必须返回 ACTIVE
9. IAM 校验登录信息中的 AgentID 与 Basis 授权记录一致
10. IAM 查询或创建 passport_idp_binding
11. IAM 创建 Session
12. 进入 consent -> authorization_code -> token
```

未授权、已取消授权或 AgentID 不匹配的企业登录不能创建 IAM Session。

三方模式相对钉钉的**唯一额外门禁**是步骤 7–9（Suite 安装授权 + AgentID 校验）；其余步骤与 `DingTalkOAuthService.finishLogin` 相同。

## 10. 登录与 Organ 映射（与钉钉一致）

### 10.1 IdP 扫码登录：不要求 Organ 映射

与钉钉浏览器扫码登录（`DingTalkOAuthService` → `AuthService.authenticateDingTalk(..., true)`）保持一致：

| 环节 | 钉钉 | 企业微信 |
|---|---|---|
| 是否校验 `idp_enterprise_organ` | **否** | **否** |
| 未绑定 Passport | 默认自动开户（`autoProvisionMissingPassport=true`） | 同左 |
| 是否校验 G2Rain `application_authorization` | **否** | **否** |
| 三方企业「已安装应用」校验 | 凭证在 IAM 配置，implicit | 显式查 `idp_enterprise_application_authorization` 为 `ACTIVE` 且 AgentID 一致 |

因此：**已安装 Suite 的企业成员可以完成 IdP 登录并拿到 IAM Session**，即使尚未配置 `idp_enterprise_organ` 或 `application_authorization`。能否进入具体 G2Rain 业务应用，仍由现有 OAuth 客户端、`application_authorization` 及机构成员关系决定，与钉钉一致。

### 10.2 `idp_enterprise_organ` 的使用场景（与钉钉相同）

`idp_enterprise_organ` **不是** IdP 扫码登录的前置条件，仅在以下场景参与（与钉钉共用同一套 Basis 能力）：

- **已登录绑定**：`POST /passport_idp_binding/bind` 时，企业型 IdP 须已有或允许自动建立企业–机构映射（机构管理员可 auto-provision）。
- **租户通讯录同步**：`TenantIdpSyncServiceImpl` 按 `idpType + enterpriseId` 解析目标 Organ（当前仍仅支持 `DINGTALK`；企微同步后续单独立项）。
- **租户开通**：`TenantProvisionServiceImpl` 建立外部企业与 Organ 关联。

企业微信 Suite 安装授权（`idp_enterprise_application_authorization`）成功**不会**自动创建 `idp_enterprise_organ` 或 `application_authorization`。

## 11. 两类授权的关系（数据层）

```text
企业微信安装授权
idp_enterprise_application_authorization
        |
        | 证明外部企业安装了企业微信 Suite
        v
idp_enterprise_organ
        |
        | 将外部企业映射到 G2Rain Organ
        v
application_authorization
```

三者分别回答：

1. 外部企业是否安装并授权了 IdP 应用？
2. 外部企业对应哪个 G2Rain Organ？
3. 该 Organ 可以使用哪些 G2Rain 应用？

企业微信授权成功不自动创建 `application_authorization`，除非产品后续明确要求自动开通 G2Rain 应用。

## 12. 身份映射

### 12.1 三方模式

| IAM 字段 | 取值 |
|---|---|
| `idpType` | `WECHAT_WORK` |
| `bindMode` | `THIRD_PARTY` |
| `idpSubject` | 优先使用企业微信服务商主体下稳定的 `open_userid`；缺失时使用 `lowercaseHex(SHA-256(enterpriseId + "\0" + encryptedUserId))` |
| `idpUserId` | 原始 `open_userid`；缺失时保存企业微信返回的加密 UserID，供审计和主体计算 |
| `corpId` | 企业微信返回的企业标识 |
| `displayName` | 企业微信成员姓名 |
| `idpApplicationCode` | SuiteID |

### 12.2 内部模式

| IAM 字段 | 取值 |
|---|---|
| `idpType` | `WECHAT_WORK` |
| `bindMode` | `INTERNAL` |
| `idpSubject` | `lowercaseHex(SHA-256(corpId + "\0" + userId))` |
| `idpUserId` | UserID |
| `corpId` | 企业 CorpID |
| `displayName` | 企业微信成员姓名 |
| `idpApplicationCode` | AgentID |

## 13. 测试方案

### 13.1 Basis

- `WECHAT_WORK` 绑定查询和保存。
- `passport_idp_binding.corp_id` 与 `idp_enterprise_organ.enterprise_id` 扩容到 `VARCHAR(128)` 后的迁移兼容性。
- 授权首次写入。
- 重复授权幂等更新。
- `PENDING` 允许 AgentID 为空，`ACTIVE` 缺少 AgentID 时拒绝写入。
- 重新授权时 AgentID 更新。
- 取消授权更新为 `REVOKED`、清空凭证字段并保留 AgentID。
- `POST /resolve` 请求体校验和无凭证响应。
- 取消后重新安装恢复。
- 乐观锁冲突。
- SuiteID 相同但企业不同的数据隔离。
- 企业相同但 SuiteID 不同的数据隔离。
- 登录查询不返回凭证密文。
- 管理端 `list` / `page` 不返回 `credentialCiphertext`、`credentialKeyId`。
- 管理端 `save` 拒绝写入凭证字段。
- `application_authorization` 行为不受影响。
- `WECHAT_WORK` 通讯录同步仍明确拒绝。

- IdP 扫码登录不依赖 `idp_enterprise_organ`（与钉钉一致）。

### 13.2 IAM

- 内部和三方授权 URL。
- Provider Token、Suite Token、内部 Token 缓存。
- State 伪造、过期和重复消费。
- 三方授权有效、未授权、取消授权。
- 三方登录返回的 AgentID 与 Basis 授权记录不一致时拒绝登录。
- 相同 UserID 位于不同 CorpID 时生成不同 `idpSubject`。
- 三方模式缺少 `open_userid` 时按企业标识和加密 UserID 生成稳定哈希主体。
- OpenUserID 身份映射。
- 已绑定和自动开户。
- Session、Cookie、Consent、授权码和 Token。
- 钉钉和账号密码登录回归。

### 13.3 跨项目契约

- IAM 使用的新版本 `g2rain-basis-api` 能正常编译。
- Feign 请求字段与 Basis Controller 一致。
- Basis 返回 `ACTIVE` 时允许登录。
- Basis 返回 `REVOKED` 或空记录时拒绝登录。
- 凭证字段不会通过登录查询接口泄露。

## 14. 实施顺序

1. Basis 扩展两个现有企业标识字段，并新增授权表、状态枚举和内部 API。
2. 发布新版 `g2rain-basis-api`。
3. IAM 升级 Basis API 依赖。
4. IAM 接入 Suite 安装、授权变更和取消授权回调。
5. IAM 实现 `THIRD_PARTY` 扫码与 Basis 授权校验。
6. IAM 实现 `WECHAT_WORK` Passport 绑定和 Session。
7. IAM 实现 `INTERNAL` 扫码。
8. 完成登录页面、跨项目契约测试和回归测试。
9. 后续单独规划企业微信通讯录同步。

## 15. 验收标准

- 使用 `IdpType.WECHAT_WORK`，没有额外枚举别名。
- `application_authorization` 和 `application_suite` 语义不变。
- 未安装 Suite 的企业成员无法登录（三方模式相对钉钉的额外约束）。
- 已取消授权的企业成员无法登录。
- 重新安装授权后可以恢复登录。
- PermanentCode 不明文落库；管理端 CRUD 与 `resolve` 均不返回凭证密文（与钉钉「凭证不进管理 API」等价）。
- IdP 扫码登录不要求 `idp_enterprise_organ`；自动开户策略与钉钉一致。
- 三方企业授权、企业到 Organ 映射和 G2Rain 应用授权相互独立。
- 企业微信身份绑定不会与钉钉、飞书绑定冲突。
- 账号密码和钉钉登录不受影响。
