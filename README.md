# WMS-ByChatGPT

`WMS-ByChatGPT` 是一个面向服务器部署、支持多端扩展的仓储管理系统仓库。

当前仓库采用前后端分离的多目录结构：

```text
WMS-ByChatGPT
├── backend   # Spring Boot 3.5 + MyBatis-Plus + MySQL + Redis
├── frontend  # Vue 3 管理后台
├── miniapp   # uni-app 小程序端（占位）
├── docs      # 项目文档
└── deploy    # 部署脚本与环境配置（占位）
```

## 目录说明

### `backend`

后端服务目录，当前已经完成第一步框架升级与重构：

- Java 17 基线
- Spring Boot 3.5.13
- MyBatis-Plus 3.5.15
- MySQL 数据源配置
- Redis 基础接入
- `com.wms.common + com.wms.modules` 分层结构

进入后端目录后启动：

```bash
cd backend
mvn spring-boot:run
```

打包命令：

```bash
cd backend
mvn package
```

### `frontend`

预留给后续的 `Vue 3 + Vite + TypeScript + Element Plus` 管理后台工程。

### `miniapp`

预留给后续的 `uni-app` 小程序端工程，与管理后台共用统一后端 API。

### `docs`

用于存放项目文档，例如：

- 开发文档
- 数据库设计
- 接口设计
- 业务流程说明
- 部署说明

### `deploy`

用于存放服务器部署相关资源，例如：

- Dockerfile
- Docker Compose
- Nginx 配置
- 启停脚本

## 当前阶段

当前已完成第一步的主要目标：后端框架升级、数据库与缓存配置切换、包结构重构，以及仓库多端目录预留。后续会继续按开发文档推进权限体系、基础业务模块、前端管理后台和小程序端。
