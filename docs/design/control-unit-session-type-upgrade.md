# 控制单元 SessionType 权限模型升级

## 1. 文档状态

- 状态：Basis 查询/同步已按 organ 落地；Manager-App 配置侧已对齐；Gateway 消费侧待落地
- 目标仓库：`g2rain-basis`（权限事实）、`g2rain-manager-app`（管理端配置）
- 协作方：`g2rain-gateway-webflux`、`g2rain-common`
- 适用范围：Gateway 对 `SessionType=MEMBER` 的接口入口权限
- Gateway 方案：`g2rain-gateway-webflux` 的 `docs/design/member-api-permission-upgrade.md`

本文描述如何在 `control_unit` 增加 `session_type`，使控制单元明确声明其允许的会话主体，并由 Basis 向 Gateway 发布**按租户**的 MEMBER API 权限事实。

已落地：`session_type` 字段、管理端采集、`GET /authority/session_api_permissions?sessionType=MEMBER&organId=`（租户开通 ∩ 已发布 MEMBER CU）、`MEMBER_PERM` 按 `organId` 失效（`sendDelete` 载荷为 Long）。  
待办：Gateway 按 organ 消费 `MemberPerm` 并切换授权源。

## 2. 背景与目标

当前 Gateway 的 MEMBER 分支复用 Passport 默认接口集合。该集合由默认落地应用中 `landing=1`、`control_unit_scope=PERPETUAL` 的控制单元计算，无法独立表达哪些 API 面向 MEMBER，会造成 Passport 与 MEMBER 权限联动；且该集合是平台全局能力，**不区分租户是否开通**。

升级目标：

1. 控制单元通过 `session_type` 明确访问主体。
2. USER、MEMBER、PASSPORT、ANONYMOUS 的接口集合可独立计算和发布。
3. MEMBER 不创建虚拟用户、虚拟角色，不把会员映射进员工角色授权链。
4. 保留控制单元作为资源交付和权限配置的基本粒度。
5. 同一个 API 可以关联多个不同 `session_type` 的控制单元。
6. MEMBER 入口权限必须跟随租户：只有该 `organId` 已开通相关 MEMBER 控制单元，Gateway 才放行对应 API。

非目标：

- 不在 Gateway 判断会员是否拥有某条业务数据。
- 不改变 `control_unit_scope` 的 CUSTOMER、OPERATION、PERPETUAL 语义。
- 不把 MEMBER 做成 Passport 式平台全局默认可访问集合。

## 3. 核心约束

### 3.1 一个控制单元只属于一种 SessionType

`control_unit.session_type` 使用 `g2rain-common` 的 `SessionType` 枚举值：

| 值 | 含义 | 典型授权方式 |
| --- | --- | --- |
| `USER` | 员工用户接口 | SessionType 过滤后，继续校验用户/角色与控制单元关系 |
| `MEMBER` | 会员自助接口 | 已发布的 MEMBER 控制单元 ∩ **该租户已开通的控制单元** → 入口 API |
| `PASSPORT` | 仅依赖 Passport 的基础接口 | 已发布的 PASSPORT 控制单元形成平台入口 API 集合 |
| `ANONYMOUS` | 匿名接口 | 仅适用于经过专项安全评审的公开能力 |

如果同一个 API 同时允许 USER 和 MEMBER 调用，应将同一个 `resource_api.id` 分别关联到 USER、MEMBER 控制单元。不得在字段中保存 `USER,MEMBER` 之类的逗号分隔值。

### 3.2 session_type 与 control_unit_scope 正交

`control_unit_scope` 描述功能性质和交付范围，`session_type` 描述请求主体。`CUSTOMER` 不自动等于 `MEMBER`，不能把所有 CUSTOMER 控制单元自动暴露给会员。

示例：

| 控制单元 | control_unit_scope | session_type |
| --- | --- | --- |
| 会员自助中心 | CUSTOMER | MEMBER |
| 客服会员管理 | OPERATION | USER |
| Passport 基础能力 | PERPETUAL | PASSPORT |
| 登录前公共能力 | PERPETUAL | ANONYMOUS |

## 4. 数据库升级

### 4.1 字段定义

最终目标结构：

```sql
ALTER TABLE control_unit
    ADD COLUMN session_type VARCHAR(32) NOT NULL
        COMMENT '会话主体类型[SessionType: USER|MEMBER|PASSPORT|ANONYMOUS]'
        AFTER application_id;

CREATE INDEX idx_control_unit_session_status
    ON control_unit (session_type, status, (IF(delete_flag = 0, 0, NULL)));
```

