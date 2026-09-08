package com.weutil.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息 VO
 *
 * <h2>说明
 * <p>用于客户端展示的用户信息。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO {

    /**
     * 用户的显示昵称，用于在界面上展示
     *
     * @example 小明
     */
    private String nickname;

    /**
     * 用户头像的完整访问 URL，可直接用于前端图片展示
     *
     * @example https://cdn.example.com/avatars/user/123456.jpg
     */
    private String avatarUrl;
}
