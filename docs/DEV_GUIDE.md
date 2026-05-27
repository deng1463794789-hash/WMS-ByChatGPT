# WMS 仓储管理系统 - 开发文档

## 1. 项目概述

### 1.1 项目简介

`WMS-ByChatGPT` 是一个面向服务器部署、支持多端扩展的仓储管理系统，采用前后端分离的多目录结构设计。

### 1.2 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 基础环境 | Java | 17 |
| 构建工具 | Maven | 3.x |
| 后端框架 | Spring Boot | 3.5.13 |
| ORM 框架 | MyBatis-Plus | 3.5.15 |
| 数据库 | MySQL | 8.x |
| 缓存 | Redis | 6.x |
| 测试 | JUnit + Spring Boot Test | - |

### 1.3 目录结构

```
WMS-ByChatGPT
├── backend           # Spring Boot 后端服务
├── frontend          # Vue 3 管理后台（预留）
├── miniapp           # uni-app 小程序端（预留）
├── docs              # 项目文档
└── deploy            # 部署脚本与环境配置
```

## 2. 后端工程结构

### 2.1 包结构

```
src/main/java/com/wms
├── WmsApplication.java           # 应用启动类
├── common                          # 公共组件
│   ├── api/
│   │   └── ApiResponse.java       # 统一响应封装
│   ├── config/
│   │   ├── MybatisPlusConfig.java # MyBatis-Plus 配置
│   │   └── WebMvcConfig.java      # Web MVC 配置（跨域）
│   └── exception/
│       ├── BusinessException.java        # 业务异常
│       └── GlobalExceptionHandler.java  # 全局异常处理
└── modules                         # 业务模块
    ├── product/                   # 商品模块
    │   ├── controller/
    │   ├── dto/
    │   ├── entity/
    │   ├── mapper/
    │   ├── service/
    │   └── vo/
    └── system/                    # 系统模块
        └── controller/
```

### 2.2 模块职责

| 模块 | 说明 |
|------|------|
| common | 公共组件：统一响应、异常处理、配置类 |
| product | 商品管理：商品 CRUD 操作 |
| system | 系统管理：健康检查等基础接口 |

## 3. 数据库设计

### 3.1 商品表 (wms_product)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| sku | VARCHAR(64) | NOT NULL, UNIQUE | 商品编码 |
| name | VARCHAR(128) | NOT NULL | 商品名称 |
| unit | VARCHAR(32) | NOT NULL | 单位 |
| stock_quantity | INT | NOT NULL, DEFAULT 0 | 库存数量 |
| safe_stock | INT | NOT NULL, DEFAULT 0 | 安全库存 |
| remark | VARCHAR(255) | NULL | 备注 |
| created_at | TIMESTAMP | NOT NULL | 创建时间 |
| updated_at | TIMESTAMP | NOT NULL | 更新时间 |

### 3.2 库存记录表 (wms_stock_record)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| product_id | BIGINT | NOT NULL, FK | 商品ID |
| biz_type | VARCHAR(32) | NOT NULL | 业务类型 |
| change_quantity | INT | NOT NULL | 变动数量 |
| after_quantity | INT | NOT NULL | 变动后数量 |
| biz_no | VARCHAR(64) | NULL | 业务单号 |
| remark | VARCHAR(255) | NULL | 备注 |
| created_at | TIMESTAMP | NOT NULL | 创建时间 |

**外键关系**: `product_id` 引用 `wms_product(id)`

### 3.3 初始化脚本

数据库初始化脚本位于：`src/main/resources/schema-mysql.sql`

## 4. API 接口规范

### 4.1 统一响应格式

```json
{
    "code": 200,
    "message": "success",
    "data": {}
}
```

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 / 业务异常 |
| 500 | 服务器内部错误 |

### 4.2 系统接口

#### 健康检查

```
GET /api/system/ping
```

响应示例：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "service": "wms-backend",
        "status": "UP"
    }
}
```

### 4.3 商品接口

#### 分页查询

```
GET /api/products
```

**请求参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| pageNum | long | 否 | 1 | 页码 |
| pageSize | long | 否 | 10 | 每页数量 |
| keyword | string | 否 | null | 搜索关键词（匹配 sku、name） |

**响应示例**：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "records": [...],
        "total": 100,
        "size": 10,
        "current": 1,
        "pages": 10
    }
}
```

