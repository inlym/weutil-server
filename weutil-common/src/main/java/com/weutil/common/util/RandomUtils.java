package com.weutil.common.util;

import java.security.SecureRandom;

/**
 * 随机工具类
 *
 * <h2>功能说明
 * <p>提供各种类型的随机数据生成功能，包括随机字符串、随机数等。
 * <p>当前支持字母数字混合字符串、小写字母数字字符串、纯数字字符串等随机字符串生成。
 *
 * <h2>安全说明
 * <p>统一使用 SecureRandom 作为随机源，输出不可预测，
 * <p>可安全用于认证令牌、验证码等安全敏感场景。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public final class RandomUtils {

    /** 随机数生成器实例，SecureRandom 线程安全，可全局共享 */
    private static final SecureRandom RANDOM = new SecureRandom();

    /** 字母数字字符集 */
    private static final String ALPHANUMERIC_CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /** 数字字符集 */
    private static final String NUMERIC_CHAR_SET = "0123456789";

    /** 小写字母数字字符集 */
    private static final String LOWERCASE_ALPHANUMERIC_CHAR_SET = "abcdefghijklmnopqrstuvwxyz0123456789";

    // 私有构造函数，防止实例化
    private RandomUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 生成随机字母数字字符串
     *
     * <h3>生成方法
     * <p>生成指定长度的随机字符串，包含大小写英文字母（A-Z, a-z）和数字（0-9）。
     * <p>使用 SecureRandom 确保不可预测性，适用于生成认证令牌、临时密码等安全敏感场景。
     *
     * @param length 字符串长度
     * @return 指定长度的随机字母数字字符串
     */
    public static String generateAlphanumeric(int length) {
        final int charSetSize = ALPHANUMERIC_CHAR_SET.length();

        StringBuilder stringBuilder = new StringBuilder(length);

        // 逐个生成随机字符
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(charSetSize);
            stringBuilder.append(ALPHANUMERIC_CHAR_SET.charAt(randomIndex));
        }

        return stringBuilder.toString();
    }

    /**
     * 生成随机小写字母数字字符串
     *
     * <h3>生成方法
     * <p>生成指定长度的随机字符串，包含小写英文字母（a-z）和数字（0-9）。
     * <p>使用 SecureRandom 确保不可预测性，适用于生成邀请码、优惠码、短链接标识等场景。
     *
     * @param length 字符串长度
     * @return 指定长度的随机小写字母数字字符串
     */
    public static String generateLowercaseAlphanumeric(int length) {
        final int charSetSize = LOWERCASE_ALPHANUMERIC_CHAR_SET.length();

        StringBuilder stringBuilder = new StringBuilder(length);

        // 逐个生成随机字符
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(charSetSize);
            stringBuilder.append(LOWERCASE_ALPHANUMERIC_CHAR_SET.charAt(randomIndex));
        }

        return stringBuilder.toString();
    }

    /**
     * 生成随机数字字符串
     *
     * <h3>生成方法
     * <p>生成指定长度的随机数字字符串，仅包含数字（0-9）。
     * <p>使用 SecureRandom 确保不可预测性，适用于生成数字验证码、短信验证码等场景。
     *
     * @param length 字符串长度
     * @return 指定长度的随机数字字符串
     */
    public static String generateNumeric(int length) {
        final int charSetSize = NUMERIC_CHAR_SET.length();

        StringBuilder stringBuilder = new StringBuilder(length);

        // 逐个生成随机字符
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(charSetSize);
            stringBuilder.append(NUMERIC_CHAR_SET.charAt(randomIndex));
        }

        return stringBuilder.toString();
    }

    /**
     * 生成随机 IP 地址
     *
     * <h3>生成方法
     * <p>生成 IPv4 格式的随机 IP 地址，格式为 a.b.c.d。
     * <p>第一段范围 1-223（避开 0.x.x.x 和组播地址 224+），其余各段范围 0-255。
     *
     * @return 随机 IP 地址
     */
    public static String generateIp() {
        return (RANDOM.nextInt(223) + 1) + "." +
            RANDOM.nextInt(256) + "." +
            RANDOM.nextInt(256) + "." +
            RANDOM.nextInt(256);
    }
}