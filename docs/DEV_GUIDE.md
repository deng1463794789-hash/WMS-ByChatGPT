# WMS 仓储管理系统 - 开发文档

---

## 1. 项目概述

### 1.1 项目简介

`WMS-ByChatGPT` 是一个面向服务器部署、支持多端扩展的仓储管理系统，采用前后端分离的多目录结构设计。

### 1.2 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 基础环境 | Java | 17 |
| 构建工具 | Maven | 3.x |
| 后端框架 | Spring Boot | 3.5.13 |
| 安全框架 | Spring Security | 6.x |
| 认证令牌 | JWT (jjwt) | 0.12.6 |
| ORM 框架 | MyBatis-Plus | 3.5.15 |
| 数据库 | MySQL | 8.x |
| 缓存 | Redis | 6.x |
| 密码加密 | BCrypt | - |
| 测试 | JUnit + Spring Boot Test | - |
| 前端框架 | Vue 3 + Vite + TypeScript | 5.x / 3.4 |
| UI 组件库 | Element Plus | 2.7 |

### 1.3 目录结构

```
WMS-ByChatGPT
├── backend                    # Spring Boot 后端服务
│   ├── src/main/java/com/wms
│   ├── src/main/resources
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   ├── application-test.yml
│   │   ├── application-prod.yml
│   │   └── schema-mysql.sql
│   ├── src/test/java
│   ├── wms_init.sql           # 独立可运行的数据库初始化脚本
│   ├── pom.xml
│   └── README.md
├── frontend                   # Vue 3 管理后台
│   ├── src
│   │   ├── api/               # API 接口层
│   │   ├── components/        # 公共组件
│   │   ├── layouts/           # 布局组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # Pinia 状态管理
│   │   ├── utils/             # 工具函数 (axios 实例、存储等)
│   │   └── views/             # 页面视图
│   ├── .env.development
│   ├── .env.production
│   ├── vite.config.ts
│   └── package.json
├── miniapp                    # uni-app 小程序端（预留）
├── docs                       # 项目文档
├── deploy                     # 部署脚本
├── start-dev.ps1              # 一键启动后端+前端 (PowerShell)
├── start-dev.bat              # 一键启动后端+前端 (双击运行)
├── .gitignore
└── README.md
```

---

## 2. 后端工程结构

### 2.1 包结构

```
com.wms
├── WmsApplication.java                    # 应用启动类
├── common                                  # 公共组件
│   ├── annotation/
│   │   ├── AuditLog.java                  # 审计日志注解
│   │   ├── Idempotent.java                # 幂等性校验注解
│   │   └── RateLimit.java                 # 限流注解
│   ├── api/
│   │   └── ApiResponse.java               # 统一响应封装
│   ├── aspect/
│   │   ├── AuditLogAspect.java            # 审计日志 AOP 实现
│   │   ├── IdempotentAspect.java          # 幂等性 AOP 实现
│   │   └── RateLimitAspect.java           # 限流 AOP 实现
│   ├── config/
│   │   ├── DataInitializer.java           # 首次启动管理员初始化
│   │   ├── MyMetaObjectHandler.java       # MyBatis-Plus 自动填充
│   │   ├── MybatisPlusConfig.java         # MyBatis-Plus 配置（乐观锁拦截器）
│   │   └── WebMvcConfig.java              # Web MVC 配置
│   ├── exception/
│   │   ├── BusinessException.java         # 业务异常
│   │   └── GlobalExceptionHandler.java    # 全局异常处理（含 Security 异常）
│   ├── filter/
│   │   └── XssFilter.java                 # XSS 过滤拦截器
│   └── security/
│       ├── SecurityConfig.java            # Spring Security 主配置
│       ├── JwtUtils.java                  # JWT 令牌生成/校验工具
│       ├── JwtAuthFilter.java             # JWT 认证过滤器
│       ├── CustomUserDetails.java         # Spring Security UserDetails 实现
│       ├── UserDetailsServiceImpl.java    # 从数据库加载用户
│       └── SecurityUtils.java             # 获取当前登录用户工具类
├── modules                                 # 业务模块
│   ├── auth/                              # 认证模块
│   │   ├── controller/AuthController.java
│   │   ├── dto/LoginRequest.java / LoginResponse.java
│   │   └── service/AuthService.java
│   ├── customer/                          # 客户模块
│   │   ├── controller/CustomerController.java
│   │   ├── entity/Customer.java
│   │   ├── mapper/CustomerMapper.java
│   │   └── service/CustomerService.java
│   ├── employee/                          # 员工模块
│   │   ├── controller/EmployeeController.java
│   │   ├── entity/Employee.java
│   │   ├── mapper/EmployeeMapper.java
│   │   └── service/EmployeeService.java
│   ├── expense/                           # 报销模块
│   │   ├── controller/ExpenseController.java
│   │   ├── entity/Expense.java
│   │   ├── mapper/ExpenseMapper.java
│   │   └── service/ExpenseService.java
│   ├── inventory/                         # 库存模块（入库/出库）
│   │   ├── controller/InboundController.java / OutboundController.java
│   │   ├── entity/InboundRecord.java / InboundItem.java / OutboundRecord.java / OutboundItem.java
│   │   ├── mapper/ (4 个 Mapper)
│   │   └── service/InboundService.java / OutboundService.java
│   ├── product/                           # 商品模块
│   │   ├── controller/ProductController.java / CategoryController.java / DashboardController.java
│   │   ├── dto/ProductCreateRequest.java / ProductUpdateRequest.java
│   │   ├── entity/Product.java / Category.java
│   │   ├── mapper/ProductMapper.java / CategoryMapper.java
│   │   ├── service/ProductService.java
│   │   └── vo/ProductVO.java
│   ├── supplier/                          # 供应商模块
│   │   ├── controller/SupplierController.java
│   │   ├── entity/Supplier.java
│   │   ├── mapper/SupplierMapper.java
│   │   └── service/SupplierService.java
│   ├── system/                            # 系统模块（用户/角色/菜单/设置）
│   │   ├── controller/SystemController.java
│   │   ├── entity/SysUser.java / SysRole.java / SysMenu.java / SysSetting.java
│   │   ├── mapper/ (4 个 Mapper)
│   │   └── service/SystemService.java
│   └── warehouse/                         # 仓库模块
│       ├── controller/WarehouseController.java
│       ├── entity/Warehouse.java
│       ├── mapper/WarehouseMapper.java
│       └── service/WarehouseService.java
```

