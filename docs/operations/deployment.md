# 构建与部署

## 构建可执行产物

```bash
mvn clean verify
```

可执行 Spring Boot 产物由 `g2rain-basis-startup` 模块生成。

## 本地构建镜像

仓库通过 Jib 构建镜像，`build.sh` 会先安装全部模块（包含 Maven 测试阶段），再在 Startup 模块执行 `jib:dockerBuild`：

```bash
./build.sh 1.0.0
```

未提供标签时本地脚本默认使用 `latest`；稳定版发布不会使用该标签。目标镜像名为：

```text
ghcr.io/g2rain/g2rain-basis:<tag>
```

本地模式需要 Docker daemon，且不会推送。`./build.sh <tag> push` 使用已由 `docker login` 保存的凭据调用 `jib:build` 推送，不需要 Docker daemon。两种模式复用相同的 Jib 配置、Eclipse Temurin Java 25 JRE 基础镜像、`com.g2rain.basis.Application` 入口和端口 `8080`。根目录保留的 Startup `Dockerfile` 不是本地脚本或 CI 发布路径；不要将它与 Jib 产物混用。

## 容器镜像发布（GHCR）

`.github/workflows/publish-ghcr.yml` 只会在推送精确格式的稳定 tag（例如 `v1.0.0`）时触发；普通分支推送和手动触发均不会发布。工作流会严格验证 tag 格式、其提交已进入 `origin/main` 历史、根 Maven 版本与 tag 一致，并检查该提交的 `docs/releases/v<版本>.md` 非空。随后使用 Temurin JDK 25 执行 `mvn clean install`，再在 Startup 模块以同一 Jib 配置推送：

CI 构建前通过临时 Maven settings 配置 Common、Spring Boot Starter、Department、MyBatis Extensions、Crafter 与 Generator 各仓库的 GitHub Maven Packages 地址及插件仓库。跨仓库包须在各 Package 的 Actions access 授予 `g2rain-basis` 读取权限；`GITHUB_TOKEN` 的 `packages: write` 本身不授予其他仓库包的读取权限。若仍出现 `401/403`，先核对包访问授权；若出现 `404`，核对制品版本与所属仓库。凭据只在运行时注入，不提交进仓库。

```text
ghcr.io/g2rain/g2rain-basis:<版本>
ghcr.io/g2rain/g2rain-basis:sha-<完整提交 SHA>
```

认证使用 GitHub Actions 提供的 `GITHUB_TOKEN`，工作流只声明 `contents: read` 和 `packages: write`，不使用 PAT Secret。临时 Maven settings 写入 runner 临时目录，仅保存 `${env.GITHUB_TOKEN}` 引用；版本校验、完整构建和 Startup Jib 均显式使用该文件。Jib 的两个标签都成功推送后才会写入 Actions Summary；Docker 登录成功、构建成功或部分 layer 上传不表示发布成功。工作流不会创建 GitHub Release、部署服务或更新 `latest`。

镜像 job 成功后，`needs` 调用 `publish-maven.yml`，将根父 POM `com.g2rain:g2rain-basis:1.0.0` 与 API `com.g2rain:g2rain-basis-api:1.0.0` 发布到 `https://maven.pkg.github.com/g2rain/g2rain-basis`。该工作流也可手动运行，但所选提交必须位于 `main` 历史；两种入口共享并发组。`-pl g2rain-basis-api -am` 只选中根父 POM 和 API，不选中 Biz/Startup。重复上传同一稳定版本可能冲突，应按版本策略处理，不能删除或覆盖既有版本。Maven 上传失败会使调用 job 失败，已经推送的镜像不会自动回滚。

Maven 消费方须声明上述 repository，并在 Maven settings 中为同 ID server 安全配置认证。跨仓库 Actions 读取需要核对对应包的访问授权和组织策略；个人读取使用具有 `read:packages` 的 PAT classic，勿将 Token 提交到仓库。公开的 GHCR 镜像可匿名拉取，不代表 Maven Packages 可匿名下载。

首次成功推送后，在 **GitHub Packages → g2rain-basis → Package settings → Change visibility → Public** 将包设为公开；若组织管理员禁止修改，请联系管理员。已有 GHCR 包即使工作流有 `packages: write`，仍可能需要在 **Package settings → Actions access** 向 `g2rain/g2rain-basis` 授予写权限。若出现 `denied`，优先检查该设置和组织 Packages 策略。

发布顺序为：将发布改动、工作流和同名发布说明按 `feature/* → develop → main` 流程合入 `main`，在该提交完成验证，再创建并推送 `v1.0.0`；确认摘要列出两个标签后，按环境部署指定版本。包公开后才可匿名执行：

```bash
docker pull ghcr.io/g2rain/g2rain-basis:1.0.0
```

私有包需要具备 `read:packages` 权限的凭据。镜像发布、匿名拉取、容器启动、在线演示和实际部署验证是独立事项；本文件不将其中任何一项视为另一项已经完成。

## 镜像内容与敏感配置

Jib 从 `g2rain-basis-startup` 的 Spring Boot 可执行 Jar 组装最终镜像，不复制仓库源代码、`codegen.properties` 或 Docker 构建上下文。打包资源规则会将 `application.yml`、`application-dev.yml`、`application-nacos.yml`、`banner.txt` 和 `logback-spring.xml` 写入 Jar，因此它们会进入镜像。

`g2rain-basis-startup/src/main/resources/application.yml`、`application-dev.yml` 和 `application-nacos.yml` 已跟踪且包含 Nacos、数据库等开发默认凭据。它们会随镜像公开，属于风险；维护者须在首次公开前将这些值迁移为环境变量、Secret 或受控 Nacos 配置，并在曾被复用时轮换。不要依赖 Docker 忽略规则处理这些已打入 Jar 的文件。生产环境应注入数据库、Redis、Kafka 和 Nacos 的真实配置与密钥，不能把生产密钥烘焙进镜像。

## 部署前检查

- MySQL 数据库结构已升级并与当前代码匹配。
- Nacos discovery/config namespace 和凭证正确。
- Redis 可达且数据隔离符合环境要求。
- 启用审计事件时 Kafka Topic 已建立。
- 所有敏感配置由部署环境注入。
- 发布提交上的 `mvn clean install` 已通过，并核对架构声明、文档和当前源码的一致性。
- 企业微信授权等跨项目变更已按实施顺序升级 `g2rain-basis-api` 和调用方。

## 运行观测

Actuator 暴露 `health` 和 `info`。生产环境应通过网关、网络策略或管理端口限制访问范围，不要无条件公开更多管理端点。