生产迁移不得直接执行上述 `NOT NULL` 版本，应采用“可空字段 → 回填 → 校验 → 非空约束”的在线升级顺序：

```sql
ALTER TABLE control_unit
    ADD COLUMN session_type VARCHAR(32) NULL
        COMMENT '会话主体类型[SessionType: USER|MEMBER|PASSPORT|ANONYMOUS]'
        AFTER application_id;
```

完成存量分类并确认不存在空值和非法值后，再收紧为非空：

```sql
ALTER TABLE control_unit
    MODIFY COLUMN session_type VARCHAR(32) NOT NULL
        COMMENT '会话主体类型[SessionType: USER|MEMBER|PASSPORT|ANONYMOUS]';
```

不得为存量数据设置无条件 `DEFAULT 'USER'`，否则 Passport、匿名或未来的 MEMBER 控制单元会被错误纳入员工权限。

### 4.2 存量数据分类

迁移前生成控制单元清单，至少包含：

- 是否存在有效的 `role_control_unit_relation`。
- 是否为当前 Passport 默认权限来源。
- 关联的菜单、页面、页面元素和 API。
- 是否同时承担员工与 Passport 等多种访问场景。

建议分类规则：

1. 只通过员工角色访问的控制单元标记为 `USER`。
2. 只承载 Passport 基础能力的控制单元标记为 `PASSPORT`。
3. 新建会员端控制单元并标记为 `MEMBER`。
4. 匿名能力必须逐项安全确认后标记为 `ANONYMOUS`。
5. 同时服务多种主体的存量控制单元必须拆分；资源可以重复关联，控制单元不能保留多主体语义。

迁移完成前必须检查：

```sql
SELECT id, control_unit_name
FROM control_unit
WHERE delete_flag = 0
  AND session_type IS NULL;
```

结果必须为空。

## 5. Basis 权限计算契约

### 5.1 查询接口

受信内部查询必须带租户：

```http
GET /authority/session_api_permissions?sessionType=MEMBER&organId={organId}
```

建议响应：

```json
{
  "status": 200,
  "data": {
    "sessionType": "MEMBER",
    "organId": 10001,
    "version": 15,
    "apiIds": [101, 102, 108]
  }
}
```

`apiIds` 必须使用 `resource_api.id`，与 Gateway 动态路由的 route ID 保持同一标识。缺少 `organId` 时不得退回「全平台 MEMBER API 清单」。

Basis 已按本节强制 `organId` 并走租户开通 SQL；Gateway 切换前需确认消费契约为按 organ 失效 + 按 organ 回源。

### 5.2 MEMBER 查询规则（按租户开通）

MEMBER 入口 API = **已发布的 MEMBER 控制单元** ∩ **该 organ 已开通的控制单元** 所覆盖的 API。

「租户已开通」复用既有应用授权链路落库事实，而不是会员个人角色：

- `application_authorization`：机构对应用/控制域授权且 `ACTIVATED`；
- 机构侧控制单元激活：应用授权时写入 / 同步到 ADMIN 角色的 `role_control_unit_relation`，且 `status=ACTIVATED`。

推荐查询形态：

```sql
SELECT DISTINCT api.id
FROM application_authorization aa
JOIN role_control_unit_relation rcur
  ON rcur.application_authorization_id = aa.id
 AND rcur.status = 'ACTIVATED'
 AND rcur.delete_flag = 0
JOIN role r
  ON r.id = rcur.role_id
 AND r.organ_id = aa.organ_id
 AND r.role_type = 'ADMIN'
 AND r.delete_flag = 0
JOIN control_unit cu
  ON cu.id = rcur.control_unit_id
 AND cu.session_type = 'MEMBER'
 AND cu.status = 'PUBLISHED'
 AND cu.delete_flag = 0
JOIN control_unit_resource_relation curr
  ON curr.control_unit_id = cu.id
 AND curr.resource_type = 'API_ENDPOINT'
 AND curr.delete_flag = 0
JOIN resource_api api
  ON api.id = curr.resource_id
 AND api.delete_flag = 0
WHERE aa.organ_id = #{organId}
  AND aa.status = 'ACTIVATED'
  AND aa.delete_flag = 0;
```

要点：

