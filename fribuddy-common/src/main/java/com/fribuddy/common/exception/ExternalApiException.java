package com.fribuddy.common.exception;

/**
 * 外部 API 异常
 *
 * <h2>主要用途
 * <p>用于对外发起 API 请求时出现错误的场景，包括但不限于：
 * <ul>
 *   <li>HTTP 请求失败（网络超时、连接异常等）</li>
 *   <li>远程服务返回错误状态码</li>
 *   <li>响应数据格式异常或解析失败</li>
 *   <li>第三方 API 服务不可用</li>
 * </ul>
 *
 * <h2>使用示例
 * <pre>{@code
 * // 1. 抛出带错误消息的异常
 * throw new ExternalApiException("调用第三方 API 失败");
 *
 * // 2. 抛出带原因异常的异常
 * try {
 *     restClient.get().uri("https://api.example.com/users").retrieve().body(String.class);
 * } catch (Exception e) {
 *     throw new ExternalApiException("获取用户信息失败", e);
 * }
 *
 * // 3. 在 Service 层统一处理
 * @Service
 * public class ExternalApiService {
 *     public String fetchData() {
 *         try {
 *             return restClient.get().uri("/api/data").retrieve().body(String.class);
 *         } catch (ResourceAccessException e) {
 *             throw new ExternalApiException("外部 API 访问超时", e);
 *         } catch (HttpClientErrorException e) {
 *             throw new ExternalApiException(String.format("外部 API 返回客户端错误：%s", e.getStatusCode()), e);
 *         } catch (HttpServerErrorException e) {
 *             throw new ExternalApiException(String.format("外部 API 服务器错误：%s", e.getStatusCode()), e);
 *         }
 *     }
 * }
 * }</pre>
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class ExternalApiException extends BaseException {

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public ExternalApiException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}