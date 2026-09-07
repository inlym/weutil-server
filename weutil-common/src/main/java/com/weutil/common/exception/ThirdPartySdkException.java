package com.weutil.common.exception;

/**
 * 第三方 SDK 异常
 *
 * <h2>主要用途
 * <p>用于调用第三方 SDK 时出现错误的场景，包括但不限于：
 * <ul>
 *   <li>SDK 初始化失败</li>
 *   <li>SDK 配置错误</li>
 *   <li>SDK 内部异常</li>
 *   <li>SDK 返回的业务异常</li>
 *   <li>SDK 调用超时</li>
 * </ul>
 *
 * <h2>使用示例
 * <pre>{@code
 * // 1. 抛出带错误消息的异常
 * throw new ThirdPartySdkException("阿里云 SDK 初始化失败");
 *
 * // 2. 抛出带原因异常的异常
 * try {
 *     aliyunSmsClient.sendSms(request);
 * } catch (Exception e) {
 *     throw new ThirdPartySdkException("发送短信失败", e);
 * }
 *
 * // 3. 在 Service 层统一处理
 * @Service
 * @RequiredArgsConstructor
 * public class AliyunSmsService {
 *     private final SendSmsClient aliyunSmsClient;
 *
 *     public void sendSms(SmsRequestDTO dto) {
 *         try {
 *             aliyunSmsClient.sendSms(buildRequest(dto));
 *         } catch (TeaException e) {
 *             throw new ThirdPartySdkException("阿里云短信服务调用失败", e);
 *         } catch (TeaUnretryableException e) {
 *             throw new ThirdPartySdkException("阿里云短信服务不可用", e);
 *         }
 *     }
 * }
 * }</pre>
 *
 * <h2>与 ExternalApiException 的区别
 * <p><b>ThirdPartySdkException</b>: 专门用于处理第三方 SDK 调用异常，通常涉及本地 SDK 库的初始化、配置和调用
 * <p><b>ExternalApiException</b>: 专门用于处理 HTTP 外部 API 请求异常，通常涉及网络请求和 HTTP 状态码
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class ThirdPartySdkException extends BaseException {

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public ThirdPartySdkException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public ThirdPartySdkException(String message, Throwable cause) {
        super(message, cause);
    }
}