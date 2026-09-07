package com.weutil.common.model.ws;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * WebSocket 消息基类
 *
 * <h2>说明
 * <p>作为 WebSocket 消息的密封基类，限定三种消息类型：
 * <p>{@code WsTextMessage} 用于文本事件消息，{@code WsBinaryMessage} 用于二进制音频消息，{@code WsPongMessage} 用于心跳保活消息。
 * <p>{@code @SuperBuilder} 是 Lombok 对继承链的强制要求：子类使用 {@code @SuperBuilder} 时，父类必须同样声明，否则编译报错。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@SuperBuilder
@NoArgsConstructor
public sealed class WsMessage permits WsTextMessage, WsBinaryMessage, WsPongMessage {
}
