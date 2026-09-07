# 实体类 JavaDoc 规范

适用于：带 `@Table` 注解的实体类。

## 字段注释规则

**摘要行只包含字段名称**，不含解释说明或标点符号。

```java
// ✅ 正确
/** 用户 ID */
private Long userId;

// ❌ 错误：含说明或标点
/** 用户 ID，用于标识用户唯一性 */
/** 创建时间。 */
```

## 额外说明

需要补充说明时使用 `<h3>字段说明` 标签，格式要求：
- 摘要行与标签之间保留一个空行
- 说明内容用 `<p>` 标签开头，每段独立一行，段内不换行

```java
// ✅ 正确：单段说明
/**
 * 音频 ID
 *
 * <h3>字段说明
 * <p>用于标识同一音频会话的所有块
 */
private String audioId;

// ✅ 正确：多段说明
/**
 * 音频配置
 *
 * <h3>字段说明
 * <p>包含音频编解码参数和采样率配置。
 * <p>该配置在音频会话初始化时由服务端下发。
 */
private AudioConfig config;

// ❌ 错误：缺少空行
/**
 * 音频 ID
 * <h3>字段说明
 * <p>用于标识同一音频会话的所有块
 */
```

## 业务字段必须包含数据库字段定义

实体类业务字段（`id/createTime/updateTime/deleteTime` 除外）必须包含 `<h3>数据库字段定义` 块。

### 内容约束

至少包含以下三项，对应 SQL 列定义：

- `数据类型`：对应 SQL 列类型
- `非空约束`：`NOT NULL` 或 `NULL`
- `默认值`：对应 SQL 默认值；SQL 列定义中不含 `DEFAULT` 子句时写 `无`（仅用于 `NOT NULL` 字段）

可额外包含 `字段约束`（如唯一索引）、`字段解释` 等补充项。

对字段的业务语义解释统一放入 `<h3>字段说明` 块，不得混入字段定义块。

```java
// ✅ 正确：字段定义块只含三项，业务解释放在字段说明中
/**
 * 解绑时间
 *
 * <h3>字段说明
 * <p>为空表示 OIDC 账户处于绑定状态，有值表示已解绑
 *
 * <h3>数据库字段定义
 * <p>数据类型：timestamp(6)
 * <p>非空约束：NULL
 * <p>默认值：NULL
 */
private Instant unbindTime;

// ✅ 正确：字段定义块包含额外的字段约束项
/**
 * 认证令牌
 *
 * <h3>数据库字段定义
 * <p>数据类型：char(32)
 * <p>非空约束：NOT NULL
 * <p>默认值：无
 * <p>字段约束：唯一索引
 */
private String token;

// ❌ 错误：字段定义块中混入了业务语义解释
/**
 * 登录次数统计
 *
 * <h3>数据库字段定义
 * <p>数据类型：int unsigned
 * <p>非空约束：NOT NULL
 * <p>默认值：0（新绑定的 OIDC 账户初始登录次数为 0）
 */
private Integer loginCount;
```

### Instant 类型字段

Java 类型为 `Instant` 的字段，无论是否可空，数据库字段定义固定写法：

```java
/**
 * 最后一次登录时间
 *
 * <h3>字段说明
 * <p>记录用户最后一次使用该 OIDC 账户成功登录系统的时间戳
 *
 * <h3>数据库字段定义
 * <p>数据类型：timestamp(6)
 * <p>非空约束：NULL
 * <p>默认值：NULL
 */
private Instant lastLoginTime;
```

## 实体类模板

`@since` 值为文件创建日期，格式 `YYYY-MM-DD`。

```java
/**
 * 实体类中文描述
 *
 * <h2>说明
 * <p>详细描述实体类的用途、业务场景和关联关系。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since YYYY-MM-DD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("table_name")
public class EntityName {

    // ================================ 通用字段 ================================

    /** 主键 ID */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 创建时间 */
    private Instant createTime;

    /** 更新时间 */
    private Instant updateTime;

    // 仅当实体在项目中被调用删除方法时才包含
    /** 删除时间 */
    @Column(isLogicDelete = true)
    private Instant deleteTime;

    // ================================ 业务字段 ================================
}
```

其他要求：字段之间保留 1 个空行；日期时间字段必须用 `Instant` 类型。
