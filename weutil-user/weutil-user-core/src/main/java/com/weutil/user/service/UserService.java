package com.weutil.user.service;

import com.weutil.user.entity.User;
import com.weutil.user.enums.UserStatus;
import com.weutil.user.exception.AccountCancelledException;
import com.weutil.user.exception.AccountLockedException;
import com.weutil.user.exception.UserNotFoundException;
import com.weutil.user.mapper.UserMapper;
import com.weutil.common.annotation.LogExecution;
import com.weutil.common.util.RandomUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;

/**
 * 用户服务类
 *
 * <h2>业务说明
 * <p>提供用户创建和查询功能，用于用户基本信息管理。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class UserService {

    /** 用户数据访问层 */
    private final UserMapper userMapper;

    // ================================ public 方法 ================================

    /**
     * 根据用户 ID 获取用户实体
     *
     * @param userId 用户 ID
     * @return 用户实体对象，不为 null
     * @throws UserNotFoundException 当用户不存在时抛出
     */
    @LogExecution
    public User getUserById(Long userId) {
        return doGetUserById(userId);
    }

    /**
     * 创建用户
     *
     * <h3>处理逻辑
     * <p>昵称为空时自动生成 user_ 加 4 位随机字符的默认昵称。
     * <p>头像暂不支持，待对象存储模块引入后随登录流程补充。
     *
     * @param nickname 用户昵称，为空时自动生成
     * @return 创建后的用户实体对象，不为 null
     */
    @LogExecution
    public User createUser(String nickname) {
        return doCreateUser(nickname);
    }

    /**
     * 检查用户状态
     *
     * <h3>处理逻辑
     * <p>根据用户主键 ID 查询用户信息。
     * <p>检查用户状态是否为正常状态。
     * <p>根据用户状态抛出对应的异常。
     *
     * @param userId 用户 ID
     * @throws AccountCancelledException 当用户账号已注销时抛出
     * @throws AccountLockedException 当用户账号已锁定时抛出
     * @throws UserNotFoundException 当用户不存在时抛出
     */
    @LogExecution
    public void checkUserStatus(@NotNull Long userId) {
        // 查询用户，不存在时抛出异常
        User user = doGetUserById(userId);

        // 根据账号状态抛出对应异常
        UserStatus status = user.getStatus();
        if (status == UserStatus.CANCELLED) {
            throw new AccountCancelledException(String.format("用户(userId=%d)账号已注销", userId));
        }
        if (status == UserStatus.LOCKED) {
            throw new AccountLockedException(String.format("用户(userId=%d)账号已锁定", userId));
        }
    }

    // ================================ private 方法 ================================

    /**
     * 获取用户实体
     *
     * @param userId 用户 ID
     * @return 用户实体对象，不为 null
     */
    private User doGetUserById(Long userId) {
        // 查询用户，不存在时抛出异常
        User user = userMapper.selectOneById(userId);

        if (user == null) {
            throw new UserNotFoundException(String.format("用户(userId=%d)不存在", userId));
        }

        return user;
    }

    /**
     * 创建用户内部实现
     *
     * <h3>处理逻辑
     * <p>若昵称为空则自动生成 user_ 加 4 位随机字符的默认昵称。
     *
     * @param nickname 用户昵称，为空时自动生成
     * @return 创建后的用户实体对象，不为 null
     */
    private User doCreateUser(String nickname) {
        // 昵称为空时自动生成默认昵称，避免用户无显示名称
        String resolvedNickname = !StringUtils.hasText(nickname)
            ? "user_" + RandomUtils.generateAlphanumeric(4)
            : nickname;

        Instant now = Instant.now();
        User user = User
            .builder()
            .nickname(resolvedNickname)
            .registerTime(now)
            .lastLoginTime(now)
            .status(UserStatus.NORMAL)
            .build();

        // 持久化用户记录
        userMapper.insertSelective(user);

        return user;
    }
}
