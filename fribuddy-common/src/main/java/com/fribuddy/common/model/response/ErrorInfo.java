package com.fribuddy.common.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 异常响应数据
 *
 * <h2>中间态说明
 * <p>本类是异常响应链路的中间态对象，由异常处理器创建，不直接对外输出。
 * <p>后续由 ErrorResponseAdvice 进行格式转换，生成对外的 ErrorResponse 统一响应。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@NoArgsConstructor
public class ErrorInfo {

    /**
     * 错误码
     *
     * @example ACCOUNT_LOCKED
     */
    private String code;

    /**
     * 国际化消息键，由 ErrorResponseAdvice 转换为实际展示文案
     *
     * @example response.user.not_found
     */
    private String i18nKey;

    /**
     * 构造方法
     *
     * <h3>使用场景
     * <p>用于创建包含错误码和国际化消息键的异常响应对象，错误消息将通过国际化消息键获取。
     *
     * @param code    错误码，用于标识具体的错误类型
     * @param i18nKey 国际化消息键，用于获取国际化的错误消息
     */
    public ErrorInfo(String code, String i18nKey) {
        this.code = code;
        this.i18nKey = i18nKey;
    }
}