---

## 3. 数据库设计

数据库名：`wms_by_chatgpt`，字符集 `utf8mb4`，存储引擎 `InnoDB`。

### 3.1 表总览（18 张表）

| # | 表名 | 说明 | 主键 |
|---|------|------|------|
| 1 | wms_product | 商品表 | id (自增) |
| 2 | wms_category | 商品分类表 | id (自增) |
| 3 | wms_stock_record | 库存变动记录表 | id (自增) |
| 4 | wms_user | 系统用户表 | id (自增) |
| 5 | wms_role | 系统角色表 | id (自增) |
| 6 | wms_menu | 菜单权限表 | id (自增) |
| 7 | wms_role_menu | 角色-菜单关联表 | id (自增) |
| 8 | wms_system_setting | 系统配置表 | id (自增) |
| 9 | wms_audit_log | 操作审计日志表 | id (自增) |
| 10 | wms_warehouse | 仓库表 | id (自增) |
| 11 | wms_supplier | 供应商表 | id (自增) |
| 12 | wms_customer | 客户表 | id (自增) |
| 13 | wms_employee | 员工表 | id (自增) |
| 14 | wms_expense | 报销表 | id (自增) |
| 15 | wms_inbound | 入库单主表 | id (自增) |
| 16 | wms_inbound_item | 入库单明细表 | id (自增) |
| 17 | wms_outbound | 出库单主表 | id (自增) |
| 18 | wms_outbound_item | 出库单明细表 | id (自增) |

### 3.2 通用字段约定

所有业务表统一包含以下字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| is_deleted | TINYINT(1) | 逻辑删除标记：0-正常，1-已删除 |
| created_at | TIMESTAMP | 创建时间，自动填充 |
| updated_at | TIMESTAMP | 更新时间，自动更新 |

商品表额外包含：`version INT`（乐观锁版本号）。

用户表额外包含：`login_fail_count INT`、`lock_until DATETIME`（防暴力破解）。

### 3.3 核心表结构

#### 商品表 (wms_product)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK AUTO_INCREMENT | 主键 |
| sku | VARCHAR(64) | NOT NULL UNIQUE | 商品编码 |
| name | VARCHAR(128) | NOT NULL | 商品名称 |
| unit | VARCHAR(32) | NOT NULL | 单位 |
| stock_quantity | INT | NOT NULL DEFAULT 0 | 库存数量 |
| safe_stock | INT | NOT NULL DEFAULT 0 | 安全库存阈值 |
| category_id | BIGINT | NULL | 分类 ID |
| price | DECIMAL(10,2) | NULL | 单价 |
| remark | VARCHAR(255) | NULL | 备注 |
| status | VARCHAR(16) | DEFAULT 'active' | 状态 |
| version | INT | NOT NULL DEFAULT 0 | 乐观锁版本号 |
| is_deleted | TINYINT | NOT NULL DEFAULT 0 | 逻辑删除 |
| created_at | TIMESTAMP | NOT NULL | 创建时间 |
| updated_at | TIMESTAMP | NOT NULL | 更新时间 |

