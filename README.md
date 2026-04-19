# G2rain Basis

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25-437291?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.1-586069?logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Maven](https://img.shields.io/badge/build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)

面向多租户 SaaS 的**业务支撑服务**：在统一平台上承载组织、租户开通、用户与通行证、应用与授权、管控域与控制单元、菜单/页面/API 资源及角色关系等**共享业务域**能力，供各业务微服务通过 HTTP / 注册发现等方式调用。

与侧重字典、国际化、路由元数据、分布式 ID 等「纯基础能力」的 **g2rain-infra** 互补：本仓库更贴近**租户、身份、权限与资源模型**等业务侧共性需求。

本项目由 **[谷雨开源](https://g2rain.com)**（G2Rain）社区维护，采用 **Apache License 2.0** 开源协议发布。

---

## 目录

- [功能概览](#功能概览)
- [技术栈](#技术栈)
- [模块结构](#模块结构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [容器镜像](#容器镜像)
- [参与贡献](#参与贡献)
- [许可证](#许可证)

---

## 功能概览

以下根据当前 `g2rain-basis-biz` 中的 **Controller 与领域划分**归纳（具体字段与接口以代码及 OpenAPI 为准）。

| 领域 | 说明 |
|------|------|
| **组织** | 组织维护、闭包/层级关系等（`/organ`）。 |
| **租户开通** | 租户级初始化编排（`/tenant_provision`）。 |
| **用户与通行证** | 用户、通行证、登录令牌等（`/user`、`/passport`、`/login_token`）。 |
| **登录** | 内部认证相关入口（`/internal_auth`，与 `LoginController` 等实现配套）。 |
| **应用与套件** | 应用、应用套件（`/application`、`/application_suite`）。 |
| **应用授权** | 应用级授权关系（`/application_authorization`）。 |
| **管控域与控制单元** | 管控域、控制单元及二者关联（`/control_domain`、`/control_unit`、`/control_domain_control_unit_relation`）。 |
| **控制单元与资源** | 控制单元与菜单/页面/API 等资源的绑定（`/control_unit_resource_relation`）。 |
| **资源与端点** | API 端点、资源菜单/页面/页面元素、资源与端点关联等（`/api_endpoint`、`/resource_menu`、`/resource_page`、`/resource_page_element`、`/resource_api_endpoint`）；统一资源入口（`/resource`）。 |
| **权限聚合** | 面向鉴权侧的数据聚合查询（`/authority`）。 |
| **角色** | 角色及用户—角色、角色—控制单元关系（`/role`、`/user_role_relation`、`/role_control_unit_relation`）。 |

**集成能力**（与 `application.yml` 一致）：

- **Nacos**：服务发现 + 动态配置；可选导入 `g2rain-basis.yml`。
- **Spring Cloud Stream**（Redis Binder）：默认输出到 **`g2rain-syncer`**；同步语义侧枚举含 `BasisSyncerEnum`（如组织名称、应用名称、组织层级等变更通知类型）。
- **数据隔离**：`g2rain.data.isolation.enabled: true`（多租户数据隔离开关，与业务表设计配合）。
- **Actuator**：`health`、`info`；**springdoc-openapi** 提供接口文档。

**持久化**：MyBatis，当前业务侧约 **20** 个 Mapper XML（`g2rain-basis-biz/src/main/resources/mybatis/mapper`）。

---

## 技术栈

| 类别 | 说明 |
|------|------|
| 运行时 | Java **25** |
| 框架 | **Spring Boot** 4.0.x、**Spring Cloud** 2025.1.x、**Spring Cloud Alibaba (Nacos)** |
| 持久化 | **MyBatis**、**MySQL** |
| 缓存 / 消息 | **Redis**、**Spring Cloud Stream**（Redis） |
| 其他 | **MapStruct**、**Lombok**、**springdoc-openapi**、**g2rain-starter-mybatis-extensions** 等 |

> 工程依赖若干 `com.g2rain` 内部 Starter（安全、Redis、缓存同步、MyBatis 扩展等）。本地完整构建需能解析对应 **Maven 仓库**；若你希望「仅开源本仓库即可构建」，欢迎在 Issue 中提出诉求，便于社区拆分或发布公共构件。

---

## 模块结构

```
g2rain-basis/
├── g2rain-basis-api/      # 对外契约：*Api、DTO/VO、枚举、错误码等
├── g2rain-basis-biz/      # 业务实现：Controller、Service、DAO、MyBatis XML
└── g2rain-basis-startup/  # 可执行应用：Spring Boot 入口、OpenAPI、全局配置
```

主类：`com.g2rain.basis.Application`。

---

## 环境要求

- **JDK 25**
- **Apache Maven 3.9+**（推荐）
- 运行期：**MySQL**、**Redis**、**Nacos**（按环境与 Nacos 配置启用）

---

## 快速开始

### 1. 克隆仓库

```bash
git clone <你的仓库克隆地址>
cd g2rain-basis
```

### 2. 编译

```bash
mvn clean package -DskipTests
```

### 3. 运行

```bash
java -jar g2rain-basis-startup/target/g2rain-basis-startup-*.jar
```

或在 `g2rain-basis-startup` 模块：

```bash
cd g2rain-basis-startup
mvn spring-boot:run
```

默认 HTTP 端口为 **8081**（由 `SERVER_PORT` 控制，见 `application.yml`）。默认 Profile 由 `SPRING_PROFILES_ACTIVE` 指定（如 `dev`）。

### 4. API 文档

启动后通过 **springdoc-openapi** 访问 Swagger UI（路径随 springdoc 版本可能为 `/swagger-ui.html` 或 `/swagger-ui/index.html`）。

---

## 配置说明

常用项见 `g2rain-basis-startup/src/main/resources/application.yml` 及 Nacos 中的 `g2rain-basis.yml`。

| 变量 / 配置 | 说明 |
|-------------|------|
| `SERVER_PORT` | HTTP 端口，默认 **8081** |
| `SPRING_PROFILES_ACTIVE` | Spring Profile |
| `NACOS_SERVER_ADDR` | Nacos 地址，默认 `127.0.0.1:8848` |
| `SPRING_CLOUD_NACOS_*` | Nacos 鉴权、命名空间、分组等（生产环境请用密钥管理，勿依赖弱默认口令） |
| `g2rain.web.*` | Web 层开关（如登录守卫、身份注入、统一 JSON 异常等，见配置文件） |

---

## 容器镜像

`g2rain-basis-startup` 已配置 **Jib**（基础镜像 `eclipse-temurin:25-jre`，镜像名 `g2rain/g2rain-basis:${project.version}`）。示例：

```bash
mvn -pl g2rain-basis-startup -am compile jib:build
# 或: jib:dockerBuild
```

容器内暴露端口以 Jib `container.ports` 与运行时 `SERVER_PORT` 为准；部署时请与编排（K8s Service / Docker `-p`）对齐。

---

## 参与贡献

欢迎通过 Issue 讨论缺陷与需求，通过 Pull Request 提交修改。提交前建议在本地执行 `mvn clean verify`（或你们约定的 CI 命令），并在 PR 中说明行为变更与兼容性。

---

## 许可证

本仓库适用 **Apache License, Version 2.0**，见 [LICENSE](LICENSE)。

```
Copyright © 2025 g2rain.com
```

---

## 链接

- **组织**：谷雨开源（G2Rain）
- **官网**：<https://www.g2rain.com>
- **克隆地址**：请将上文 `<你的仓库克隆地址>` 替换为实际托管平台 URL。
