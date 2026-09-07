package com.weutil.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标准响应数据
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardResponse {
    /**
     * 错误码，0 表示成功，大于 0 表示业务错误
     *
     * @example 0
     */
    private Integer errorCode;

    /**
     * 错误消息，描述操作结果或错误原因
     *
     * @example 操作成功
     */
    private String errorMessage;

      /**
     * 创建成功响应
     *
     * <h3>构造方法
     * <p>用于创建表示操作成功的标准响应对象。错误码固定为 0，表示成功状态。
     *
     * @param message 成功消息，提供对操作结果的描述信息
     * @return 包含成功状态码和指定消息的 StandardResponse 对象
     */
    public static StandardResponse success(String message) {
        return StandardResponse.builder().errorCode(0).errorMessage(message).build();
    }

    /**
     * 创建错误响应
     *
     * <h3>构造方法
     * <p>用于创建包含错误码和错误消息的标准错误响应对象。通常用于处理业务逻辑中的异常情况或验证失败场景。
     *
     * @param errorCode   错误码，用于标识具体的错误类型，非 0 值表示错误状态
     * @param errorMessage 错误消息，提供对错误的详细描述信息
     * @return 包含指定错误码和错误消息的 StandardResponse 对象
     */
    public static StandardResponse error(Integer errorCode, String errorMessage) {
        return StandardResponse.builder().errorCode(errorCode).errorMessage(errorMessage).build();
    }
}