#### 系统用户表 (wms_user)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK AUTO_INCREMENT | 主键 |
| username | VARCHAR(64) | NOT NULL UNIQUE | 用户名 |
| password | VARCHAR(128) | NOT NULL | 密码（BCrypt 加密） |
| real_name | VARCHAR(64) | NOT NULL | 真实姓名 |
| phone | VARCHAR(20) | NULL | 手机号 |
| email | VARCHAR(64) | NULL | 邮箱 |
| avatar | VARCHAR(255) | NULL | 头像 URL |
| role_id | BIGINT | NULL | 角色 ID |
| status | VARCHAR(16) | DEFAULT 'active' | active / locked / inactive |
| login_fail_count | INT | DEFAULT 0 | 连续登录失败次数 |
| lock_until | DATETIME | NULL | 锁定截止时间 |
| is_deleted | TINYINT | NOT NULL DEFAULT 0 | 逻辑删除 |
| created_at | TIMESTAMP | NOT NULL | 创建时间 |
| updated_at | TIMESTAMP | NOT NULL | 更新时间 |

#### 菜单权限表 (wms_menu)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK AUTO_INCREMENT | 主键 |
| parent_id | BIGINT | NOT NULL DEFAULT 0 | 父级菜单 ID |
| name | VARCHAR(64) | NOT NULL | 菜单名称 |
| path | VARCHAR(128) | NULL | 路由路径 |
| component | VARCHAR(128) | NULL | 组件路径 |
| icon | VARCHAR(64) | NULL | 图标 |
| sort | INT | NOT NULL DEFAULT 0 | 排序 |
| type | VARCHAR(16) | DEFAULT 'menu' | 类型（menu / button） |
| permission | VARCHAR(128) | NULL | 权限标识 |
| status | VARCHAR(16) | DEFAULT 'active' | 状态 |
| is_deleted | TINYINT | DEFAULT 0 | 逻辑删除 |
| created_at | TIMESTAMP | NOT NULL | 创建时间 |

#### 入库单主表 (wms_inbound)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK AUTO_INCREMENT | 主键 |
| inbound_no | VARCHAR(64) | NOT NULL UNIQUE | 入库单号 |
| type | VARCHAR(32) | NOT NULL | 入库类型 |
| product_count | INT | DEFAULT 0 | 商品种类数 |
| total_quantity | INT | DEFAULT 0 | 总数量 |
| operator | VARCHAR(64) | NULL | 操作人 |
| inbound_time | DATETIME | NULL | 入库时间 |
| remark | VARCHAR(255) | NULL | 备注 |
| status | VARCHAR(16) | DEFAULT 'pending' | pending / completed / cancelled |
| is_deleted | TINYINT | DEFAULT 0 | 逻辑删除 |
| created_at | TIMESTAMP | NOT NULL | 创建时间 |

#### 入库单明细表 (wms_inbound_item)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK AUTO_INCREMENT | 主键 |
| inbound_id | BIGINT | NOT NULL FK | 入库单 ID |
| product_id | BIGINT | NOT NULL | 商品 ID |
| product_name | VARCHAR(128) | NULL | 商品名称 |
| sku | VARCHAR(64) | NULL | 商品编码 |
| quantity | INT | DEFAULT 0 | 数量 |
| price | DECIMAL(10,2) | NULL | 单价 |
| remark | VARCHAR(255) | NULL | 备注 |
| is_deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

> 出库单 (wms_outbound / wms_outbound_item) 结构与入库单对应，字段命名对应替换。

#### 操作审计日志表 (wms_audit_log)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK AUTO_INCREMENT | 主键 |
| user_id | BIGINT | NULL | 操作用户 ID |
| username | VARCHAR(64) | NULL | 操作用户名 |
| client_ip | VARCHAR(64) | NULL | 客户端 IP |
| http_method | VARCHAR(8) | NULL | 请求方法 |
| uri | VARCHAR(255) | NULL | 请求 URI |
| module | VARCHAR(64) | NULL | 模块名称 |
| action | VARCHAR(128) | NULL | 操作描述 |
| elapsed_ms | INT | NULL | 耗时（毫秒） |
| status | VARCHAR(16) | DEFAULT 'success' | success / failed |
| error_msg | VARCHAR(512) | NULL | 错误信息 |
| created_at | TIMESTAMP | NOT NULL | 操作时间 |

