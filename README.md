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

- 应用与应用授权管理
- 组织、租户开通与基础治理
- 用户、通行证与登录相关能力
- 菜单、页面、页面元素与 API 资源模型
- 角色、控制单元与资源授权关系
- 面向鉴权侧的 `Authority` 聚合查询
- 统一运行配置、部署入口与服务化交付结构

## 5. 技术栈

- 语言与运行时：`Java 25`
- 后端框架：`Spring Boot 4`、`Spring Cloud 2025`
- 数据访问：`MyBatis`
- 基础依赖：`MySQL`、`Redis`、`Nacos`
- 工程能力：`MapStruct`、`Lombok`、`springdoc-openapi`
- 构建工具：`Maven`

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

- 当前扫描结果未发现测试文件，建议后续补齐关键领域与权限链路测试
- 需在文档中明确 `JDK 25` 的运行与构建前提
- 建议后续逐步补充质量门槛、测试命令与 CI 说明

## 10. 相关仓库

- `g2rain-common`：平台公共基础能力仓库
- `g2rain-iam`：统一身份认证与令牌治理能力
- `g2rain-infra`：基础设施与平台公共服务能力
- `g2rain-manager-app`：平台管理端应用
- `g2rain-main-shell`：平台主壳与子应用协同入口

## 11. 使用建议

- 适合作为平台统一组织、资源与权限治理的核心支撑服务使用
- 适合与平台控制台、子应用和统一身份体系协同接入
- 推荐优先通过 `build.sh` 或 Jib 构建镜像，保持与仓库默认构建方式一致
- 若采用手工 `Dockerfile` 构建，应确认传入正确的 `JAR_FILE` 与 `BUILD_VERSION`
- 不建议将其简单理解为通用工具库或单一业务模块
- 仓库正式对外命名与上下游依赖图建议结合平台总览持续校准

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

- **站点**: https://www.g2rain.com/
- **Issues**: [GitHub Issues](https://github.com/g2rain/g2rain/issues)
- **讨论**: [GitHub Discussions](https://github.com/g2rain/g2rain/discussions)
- **邮箱**: g2rain_developer@163.com

## 15. 致谢

感谢所有为这个项目做出贡献的开发者们。

如果这个项目对您有帮助，欢迎 Star 支持。
