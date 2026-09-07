package com.weutil.common.exception;

/**
 * 系统配置异常
 *
 * <h2>异常说明
 * <p>当系统配置缺失或配置项无效时抛出此异常。
 *
 * <h2>使用场景
 * <p>用于标识系统级别的配置问题，如：
 * <ul>
 *   <li>必需的配置项缺失</li>
 *   <li>配置的模板或资源未找到</li>
 *   <li>配置值不合法导致系统无法正常运行</li>
 * </ul>
 *
 * <h2>使用示例
 * <pre>{@code
 * if (systemPromptTemplate == null) {
 *     throw new SystemConfigurationException(
 *         String.format("未找到 code=%s 的系统提示词模板", code)
 *     );
 * }
 * }</pre>
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class SystemConfigurationException extends BaseException {

    /**
     * 构造方法
     *
     * @param message 详细错误消息
     */
    public SystemConfigurationException(String message) {
        super(message);
    }
}