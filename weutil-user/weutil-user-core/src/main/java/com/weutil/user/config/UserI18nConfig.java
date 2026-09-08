package com.weutil.user.config;

import com.weutil.common.extension.MessageSourceBasenameCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 用户模块国际化配置类
 *
 * <h2>说明
 * <p>向通用模块的国际化配置声明用户模块的多语言资源文件 basename。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Configuration
public class UserI18nConfig implements MessageSourceBasenameCustomizer {

    /** 用户模块多语言资源文件 basename */
    private static final String BASENAME = "i18n/user-core";

    // ================================ public 方法 ================================

    /**
     * 声明消息源 basename
     *
     * @return basename 列表，不为 null
     */
    @Override
    public List<String> declareBasenames() {
        return List.of(BASENAME);
    }
}