### 3.4 ER 关系

```
wms_user ─── role_id ───> wms_role
wms_role ───┬──> wms_role_menu <──┬── wms_menu
            │                      │
wms_product ─┬──> wms_inbound_item  >── wms_inbound
             └──> wms_outbound_item >── wms_outbound
             └──> wms_stock_record
wms_product ──> wms_category
```

### 3.5 初始化脚本

独立可运行的 SQL 文件位于 `backend/wms_init.sql`，包含：

- 自动建库
- 删表重建（可反复执行）
- 18 张表的完整 DDL
- 初始管理员 `admin / admin123`（BCrypt 加密）
- 3 个角色（管理员 / 仓库管理员 / 操作员）
- 17 个菜单和权限
- 系统配置默认值

使用方式：
```bash
mysql -u root -p < backend/wms_init.sql
```

---

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
| 401 | 未登录 / 认证失败 |
| 403 | 无权限 |
| 500 | 服务器内部错误 |

### 4.2 认证接口

所有接口需要在请求头携带 `Authorization: Bearer <token>`（登录接口除外）。

#### 登录

```
POST /api/auth/login
```

**请求体**：
```json
{
    "username": "admin",
    "password": "admin123",
    "remember": true
}
```

**校验规则**：
- `username`: 非空
- `password`: 非空

**响应**：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbG...",
        "refreshToken": "eyJhbG...",
        "userInfo": {
            "id": 1,
            "username": "admin",
            "realName": "系统管理员",
            "avatar": null,
            "roleId": 1,
            "roleName": "管理员"
        },
        "menus": [ ... ]
    }
}
```

**安全机制**：同一 IP 5 分钟内最多尝试 5 次（`@RateLimit(maxAttempts=5, windowSeconds=300)`）。

#### 登出

```
POST /api/auth/logout
```

无需请求体。登出后 token 失效（前端清除 token 即可，无状态 JWT 服务端不做额外处理）。

#### 获取当前用户信息

```
GET /api/auth/userinfo
```

从 SecurityContext 获取当前登录用户，返回用户信息和角色。

#### 获取当前用户菜单树

```
GET /api/auth/menus
```

根据当前用户的 role_id，查询该角色拥有的菜单权限，返回树形结构。

### 4.3 系统管理接口

> 标注 `ROLE_ADMIN` 表示仅管理员可访问。

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/users | 用户分页列表 | ROLE_ADMIN |
| POST | /api/users | 创建用户 | ROLE_ADMIN |
| PUT | /api/users/{id} | 修改用户 | ROLE_ADMIN |
| DELETE | /api/users/{id} | 删除用户（逻辑删除） | ROLE_ADMIN |
| PUT | /api/users/{id}/reset-password | 重置密码 | ROLE_ADMIN |
| GET | /api/roles | 角色分页列表 | ROLE_ADMIN |
| GET | /api/roles/all | 全部角色下拉 | ROLE_ADMIN |
| POST | /api/roles | 创建角色 | ROLE_ADMIN |
| PUT | /api/roles/{id} | 修改角色 | ROLE_ADMIN |
| DELETE | /api/roles/{id} | 删除角色 | ROLE_ADMIN |
| GET | /api/permissions/tree | 权限菜单树 | ROLE_ADMIN |
| GET | /api/system/setting | 获取系统设置 | ROLE_ADMIN |
| PUT | /api/system/setting | 修改系统设置 | ROLE_ADMIN |
| GET | /api/system/ping | 健康检查 | 无需认证 |

#### 创建用户

```
POST /api/users
```

**请求体**：
```json
{
    "username": "zhangsan",
    "password": "123456",
    "realName": "张三",
    "phone": "13800138000",
    "email": "zhangsan@wms.com",
    "roleId": 2,
    "status": "active"
}
```

密码在服务端使用 BCrypt 加密后存储，不在日志中打印原始密码。

#### 创建角色

```
POST /api/roles
```

**请求体**：
```json
{
    "code": "finance",
    "name": "财务",
    "description": "财务相关权限",
    "menuIds": [1, 2, 3]
}
```

### 4.4 商品管理接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/products | 商品分页列表 | 需登录 |
| GET | /api/products/{id} | 商品详情 | 需登录 |
| POST | /api/products | 创建商品 | 需登录 |
| PUT | /api/products/{id} | 修改商品 | 需登录 |
| DELETE | /api/products/{id} | 删除商品（逻辑删除） | ROLE_ADMIN |
| POST | /api/products/batch-delete | 批量删除 | ROLE_ADMIN |
| PUT | /api/products/{id}/stock | 调整库存 | 需登录 |
| GET | /api/products/export | 导出数据 | 需登录 |

#### 分页查询

```
GET /api/products?pageNum=1&pageSize=10&keyword=螺丝&stockStatus=low
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | long | 否 | 页码，默认 1 |
| pageSize | long | 否 | 每页数量，默认 10 |
| keyword | string | 否 | 模糊搜索（sku / name） |
| stockStatus | string | 否 | low（低于安全库存）/ out（缺货） |

