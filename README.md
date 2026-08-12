<p align="center">
  <img src="https://github.com/g2rain.png" alt="G2Rain" width="180" />
</p>

# g2rain-basis

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25-437291?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.1-586069?logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Maven](https://img.shields.io/badge/build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)

下一代AI软件开发范式，AI原生Agent平台，开源的企业级SaaS底座。

平台核心主数据与权限治理服务，围绕组织、租户、用户、通行证、应用、资源、角色与权限提供统一领域能力；采用 API、业务实现与启动模块分层组织后端工程

[官网](https://www.g2rain.com) · [Issues](https://github.com/g2rain/g2rain/issues) · [Discussions](https://github.com/g2rain/g2rain/discussions)

## 目录

- 项目简介
- 平台定位
- 业务域说明
- 功能概览
- 技术栈
- 环境要求
- 快速开始
- 构建与镜像
- 与关联仓库的关系
- 模块说明
- 职责边界
- 主要 HTTP 路径
- 关联仓库
- 参与贡献
- 许可证
- 联系我们
- 致谢

## 项目简介

平台核心主数据与权限治理服务，围绕组织、租户、用户、通行证、应用、资源、角色与权限提供统一领域能力；采用 API、业务实现与启动模块分层组织后端工程

## 平台定位

该仓库位于 g2rain 后端平台链路中，承担“后端基础服务”的角色。

## 业务域说明

该仓库聚焦于 `平台基础能力`。

核心对象包括：
- 访问令牌
- 会话
- 身份提供方绑定
- 通行证
- 应用

主要流程包括：
- 凭证登录与已认证会话建立流程
- 授权确认与授权码签发流程
- 令牌签发、刷新与交换流程

## 功能概览

| 能力 | 说明 |
| --- | --- |
| 组织与租户治理 | 维护组织、租户开通、用户及其身份归属等平台主数据。 |
| 应用与授权管理 | 维护应用、应用套件、应用授权及身份提供方接入配置。 |
| 资源模型 | 统一管理菜单、页面、页面元素与 API 资源。 |
| 角色与功能权限 | 维护角色、功能权限、业务能力及其资源关系。 |
| 统一权限聚合 | 为 IAM、网关和平台应用提供主体权限与 Authority 聚合查询。 |

## 技术栈

| 类别 | 说明 |
| --- | --- |
| 运行时 | Java 25、Spring Boot 4.0.5、Spring Cloud 2025.1.1 |
| 安全与令牌 | g2rain-starter-aegis-core |
| 基础设施 | Redis、Nacos、OpenFeign |
| 内部 API | g2rain-basis-api |
| 其他 | Lombok |

## 环境要求

- JDK 25+
- Maven 3.9+
- Redis
- Nacos
- 可访问的 g2rain-basis 服务

## 快速开始

| 步骤 | 命令或位置 | 说明 |
| --- | --- | --- |
| 准备运行环境 | JDK 25+、Maven 3.9+、Redis、Nacos | 后端服务启动前需要准备 Java 构建环境和平台依赖的基础设施。 |
| 调整配置 | `src/main/resources/application.yml` | 按需设置 SERVER_PORT、SPRING_PROFILES_ACTIVE、NACOS_SERVER_ADDR 等环境变量。 |
| 构建项目 | `mvn clean package` | 执行 Maven 构建并生成可执行 Jar。 |
| 本地启动 | `mvn spring-boot:run` | 以当前 profile 启动服务，默认端口以 application.yml 中的 SERVER_PORT 为准。 |

版本号以项目构建配置为准，当前识别为 `1.0.0`。

## 构建与镜像

| 目标 | 命令 | 产物 | 说明 |
| --- | --- | --- | --- |
| 可执行 Jar | `mvn clean package` | `g2rain-basis-1.0.0.jar` | 执行 Maven 标准构建，生成服务可执行产物。 |
| 本地运行 | `mvn spring-boot:run` | 本地 Spring Boot 进程 | 使用当前 profile 启动服务，便于本地联调。 |
| 构建脚本 | `./build.sh` | 脚本定义的构建结果 | 仓库提供 build.sh，可承载组织内约定的镜像或发布流程。 |

## 与关联仓库的关系

本仓库不直接承载用户、通行证、应用等主数据，而是作为认证体验与令牌发放服务，与 g2rain-basis-api 分工协作，完成主数据访问与认证链路闭环。

## 模块说明

| 模块 | 职责说明 | 代码线索 |
| --- | --- | --- |
| g2rain-basis-api | 定义组织、用户、应用、资源与权限等领域 API 契约。 | g2rain-basis-api |
| g2rain-basis-biz | 实现平台主数据、资源模型与权限治理业务。 | g2rain-basis-biz |
| g2rain-basis-startup | 提供 Spring Boot 启动入口与运行配置。 | g2rain-basis-startup |

## 职责边界

该仓库主要负责：
- 负责对应平台基础领域的 API、业务规则、数据持久化与运行时服务
- 负责向网关、IAM、平台应用或业务服务提供可复用的基础能力

该仓库默认不负责：
- 不负责具体业务域的产品流程和业务前端实现
- 不替代网关统一入口、IAM 认证协议或部署编排职责

## 主要 HTTP 路径

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| DELETE | /application/{id} | 对外暴露的服务接口 |
| DELETE | /application_authorization/{id} | 对外暴露的服务接口 |
| DELETE | /application_idp_provision/{id} | 对外暴露的服务接口 |
| DELETE | /control_domain/{id} | 对外暴露的服务接口 |
| DELETE | /control_unit/{id} | 对外暴露的服务接口 |
| DELETE | /idp_enterprise_application_authorization/{id} | 对外暴露的服务接口 |
| DELETE | /idp_enterprise_organ/{id} | 对外暴露的服务接口 |
| DELETE | /organ/{id} | 对外暴露的服务接口 |
| DELETE | /passport/{id} | 对外暴露的服务接口 |
| DELETE | /passport_idp_binding/{id} | 对外暴露的服务接口 |
| DELETE | /personal_static_access_token/{id} | 对外暴露的服务接口 |
| DELETE | /resource_api/{id} | 对外暴露的服务接口 |
| DELETE | /resource_menu/{id} | 对外暴露的服务接口 |
| DELETE | /resource_page/{id} | 对外暴露的服务接口 |
| DELETE | /resource_page_element/{id} | 对外暴露的服务接口 |
| DELETE | /role/{id} | 对外暴露的服务接口 |
| DELETE | /service_registry/{id} | 对外暴露的服务接口 |
| DELETE | /user/{id} | 对外暴露的服务接口 |
| GET | /{applicationCode}/public_key/descriptor | 对外暴露的服务接口 |
| GET | /abc | 对外暴露的服务接口 |
| GET | /anonymous_token_context | 对外暴露的服务接口 |
| GET | /apis | 对外暴露的服务接口 |
| GET | /application/{id}/has_public_key | 对外暴露的服务接口 |
| GET | /application/{id}/public_key | 对外暴露的服务接口 |
| GET | /application/id_name_map | 对外暴露的服务接口 |
| GET | /authority/menus | 对外暴露的服务接口 |
| GET | /authority/resources | 对外暴露的服务接口 |
| GET | /authority/user | 对外暴露的服务接口 |
| GET | /hierarchy/exists | 对外暴露的服务接口 |
| GET | /idp_enterprise_application_authorization/list | 对外暴露的服务接口 |
| GET | /idp_enterprise_application_authorization/page | 对外暴露的服务接口 |
| GET | /list | 对外暴露的服务接口 |
| GET | /organ/hierarchy | 对外暴露的服务接口 |
| GET | /organ/search | 对外暴露的服务接口 |
| GET | /page | 对外暴露的服务接口 |
| GET | /passport_api_permissions | 对外暴露的服务接口 |
| GET | /role_control_unit_relation/role/{roleId} | 对外暴露的服务接口 |
| GET | /route_definitions | 对外暴露的服务接口 |
| GET | /static_access_token_context | 对外暴露的服务接口 |
| GET | /token_context | 对外暴露的服务接口 |
| GET | /user/role/{roleId} | 对外暴露的服务接口 |
| GET | /user_options | 对外暴露的服务接口 |
| POST | /{applicationCode}/save | 对外暴露的服务接口 |
| POST | /application/{id}/public_key | 对外暴露的服务接口 |
| POST | /application/{id}/status | 对外暴露的服务接口 |
| POST | /application/save | 对外暴露的服务接口 |
| POST | /application_authorization/{id}/status | 对外暴露的服务接口 |
| POST | /application_authorization/save | 对外暴露的服务接口 |
| POST | /application_idp_provision/save | 对外暴露的服务接口 |
| POST | /application_suite/save | 对外暴露的服务接口 |
| POST | /bind | 对外暴露的服务接口 |
| POST | /control_domain/save | 对外暴露的服务接口 |
| POST | /control_domain_control_unit_relation/save | 对外暴露的服务接口 |
| POST | /control_unit/{id}/status | 对外暴露的服务接口 |
| POST | /control_unit/save | 对外暴露的服务接口 |
| POST | /control_unit_resource_relation/save | 对外暴露的服务接口 |
| POST | /fetch_member | 对外暴露的服务接口 |
| POST | /fetch_snapshot | 对外暴露的服务接口 |
| POST | /generate | 对外暴露的服务接口 |
| POST | /id_name_map | 对外暴露的服务接口 |
| POST | /idp_enterprise_application_authorization/save | 对外暴露的服务接口 |
| POST | /internal/idp/enterprise-application-authorization/resolve | 对外暴露的服务接口 |
| POST | /internal/idp/enterprise-application-authorization/revoke | 对外暴露的服务接口 |
| POST | /internal/idp/enterprise-application-authorization/upsert | 对外暴露的服务接口 |
| POST | /internal_login | 对外暴露的服务接口 |
| POST | /join_organ | 对外暴露的服务接口 |
| POST | /organ/{descendantId}/hierarchy | 对外暴露的服务接口 |
| POST | /organ/{id}/status | 对外暴露的服务接口 |
| POST | /organ/save | 对外暴露的服务接口 |
| POST | /passport/{id}/password | 对外暴露的服务接口 |
| POST | /passport/{id}/status | 对外暴露的服务接口 |
| POST | /personal_static_access_token/{id}/status | 对外暴露的服务接口 |
| POST | /personal_static_access_token/save | 对外暴露的服务接口 |
| POST | /resolve | 对外暴露的服务接口 |
| POST | /resource/{applicationId}/upload | 对外暴露的服务接口 |
| POST | /resource_api/{serviceCode}/import | 对外暴露的服务接口 |
| POST | /resource_api/save | 对外暴露的服务接口 |
| POST | /resource_menu/save | 对外暴露的服务接口 |
| POST | /resource_page/save | 对外暴露的服务接口 |
| POST | /resource_page_element/save | 对外暴露的服务接口 |
| POST | /role/save | 对外暴露的服务接口 |
| POST | /role_control_unit_relation/save | 对外暴露的服务接口 |
| POST | /save | 对外暴露的服务接口 |
| POST | /service_registry/save | 对外暴露的服务接口 |
| POST | /sync | 对外暴露的服务接口 |
| POST | /tenant_provision/provision_account | 对外暴露的服务接口 |
| POST | /user/save | 对外暴露的服务接口 |
| POST | /user_role_relation/assign_users | 对外暴露的服务接口 |
| POST | /user_role_relation/save | 对外暴露的服务接口 |

## 关联仓库

| 仓库 | 协作关系 |
| --- | --- |
| g2rain-basis-api | 通过内部 API 访问平台基础主数据与基础服务能力。 |
| g2rain-common | 复用平台公共规范、通用模型、工具能力或基础依赖约束。 |
| g2rain-iam | 协同完成登录认证、令牌发放、SSO 回调或前端登录态衔接。 |
| g2rain-infra | 协同提供路由、配置、基础设施数据或平台运行支撑能力。 |

## 参与贡献

我们欢迎所有形式的贡献：Issue 反馈、文档改进、功能建议与代码提交。

推荐流程：

1. Fork 本仓库。
2. 创建特性分支：`git checkout -b feature/your-feature-name`。
3. 提交更改：`git commit -m "Add some feature"`。
4. 推送分支：`git push origin feature/your-feature-name`。
5. 提交 Pull Request。

代码贡献前请尽量补充必要的测试和文档，并确保构建、测试与静态检查通过。

## 许可证

本项目基于 [Apache 2.0许可证](https://github.com/g2rain/g2rain-common/blob/main/LICENSE) 开源。

## 联系我们

- Issues: [GitHub Issues](https://github.com/g2rain/g2rain/issues)
- 讨论: [GitHub Discussions](https://github.com/g2rain/g2rain/discussions)
- 邮箱: g2rain_developer@163.com

## 致谢

感谢所有为 g2rain 项目提交 Issue、代码、文档、建议和使用反馈的开发者们！
