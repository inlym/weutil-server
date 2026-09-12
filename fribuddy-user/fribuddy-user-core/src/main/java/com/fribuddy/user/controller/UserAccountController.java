package com.fribuddy.user.controller;

import com.fribuddy.user.exception.AccountCancelledException;
import com.fribuddy.user.service.UserAccountService;
import com.fribuddy.common.annotation.UserId;
import com.fribuddy.common.annotation.UserPermission;
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
     * 注销完成后抛出异常而非正常返回，由异常处理器返回特定错误码，前端据此清除登录态并跳转登录页。
     *
     * @param userId 当前登录用户 ID
     */
    @UserPermission
    @PostMapping("/accounts/cancellation")
    public void cancelAccount(@UserId long userId) {
        userAccountService.cancelAccount(userId);

        // 抛出账户已注销异常，促使全局异常处理器返回特定的错误码
        // 前端可根据错误码立即清除登录状态并跳转到登录页
        throw new AccountCancelledException("账户已注销");
    }
}
