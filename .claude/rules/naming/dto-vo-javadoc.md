# DTO/VO JavaDoc 规范（Apifox Helper，优先级最高）

与其他注释规范冲突时，以本文件为准。

## DTO/VO 字段注释

必须使用多行注释，包含：
1. 摘要行：完整句子描述字段含义（不用简短字段名）
2. `@example` 标签：提供示例值

```java
// ✅ 正确
/**
 * 客户端所在时区，用于将录制时间戳转换为本地时间
 *
 * @example Asia/Shanghai
 */
private String timeZone;

// ❌ 错误：单行注释
/** 时区 */
private String timeZone;

// ❌ 错误：使用 <h3>字段说明 标签
/**
 * 时区
 *
 * <h3>字段说明
 * <p>客户端所在时区...
 */
```

## 控制器 API 方法注释

摘要行后不留空行、不使用任何标签，紧跟描述内容：

```java
// ✅ 正确：有描述内容时，描述紧跟摘要行，描述与标签之间留空行
/**
 * 上传录音
 * 接收 APP 端上传的录音数据，触发音频存储、情绪分析和日程提取的完整处理链路。
 *
 * @param userId 用户 ID
 * @param dto    录音上传请求数据
 * @return 请求成功响应
 */

// ✅ 正确：无描述内容时，摘要行与标签之间保留空行
/**
 * 获取基础模型列表
 *
 * @return 基础模型列表响应
 */

// ❌ 错误：摘要行与描述之间有空行
/**
 * 上传录音
 *
 * <h3>处理流程
 * <p>接收 APP 端上传的录音数据...
 */

// ❌ 错误：无描述内容时，摘要行与标签之间没有空行
/**
 * 获取基础模型列表
 * @return 基础模型列表响应
 */
```

## 类级 JavaDoc

- 必须包含 `@author <a href="https://www.inlym.com">inlym</a>`
- 必须包含 `@since <创建日期>`，格式 `YYYY-MM-DD`
- 类注释用 `<h2>` 标签，方法注释用 `<h3>` 标签
- 方法内部注释使用 `//` 格式，禁止使用 JavaDoc

## 控制器类 Apifox 标签

控制器类必须包含 `@module` 和 `@folder` 标签，用于 Apifox Helper 插件输出 API 文档时的分组归类：

- `@module <一级模块名>`：Apifox 文档中的模块分组
- `@folder <路径>`：Apifox 文档中的目录路径，与 `@module` 同级时直接写模块名；需要进一步细分时使用 `<模块>/<子分类>` 形式

两个标签必须成对出现，位于 `<h2>` 描述块之后、`@author` 之前：

```java
/**
 * 聊天控制器类
 *
 * <h2>功能说明
 * <p>提供聊天相关的 HTTP API。
 *
 * @module 对话
 * @folder 对话/情绪洞察
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2025-01-19
 */
```
