package com.fribuddy.aliyun.sms.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 阿里云短信服务配置属性
 *
 * <h2>说明
 * <p>封装阿里云短信服务的配置项，包括短信签名和验证码短信模板编码。
 * <p>主配置文件中为占位内容，真实值由 application-local.yml 等环境配置文件提供。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-08
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "fribuddy.aliyun.sms")
public class AliyunSmsProperties {

    /**
     * 短信签名
     *
     * <h3>字段说明
     * <p>短信开头【】中的签名内容，需先在阿里云短信控制台申请并通过审核
     */
    @NotBlank(message = "短信签名不能为空")
    private String signName;

    /** 验证码短信模板编码 */
    @NotBlank(message = "验证码短信模板编码不能为空")
    private String templateCode;
}
