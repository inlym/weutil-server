package com.fribuddy.common.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Locale;

/**
 * 国际化服务类
 *
 * <h2>服务说明
 * <p>提供多语言消息获取功能，用于支持应用程序的国际化需求。
 * <p>通过 MessageSource 从资源文件中获取本地化消息，语言环境由请求上下文（LocaleContextHolder）解析，对应客户端 Accept-Language。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Service
@RequiredArgsConstructor
@Validated
public class I18nService {

    /** 国际化消息源 */
    private final MessageSource messageSource;

    // ================================ public 方法 ================================

    /**
     * 获取国际化消息
     *
     * <h3>异常行为
     * <p>消息键不存在时抛出 NoSuchMessageException，由调用方保证键值有效。
     *
     * @param key 消息键，对应资源文件中的键名
     * @return 本地化消息内容
     */
    public String getMessage(@NotBlank String key) {
        return doGetMessage(key, null, LocaleContextHolder.getLocale());
    }

    /**
     * 获取带参数的国际化消息
     *
     * <h3>占位符格式
     * <p>资源文件中使用 {0}, {1}, {2}... 表示占位符，按参数顺序替换。
     * <p>示例：welcome.message=Welcome {0}, your account balance is {1}
     *
     * <h3>异常行为
     * <p>消息键不存在时抛出 NoSuchMessageException，由调用方保证键值有效。
     *
     * @param key  消息键，对应资源文件中的键名
     * @param args 占位符参数，用于替换消息中的 {n} 占位符
     * @return 本地化消息内容
     */
    public String getMessage(@NotBlank String key, Object... args) {
        return doGetMessage(key, args, LocaleContextHolder.getLocale());
    }

    // ================================ private 方法 ================================

    /**
     * 委托 MessageSource 获取消息
     *
     * @param key    消息键
     * @param args   参数数组
     * @param locale 语言环境
     * @return 本地化消息内容
     */
    private String doGetMessage(String key, Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, locale);
    }
}
