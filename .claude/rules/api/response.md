# API 响应规范

## 响应结构

- 成功：直接返回业务数据，无统一包装；无内容时响应体为空
- 失败：错误信息统一封装在 `error` 字段中

```json
{ "error": { "code": "USER_NOT_FOUND", "message": "用户名或密码错误" } }
```

## 错误码规范

业务错误码（`error.code`）与 HTTP 状态码无关，不得混淆：

- 错误码为字符串，仅在错误响应中出现，如 `USER_NOT_FOUND`
- HTTP 状态码表示请求执行结果，与业务错误码独立

```json
// ✅ 正确：业务错误码独立
{ "error": { "code": "USER_NOT_FOUND", "message": "用户名或密码错误" } }
// HTTP 200 OK

// ❌ 错误：用 HTTP 状态码作为业务错误码
{ "error": { "code": 401, "message": "未授权" } }
```
