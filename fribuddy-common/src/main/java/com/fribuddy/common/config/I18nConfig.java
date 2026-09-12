package com.fribuddy.common.config;

import com.fribuddy.common.extension.MessageSourceBasenameCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * 国际化配置类
 *
 * <h2>配置说明
 * <p>配置国际化相关 Bean：MessageSource 加载多语言资源文件，LocaleResolver 根据客户端 Accept-Language 请求头解析语言。
 * <p>MessageSource 在此显式声明，替代 Spring Boot 自动配置——自动配置要求默认资源文件（messages.properties）存在，而本项目只保留语言特定的资源文件。
 *
 * <h2>扩展机制说明
 * <p>通用模块自身的资源文件 basename 在此直接配置；业务模块通过 MessageSourceBasenameCustomizer 接口声明各自的 basename，由通用模块自动收集并统一注册。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Configuration
@RequiredArgsConstructor
public class I18nConfig {

    // ================================ 静态常量字段 ================================

    /** 默认语言 */
    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("zh-CN");

    /** 支持的语言列表，区域限定条目（zh-CN、en-US）精确匹配完整标签，纯语言条目（zh、en）作为裸标签及区域变体的回退目标 */
    private static final List<Locale> SUPPORTED_LOCALES = List.of(
        Locale.forLanguageTag("zh-CN"),
        Locale.forLanguageTag("en-US"),
        Locale.forLanguageTag("zh"),
        Locale.forLanguageTag("en")
    );

    /** 通用模块多语言资源文件 basename */
    private static final String COMMON_BASENAME = "i18n/common";

    // ================================ 依赖注入字段 ================================

    /** 消息源 basename 定制器列表，由各业务模块实现并注入 */
    private final List<MessageSourceBasenameCustomizer> customizers;

    // ================================ public 方法 ================================

    /**
     * 配置 LocaleResolver Bean
     *
     * <h3>配置说明
     * <p>使用 AcceptHeaderLocaleResolver 解析客户端 Accept-Language 请求头，根据支持的语言列表匹配。
     *
     * @return LocaleResolver 本地化解析器实例
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();

        // 限定可识别的语言为中文和英文，白名单外的请求回退到默认语言
        resolver.setSupportedLocales(SUPPORTED_LOCALES);

        // 设置回退语言，用于客户端未发送 Accept-Language 或请求语言不在白名单时的兜底
        resolver.setDefaultLocale(DEFAULT_LOCALE);

        return resolver;
    }

    /**
     * 配置 MessageSource Bean
     *
     * <h3>配置说明
     * <p>基于 ResourceBundleMessageSource 从 classpath 加载通用模块自身的多语言资源文件，以及各业务模块通过定制器声明的资源文件，未命中对应语言时不回退到 JVM 系统区域。
     *
     * @return MessageSource 消息源实例
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        // 注册通用模块自身的 basename
        messageSource.setBasename(COMMON_BASENAME);

        // 注册各业务模块通过定制器声明的 basename
        customizers.forEach(customizer -> messageSource.addBasenames(
            customizer.declareBasenames().toArray(String[]::new)
        ));

        // 设置资源文件编码
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());

        // 关闭系统区域回退，未命中对应语言时返回 null 而非回退到 JVM 系统区域
        messageSource.setFallbackToSystemLocale(false);

        return messageSource;
    }
}
