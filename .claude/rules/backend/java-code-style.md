# Java 代码风格规范

## 禁止项

- 禁止使用 `var` 关键字，必须明确指定变量类型
- 禁止使用通配符导入（`import package.*`）
- 禁止使用 `record` 关键字，必须用 Lombok 注解的普通类
- 布尔字段禁止使用 `is` 前缀（用 `deleted` 不用 `isDeleted`）
- 禁止使用行尾注释，注释必须放在被注释代码的上一行，保持相同缩进
- 禁止在参数或变量类型上使用全路径限定名，必须通过 `import` 引入
- 禁止包含未使用的 `import` 语句
- JavaDoc 中禁止使用 `{@link}` 标签，避免引入代码本身不需要的类依赖
- 字符串有效性判断禁止手写 null 检查与 `isBlank()` 组合，必须使用 `StringUtils.hasText()`

```java
// ✅ 正确
if (!StringUtils.hasText(nickname)) { ... }

// ❌ 错误
if (nickname == null || nickname.isBlank()) { ... }
```

- 禁止使用 `@SuppressWarnings("unchecked")`，必须修正产生警告的根本原因（如引入正确的泛型类型、消除强制类型转换）

## Lombok 规范

数据类标准注解组合：`@Data @Builder @NoArgsConstructor @AllArgsConstructor`

## 方法排序

public 方法在前，private 方法在后，用分隔注释标识分组：
```
// ================================ public 方法 ================================
// ================================ private 方法 ================================
```

## 方法重写

子类重写方法中调用 `super` 必须放在方法最后。

## 方法调用

`public` 方法仅作为外部入口，禁止在类内部被其他方法调用。需要复用时，将业务逻辑提取到 `private` 方法，`public` 方法直接委托：

```java
// ✅ 正确：public 方法委托 private 方法
public void say() {
    doSay();
}

public void eat() {
    doSay();
}

private void doSay() {
    // 实际业务逻辑
}

// ❌ 错误：类内部调用 public 方法
public void say() {
    // 实际业务逻辑
}

public void eat() {
    say();
}
```

## 换行规则

- 每行不超过 120 字符
- 能一行放下则放在一行；需要换行时每一项都换行（含第一项），闭括号 `)` 和分号 `;` 独立成行
- 链式调用（Builder）从第二行开始，`.` 对齐
- 判断是否换行必须先计算字符数，不凭感觉
- 纯字面量字符串不受行宽限制约束，禁止用 `+` 拼接拆分为多段

适用场景一致，均遵循"要么单行，要么每项换行（含第一项）"原则：

```java
// ✅ 方法参数换行
public void createUser(
    String username,
    String email,
    Integer age
) { ... }

// ✅ 链式调用换行
QueryWrapper.create()
    .select(USER_INFO.ALL_COLUMNS)
    .where(USER_INFO.USERNAME.eq(username));

// ✅ 注解参数换行
@Table(
    value = "user_info",
    schema = "weutil"
)

// ✅ 条件语句换行
if (
    user == null
    || user.getId() == null
    || user.getUsername() == null
) { ... }

// ✅ 正确：纯字面量字符串保持完整，超出行宽也不断开
@Schema(
    description = "'increase' when user wants louder, 'decrease' when user wants quieter, 'set' when user specifies exact volume."
)
private VolumeAction action;

// ❌ 错误：纯字面量用 + 拼接拆分
@Schema(
    description = "'increase' when user wants louder, "
        + "'decrease' when user wants quieter, "
        + "'set' when user specifies exact volume."
)
private VolumeAction action;
```

## 类 JavaDoc

所有类文件必须在类声明前包含 `@author` 和 `@since` 标签：

- `@author <a href="https://www.inlym.com">inlym</a>`
- `@since <创建日期>`：值为该文件的创建日期，格式 `YYYY-MM-DD`

```java
/**
 * 用户认证服务
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-01-15
 */
@Service
@RequiredArgsConstructor
public class UserAuthService {
```

## 字段注释

类的所有字段都必须有 `/** */` JavaDoc 注释，包括 `static final` 常量和 `private final` 依赖注入字段。注释为单行摘要，简述字段用途。

