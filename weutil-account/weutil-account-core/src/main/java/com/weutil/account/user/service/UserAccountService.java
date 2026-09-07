package com.weutil.account.user.service;

import com.weutil.account.user.entity.User;
import com.weutil.account.user.enums.UserStatus;
import com.weutil.account.user.mapper.UserMapper;
import com.weutil.common.annotation.LogExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 用户账户服务类
 *
 * <h2>业务说明
 * <p>提供用户账户管理功能，包括账户注销、状态变更等操作。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class UserAccountService {

    /** 用户数据访问层 */
    private final UserMapper userMapper;

    /** 用户服务 */
    private final UserService userService;

    // ================================ public 方法 ================================

    /**
     * 注销账户
     *
     * <h3>处理逻辑
     * <p>根据用户主键 ID 查询用户信息。
     * <p>将用户状态修改为已注销状态。
     * <p>更新用户信息到数据库。
     *
     * @param userId 用户 ID
     * @throws UserNotFoundException 当用户不存在时抛出
     */
    @LogExecution
    public void cancelAccount(long userId) {
        // 确认用户存在，不存在时抛出异常
        userService.getUserById(userId);

        // 将用户状态更新为已注销
        User updateUser = User
            .builder()
            .id(userId)
            .status(UserStatus.CANCELLED)
            .build();

        userMapper.update(updateUser);
    }
}
