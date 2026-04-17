# Local Warehouse Backend

本项目是一个本地部署的仓储管理系统后端基础框架，基于 `Spring Boot 2.7 + MyBatis-Plus + SQLite + Maven + Java 8` 搭建。

当前版本的目标不是完整业务，而是先提供一个可以直接启动、可以连接数据库、结构清晰、方便后续扩展和迁移 MySQL 的后端骨架。

## 当前技术栈

- Java 8
- Maven
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.7
- SQLite
- Tomcat Embedded

## 当前已实现功能

当前项目已经具备以下基础能力：

- Spring Boot 后端项目可正常启动
- SQLite 数据库自动初始化
- MyBatis-Plus 已接入并可进行基础数据操作
- 提供统一响应体 `ApiResponse`
- 提供全局异常处理 `GlobalExceptionHandler`
- 提供跨域配置，便于后续对接 Vue 前端
- 提供商品 `product` 示例模块，作为后续仓储业务扩展模板

## 当前模块说明

### 1. 系统模块

用于验证服务是否正常运行。

接口：

- `GET /api/system/ping`

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "service": "local-warehouse-backend",
    "status": "UP"
  }
}
```

### 2. 商品模块

当前作为演示模块存在，用于验证 Controller、Service、Mapper、Entity、DTO、VO 这一整套分层结构是可工作的。

已提供接口：

- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

支持能力：

- 商品新增
- 商品修改
- 商品删除
- 商品详情查询
- 商品分页查询
- 按 `sku` / `name` 关键字模糊查询
- `sku` 唯一性校验

## 项目结构

```text
src/main/java/com/localwarehouse/backend
├── common
│   ├── api
│   └── exception
├── config
├── module
│   ├── product
│   │   ├── controller
│   │   ├── dto
│   │   ├── entity
│   │   ├── mapper
│   │   ├── service
│   │   └── vo
│   └── system
│       └── controller
```

分层职责：

- `controller`：对外提供 REST 接口
- `service`：处理业务逻辑
- `mapper`：数据库访问层
- `entity`：数据库实体对象
- `dto`：请求参数对象
- `vo`：返回结果对象
- `config`：项目配置类
- `common`：公共响应、异常处理等通用能力

## 当前数据库设计

项目启动时会自动执行 `src/main/resources/schema.sql` 初始化数据库。

当前已创建表：

- `wms_product`：商品表
- `wms_stock_record`：库存流水表基础结构

SQLite 数据库文件位置：

```text
./data/warehouse.db
```

## 启动方式

### 1. 命令行启动

```bash
mvn spring-boot:run
```

### 2. 指定端口启动

如果本机 `8080` 已被占用，可以临时指定其他端口：

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## 默认访问地址

默认端口为 `8080`，如果改了端口请自行替换。

- 健康检查：`GET /api/system/ping`
- 商品分页：`GET /api/products`
- 商品详情：`GET /api/products/{id}`

注意：

- 访问 `http://localhost:8080/` 会返回 404，这是正常的
- 当前项目是纯后端接口项目，没有首页页面

## 示例请求

### 新增商品

```http
POST /api/products
Content-Type: application/json
```

```json
{
  "sku": "P10001",
  "name": "测试商品",
  "unit": "件",
  "stockQuantity": 100,
  "safeStock": 20,
  "remark": "演示数据"
}
```

## 当前限制

当前版本仍然是第一阶段的后端基础框架，尚未实现：

- 用户登录与权限控制
- 采购管理
- 销售管理
- 入库单/出库单业务
- 库存盘点
- 库位管理
- 操作日志
- 与 Vue 前端联调页面

## 后续建议迭代方向

建议下一步按以下顺序继续扩展：

1. 完善商品、供应商、客户基础资料
2. 增加入库、出库、库存流水完整业务
3. 增加统一分页返回结构和查询条件封装
4. 设计适配 Vue 的接口规范
5. 预留 SQLite 向 MySQL 迁移的字段和命名规范

## 说明

这是一个适合本地电脑部署的单机版后端项目，后续可以继续在此基础上扩展完整仓储管理系统功能。
