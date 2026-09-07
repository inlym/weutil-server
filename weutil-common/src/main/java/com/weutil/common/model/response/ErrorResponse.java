package com.weutil.common.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 异常响应数据
 *
 * <h2>类说明
 * <p>用于封装请求处理过程中发生异常时的响应信息。与 StandardResponse 字段结构保持一致，专门用于异常场景下的数据返回。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@NoArgsConstructor
public class ErrorResponse {

    /**
     * 错误码，0 表示成功，大于 0 表示业务错误
     *
     * @example 1001
     */
    private Integer errorCode;

    /**
     * 国际化消息键，由客户端转换为实际展示文案
     *
     * @example error.user.not_found
     */
    private String i18nKey;

    /**
     * 错误消息，描述具体的错误原因
     *
     * @example 用户不存在
     */
    private String errorMessage;

    /**
     * 构造方法
     *
     * <h3>使用场景
     * <p>用于创建包含错误码和国际化消息键的异常响应对象，错误消息将通过国际化消息键获取。
     *
     * @param errorCode 错误码，用于标识具体的错误类型，非 0 值表示错误状态
     * @param i18nKey   国际化消息键，用于获取国际化的错误消息
     */
    public ErrorResponse(Integer errorCode, String i18nKey) {
        this.errorCode = errorCode;
        this.i18nKey = i18nKey;
    }

    /**
     * 构造方法
     *
     * <h3>使用场景
     * <p>用于创建包含错误码、国际化消息键和错误消息的异常响应对象，统一封装异常场景下的响应信息。
     *
     * @param errorCode    错误码，用于标识具体的错误类型，非 0 值表示错误状态
     * @param i18nKey      国际化消息键，用于获取国际化的错误消息
     * @param errorMessage 错误消息，提供对错误的详细描述信息
     */
    public ErrorResponse(Integer errorCode, String i18nKey, String errorMessage) {
        this.errorCode = errorCode;
        this.i18nKey = i18nKey;
        this.errorMessage = errorMessage;
    }
}
