# Java 命名规范

## 基础命名

| 类型 | 规范 | 示例 |
|---|---|---|
| 类名 | PascalCase | `UserService` |
| 方法/变量 | camelCase | `getUserById` |
| 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| 包名 | 全小写点分隔 | `com.weutil.user` |

## 数据模型后缀

- API 请求模型：`DTO` 后缀（`UserLoginDTO`）
- API 响应模型：`VO` 后缀（`UserInfoVO`）
- 大模型调用请求：`Request` 后缀（`ChatEmotionAnalysisRequest`），见 `backend/llm-service.md`
- 大模型调用结果：`Result` 后缀（`ChatEmotionAnalysisResult`），见 `backend/llm-service.md`
- 禁止：`RequestDTO`、`ResponseDTO` 等冗余后缀

## 数据模型放置

- 字段数 < 4 且仅当前控制器使用：作为控制器静态内部类
- 字段数 ≥ 4 或跨控制器使用：独立类文件放在 `model` 目录

## Instant 字段命名

`Instant` 类型字段以 `Time` 为后缀，前缀必须是动词或动词+名词组合：

```java
// ✅ 正确：动词前缀
private Instant createTime;
private Instant updateTime;
private Instant deleteTime;
private Instant readTime;
private Instant unbindTime;

// ❌ 错误：名词或形容词前缀，缺少动作语义
private Instant expiryTime;
private Instant registrationTime;
```

## ID 字段注释

ID 字段注释必须用 `ID`，禁止用 `标识`：
- ✅ `/** 用户 ID */`
- ❌ `/** 用户标识 */`

## 实体 ID 命名

表示实体主键 ID 的变量，名称必须使用完整实体名 + `Id` 的驼峰形式。

适用于：
- 方法入参中表示某个实体的主键 ID
- 实体类中引用其他实体主键的关联字段

```java
// ✅ 正确：完整实体名 + Id
public ChatConversation getByIdAndUserId(Long chatConversationId, Long userId) { ... }

// ✅ 正确：实体类关联字段
private Long userId;
private Long deviceId;
private Long presetAgentConfigId;

// ❌ 错误：泛化的 id，无法区分实体类型
public ChatConversation getByIdAndUserId(Long id, Long userId) { ... }

// ❌ 错误：缩写，丢失上下文
public ChatConversation getByIdAndUserId(Long convId, Long userId) { ... }
```

## 术语翻译

AI 对话相关业务中，`memory` 必须译为"记忆"，禁止译为"内存"。

## 注释禁用项目名与产品名

注释中禁止出现项目名 `weutil` 和产品名「小鸣助手」，注释本身已在项目上下文中，名称前缀不提供信息：

```java
// ✅ 正确
/** 应用程序启动类 */
/** 服务端项目的主启动入口 */

// ❌ 错误
/** weutil 应用程序启动类 */
/** 「小鸣助手」服务端项目的主启动入口 */
```

仅约束注释文本，包名 `com.weutil`、配置键 `weutil.*`、配置值 `weutil-server`、请求头前缀 `x-weutil-` 等代码标识符不受影响。