#### 详情查询

```
GET /api/products/{id}
```

#### 新增商品

```
POST /api/products
```

**请求体**：
```json
{
    "sku": "SKU001",
    "name": "商品名称",
    "unit": "个",
    "stockQuantity": 100,
    "safeStock": 10,
    "remark": "备注"
}
```

**校验规则**：
- `sku`: 非空，长度 ≤ 64
- `name`: 非空，长度 ≤ 128
- `unit`: 非空，长度 ≤ 32
- `stockQuantity`: 非空，≥ 0
- `safeStock`: 非空，≥ 0
- `remark`: 长度 ≤ 255

#### 更新商品

```
PUT /api/products/{id}
```

请求体同新增接口。

#### 删除商品

```
DELETE /api/products/{id}
```

## 5. 配置说明

### 5.1 多环境配置

| 环境 | 配置文件 | 用途 |
|------|----------|------|
| dev | application-dev.yml | 开发环境 |
| test | application-test.yml | 测试环境（使用 H2 内存数据库） |
| prod | application-prod.yml | 生产环境 |

### 5.2 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| WMS_SERVER_PORT | 服务端口 | 8080 |
| WMS_DB_HOST | 数据库地址 | 127.0.0.1 |
| WMS_DB_PORT | 数据库端口 | 3306 |
| WMS_DB_NAME | 数据库名 | wms_by_chatgpt |
| WMS_DB_USERNAME | 数据库用户名 | root |
| WMS_DB_PASSWORD | 数据库密码 | root |
| WMS_REDIS_HOST | Redis 地址 | 127.0.0.1 |
| WMS_REDIS_PORT | Redis 端口 | 6379 |
| WMS_REDIS_DATABASE | Redis 数据库 | 0 |
| WMS_REDIS_PASSWORD | Redis 密码 | (空) |

### 5.3 配置文件位置

- 主配置：`src/main/resources/application.yml`
- 开发配置：`src/main/resources/application-dev.yml`
- 测试配置：`src/main/resources/application-test.yml`
- 生产配置：`src/main/resources/application-prod.yml`

## 6. 业务模块开发规范

### 6.1 模块创建步骤

1. **创建 Entity**：对应数据库表，使用 `@TableName` 注解
2. **创建 Mapper**：继承 `BaseMapper<T>`，使用 `@Mapper` 注解
3. **创建 DTO**：请求数据传输对象，包含校验注解
4. **创建 VO**：响应视图对象
5. **创建 Service 接口**：定义业务方法，继承 `IService<T>`
6. **创建 ServiceImpl**：实现业务逻辑，继承 `ServiceImpl<Mapper, Entity>` 并实现 Service 接口
7. **创建 Controller**：定义 RESTful 接口

### 6.2 代码示例

**Controller 示例**：
```java
@RestController
@RequestMapping("/api/xxx")
public class XxxController {

    private final XxxService xxxService;

    public XxxController(XxxService xxxService) {
        this.xxxService = xxxService;
    }

    @GetMapping
    public ApiResponse<IPage<XxxVO>> page(...) {
        return ApiResponse.success(xxxService.pageXxx(...));
    }
}
```

**Service 实现示例**：
```java
@Service
public class XxxServiceImpl extends ServiceImpl<XxxMapper, Xxx> implements XxxService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void businessMethod(XxxRequest request) {
        // 业务逻辑
    }
}
```

### 6.3 校验与异常处理

- 使用 `jakarta.validation` 注解进行参数校验
- 业务异常使用 `BusinessException`
- 全局异常由 `GlobalExceptionHandler` 统一处理

## 7. 项目构建与运行

### 7.1 启动应用

```bash
cd backend
mvn spring-boot:run
```

### 7.2 打包

```bash
cd backend
mvn package
```

### 7.3 运行测试

```bash
cd backend
mvn test
```

## 8. 后续开发计划

- [ ] 权限体系（认证与授权）
- [ ] 基础主数据模块（供应商、客户、仓库等）
- [ ] 库存业务模块（入库、出库、盘点）
- [ ] 前端管理后台（Vue 3 + Vite + TypeScript + Element Plus）
- [ ] 小程序端（uni-app）
- [ ] 接口文档（Swagger/OpenAPI）
