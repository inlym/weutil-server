package com.weutil.aliyun.pns.config;

import com.weutil.common.extension.MessageSourceBasenameCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 号码认证模块国际化配置类
 *
 * <h2>说明
 * <p>向通用模块的国际化配置声明号码认证模块的多语言资源文件 basename。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-10
 */
@Configuration
public class AliyunPnsI18nConfig implements MessageSourceBasenameCustomizer {

    /** 号码认证模块多语言资源文件 basename */
    private static final String BASENAME = "i18n/aliyun-pns";

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
