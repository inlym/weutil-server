package com.weutil.common.extension;

import java.util.List;

/**
 * 消息源 basename 定制器接口
 *
 * <h2>说明
 * <p>业务模块可实现此接口，声明本模块的多语言资源文件 basename。
 * <p>通用模块的国际化配置会自动收集所有实现类，统一注册到 MessageSource。
 *
 * <h2>命名约定
 * <p>basename 形如 i18n/<模块标识>，模块标识取所属业务模块或子模块的包路径，去掉 com.weutil. 前缀后将剩余段的点号替换为连字符。
 * <p>例如 com.weutil.common → i18n/common，com.weutil.modules.device → i18n/modules-device。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public interface MessageSourceBasenameCustomizer {

    /**
     * 声明消息源 basename
     *
     * <h3>方法说明
     * <p>业务模块实现此方法，声明本模块提供的多语言资源文件 basename。
     * <p>basename 为 classpath 下的资源路径，不含语言后缀和 .properties 扩展名（如 i18n/common）。
     * <p>通用模块的国际化配置会自动收集所有声明，统一注册到 MessageSource。
     *
     * @return basename 列表，不允许返回 null
     */
    List<String> declareBasenames();
}
