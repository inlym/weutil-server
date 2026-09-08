package com.weutil.user.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息更新 DTO
 *
 * <h2>说明
 * <p>用于接收修改用户信息时的请求数据。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoUpdateDTO {

    /**
     * 用户的显示昵称，用于在界面上展示，为 null 时表示不修改
     *
     * @example 小明
     */
    @Size(max = 100)
    private String nickname;

    /**
     * 用户头像图片在对象存储中的存储键名，为 null 时表示不修改
     *
     * @example avatars/user/123456.jpg
     */
    @Size(max = 50)
    private String avatarKey;
}
