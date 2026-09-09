package com.weutil.common.exception;

/**
 * 业务异常基类
 *
 * <h2>类说明
 * <p>作为项目所有业务异常类的基类，提供统一的异常处理能力。
 * <p>异常只负责传递错误消息，code 和 i18nKey 由异常处理器统一管理。
 *
 * <h2>使用示例
 * <pre>{@code
 * // 1. 创建自定义异常类
 * public class UserNotFoundException extends BaseException {
 *     public UserNotFoundException(String message) {
 *         super(message);
 *     }
 * }
 *
 * // 2. 在业务代码中抛出异常
 * @Service
 * @RequiredArgsConstructor
 * public class UserService {
 *     public User getUser(Long userId) {
 *         User user = userMapper.selectOneByCondition(USER_INFO.ID.eq(userId));
 *         if (user == null) {
 *             throw new UserNotFoundException(String.format("用户不存在，用户 ID：%d", userId));
 *         }
 *         return user;
 *     }
 * }
 *
 * // 3. 在全局异常处理器中处理
 * @ExceptionHandler(UserNotFoundException.class)
 * public ErrorInfo handleUserNotFoundException(UserNotFoundException e) {
 *     log.warn("用户未找到: {}", e.getMessage());
 *     return new ErrorInfo("USER_NOT_FOUND", "response.user.not_found");
 * }
 * }</pre>
 *
 * <h2>设计原则
 * <p>所有自定义业务异常都应继承此类，以确保异常处理的统一性和可维护性。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class BaseException extends RuntimeException {

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public BaseException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
