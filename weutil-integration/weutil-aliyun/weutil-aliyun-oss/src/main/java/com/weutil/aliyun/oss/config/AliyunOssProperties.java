package com.weutil.aliyun.oss.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 阿里云 OSS 配置属性
 *
 * <h2>说明
 * <p>封装阿里云 OSS 的配置项，包括桶所在地域和用户文件桶名称。
 * <p>主配置文件中为占位内容，真实值由 application-local.yml 等环境配置文件提供。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-08
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "weutil.aliyun.oss")
public class AliyunOssProperties {

    /**
     * 桶所在地域 ID
     *
     * <h3>字段说明
     * <p>SDK 根据地域 ID 拼接接入端点，并参与请求的 V4 签名，如 cn-hangzhou
     */
    @NotBlank(message = "桶所在地域 ID 不能为空")
    private String region;

    /**
     * 用户文件桶名称
     *
     * <h3>字段说明
     * <p>存放头像、图片、docx 等用户上传的异构文件，由服务端程序写入
     */
    @NotBlank(message = "用户文件桶名称不能为空")
    private String bucketName;
}
