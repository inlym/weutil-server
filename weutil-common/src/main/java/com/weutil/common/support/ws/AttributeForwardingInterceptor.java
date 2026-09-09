package com.weutil.common.support.ws;

import com.weutil.common.constants.ContextKeys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手拦截器，将客户端 IP 传递至 WebSocket 会话
 *
 * <h2>说明
 * <p>在 WebSocket 握手阶段，将 HTTP 请求中由 IP 过滤器设置的客户端 IP 地址
 * 复制到 WebSocket 会话的 attributes 中，使其在连接生命周期内可用。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class AttributeForwardingInterceptor implements HandshakeInterceptor {

    // ================================ public 方法 ================================

    /**
     * 握手前置处理，将客户端 IP 传递至 WebSocket 会话属性中
     *
     * <h3>处理逻辑
     * <p>从 Servlet 请求中提取由 IP 过滤器设置的客户端 IP 地址，
     * 写入 WebSocket 会话的 attributes map，供后续处理器使用
     *
     * @param request    当前 HTTP 请求
     * @param response   当前 HTTP 响应
     * @param wsHandler  将要处理 WebSocket 消息的处理器
     * @param attributes WebSocket 会话属性 map，握手完成后将传递给会话
     * @return 始终返回 true，允许握手继续
     */
    @Override
    public boolean beforeHandshake(
        @NonNull ServerHttpRequest request,
        @NonNull ServerHttpResponse response,
        @NonNull WebSocketHandler wsHandler,
        @NonNull Map<String, Object> attributes
    ) {
        // 握手请求均为 Servlet 实现，仅该类型能提取 IP 过滤器写入的请求属性
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            Object clientIp = httpRequest.getAttribute(ContextKeys.CLIENT_IP);

            // 客户端 IP 为空说明 IP 过滤器未处理该请求，跳过传递
            if (clientIp != null) {
                attributes.put(ContextKeys.CLIENT_IP, clientIp);
            }
        }

        return true;
    }

    /**
     * 握手后置处理（空实现）
     *
     * @param request   当前 HTTP 请求
     * @param response  当前 HTTP 响应
     * @param wsHandler 处理 WebSocket 消息的处理器
     * @param exception 握手过程中产生的异常，无异常时为 null
     */
    @Override
    public void afterHandshake(
        @NonNull ServerHttpRequest request,
        @NonNull ServerHttpResponse response,
        @NonNull WebSocketHandler wsHandler,
        @NonNull Exception exception
    ) {
        // 握手后无需额外处理
    }
}
