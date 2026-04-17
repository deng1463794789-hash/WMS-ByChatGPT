# Local Warehouse Backend

本目录是 `WMS-ByChatGPT` 仓库中的后端模块，当前是一个可运行的 Spring Boot 后端基础框架。

技术栈：

- Java 8
- Maven
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.7
- SQLite

## 当前已实现内容

- Spring Boot 后端基础工程
- SQLite 数据库自动初始化
- MyBatis-Plus 基础接入
- 统一响应体 `ApiResponse`
- 全局异常处理 `GlobalExceptionHandler`
- 跨域配置，便于后续对接前端
- `product` 示例模块
- `system` 健康检查接口

## 当前接口

### 系统接口

- `GET /api/system/ping`

### 商品接口

- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

## 数据库

SQLite 文件默认位于：

```text
./data/warehouse.db
```

初始化脚本：

```text
src/main/resources/schema.sql
```

当前表：

- `wms_product`
- `wms_stock_record`

## 从仓库根目录进入 backend 后启动

```bash
cd backend
mvn spring-boot:run
```

如果端口 `8080` 被占用：

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## 打包

```bash
cd backend
mvn package
```

## 说明

当前版本仍然是第一阶段的后端基础框架，后续会继续扩展完整的仓储业务模块，并与 `frontend` 目录下的 Vue 3 + Element Plus 前端对接。