#### 调整库存

```
PUT /api/products/{id}/stock
```

**请求体**：
```json
{
    "stockQuantity": 100,
    "remark": "手动盘点调整"
}
```

> 该操作会同时往 wms_stock_record 插入一条变动记录。

### 4.5 分类接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/categories | 全量分类列表 | 需登录 |
| GET | /api/dashboard | 仪表盘数据 | 需登录 |

### 4.6 仓库管理接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/warehouses | 仓库分页列表 | 需登录 |
| GET | /api/warehouses/{id} | 仓库详情 | 需登录 |
| POST | /api/warehouses | 创建仓库 | 需登录 |
| PUT | /api/warehouses/{id} | 修改仓库 | 需登录 |
| DELETE | /api/warehouses/{id} | 删除仓库（逻辑删除） | 需登录 |
| POST | /api/warehouses/batch-delete | 批量删除 | 需登录 |

### 4.7 供应商管理接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/suppliers | 供应商分页列表 | 需登录 |
| GET | /api/suppliers/{id} | 供应商详情 | 需登录 |
| POST | /api/suppliers | 创建供应商 | 需登录 |
| PUT | /api/suppliers/{id} | 修改供应商 | 需登录 |
| DELETE | /api/suppliers/{id} | 删除供应商（逻辑删除） | 需登录 |
| POST | /api/suppliers/batch-delete | 批量删除 | 需登录 |

### 4.8 客户管理接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/customers | 客户分页列表 | 需登录 |
| GET | /api/customers/{id} | 客户详情 | 需登录 |
| POST | /api/customers | 创建客户 | 需登录 |
| PUT | /api/customers/{id} | 修改客户 | 需登录 |
| DELETE | /api/customers/{id} | 删除客户（逻辑删除） | 需登录 |
| POST | /api/customers/batch-delete | 批量删除 | 需登录 |

### 4.9 员工管理接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/employees | 员工分页列表 | 需登录 |
| GET | /api/employees/{id} | 员工详情 | 需登录 |
| POST | /api/employees | 创建员工 | 需登录 |
| PUT | /api/employees/{id} | 修改员工 | 需登录 |
| DELETE | /api/employees/{id} | 删除员工（逻辑删除） | 需登录 |
| POST | /api/employees/batch-delete | 批量删除 | 需登录 |

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | long | 否 | 页码 |
| pageSize | long | 否 | 每页数量 |
| keyword | string | 否 | 模糊搜索（name / code） |
| department | string | 否 | 按部门筛选 |

### 4.10 报销管理接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/expenses | 报销单分页列表 | 需登录 |
| GET | /api/expenses/{id} | 报销单详情 | 需登录 |
| POST | /api/expenses | 创建报销单 | 需登录 |
| PUT | /api/expenses/{id} | 修改报销单 | 需登录 |
| PUT | /api/expenses/{id}/approve | 审批报销单 | 需登录 |
| DELETE | /api/expenses/{id} | 删除报销单（逻辑删除） | 需登录 |
| POST | /api/expenses/batch-delete | 批量删除 | 需登录 |

**查询参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | long | 否 | 页码 |
| pageSize | long | 否 | 每页数量 |
| keyword | string | 否 | 模糊搜索 |
| status | string | 否 | pending / approved / rejected |
| type | string | 否 | 报销类型 |

**审批请求体**：
```json
{
    "action": "approve",
    "reason": "审批通过"
}
```

### 4.11 入库管理接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/inbound | 入库单分页列表 | 需登录 |
| GET | /api/inbound/{id} | 入库单详情（含明细） | 需登录 |
| POST | /api/inbound | 创建入库单（幂等性校验） | ROLE_ADMIN / ROLE_WAREHOUSE |

#### 创建入库单

```
POST /api/inbound
```

**请求体**：
```json
{
    "type": "采购入库",
    "inboundTime": "2026-05-28 10:00:00",
    "operator": "张三",
    "remark": "采购入库",
    "items": [
        {
            "productId": 1,
            "quantity": 100,
            "remark": ""
        }
    ]
}
```

