# WMS-ByChatGPT

`WMS-ByChatGPT` 是一个前后端分离的仓储管理系统仓库。

当前仓库采用多目录结构：

```text
WMS-ByChatGPT
├── backend   # Spring Boot + SQLite + MyBatis-Plus 后端
├── frontend  # Vue 3 + Element Plus 前端（占位）
└── docs      # 项目文档
```

## 目录说明

### `backend`

后端服务目录，当前已经包含可运行的 Spring Boot 项目。

技术栈：

- Java 8
- Maven
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.7
- SQLite

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

如果本机 `8080` 已被占用，可以改用：

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### `frontend`

前端目录预留给后续的 `Vue 3 + Element Plus` 项目。

### `docs`

项目文档目录，后续可用于存放：

- 接口文档
- 数据库设计
- 需求说明
- 开发计划
- 部署说明

## 当前状态

目前仓库中已经完成后端基础框架拆分整理，前端与文档目录已预留，便于后续继续扩展。