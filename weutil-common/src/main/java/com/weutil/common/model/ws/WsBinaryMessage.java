package com.weutil.common.model.ws;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * WebSocket 二进制消息
 *
 * <h2>说明
 * <p>封装 WebSocket 二进制消息，仅包含原始字节载荷。
 * <p>声明为 final，不允许继承。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final class WsBinaryMessage extends WsMessage {

    /** 二进制载荷 */
    @JsonIgnore
    private byte[] payload;
}
