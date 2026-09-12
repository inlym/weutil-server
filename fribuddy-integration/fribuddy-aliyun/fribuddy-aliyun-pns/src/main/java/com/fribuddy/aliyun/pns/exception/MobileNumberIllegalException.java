package com.fribuddy.aliyun.pns.exception;

import com.fribuddy.common.exception.BaseException;

/**
 * 手机号格式异常
 *
 * <h2>异常说明
 * <p>当短信认证服务判定手机号格式不合法时抛出此异常。
 * <p>对应阿里云响应业务码 isv.MOBILE_NUMBER_ILLEGAL。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-10
 */
public class MobileNumberIllegalException extends BaseException {

    /**
     * 构造方法
     *
     * <h3>使用场景
     * <p>创建手机号格式异常实例
     *
     * @param message 详细错误消息
     */
    public MobileNumberIllegalException(String message) {
        super(message);
    }
}