**幂等性保护**：同一请求在 5 秒内重复提交会被拒绝（`@Idempotent(expire=5)`）。前端需生成并传入 `X-Idempotent-Key` 头部。

### 4.12 出库管理接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/outbound | 出库单分页列表 | 需登录 |
| GET | /api/outbound/{id} | 出库单详情（含明细） | 需登录 |
| POST | /api/outbound | 创建出库单（幂等性校验） | ROLE_ADMIN / ROLE_WAREHOUSE |

请求体和入库单类似，字段对应替换。

---

## 5. 安全体系

### 5.1 架构概览

```
请求进来
    │
    ▼
┌──────────────────┐
│   XssFilter      │  ← 对所有请求参数做 HTML 转义
└────────┬─────────┘
         ▼
┌──────────────────┐
│  JwtAuthFilter   │  ← 从 Authorization 头提取 Bearer Token，验证并注入 SecurityContext
└────────┬─────────┘
         ▼
┌──────────────────┐
│ Spring Security  │  ← URL 级别鉴权（SecurityConfig.filterChain）
│  + @Secured      │  ← 方法级别鉴权（@Secured("ROLE_ADMIN")）
└────────┬─────────┘
         ▼
┌──────────────────┐
│  @AuditLog AOP   │  ← 记录操作审计日志
│  @RateLimit AOP  │  ← 接口限流（登录等）
│  @Idempotent AOP │  ← 幂等性校验（入库/出库）
└────────┬─────────┘
         ▼
    Controller → Service → Mapper → DB
```

### 5.2 认证流程

1. 用户登录 → `AuthController.login()` → `AuthenticationManager.authenticate()` 验证 BCrypt 密码
2. 密码正确 → `JwtUtils.generateToken()` 生成 JWT（HS256，默认 24 小时有效）
3. 客户端获取 token，后续每次请求在 Header 中携带：`Authorization: Bearer <token>`
4. `JwtAuthFilter` 拦截请求，解析 token，从数据库加载 `UserDetails`，设置 `SecurityContext`

### 5.3 权限模型（RBAC）

```
用户 (wms_user) ── role_id ──> 角色 (wms_role) ──> 角色-菜单 (wms_role_menu) ──> 菜单/权限 (wms_menu)
```

- **URL 级别**：SecurityConfig 中定义基于 `hasRole()` 的路径规则
- **方法级别**：Controller 类或方法上使用 `@Secured("ROLE_ADMIN")`

### 5.4 角色定义

| 角色 code | 说明 | 权限范围 |
|-----------|------|----------|
| admin | 管理员 | 所有权限，系统管理、删除操作 |
| warehouse | 仓库管理员 | 入库/出库创建，基础 CRUD |
| operator | 操作员 | 查询、查看，无删除和系统管理权限 |

### 5.5 安全加固清单

| 安全措施 | 实现位置 | 说明 |
|----------|----------|------|
| BCrypt 密码加密 | SecurityConfig + SystemService | 注册时自动加密，登录时自动比对 |
| JWT 无状态认证 | JwtUtils + JwtAuthFilter | HMAC-SHA256 签名，支持 refresh token |
| CORS 白名单 | SecurityConfig.corsConfigurationSource() | 仅允许 localhost / 127.0.0.1 |
| XSS 过滤 | XssFilter | 所有输入参数 HTML 实体转义 |
| 登录限流 | @RateLimit + RateLimitAspect | 同 IP 5 分钟最多 5 次 |
| 幂等性校验 | @Idempotent + IdempotentAspect | 防止重复提交入库/出库单 |
| 操作审计 | @AuditLog + AuditLogAspect | 记录所有增删改操作 |
| 逻辑删除 | MyBatis-Plus @TableLogic | 所有 DELETE 操作转为 UPDATE is_deleted=1 |
| 乐观锁 | MyBatis-Plus @Version + OptimisticLockerInnerInterceptor | 防止并发修改商品库存 |
| 数据脱敏 | SystemService.maskPhone() | 用户列表中手机号中间四位脱敏 |
| Security 异常统一处理 | GlobalExceptionHandler | 401/403 返回友好 JSON |

### 5.6 默认账户

首次启动时 `DataInitializer` 自动创建：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |

---

## 6. 配置说明

### 6.1 多环境配置

| 环境 | 配置文件 | 激活方式 |
|------|----------|----------|
| dev | application-dev.yml | 默认，`spring.profiles.active=dev` |
| test | application-test.yml | 使用 H2 内存数据库 |
| prod | application-prod.yml | 生产环境 |

### 6.2 环境变量

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
| WMS_JWT_SECRET | JWT 签名密钥 | (内置默认值) |
| WMS_JWT_EXPIRATION | JWT 过期时间（毫秒） | 86400000 (24h) |

