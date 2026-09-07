# Spring 分层规范

## 控制器层

**通用约束**

- 必须使用 `@RestController`，禁止用 `@Controller + @ResponseBody` 组合
- 禁止在控制器类上使用 `@RequestMapping`
- 方法直接返回响应数据，禁止用 `ResponseEntity` 包装
- 返回值不允许为 `void`，无内容时返回 `EmptyResponse.success()`
- 参数校验注解不使用 `message` 字段
- `@UserId` 必须用 `long` 基本类型，禁止用 `Long`
- `@RequestBody` 参数类型必须以 `DTO` 结尾，变量名固定为 `dto`

**路径规范**

- 请求路径资源名称用复数名词，多个单词间用 `-` 连接（如 `/chat-conversations`）
- 操作语义由 HTTP 方法（GET/POST/PUT/DELETE/PATCH）表达，禁止在路径中使用动词（如 `GET /users/{userId}` 而非 `POST /getUser`）
- 非 CRUD 动作（如激活、重置）用子资源名词表达（如 `POST /users/{userId}/activation`）
- 层级关系通过路径嵌套表达（如 `/users/{userId}/devices`）
- 路径变量命名与方法参数保持一致，遵循完整实体名 + `Id` 规则（如 `{userId}`，禁止用 `{id}`）
- 使用 `@AdminPermission` 的方法路径必须以 `/admin/` 开头，反之亦然

## 服务层

- 使用 `@Service` 注解，类名以 `Service` 结尾
- 不使用 `Impl` 后缀，不实现接口
- 禁止使用 `HttpServletRequest`、`HttpServletResponse`、`HttpSession` 等 Web 层对象

## 依赖注入

- 使用 `@RequiredArgsConstructor`，字段声明为 `private final`
- 禁止使用 `@Autowired`
- 字段名使用类名首字母小写形式，不允许缩写
- 获取原型 Bean 必须用 `ObjectProvider#getObject()`，禁止用 `BeanFactory#getBean()`

## 配置管理

- 禁止使用 `@Value`，必须使用 `@ConfigurationProperties` 注解类
- Properties 类必须位于 `config` 包，以 `Properties` 结尾，添加 `@Validated`

## Config 类规范

- 以 `Config` 结尾，必须位于 `config` 包，使用 `@Configuration` 注解
- 职责：定义和配置 Bean 实例，供其他类注入其内部的 Bean
- 禁止在其他类中注入 Config 类本身，只注入其中的 Bean

## 启动初始化器

- 实现 `ApplicationRunner` 的类，`run` 方法的 `args` 参数必须加 `@NonNull` 注解（`lombok.NonNull`），与 Spring 7 在 `ApplicationRunner` 上声明的 `@NullMarked` 约束保持一致

```java
@Override
public void run(@NonNull ApplicationArguments args) { ... }
```

## 查询方法命名契约

- `getBy*`：不允许返回 `null`，内部未找到时抛出异常；调用处无需做 null 判断
- `findBy*`：未找到时返回 `null`，不抛出异常；调用处必须做 null 判断
