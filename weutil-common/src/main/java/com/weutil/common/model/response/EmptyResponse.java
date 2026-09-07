package com.weutil.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 空响应数据
 *
 * <h2>类说明
 * <p>用于无需返回业务数据的操作场景，如保存、更新、删除等写操作。
 * <p>理论上成功操作应无返回内容（类似 HTTP 204 No Content），但由于 JSON 格式要求，
 * <p>返回此空响应作为占位，前端无需解析使用，只需判断 HTTP 状态码是否为成功。
 *
 * <h2>使用场景
 * <p>设备绑定/解绑、设备控制、数据保存、数据更新、数据删除等操作。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmptyResponse {

    /** 操作成功 */
    public static final EmptyResponse SUCCESS = EmptyResponse.success();

    /**
     * 国际化消息键，由客户端转换为实际展示文案
     *
     * @example response.success
     */
    private String i18nKey;

    /**
     * 错误码，0 表示成功，大于 0 表示业务错误
     *
     * @example 0
     */
    private Integer errorCode;

    /**
     * 创建成功响应的空响应对象
     *
     * <h3>方法说明
     * <p>创建表示操作成功的空响应对象，自动设置错误码为 0。
     * <p>使用默认的成功消息键 "response.success"，由 ResponseBodyAdvice 转换为实际文案。
     *
     * @return 包含错误码 0 和默认国际化消息键的 EmptyResponse 对象
     * @author <a href="https://www.inlym.com">inlym</a>
     * @since 2026-09-07
     */
    public static EmptyResponse success() {
        return EmptyResponse
            .builder()
            .i18nKey("response.success")
            .errorCode(0)
            .build();
    }
}
