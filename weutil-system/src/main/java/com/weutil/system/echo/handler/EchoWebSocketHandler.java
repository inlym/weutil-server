package com.weutil.system.echo.handler;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 回显 WebSocket 处理器
 *
 * <h2>类说明
 * <p>接收客户端发送的文本消息，将相同内容原样返回给客户端。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-09
 */
@Component
public class EchoWebSocketHandler extends TextWebSocketHandler {

    /**
     * 回显文本消息
     *
     * @param session WebSocket 会话
     * @param message 客户端发送的文本消息
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        // 将客户端发送的文本内容原样返回
        session.sendMessage(new TextMessage(message.getPayload()));
    }
}
