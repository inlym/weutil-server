package com.fribuddy.common.exception;

/**
 * 实体未找到异常
 *
 * <h2>异常说明
 * <p>当查询实体时未找到对应记录时抛出此异常，涵盖通过 ID 查询未找到和归属校验不匹配两种情况。
 * <p>出于安全考虑，两种情况对前端统一返回相同的错误响应，避免泄露实体归属信息。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class EntityNotFoundException extends BaseException {

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
