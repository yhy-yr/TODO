# Todo API

这是一个使用 Spring Boot 开发的待办事项后端项目。项目以 Todo 的增删改查为基础，练习了分层设计、参数校验、分页查询、数据库迁移、统一响应和异常处理等常见后端开发知识。

## 已实现功能

- 新增待办事项
- 根据 ID 查询待办事项
- 分页查询待办事项
- 根据完成状态筛选
- 根据标题关键字模糊查询
- 修改标题、完成状态和优先级
- 删除未完成的待办事项
- 使用 DTO 接收请求和返回数据
- 使用 Jakarta Validation 校验请求参数
- 使用统一的 JSON 格式返回结果
- 使用全局异常处理返回 400 和 404 错误
- 使用 JPA Auditing 自动记录创建时间和修改时间
- 使用 Flyway 管理数据库结构
- 使用 JUnit、Mockito 和 MockMvc 编写基础测试

## 技术栈

| 技术 | 用途 |
| --- | --- |
| Java 17 | 开发语言 |
| Spring Boot 4.1.1 | 项目基础框架 |
| Spring Web MVC | 编写 REST API |
| Spring Data JPA | 数据库访问 |
| Jakarta Validation | 请求参数校验 |
| MySQL | 保存业务数据 |
| Flyway | 管理数据库版本 |
| Maven | 依赖管理和项目构建 |
| JUnit、Mockito、MockMvc | 单元测试和接口测试 |

## 项目结构

```text
src
├── main
│   ├── java/com/example/demo
│   │   ├── config        # Spring 和 JPA 配置
│   │   ├── controller    # 接收 HTTP 请求
│   │   ├── dto           # 请求和响应对象
│   │   ├── entity        # JPA 实体和枚举
│   │   ├── exception     # 自定义异常和全局异常处理
│   │   ├── repository    # 数据访问层
│   │   ├── result        # 统一响应结构
│   │   └── service       # 业务逻辑层
│   └── resources
│       ├── db/migration  # Flyway 数据库迁移脚本
│       └── application.yml
└── test
    └── java              # JUnit、Mockito 和 MockMvc 测试
```

一次请求的主要调用过程是：

```text
客户端 -> Controller -> Service -> Repository -> MySQL
```

## 运行环境

运行项目前需要准备：

- JDK 17
- MySQL 8.x
- 可选：IntelliJ IDEA

项目自带 Maven Wrapper，因此不需要单独安装 Maven。

## 初始化数据库

先登录 MySQL，然后创建数据库：

```sql
CREATE DATABASE todo_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

不需要手动创建 `todo` 表。应用启动时，Flyway 会按照下面的脚本创建或更新表结构：

```text
V1__create_todo_table.sql
V2__add_priority_to_todo.sql
```

已经执行过的迁移文件不要修改。以后需要改变表结构时，应新增 `V3__...sql`、`V4__...sql` 等迁移文件。

## 配置数据库密码

数据库连接地址和用户名在 `application.yml` 中配置。密码通过环境变量 `DB_PASSWORD` 传入，避免把密码直接写进代码仓库。

macOS 或 Linux：

```bash
export DB_PASSWORD='你的MySQL密码'
```

如果数据库用户名不是 `root`，再设置：

```bash
export DB_USERNAME='你的MySQL用户名'
```

在 IntelliJ IDEA 中，可以打开运行配置，在 **Environment variables** 中分别添加：

```text
DB_USERNAME=root
DB_PASSWORD=你的MySQL密码
```

两个变量必须分开填写，不能把它们拼成同一个变量值。

## 启动项目

macOS 或 Linux：

```bash
./mvnw spring-boot:run
```

Windows：

```powershell
.\mvnw.cmd spring-boot:run
```

看到应用成功启动后，接口地址为：

```text
http://localhost:8080/todo
```

## 接口说明

### 1. 新增待办事项

```http
POST /todo
Content-Type: application/json
```

请求体：

```json
{
  "title": "学习 Spring Boot",
  "done": false,
  "priority": "HIGH"
}
```

`done` 可以不传，默认值为 `false`；`priority` 可以不传，默认值为 `MEDIUM`。

优先级只接受下面三个大写值：

- `LOW`
- `MEDIUM`
- `HIGH`

### 2. 根据 ID 查询

```http
GET /todo/1
```

### 3. 分页查询

```http
GET /todo?page=0&size=5&sort=id,desc
```

说明：

- `page` 从 `0` 开始。
- `size` 表示每页数量，默认是 `5`。
- `sort=id,desc` 表示按照 ID 倒序排列。

### 4. 按完成状态筛选

```http
GET /todo?done=false
```

### 5. 按标题关键字查询

```http
GET /todo?keyword=Spring
```

### 6. 同时使用筛选、搜索和分页

```http
GET /todo?done=false&keyword=Spring&page=0&size=5
```

### 7. 修改待办事项

```http
PATCH /todo/1
Content-Type: application/json
```

请求体可以只提供需要修改的字段：

```json
{
  "done": true,
  "priority": "LOW"
}
```

如果请求体中没有提供任何可修改字段，接口会返回 400。

### 8. 删除待办事项

```http
DELETE /todo/1
```

当前业务规则规定：已经完成的待办事项不能删除。

## 统一响应格式

请求成功时：

```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "id": 1,
    "title": "学习 Spring Boot",
    "done": false,
    "priority": "HIGH",
    "createdAt": "2026-09-27T10:00:00",
    "updatedAt": "2026-09-27T10:00:00"
  }
}
```

请求失败时：

```json
{
  "code": 404,
  "msg": "id 不存在：1",
  "data": null
}
```

需要区分两种状态码：

- HTTP 状态码由 Controller 和全局异常处理器设置。
- JSON 中的 `code` 是项目统一响应对象中的业务状态码。

## 运行测试

确保测试所需的数据库可以连接，然后执行：

```bash
./mvnw test
```

当前测试覆盖了以下内容：

- Todo 实体转换为响应 DTO
- 未完成的 Todo 可以删除
- 已完成的 Todo 禁止删除
- 根据 ID 查询接口的正常响应
- 非法优先级返回 400
- Spring 应用上下文可以启动

如果旧版本 IntelliJ IDEA 直接运行测试时出现 JUnit Platform 的 `NoSuchMethodError`，可以先使用 `./mvnw test` 运行，或者升级 IntelliJ IDEA。

## 学习重点

这个项目适合用来理解以下知识：

1. Controller、Service、Repository 各自负责什么。
2. 为什么请求参数和数据库实体要使用不同的类。
3. `@Valid`、`@NotBlank`、`@Size` 等校验注解如何工作。
4. `@Transactional` 如何管理数据库事务。
5. Spring Data JPA 如何根据方法名生成查询。
6. 分页参数如何传入 Repository。
7. Flyway 为什么要求已经执行的迁移文件保持不变。
8. 单元测试、Controller 测试和完整上下文测试有什么区别。

## 后续计划

- 独立完成一个图书管理模块
- 增加图书分类，练习一对多关系和外键
- 增加库存修改，练习事务和并发问题
- 学习 MySQL 索引和 `EXPLAIN`
- 在理解缓存使用场景后加入 Redis
- 补充更多边界条件测试

## 注意事项

- 不要把数据库密码提交到 Git。
- 修改数据库结构时新增 Flyway 迁移文件，不要修改已经执行过的脚本。
- 金额字段应使用 `BigDecimal`，不要使用 `double`。
- 枚举建议使用字符串保存，避免枚举顺序变化造成旧数据含义改变。
- Controller 负责接收请求，核心业务规则应放在 Service 中。

