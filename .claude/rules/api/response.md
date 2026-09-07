# API 响应规范

## 错误码规范

业务错误码（`errorCode`）与 HTTP 状态码无关，不得混淆：

- 错误码 `> 0` 表示业务错误
- HTTP 状态码表示请求执行结果，与业务错误码独立

```json
// ✅ 正确：业务错误码独立
{ "code": 1001, "message": "用户名或密码错误", "data": null }
// HTTP 200 OK

// ❌ 错误：用 HTTP 状态码作为业务错误码
{ "code": 401, "message": "未授权", "data": null }
```
