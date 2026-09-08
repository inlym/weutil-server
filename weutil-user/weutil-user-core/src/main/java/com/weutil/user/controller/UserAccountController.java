package com.weutil.user.controller;

import com.weutil.user.exception.AccountCancelledException;
import com.weutil.user.service.UserAccountService;
import com.weutil.common.annotation.UserId;
import com.weutil.common.annotation.UserPermission;
import com.weutil.common.model.response.EmptyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户账户控制器类
 *
 * <h2>功能说明
 * <p>提供用户账户管理的 HTTP API，包括账户注销等操作。
 *
 * @module 用户
 * @folder 用户/账户管理
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@RequiredArgsConstructor
@RestController
public class UserAccountController {

    /** 用户账户服务 */
    private final UserAccountService userAccountService;

    /**
     * 注销账户
     *
     * @param userId 当前登录用户 ID
     * @return 空响应（实际不会返回，因为会抛出异常促使前端跳转登录页）
     */
    @UserPermission
    @PostMapping("/accounts/cancellation")
    public EmptyResponse cancelAccount(@UserId long userId) {
        userAccountService.cancelAccount(userId);

        // 抛出账户已注销异常，促使全局异常处理器返回特定的错误码
        // 前端可根据错误码立即清除登录状态并跳转到登录页
        throw new AccountCancelledException("账户已注销");
    }
}
