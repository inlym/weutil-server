package com.weutil.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标准响应数据
 *
 * <h2>类说明
 * <p>所有 HTTP 接口对外的统一响应结构。
 * <p>由 StandardResponseAdvice 将 ErrorResponse、EmptyResponse 转换生成本对象。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
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
}
