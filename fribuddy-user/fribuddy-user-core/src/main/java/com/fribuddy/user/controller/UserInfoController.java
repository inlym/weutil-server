package com.fribuddy.user.controller;

import com.fribuddy.user.model.UserInfoUpdateDTO;
import com.fribuddy.user.model.UserInfoVO;
import com.fribuddy.user.service.UserInfoService;
import com.fribuddy.common.annotation.UserId;
import com.fribuddy.common.annotation.UserPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户信息控制器类
 *
 * <h2>功能说明
 * <p>提供用户信息查询和修改的 HTTP API。
 *
 * @module 用户
 * @folder 用户/个人信息
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@RequiredArgsConstructor
@RestController
public class UserInfoController {

    /** 用户信息服务 */
    private final UserInfoService userInfoService;

    /**
     * 获取用户信息
     *
     * @param userId 当前登录用户 ID
     * @return 用户信息
     */
    @UserPermission
    @GetMapping("/user-info")
    public UserInfoVO getUserInfo(@UserId long userId) {
        return userInfoService.getUserInfo(userId);
    }

    /**
     * 修改用户信息
     *
     * @param userId 当前登录用户 ID
     * @param dto    用户信息更新 DTO
     * @return 更新后的用户信息
     */
    @UserPermission
    @PutMapping("/user-info")
    public UserInfoVO updateUserInfo(@UserId long userId, @Valid @RequestBody UserInfoUpdateDTO dto) {
        return userInfoService.updateUserInfo(userId, dto);
    }
}
