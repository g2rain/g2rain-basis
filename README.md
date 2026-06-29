# g2rain-basis

## 1. 徽标与状态标识

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25-437291?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.1-586069?logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Maven](https://img.shields.io/badge/build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)

## 2. 项目简介

`g2rain-basis` 是 G2rain 平台中的平台核心服务仓库之一，负责沉淀组织、租户、用户、应用、资源、角色、权限等共享业务域能力，为平台控制台、子应用与后端服务提供统一的治理底座。

## 3. 平台定位

在 G2rain“企业级 AI 原生开源 SaaS 平台”体系中，`g2rain-basis` 位于平台核心服务层，重点承载应用管理、组织与租户治理、资源模型、角色权限控制与 `Authority` 聚合查询等能力。

它也是平台“应用-权限-交付模型”的关键承载仓库之一：

- 应用在平台侧作为最小权限控制单元存在
- 页面、按钮、接口等资源聚合到应用
- 租户角色按应用功能权限分配与调整
- 多个功能权限可以进一步聚合为业务能力
- 业务能力可继续作为交付与商业化能力单元向上衔接

它与 `g2rain-common`、`g2rain-iam`、`g2rain-infra` 等仓库协同，共同构成平台统一身份、统一资源、统一权限与统一交付体系的重要组成部分。

## 4. 核心能力

- 应用与交付单元建模：解决“平台如何把页面、按钮、接口收敛成可治理、可交付、可授权对象”的问题，通过 `Application`、`ApplicationAuthorization`、`ApplicationSuite` 等模型，把应用定义为最小控制单元，并为后续业务能力聚合与商业化交付预留统一入口。
- 组织、租户与用户基础治理：解决平台多租户场景下组织、租户、用户、通行证之间的基础主数据管理问题，通过 `Organ`、`TenantProvision`、`User`、`Passport`、`Login` 等服务形成统一的开通、登录、绑定与身份归属底座。
- 资源模型与权限承载体系：解决菜单、页面、页面元素、API 等资源分散定义、难以统一授权的问题，通过 `Resource`、`ResourceMenu`、`ResourcePage`、`ResourcePageElement`、`ResourceApi` 等资源模型，把前后端能力沉淀成平台可管理对象。
- 角色、控制单元与授权关系编排：解决“角色如何承接应用权限、资源权限和控制域关系”的问题，通过 `Role`、`ControlUnit`、`ControlDomain` 及其关系服务，把角色授权从简单角色表扩展为可组合、可扩展的控制关系网络。
- 面向鉴权侧的 `Authority` 聚合查询：解决 IAM、网关、子应用在运行期需要一次性获取可用应用、角色、资源与控制范围的问题，通过 `AuthorityController` 与相关聚合服务，把多张业务表的授权结果汇总成统一鉴权视图。
- 服务注册与资源导入支撑：解决微服务、应用资源与平台治理体系之间的接入衔接问题，通过 `ServiceRegistry` 及应用/资源相关接口，为服务注册、资源导入、应用侧接入提供平台入口。
- 审计与运行支撑能力：解决平台核心服务在生产环境中的审计记录、运行配置和镜像交付问题，通过 `AuditEvent`、`g2rain-basis-startup`、`build.sh` 与 Jib 构建链路，保证服务既能被治理，也能被稳定交付。

## 5. 技术栈

- 语言与运行时：`Java 25`
- 后端框架：`Spring Boot 4`、`Spring Cloud 2025`
- 数据访问：`MyBatis`
- 基础依赖：`MySQL`、`Redis`、`Nacos`
- 工程能力：`MapStruct`、`Lombok`、`springdoc-openapi`
- 构建工具：`Maven`
- 交付方式：`build.sh` + `Jib` 本地镜像构建

## 6. 快速开始

### 环境要求

- `JDK 25`
- `Maven 3.9+`
- 可用的 `MySQL`、`Redis`、`Nacos` 环境

### Maven 构建

```bash
mvn clean package -DskipTests
```

### 本地运行

```bash
cd g2rain-basis-startup
mvn spring-boot:run
```

或：

```bash
java -jar g2rain-basis-startup/target/g2rain-basis-startup-*.jar
```

### 使用 `build.sh` 构建镜像

仓库根目录提供了 `build.sh`，用于一键完成 Maven 构建并基于 Jib 生成本地 Docker 镜像。

```bash
./build.sh
./build.sh 1.0.0
```

脚本默认执行逻辑：

- 先在仓库根目录执行 `mvn -DskipTests=true clean install`
- 再进入 `g2rain-basis-startup` 模块执行 `mvn -DskipTests=true compile jib:dockerBuild`
- 默认镜像名为 `g2rain/g2rain-basis:<tag>`

## 7. 项目结构

```text
g2rain-basis/
├── build.sh                # 一键构建并生成本地 Docker 镜像
├── g2rain-basis-api/       # 对外契约：API、DTO、VO、枚举
├── g2rain-basis-biz/       # 业务实现：Controller、Service、DAO、资源授权逻辑
└── g2rain-basis-startup/   # 启动入口：运行配置、日志、部署相关文件
```

### 结构说明

- `g2rain-basis-api/`：对外暴露 API 接口、选择 DTO、展示 VO 与枚举定义，主要承担“契约稳定层”职责，方便其他服务或前端调用方按统一模型接入。
- `g2rain-basis-biz/`：承载真正的领域实现，内部按 `controller / service / dao / converter / model / audit` 分层，覆盖应用、资源、角色、组织、登录、审计等多个核心域服务。
- `g2rain-basis-startup/`：承载 Spring Boot 启动入口与运行时配置，包括 `Application`、参数解析器、Redis 配置、虚拟线程配置、`application.yml`、日志配置等。
- `build.sh`：承载仓库推荐交付入口，适合本地或 CI 中快速生成 `g2rain/g2rain-basis:<tag>` 镜像。

### 核心业务流程介绍

#### 1. 应用-资源-权限建模主线
- 产品或研发先在平台侧定义 `Application`
- 再将菜单、页面、页面元素、API 等资源归属到应用
- 角色不直接面向零散资源授权，而是通过控制单元、控制域与资源关系承接授权边界
- 这一主线保证了“应用是最小权限控制单元、资源是权限载体、角色是租户侧分配对象”这套平台约定能够稳定落地

#### 2. 租户开通与组织治理主线
- 平台通过 `TenantProvision`、`Organ`、`User`、`Passport` 等服务承载租户初始化、组织维护、用户归属与登录主体关联
- 组织与用户模型不仅服务管理后台，也服务 IAM、审计与子应用接入侧的身份归属判断
- 这一主线解决的是“谁属于哪个租户、哪个组织、拥有哪些可用应用与角色”的基础治理问题

#### 3. Authority 聚合查询主线
- 当 IAM、网关或子应用需要获取用户授权结果时，不会逐个查询角色、资源、应用表
- `Authority` 相关服务会汇总应用范围、角色关系、控制单元、资源授权结果
- 最终输出统一鉴权视图，供鉴权链路和子应用运行期消费
- 这一主线是 `basis` 从“后台管理服务”升级为“平台治理底座”的关键差异点

#### 4. 服务注册与平台接入主线
- 微服务或平台能力可通过 `ServiceRegistry`、应用与资源相关接口向平台登记
- 登记后的能力才能进一步纳入资源、权限、交付和控制关系体系
- 这一主线把外部接入与平台内部治理模型衔接起来，而不是让应用侧各自维护一套独立权限配置

## 8. 常用命令

```bash
# 全量构建
mvn clean package

# 跳过测试构建
mvn clean package -DskipTests

# 启动 startup 模块
mvn -pl g2rain-basis-startup -am spring-boot:run

# 使用脚本构建 Docker 镜像
./build.sh

# 指定镜像标签构建 Docker 镜像
./build.sh 1.0.0

# 使用 Jib 直接构建本地镜像
mvn -pl g2rain-basis-startup -am -DskipTests=true compile jib:dockerBuild
```

## 9. 质量与测试

- 当前扫描结果未发现测试文件，建议后续优先补齐应用授权、角色控制单元关系、`Authority` 聚合查询、租户开通等关键链路测试
- 需在文档中明确 `JDK 25` 的运行与构建前提
- 当前交付方式已较明确，但后续仍建议补充 CI、配置校验与核心接口回归测试说明

## 10. 相关仓库

- `g2rain-common`：平台公共基础能力仓库
- `g2rain-iam`：统一身份认证与令牌治理能力
- `g2rain-infra`：基础设施与平台公共服务能力
- `g2rain-manager-app`：平台管理端应用
- `g2rain-main-shell`：平台主壳与子应用协同入口

## 11. 使用建议

- 适合作为平台统一组织、资源与权限治理的核心支撑服务使用
- 适合与平台控制台、子应用和统一身份体系协同接入，而不是作为单一业务服务独立理解
- 如果需要对接子应用、网关或 IAM，建议优先理解“应用-资源-角色-ControlUnit-Authority”这条主线
- 推荐优先通过 `build.sh` 或 Jib 构建镜像，保持与仓库默认构建方式一致
- 若后续继续增强 README，应重点补充关键鉴权查询、租户开通与资源导入接口的实际示例

## 12. 贡献指南

欢迎以 Issue、文档改进、测试补充、代码优化、功能增强等形式参与贡献。

建议流程：

1. Fork 本仓库
2. 创建特性分支
3. 提交修改
4. 推送分支
5. 提交 Pull Request

提交前请尽量确保：

- 遵循现有技术栈与代码规范
- 补充必要测试
- 更新相关文档
- 确保测试通过

## 13. 许可证

本项目基于 [Apache 2.0许可证](LICENSE) 开源。

## 14. 联系我们

- **站点**: https://www.g2rain.com/
- **Issues**: [GitHub Issues](https://github.com/g2rain/g2rain/issues)
- **讨论**: [GitHub Discussions](https://github.com/g2rain/g2rain/discussions)
- **邮箱**: g2rain_developer@163.com

## 15. 致谢

感谢所有为这个项目做出贡献的开发者们。

如果这个项目对您有帮助，欢迎 Star 支持。