### 6.3 JWT 配置（application-dev.yml）

```yaml
wms:
  jwt:
    secret: VGhpcy1Jcy1XTVMtU2VjdXJlLUtleS1Gb3ItSldULU1pbmltdW0tMjU2LUJpdHM=
    expiration: 86400000
```

生产环境请更换 `secret` 为随机生成的 256-bit 密钥。

---

## 7. 项目构建与运行

### 7.1 一键启动（开发环境）

双击项目根目录下的 `start-dev.bat`，脚本自动：
1. 检查 Maven / Node.js 环境
2. 自动 `npm install` 前端依赖（首次）
3. 并行启动后端（8080 端口）和前端（3000 端口）
4. 自动打开浏览器访问 `http://localhost:3000`
5. Ctrl+C 或关闭窗口自动停止所有服务

```
./start-dev.bat              # Windows 双击运行
./start-dev.ps1              # PowerShell 运行
```

### 7.2 手动启动

**后端**：
```bash
cd backend
mvn spring-boot:run
```

**前端**：
```bash
cd frontend
npm install      # 首次需要
npm run dev
```

### 7.3 打包

```bash
cd backend
mvn package
```

产物：`backend/target/wms-backend-0.1.0-SNAPSHOT.jar`

### 7.4 运行测试

```bash
cd backend
mvn test
```

---

## 8. 部署架构（推荐）

### 8.1 生产环境拓扑

```
                        ┌─────────────────────┐
    用户 / 小程序 ←────→ │     Nginx :443      │
    管理员浏览器 ←────→  │  (HTTPS / 反向代理)  │
                        └──────┬──────────────┘
                               │
                    ┌──────────┴──────────┐
                    │                      │
              /api/*                    /*
              → 后端 :8080              → 管理后台静态文件
              (java -jar)               (frontend/dist/)
```

### 8.2 角色分工

| 组件 | 职责 |
|------|------|
| Nginx | HTTPS 终结、静态文件托管、反向代理 `api/*`、限流、日志 |
| 后端 JAR | 业务逻辑、API、认证鉴权 |
| 管理后台 | Nginx 托管纯静态文件 |
| 小程序端 | 直接调后端 API，不需要访问管理后台 |
| MySQL | 数据持久化 |
| Redis | 缓存、分布式锁 |

### 8.3 推荐部署方案

由于项目有管理后台 + 小程序两个前端，推荐使用 **Nginx + 独立后端 JAR** 的部署方式：
- 管理后台改版只需重新上传静态文件，无需重启后端
- 小程序只关心 API，与后台管理完全解耦
- 脚本方案（`start-dev.bat`）仅用于本地开发

---

## 9. 业务模块开发规范

### 9.1 模块创建步骤

1. **创建 Entity**：对应数据库表，使用 `@TableName`、`@TableLogic`、`@Version` 等注解
2. **创建 Mapper**：继承 `BaseMapper<T>`，使用 `@Mapper` 注解
3. **创建 Service 接口**：定义业务方法，继承 `IService<T>`
4. **创建 ServiceImpl**：实现业务逻辑，继承 `ServiceImpl<Mapper, Entity>` 并实现 Service 接口
5. **创建 Controller**：定义 RESTful 接口，标注 `@AuditLog`

### 6.2 统一约定

- 所有 DELETE 操作均为逻辑删除，由 MyBatis-Plus `@TableLogic` 自动处理
- 所有写操作（POST / PUT / DELETE）必须加 `@AuditLog`
- 涉及库存修改的操作使用 `@Version` 乐观锁，捕获并发冲突
- 入库/出库等不可重入操作使用 `@Idempotent` 幂等校验
- 用户密码使用 `passwordEncoder.encode()` 加密，不可存储明文
- 接口返回敏感数据（手机号等）需要脱敏处理
- CORS 仅对 `localhost` / `127.0.0.1` 开放

---

## 10. 前端开发说明

### 10.1 技术栈

| 技术 | 版本 |
|------|------|
| Vue 3 | ^3.4 |
| Vite | ^5.2 |
| TypeScript | ~5.4 |
| Vue Router | ^4.3 |
| Pinia | ^2.1 |
| Axios | ^1.6 |
| Element Plus | ^2.7 |
| Sass | ^1.72 |

### 10.2 目录结构

