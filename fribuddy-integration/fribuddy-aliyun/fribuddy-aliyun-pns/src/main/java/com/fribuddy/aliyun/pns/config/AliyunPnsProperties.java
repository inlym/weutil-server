package com.fribuddy.aliyun.pns.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 阿里云号码认证服务配置属性
 *
 * <h2>说明
 * <p>封装阿里云号码认证服务的配置项，当前仅包含短信认证签名。
 * <p>主配置文件中为占位内容，真实值由 application-local.yml 等环境配置文件提供。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-10
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "fribuddy.aliyun.pns")
public class AliyunPnsProperties {

    /**
     * 短信认证签名
     *
     * <h3>字段说明
     * <p>短信开头【】中的签名内容，需先在号码认证服务控制台申请并通过审核，与短信服务的签名相互独立
     */
    @NotBlank(message = "短信认证签名不能为空")
    private String signName;
}
