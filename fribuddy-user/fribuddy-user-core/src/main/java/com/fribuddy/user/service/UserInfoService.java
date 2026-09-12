package com.fribuddy.user.service;

import com.fribuddy.user.entity.User;
import com.fribuddy.user.mapper.UserMapper;
import com.fribuddy.user.model.UserInfoUpdateDTO;
import com.fribuddy.user.model.UserInfoVO;
import com.fribuddy.common.annotation.LogExecution;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 用户信息服务类
 *
 * <h2>业务说明
 * <p>提供用户信息的查询和更新功能，用于用户个人资料管理。
 * <p>头像访问 URL 的生成与头像键名的转存待对象存储模块接线后实现。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class UserInfoService {

    /** 用户数据访问层 */
    private final UserMapper userMapper;

    /** 用户服务 */
    private final UserService userService;

    // ================================ public 方法 ================================

    /**
     * 获取用户信息
     *
     * <h3>处理逻辑
     * <p>根据用户 ID 查询用户实体。
     * <p>构建 UserInfoVO 对象并返回，头像访问 URL 待对象存储模块接线后生成。
     *
     * @param userId 用户 ID
     * @return 用户信息 VO，不为 null
     */
    @LogExecution
    public UserInfoVO getUserInfo(long userId) {
        return doGetUserInfo(userId);
    }

    /**
     * 修改用户信息
     *
     * <h3>处理逻辑
     * <p>以纯 Builder 方式构建仅含主键和待更新字段的专用更新实例并持久化。
     * <p>昵称为 null 时表示不修改，不加入更新实例。
     * <p>所有字段均为 null 时跳过更新直接返回当前信息，避免向数据库发送无 SET 列的非法 SQL。
     * <p>头像键名暂不处理，待对象存储模块接线后实现转存逻辑。
     * <p>更新完成后返回最新的用户信息。
     *
     * @param userId 用户 ID
     * @param dto    用户信息更新 DTO
     * @return 更新后的用户信息 VO，不为 null
     */
    @LogExecution
    public UserInfoVO updateUserInfo(long userId, @Valid @NotNull UserInfoUpdateDTO dto) {
        // 按需填充待更新字段，null 字段不加入更新实例
        boolean hasUpdateField = false;
        User.UserBuilder updateUserBuilder = User.builder().id(userId);
        if (dto.getNickname() != null) {
            updateUserBuilder.nickname(dto.getNickname());
            hasUpdateField = true;
        }

        // 无任何待更新字段时跳过数据库操作
        if (!hasUpdateField) {
            log.trace("请求未携带任何待更新字段，跳过更新，用户 ID：{}", userId);
            return doGetUserInfo(userId);
        }

        // 持久化更新
        userMapper.update(updateUserBuilder.build());

        return doGetUserInfo(userId);
    }

    // ================================ private 方法 ================================

    /**
     * 查询并构建用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息 VO，不为 null
     */
    private UserInfoVO doGetUserInfo(long userId) {
        // 查询用户实体
        User user = userService.getUserById(userId);

        // 头像访问 URL 待对象存储模块接线后生成，暂只返回昵称
        return UserInfoVO
            .builder()
            .nickname(user.getNickname())
            .build();
    }
}