```java
// ✅ 正确（AppleIdentityTokenService）
/** Apple 令牌签发方固定值 */
private static final String EXPECTED_ISSUER = "https://appleid.apple.com";

/** Apple 公钥获取服务 */
private final ApplePublicKeyService applePublicKeyService;

// ❌ 错误（UniAppPushService）
private final UniAppPushBindingMapper uniAppPushBindingMapper;
private final UniAppApiService uniAppApiService;
```

数据类（实体类、DTO/VO、普通模型类）的字段注释格式以 `naming/javadoc-style.md` 为准。

## 方法 JavaDoc

所有方法必须有 JavaDoc 注释。方法体仅包含一行委托调用时，可省略方法说明部分（`<h3>` 块），摘要行和 `@param` / `@return` 仍不可省略。

`@Override` 方法若父类/接口已有完整 JavaDoc，子类可省略。

控制器 API 方法的注释格式以 `naming/dto-vo-javadoc.md` 为准，优先级最高，不受本节规范约束。

格式：

1. 摘要行：简述方法作用，不超过 10 字，不含标点符号和标签
2. 方法说明：使用 `<h3>` 表示标题，`<p>` 表示内容，解释核心处理逻辑或需要补充的内容。每个事项对应一个 `<h3>` + `<p>` 组合，多个组合之间保留一个空行
3. 标签：有参数或返回值时补充 `@param` / `@return`

`@return` 必须说明返回值是否可能为 null，与查询命名契约保持一致（`getBy*` 不返回 null，`findBy*` 可能返回 null）。

摘要行和标签是下限，允许用 `<h3>` 块补充非自明的业务细节（如截断规则、副作用、前置条件）。优化 JavaDoc 时保留有实质信息量的补充块，只删除冗余部分。

删除操作精确到**句子级别**，不以块为单位整块删除。块内若同时存在冗余句（重复摘要行语义）和实质句（隐含约束、不自明机制），只删冗余句，保留实质句：

```java
// ❌ 错误：整块删除，连同实质句一起丢失
/**
 * 创建 DynamoDB 客户端 Bean
 *
 * @return DynamoDbClient 实例
 */

// ❌ 错误：保留整块，含冗余句
/**
 * 创建 DynamoDB 客户端 Bean
 *
 * <h3>说明
 * <p>配置并创建 AWS DynamoDB 服务客户端实例。   ← 冗余，重复摘要行
 * <p>使用默认凭证提供链，支持共享凭证文件、环境变量等多种凭证来源。
 *
 * @return DynamoDbClient 实例
 */

// ✅ 正确：只删冗余句，保留实质句，修正 <h3> 标题
/**
 * 创建 DynamoDB 客户端 Bean
 *
 * <h3>凭证加载
 * <p>使用默认凭证提供链，支持共享凭证文件、环境变量等多种凭证来源。
 *
 * @return DynamoDbClient 实例
 */
```

```java
// ✅ 正确：完整的 JavaDoc 结构
/**
 * 创建一次性调度任务
 *
 * <h3>处理逻辑
 * <p>在指定时间点触发一次性任务，向配置的 SQS 队列投递消息负载。
 * <p>任务执行后自动删除调度记录，避免重复触发。
 *
 * @param executionTime 任务执行时间（UTC）
 * @param input 消息负载（JSON 字符串）
 * @return 调度任务 ARN
 */
public String createSchedule(Instant executionTime, String input) { ... }

// ✅ 正确：多组方法说明，组间空行分隔
/**
 * 按用户名查找用户
 *
 * <h3>查询策略
 * <p>优先从缓存查询，未命中时回源数据库。
 *
 * <h3>缓存更新
 * <p>回源成功后将结果写入缓存，TTL 为 30 分钟。
 *
 * @param username 用户名
 * @return 用户信息，不存在时为 null
 */
private User findByUsername(String username) { ... }

// ✅ 正确：委托方法省略方法说明
/**
 * 发送验证码
 *
 * @param phone 手机号
 */
public void sendVerificationCode(String phone) {
    doSendVerificationCode(phone);
}

// ✅ 正确：无需补充说明时只写摘要行
/**
 * 清理过期会话缓存
 */
private void cleanExpiredSessions() { ... }

// ❌ 错误：摘要行过长且含标点
/**
 * 创建一次性调度任务，在指定时间点向配置的 SQS 队列投递消息负载，任务执行后自动删除调度记录。
 *
 * @param executionTime 任务执行时间（UTC）
 */
public String createSchedule(Instant executionTime, String input) { ... }

// ❌ 错误：private 方法缺少 JavaDoc
private User findByUsername(String username) { ... }
```

