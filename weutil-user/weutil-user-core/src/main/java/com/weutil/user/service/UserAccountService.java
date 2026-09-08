package com.weutil.user.service;

import com.weutil.user.credential.service.UserCredentialService;
import com.weutil.user.entity.User;
import com.weutil.user.enums.UserStatus;
import com.weutil.user.mapper.UserMapper;
import com.weutil.common.annotation.LogExecution;
import lombok.RequiredArgsConstructor;
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
@Service
@RequiredArgsConstructor
@Validated
public class UserAccountService {

    /** 用户数据访问层 */
    private final UserMapper userMapper;

    /** 用户服务 */
    private final UserService userService;

    /** 用户认证凭证服务 */
    private final UserCredentialService userCredentialService;

    // ================================ public 方法 ================================

    /**
     * 注销账户
     *
     * <h3>处理逻辑
     * <p>确认用户存在后，先吊销该用户全部认证凭证，使存量令牌立即失效。
     * <p>再将用户状态更新为已注销。
     * <p>先吊销后改状态：若两步之间失败，用户令牌已失效但状态未变更，
     * <p>重新登录后可继续使用，优于反向顺序下注销状态残留有效令牌的结果。
     *
     * @param userId 用户 ID
     * @throws UserNotFoundException 当用户不存在时抛出
     */
    @LogExecution
    public void cancelAccount(long userId) {
        // 确认用户存在，不存在时抛出异常
        userService.getUserById(userId);

        // 吊销该用户全部认证凭证，存量令牌立即失效
        userCredentialService.revokeByUserId(userId);

        // 将用户状态更新为已注销
        User updateUser = User
            .builder()
            .id(userId)
            .status(UserStatus.CANCELLED)
            .build();

        userMapper.update(updateUser);
    }
}