```
frontend/src
├── api/               # 接口封装 & 类型定义
│   ├── types/         # 类型声明
│   ├── product.ts     # 商品接口
│   └── system.ts      # 系统接口
├── components/        # 全局组件
│   ├── AppHeader/     # 顶部栏
│   ├── AppSidebar/    # 侧边栏
│   ├── AppMain/       # 主内容区
│   ├── AppBreadcrumb/ # 面包屑
│   ├── AppTagsView/   # 标签页
│   ├── Pagination/    # 分页组件
│   ├── SearchForm/    # 搜索表单
│   ├── Table/         # 表格组件
│   └── LoadingBar/    # 加载进度条
├── layouts/           # 布局
├── router/            # 路由 & 守卫
│   ├── guards.ts      # 路由守卫（权限校验）
│   └── routes.ts      # 路由表
├── stores/            # Pinia 状态管理
│   ├── modules/
│   │   ├── app.ts     # 应用全局状态
│   │   ├── user.ts    # 用户状态（token、权限等）
│   │   ├── permission.ts # 权限路由
│   │   └── tagsView.ts   # 标签页
├── utils/
│   ├── request.ts     # Axios 实例（拦截器、Token 注入）
│   ├── storage.ts     # localStorage 封装
│   └── validate.ts    # 表单校验
├── views/             # 页面视图
│   ├── login/         # 登录页
│   ├── dashboard/     # 首页仪表盘
│   ├── product/       # 商品管理（列表 / 详情）
│   ├── inventory/     # 库存管理（入库 / 出库 / 查询）
│   ├── warehouse/     # 仓库管理
│   ├── supplier/      # 供应商管理
│   ├── customer/      # 客户管理
│   ├── employee/      # 员工管理
│   ├── expense/       # 报销管理
│   └── system/        # 系统管理（用户 / 角色 / 设置）
└── assets/styles/     # 全局样式
```

### 10.3 开发约定

- Axios 拦截器自动注入 `Authorization: Bearer <token>`
- 401 响应自动跳转登录页
- 路由守卫根据 `getMenus()` 返回的菜单树动态过滤可访问路由

---

## 11. 后续规划

- [ ] 库存盘点
- [ ] 库存调拨
- [ ] 小程序端（uni-app）
- [ ] 接口文档 Swagger/OpenAPI
- [ ] AI 功能模块（智能查询、库存预测、异常检测等，详见下方章节）

---

## 12. AI 功能规划（预留）

本章节规划了未来可引入的 AI 能力，采用轻量化方案，保持现有技术栈统一。

### 12.1 功能列表

| 优先级 | 功能 | 依赖 LLM | 难度 | 价值 |
|--------|------|:------:|:----:|:----:|
| 🥇 | 自然语言查询助手 | 是 | 中 | 高 |
| 🥈 | Dashboard 智能运营摘要 | 是 | 低 | 高 |
| 🥉 | 智能库存预测 | 否 | 中 | 高 |
| 4 | 入库/出库异常检测 | 否 | 低 | 中 |
| 5 | 智能补货建议 | 是 | 中 | 中 |
| 6 | OCR 单据识别入库 | 是 | 高 | 中 |

### 12.2 技术方案

```
前端 (Vue 3)
    ↓ HTTP
后端 (Spring Boot) -- 新增 com.wms.modules.ai 模块
    ↓ 调用
LLM API (OpenAI / 通义千问 / DeepSeek)
    ↓ 返回
结构化数据 + 自然语言分析
```

在后端新增 `ai` 模块封装 LLM 调用，不引入 Python 或重型 ML 框架。

### 12.3 新增数据表（预留）

```sql
-- AI 查询日志
CREATE TABLE IF NOT EXISTS wms_ai_query_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    query_text VARCHAR(500) NOT NULL,
    response_text TEXT,
    model VARCHAR(64),
    token_used INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 运营摘要缓存
CREATE TABLE IF NOT EXISTS wms_ai_daily_summary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    summary_date DATE NOT NULL UNIQUE,
    total_inbound INT DEFAULT 0,
    total_outbound INT DEFAULT 0,
    ai_summary TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 异常操作记录
CREATE TABLE IF NOT EXISTS wms_anomaly_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    anomaly_type VARCHAR(32) NOT NULL,
    severity VARCHAR(16) NOT NULL,
    description VARCHAR(500),
    handled TINYINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### 12.4 新增环境变量（预留）

| 变量名 | 说明 | 示例 |
|--------|------|------|
| WMS_AI_ENABLED | AI 功能开关 | true |
| WMS_AI_PROVIDER | LLM 提供商 | openai / deepseek |
| WMS_AI_API_KEY | LLM API 密钥 | sk-xxx |
| WMS_AI_MODEL | 默认模型名 | gpt-4o-mini |

> 以上 AI 功能均为预留规划，待基础业务模块完成后再逐步落地实施。