## 方法内部注释

方法内部每个逻辑步骤都必须用 `//` 注释说明其业务含义（"是什么"或"为什么"），而非描述代码行为。步骤之间保留一个空行，注释紧贴对应代码。

当已有注释偏向描述代码行为时，应改写为业务含义，不可删除注释本身。"都必须"意味着注释不可省略，修正路径只有改写措辞。

**例外**：项目内部自建模型类的字段赋值（如 `entity.setField(dto.getField())`）不需要注释，字段名已足够自明。标准响应构造（如 `return new ErrorInfo("INVALID_CURSOR", "response.cursor.invalid");`）同理，不构成独立业务步骤，可省略注释。

**反例外**：外部模型类（尤其是第三方 SDK 的 Builder，如 AWS SDK）的每个配置项都必须注释说明其业务含义，不可省略。

```java
// ✅ 正确：每个步骤都有业务含义注释
private void sendVoiceNotDetectedMessage() {
    // 获取"语音未检测到"的随机提示文本
    String text = voicePhraseService.getRandomPhrase(VoicePhraseScene.VOICE_NOT_DETECTED);

    // 若可播报文本网关存在，将文本传递给网关处理
    if (speakableTextGateway != null) {
        speakableTextGateway.acceptOnce(text);
    }
}

// ✅ 正确：拦截器中的阶段注释，变量名无法完整表达阶段含义
public ClientHttpResponse intercept(...) throws IOException {
    Instant startTime = Instant.now();

    // 记录请求开始日志
    String requestData = body.length > 0 ? new String(body, StandardCharsets.UTF_8) : "[Empty]";
    log.trace("[HTTP] [Start] {} {}\n{}", request.getMethod(), request.getURI(), requestData);

    // 执行实际 HTTP 请求
    ClientHttpResponse response = execution.execute(request, body);

    // 记录响应日志及耗时
    long duration = Duration.between(startTime, Instant.now()).toMillis();
    ...
}

// ✅ 正确：模型字段赋值无需注释
MoodSchedule schedule = new MoodSchedule();
schedule.setUserId(dto.getUserId());
schedule.setTitle(dto.getTitle());

// ❌ 错误：注释描述代码行为而非业务含义
// 调用 getRandomPhrase 方法
String text = voicePhraseService.getRandomPhrase(VoicePhraseScene.VOICE_NOT_DETECTED);

// ✅ 正确：外部 SDK Builder 每项配置都有业务含义注释
CreateScheduleRequest request = CreateScheduleRequest
    .builder()
    // 调度名称，在同一账户下必须唯一
    .name(scheduleName)
    // 调度表达式，at() 格式指定精确的一次性执行时间
    .scheduleExpression(atExpression(executionTime))
    // OFF 表示在精确时间点触发，不启用弹性窗口
    .flexibleTimeWindow(FlexibleTimeWindow.builder().mode(FlexibleTimeWindowMode.OFF).build())
    // 任务执行完成后自动删除，避免残留调度记录
    .actionAfterCompletion(ActionAfterCompletion.DELETE)
    .build();

// ❌ 错误：外部 SDK Builder 省略配置项注释
CreateScheduleRequest request = CreateScheduleRequest
    .builder()
    .name(scheduleName)
    .scheduleExpression(atExpression(executionTime))
    .flexibleTimeWindow(FlexibleTimeWindow.builder().mode(FlexibleTimeWindowMode.OFF).build())
    .actionAfterCompletion(ActionAfterCompletion.DELETE)
    .build();
```

## RestClient

`retrieve().body()` 的泛型类型必须以 `Response` 结尾。
