package com.fribuddy.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误响应数据
 *
 * <h2>类说明
 * <p>异常场景下对外的统一响应结构，错误信息封装在 error 字段中。
 * <p>由 ErrorResponseAdvice 将 ErrorInfo 转换生成本对象，message 为已解析的展示文案。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * 错误信息，封装错误码和错误消息
     *
     * @example {"code": "USER_NOT_FOUND", "message": "用户不存在"}
     */
    private Error error;

    /**
     * 构造方法
     *
     * <h3>使用场景
     * <p>用于创建包含错误码和错误消息的错误响应对象，错误消息为已解析的展示文案。
     *
     * @param code    错误码，用于标识具体的错误类型
     * @param message 错误消息，描述具体的错误原因
     */
    public ErrorResponse(String code, String message) {
        this.error = new Error(code, message);
    }

    /**
     * 错误信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Error {

        /**
         * 错误码，用于标识具体的错误类型
         *
         * @example USER_NOT_FOUND
         */
        private String code;

        /**
         * 错误消息，描述具体的错误原因
         *
         * @example 用户不存在
         */
        private String message;
    }
}
