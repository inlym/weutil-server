package com.weutil.common.exception;

/**
 * WebSocket 异常
 *
 * <h2>主要用途
 * <p>用于 WebSocket 连接建立、通信过程中出现错误的场景，包括但不限于：
 * <ul>
 *   <li>WebSocket 握手失败</li>
 *   <li>连接超时或中断</li>
 *   <li>消息发送或接收失败</li>
 *   <li>WebSocket 服务器返回错误</li>
 *   <li>连接状态异常</li>
 * </ul>
 *
 * <h2>使用示例
 * <pre>{@code
 * // 1. 抛出带错误消息的异常
 * throw new WebSocketException("WebSocket 连接建立失败");
 *
 * // 2. 抛出带原因异常的异常
 * try {
 *     session = webSocketClient.execute(handler, headers, uri).get();
 * } catch (Exception e) {
 *     throw new WebSocketException("创建 WebSocket 会话失败", e);
 * }
 *
 * // 3. 在 Service 层统一处理
 * @Service
 * public class WebSocketService {
 *     public WebSocketSession connect(String url) {
 *         try {
 *             return webSocketClient.execute(handler, headers, URI.create(url)).get();
 *         } catch (InterruptedException e) {
 *             Thread.currentThread().interrupt();
 *             throw new WebSocketException("WebSocket 连接被中断", e);
 *         } catch (ExecutionException e) {
 *             throw new WebSocketException("WebSocket 连接执行失败", e);
 *         }
 *     }
 * }
 * }</pre>
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class WebSocketException extends BaseException {

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public WebSocketException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public WebSocketException(String message, Throwable cause) {
        super(message, cause);
    }
}