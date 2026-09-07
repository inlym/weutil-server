# Spring Boot 配置文件格式规范

适用于 `application.yml` 及 `application-*.yml` 配置文件。

## 分组注释

一级属性之间使用分组注释分隔，格式为 `#` 加简短描述，上方保留一个空行：

```yaml
# 日志配置
logging:
    level:
        com.weutil: DEBUG

# Spring 框架配置
spring:
    application:
        name: "weutil-server-dev"
```

同一个一级属性内的子属性不使用分组注释。

## 空行规则

二级属性之间保留一个空行。三级及以下层级的连续属性之间不插入额外空行：

```yaml
spring:
    # 应用名称，用于服务注册和监控标识
    application:
        name: "weutil-server-dev"

    # 数据源配置
    datasource:
        # 数据库连接 URL
        url: jdbc:mysql://...
        # 数据库用户名
        username: "admin"
        # 数据库密码
        password: "secret"
```

## 注释规范

每个属性必须包含注释，注释位于属性上方，与属性保持相同缩进。注释应描述属性的含义或用途，至少包含属性名无法直接传达的信息：

```yaml
# ✅ 正确：注释补充了属性名未表达的信息
# 第三方服务端点地址（需要从服务商控制台获取）
endpoint: "https://api.example.com"

# ✅ 正确：属性名已足够自明时，允许仅作简要描述
# Redis 服务器地址
host: "127.0.0.1"

# ❌ 错误：完全无注释
host: "127.0.0.1"
```

枚举类型属性必须说明可选值：

```yaml
# ✅ 正确
# 运行环境（可选值：DEV、PROD）
env: "dev"

# ❌ 错误：未说明可选值
# 运行环境
env: "dev"
```

具有特殊配置原因的属性应在注释中补充说明：

```yaml
# ✅ 正确：说明了配置原因
# 连接池最大连接数，1 核服务器建议 3-5
maximum-pool-size: 5

# 连接最大存活时间（毫秒），必须小于 MySQL wait_timeout
max-lifetime: 1500000
```