1. 读取 `role_control_unit_relation` **仅作为机构开通事实**（ADMIN / 应用授权副本），不表示会员拥有员工角色。
2. 不使用 `userId`、`roleIds`、会员侧角色模型。
3. `landing` 与 `control_unit_scope=PERPETUAL` 不再作为 MEMBER 入口授权条件。
4. 平台仅发布 MEMBER 控制单元、但某租户未开通时，该租户查询结果必须为空或不含对应 API。

### 5.3 USER 查询规则保持不变

USER 先限定 `control_unit.session_type=USER`，再按照用户或角色已获得的控制单元计算 API。新增字段不能使 USER 绕过角色授权。

### 5.4 管理端协作

`g2rain-manager-app` 控制单元页须采集并展示 `sessionType`（创建必填、编辑只读），角色分配仅展示/提交 `USER` 控制单元。MEMBER 控制单元通过应用授权 / 控制域交付给租户开通，不得依赖「给会员分配员工角色」。详见 Manager-App 设计文档。

## 6. 权限变更同步

新增同步数据源：

```text
MEMBER_PERM
```

与 `USER_PERM` 共用 cache-sync 机制，但独立 `dataSource`。推荐对齐按机构失效：

```json
{
  "dataSource": "MEMBER_PERM",
  "organId": 10001
}
```

实现为 `sendDelete(MEMBER_PERM, organId)`（载荷 Long），与 `USER_PERM` 一致。

以下变化必须触发对应 `organId` 的 MEMBER 权限失效（多租户受影响时逐个 organ 通知，或发送约定的全量 organ 失效）：

- 租户应用授权开通、关停、控制域控制单元同步到机构。
- 机构侧 MEMBER 控制单元激活 / 关停。
- MEMBER 控制单元发布、停用、删除。
- 控制单元的 `session_type` 变入或变出 MEMBER。
- MEMBER 控制单元新增、删除 API_ENDPOINT 关系。
- 关联 API 被删除或失效。

Gateway 收到失效后丢弃该 `organId` 缓存，下次请求再拉第 5.1 节快照。Basis 是权限事实所有者，Gateway 缓存不是主数据源。

契约说明：`MEMBER_PERM` 载荷为 `organId`（Long，`sendDelete`），与早期「全局 version VO」方案不兼容；Gateway 尚未落地消费前以本节为准。

## 7. 发布顺序

1. 发布兼容旧库的 Basis 代码：可读取空 `session_type`，但空值不进入新权限集合。
2. 数据库增加可空字段和索引。
3. 完成存量控制单元分类；对多主体控制单元执行拆分。
4. 创建 MEMBER 控制单元并关联允许会员访问的 API；确认控制域 / 应用授权能把 MEMBER 单元开通到租户。
5. 将 `/authority/session_api_permissions` 改为强制 `organId`，按第 5.2 节计算；用已开通 / 未开通租户核对结果。
6. `MEMBER_PERM` 改为按 `organId` 失效；发布支持按 organ 缓存的 Gateway（先影子模式）。
7. Gateway 切换到按租户的 MemberPerm。
8. 确认没有空值后，将 `session_type` 收紧为 `NOT NULL`。
9. 稳定运行后停止 MEMBER 对 Passport 默认权限集合的兼容读取。

## 8. 回滚

- Gateway 切换前回滚：停用新查询和同步即可，不删除已回填字段。
- Gateway 切换后短期回滚：通过受控开关恢复旧权限源，同时告警该模式会重新引入 Passport/MEMBER 耦合，并**失去租户开通门禁**。
- 不建议回滚数据库字段；保留字段不会影响旧代码，直接删除字段会破坏已完成的权限分类。
- 回滚不能删除 `control_unit_resource_relation` 中原有资源关系。

## 9. 验收标准

- MEMBER API 集合只来自 `session_type=MEMBER` 且已发布、**且该 organ 已开通**的控制单元。
- 平台已发布但租户未开通：该 organ 查询不含对应 API；Gateway enforce 后拒绝。
- 租户 A 已开通、租户 B 未开通：同一 apiId 仅对 A 放行。
- 给 USER 或 PASSPORT 控制单元增加 API，不会扩大 MEMBER 权限。
- 租户关停应用授权或 MEMBER 控制单元后，对应 organ 同步失效后被拒绝。
- 同一个 API 可以通过不同控制单元同时授权给 USER 和 MEMBER。
- MEMBER 权限查询不依赖会员 `userId` 或会员角色；可读 ADMIN/`application_authorization` 仅作开通事实。
- 空、非法 `session_type` 不会被静默当成 USER 或 MEMBER。
- 同步事件重复、乱序和短暂失败不会导致未开通租户被错误放行。

