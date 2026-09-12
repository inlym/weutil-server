package com.fribuddy.user.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户账户注销事件
 *
 * <h2>说明
 * <p>账户注销流程中发布此事件，供下游模块执行注销伴随操作（如吊销认证凭证）。
 * <p>仅携带用户主键 ID，监听者可按需查询完整数据。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountCancelledEvent {

    /** 用户 ID */
    private Long userId;
}
