package com.weutil.aliyun.captcha.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 阿里云验证码服务配置属性
 *
 * <h2>说明
 * <p>封装阿里云验证码服务的配置项，当前仅包含验证场景 ID。
 * <p>主配置文件中为占位内容，真实值由 application-local.yml 等环境配置文件提供。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-10
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "weutil.aliyun.captcha")
public class AliyunCaptchaProperties {

    /**
     * 验证场景 ID
     *
     * <h3>字段说明
     * <p>控制台新建验证场景后生成的唯一标识，服务端写入验签请求，防止前端被篡改为其他场景
     */
    @NotBlank(message = "验证场景 ID 不能为空")
    private String sceneId;
}
