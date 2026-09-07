package com.weutil.aliyun.oss.config;

import com.aliyun.credentials.Client;
import com.aliyun.credentials.models.CredentialModel;
import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.Credentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 配置
 *
 * <h2>说明
 * <p>注册阿里云 OSS 客户端 Bean，鉴权由公共凭据模块提供的凭据客户端完成。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-08
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AliyunOssConfig {

    /** 阿里云凭据客户端 */
    private final Client credentialsClient;

    /** 阿里云 OSS 配置属性 */
    private final AliyunOssProperties aliyunOssProperties;

    // ================================ public 方法 ================================

    /**
     * 配置 OSS 客户端 Bean
     *
     * <h3>凭据桥接
     * <p>OSS SDK 自身的凭据提供器不支持默认凭据链，通过适配方法将公共凭据客户端
     * 桥接为 SDK 的凭据提供器，保持全项目统一从默认凭据链获取凭据。
     *
     * <h3>资源释放
     * <p>客户端内部持有连接池，注册为 Bean 时声明 close 销毁方法，随容器关闭释放连接。
     *
     * @return 阿里云 OSS 客户端
     */
    @Bean(destroyMethod = "close")
    public OSSClient ossClient() {
        OSSClient client = OSSClient
            .newBuilder()
            // 桶所在地域，SDK 依此拼接接入端点并参与 V4 签名
            .region(aliyunOssProperties.getRegion())
            // 凭据提供器，每次请求时经默认凭据链获取有效凭据
            .credentialsProvider(this::getSdkCredentials)
            .build();

        log.info("阿里云 OSS 客户端初始化成功");

        return client;
    }

    // ================================ private 方法 ================================

    /**
     * 获取 SDK 凭据
     *
     * <h3>凭据转换
     * <p>从公共凭据客户端读取当前凭据（AccessKey 或 STS 临时凭据），转换为 OSS SDK 的凭据对象。
     *
     * @return OSS SDK 凭据对象
     */
    private Credentials getSdkCredentials() {
        CredentialModel credential = credentialsClient.getCredential();

        return new Credentials(
            credential.getAccessKeyId(),
            credential.getAccessKeySecret(),
            credential.getSecurityToken()
        );
    }
}
