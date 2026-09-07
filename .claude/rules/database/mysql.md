# MySQL 建表规范

## 固定基础字段

每张表必须包含以下字段（顺序固定，放在表结构最前面）：

```sql
`id`          bigint unsigned not null auto_increment comment '主键 ID',
`create_time` timestamp(6)    not null default current_timestamp(6) comment '创建时间',
`update_time` timestamp(6)    not null default current_timestamp(6) on update current_timestamp(6) comment '更新时间'
```

`delete_time` 仅当实体类在项目中被调用了删除方法（如 `mapper.deleteById()`）时才需要：

```sql
`delete_time` timestamp(6) null default null comment '删除时间'
```

## 实体类到 SQL 映射规范

建表语句的各要素从实体类中对应提取：

| SQL 要素 | 来源 |
|---|---|
| 表名 | `@Table` 注解的 `value` 值 |
| 表注释 | 实体类 JavaDoc 摘要行 |
| 列名 | 字段名驼峰转下划线 |
| 列注释 | 字段 JavaDoc 摘要行 |
| 列定义 | 字段 JavaDoc `<h3>数据库字段定义` 块 |
| 列索引 | 未显式声明时不创建索引 |

分组注释（通用字段、关联字段、业务字段等）与实体类保持一致。

## 字段默认值原则

业务字段默认为 `null`，只有"缺失会导致逻辑崩溃"的字段才用 `not null`。

判断标准：字段为空会导致系统核心功能无法运行、数据关联失败、或业务状态无法判断，才使用 `not null`。

```sql
-- ✅ 可选信息，默认 null
`description`  varchar(500)             default null comment '描述',
`remark`       varchar(1000)            default null comment '备注',

-- ✅ 核心关联字段，not null
`user_id`      bigint unsigned not null default 0 comment '用户 ID',
`status`       int unsigned    not null default 0 comment '状态',
```

## 字段类型映射

| Java 类型 | MySQL 类型（not null 场景） |
|---|---|
| `Long` | `bigint unsigned not null default 0` |
| `Integer` | `int unsigned not null default 0` |
| `String` | `varchar(100) not null` |
| `Instant` | `timestamp(6) not null default current_timestamp(6)` |

null 场景：去掉 `not null default ...`，改为 `default null`。

String 类型 NOT NULL 字段省略 DEFAULT 子句，禁止使用空字符串作为默认值。

超过 5000 字符的长文本（如 system prompt）允许用 `text`，需在建表注释中说明原因。

## 建表格式规范

字段按分组排列，用分隔注释标识：

- **通用字段**：`id`、`create_time`、`update_time`（固定在最前），`delete_time`（条件性，见"固定基础字段"章节）
- **关联字段**：外键和关联 ID（可选，位于通用字段和业务字段之间）
- **业务字段**：其余业务数据字段

```sql
create table `table_name` (
    -- ================================ 通用字段 ================================
    `id`          bigint unsigned not null auto_increment comment '主键 ID',
    `create_time` timestamp(6)    not null default current_timestamp(6) comment '创建时间',
    `update_time` timestamp(6)    not null default current_timestamp(6) on update current_timestamp(6) comment '更新时间',
    `delete_time` timestamp(6)    null default null comment '删除时间',

    -- ================================ 关联字段 ================================
    `user_id`     bigint unsigned not null default 0 comment '用户 ID',

    -- ================================ 业务字段 ================================
    `content`     varchar(500)             default null comment '内容',

    primary key (`id`)
) engine = InnoDB default character set = `utf8mb4` comment = '表名';
```

### 可空 timestamp(6) 列

必须显式写 `null` 约束：`timestamp(6) null default null`。

### 默认值为"无"

实体类 JavaDoc 中数据库字段定义的默认值为"无"时，SQL 中省略 DEFAULT 子句。此时依赖数据库 NOT NULL 约束在插入时拦截空值，避免被默认值掩盖错误。

## 列注释

- 简短描述列含义，不加解释说明
- ID 字段注释用 `ID`，禁止用 `标识`
- 所有列注释使用中文

## SQL 文件管理

- 建表 SQL 文件统一放在 `DevOps/mysql/` 目录，一表一文件，文件名为表名（如 `user_info.sql`）
- 子目录按包名规则确定：
  - 包名前缀为 `com.weutil.modules` 时，取下一级包名作为目录名（如 `com.weutil.modules.device.core.entity` → `device`）
  - 包名前缀为 `com.weutil` 时，取下一级包名作为目录名（如 `com.weutil.calendar.entity` → `calendar`）

## MyBatis-Flex 使用规范

- 禁止手动创建 Mapper 接口文件，项目已在 `mybatis-flex.config` 中配置自动生成
- 插入操作仅允许使用 `insertSelective`，禁止使用 `insert` 等其他插入方法

```java
// 插入（仅允许 insertSelective）
userMapper.insertSelective(user);

// 查询（自动过滤逻辑删除）
userMapper.selectOneByCondition(USER_INFO.USERNAME.eq(username));

// 更新（见下方 update 规范）
userMapper.update(updateUser);

// 删除（自动逻辑删除）
userMapper.deleteById(userId);
```

- 禁止用字符串条件 `.eq("username", username)`
- 禁止在条件中加 `.limit(1)`

## update 规范

必须用 Builder 方式构建专用更新实例，只含主键 `id` 和需更新的字段，然后调用 `mapper.update()`：

```java
User updateUser = User
    .builder()
    .id(userId)
    .email(newEmail)
    .build();
userMapper.update(updateUser);
```

- 禁止先查询再修改整个对象后 update
- 禁止用 `new` + setter 方式构建更新实体

**例外**：需要数据库原子操作（如字段自增）时，允许使用 `UpdateChain`：

```java
// 数据库层面原子自增，Builder 方式无法实现此语义
UpdateChain.of(FixedCodePhone.class)
    .setRaw(FIXED_CODE_PHONE.USAGE_COUNT, "usage_count + 1")
    .where(FIXED_CODE_PHONE.ID.eq(id))
    .update();
```
