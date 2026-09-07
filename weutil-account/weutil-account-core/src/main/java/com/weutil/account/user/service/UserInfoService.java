package com.weutil.account.user.service;

import com.weutil.account.user.entity.User;
import com.weutil.account.user.mapper.UserMapper;
import com.weutil.account.user.model.UserInfoUpdateDTO;
import com.weutil.account.user.model.UserInfoVO;
import com.weutil.common.annotation.LogExecution;
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
 * <p>头像相关能力待对象存储模块引入后提供。
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
     * <p>构建 UserInfoVO 对象并返回，头像访问 URL 待对象存储模块引入后生成。
     *
     * @param userId 用户 ID
     * @return 用户信息 VO，不为 null
     */
    @LogExecution
    public UserInfoVO getUserInfo(long userId) {
        // 查询用户实体
        User user = userService.getUserById(userId);

        // 头像访问 URL 待对象存储模块引入后生成，暂只返回昵称
        return UserInfoVO
            .builder()
            .nickname(user.getNickname())
            .build();
    }

    /**
     * 修改用户信息
     *
     * <h3>处理逻辑
     * <p>构建待更新的 User 实体对象，仅包含主键 ID 和需要更新的字段。
     * <p>若昵称不为空，则直接赋值对应字段。
     * <p>头像键名暂不处理，待对象存储模块引入后实现转存逻辑。
     * <p>最后调用更新方法将修改持久化到数据库。
     *
     * @param userId 用户 ID
     * @param dto    用户信息更新 DTO
     */
    @LogExecution
    public void updateUserInfo(long userId, @Valid @NotNull UserInfoUpdateDTO dto) {
        // 构建仅含主键的更新实体，后续按需填充字段
        User updateUser = User
            .builder()
            .id(userId)
            .build();

        // 昵称不为空时更新昵称
        String nickname = dto.getNickname();
        if (nickname != null) {
            updateUser.setNickname(nickname);
        }

        userMapper.update(updateUser);
    }
}
