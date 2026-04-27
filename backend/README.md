# WMS Backend

本目录是 `WMS-ByChatGPT` 仓库中的后端模块。

当前版本已经完成第一步“框架升级与重构”，核心技术栈如下：

- Java 17
- Maven
- Spring Boot 3.5.13
- MyBatis-Plus 3.5.15
- MySQL
- Redis

## 当前已完成内容

- Spring Boot 3 后端基础工程
- 多环境配置：`dev / test / prod`
- MySQL 数据源配置
- Redis 基础接入
- MyBatis-Plus 分页插件配置
- 统一响应体 `ApiResponse`
- 全局异常处理 `GlobalExceptionHandler`
- 跨域配置
- `product` 示例模块
- `system` 健康检查接口

## 当前包结构

```text
src/main/java/com/wms
├── common
│   ├── api
│   ├── config
│   └── exception
├── modules
│   ├── product
│   └── system
└── WmsApplication.java
```

## 当前接口

### 系统接口

- `GET /api/system/ping`

### 商品接口

- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

## 数据库初始化

初始化脚本：

```text
src/main/resources/schema-mysql.sql
```

当前表：

- `wms_product`
- `wms_stock_record`

## 启动方式

从仓库根目录进入 `backend` 后启动：

```bash
cd backend
mvn spring-boot:run
```

默认启用 `dev` profile，并按以下环境变量连接本地 MySQL / Redis：

- `WMS_DB_HOST`
- `WMS_DB_PORT`
- `WMS_DB_NAME`
- `WMS_DB_USERNAME`
- `WMS_DB_PASSWORD`
- `WMS_REDIS_HOST`
- `WMS_REDIS_PORT`
- `WMS_REDIS_DATABASE`
- `WMS_REDIS_PASSWORD`

## 打包

```bash
cd backend
mvn package
```

## 说明

这一步的目标是先把后端技术基线切换到适合服务器部署和多端扩展的结构。后续会继续补齐权限体系、基础主数据、库存业务模块和接口文档。
