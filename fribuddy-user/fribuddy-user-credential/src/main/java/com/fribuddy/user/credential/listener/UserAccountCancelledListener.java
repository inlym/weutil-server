package com.fribuddy.user.credential.listener;

import com.fribuddy.user.credential.service.UserCredentialService;
import com.fribuddy.user.event.UserAccountCancelledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * 用户账户注销事件监听器
 *
 * <h2>说明
 * <p>监听用户账户注销事件，吊销该用户全部认证凭证，使存量令牌立即失效。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-09
 */
@Service
@RequiredArgsConstructor
public class UserAccountCancelledListener {

    /** 用户认证凭证服务 */
    private final UserCredentialService userCredentialService;

    // ================================ public 方法 ================================

    /**
     * 处理用户账户注销事件
     *
     * <h3>处理逻辑
     * <p>吊销该用户全部认证凭证，存量令牌立即失效。
     *
     * <h3>时序约束
     * <p>注销流程依赖"先吊销凭证、后变更状态"的顺序，监听方法必须保持同步执行，禁止改为异步。
     * <p>异步监听会导致状态变更失败时，已启动的注销流程残留有效令牌。
     *
     * @param event 用户账户注销事件
     */
    @EventListener
    public void onUserAccountCancelled(UserAccountCancelledEvent event) {
        // 吊销该用户全部认证凭证，存量令牌立即失效
        userCredentialService.revokeByUserId(event.getUserId());
    }
}
