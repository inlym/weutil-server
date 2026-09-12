package com.fribuddy.user.credential.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 访问令牌 VO
 *
 * <h2>说明
 * <p>用户登录成功后返回的 API 访问鉴权信息。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenVO {

    /**
     * 用于后续 API 请求鉴权的访问令牌，通过 x-fribuddy-user-token 请求头携带
     *
     * @example aB3fC2dE9bF4071625aB3fC2dE9bF407
     */
    private String token;
}
