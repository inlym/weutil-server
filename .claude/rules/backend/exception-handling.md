# 异常处理规范

## 核心原则

- 禁止在控制器和服务类中使用 `try...catch`
- 所有异常由全局异常处理器统一捕获和记录
- 必须使用 `try...catch` 时，需在 `try` 上方用 `//` 注释说明原因

## catch 块规范

catch 块只做一件事：要么转换异常抛出，要么仅打印日志（用于 fallback 场景，不中断流程）。禁止在同一 catch 块中同时打印日志并抛出异常。必须使用 try-catch 时，在 `try` 上方用 `//` 注释说明原因：

```java
// ✅ 正确：只做异常转换
// S3 SDK 不提供受检异常，必须在此捕获以转换为业务异常
try {
    s3Client.putObject(request);
} catch (Exception e) {
    throw new ThirdPartySdkException(String.format("上传文件到 S3 失败，存储桶：%s，键名：%s", bucketName, key), e);
}

// ✅ 正确：只打印日志（fallback 场景，异常无需向上传播）
// WebSocket 关闭操作可能因会话已释放而失败，异常无需向上传播
try {
    session.close();
} catch (Exception e) {
    log.error("关闭 WebSocket 会话失败", e);
}

// ❌ 错误：缺少原因注释
try {
    s3Client.putObject(request);
} catch (Exception e) {
    throw new ThirdPartySdkException(String.format("上传文件到 S3 失败，存储桶：%s，键名：%s", bucketName, key), e);
}

// ❌ 错误：同时打印日志并抛出异常
} catch (Exception e) {
    log.error("上传失败", e);
    throw new ThirdPartySdkException("上传失败", e);
}
```

## 异常消息规范

消息必须包含：操作描述 + 关键参数值，便于直接定位问题。消息含变量时必须使用 `String.format()` 构建，禁止用 `+` 拼接。

```java
// ✅ 正确
throw new ThirdPartySdkException(
    String.format("上传音频到 S3 失败，存储桶：%s，键名：%s，大小：%d 字节", bucketName, key, fileSize),
    e
);

// ❌ 错误：消息过于简单
throw new ThirdPartySdkException("上传失败", e);

// ❌ 错误：用 + 拼接变量
throw new ThirdPartySdkException("上传失败，存储桶：" + bucketName + "，键名：" + key, e);
```
