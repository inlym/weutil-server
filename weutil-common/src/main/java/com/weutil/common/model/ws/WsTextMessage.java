package com.weutil.common.model.ws;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * WebSocket 文本消息
 *
 * <h2>说明
 * <p>封装 WebSocket 文本事件消息，包含事件名称和发送时间。
 * <p>声明为 non-sealed，允许具体事件消息类继承扩展。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public non-sealed class WsTextMessage extends WsMessage {

    /** 事件名称 */
    private String event;

    /** 消息发送时间 */
    private Instant timestamp;

    /**
     * 创建指定事件的文本消息
     *
     * @param event 事件名称
     * @return 文本消息
     */
    public static WsTextMessage of(String event) {
        return WsTextMessage.builder().event(event).build();
    }
}
