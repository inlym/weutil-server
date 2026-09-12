package com.fribuddy.aliyun.sms.config;

import com.aliyun.credentials.Client;
import com.aliyun.teaopenapi.models.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云短信服务配置
 *
 * <h2>说明
 * <p>注册阿里云短信服务客户端 Bean，鉴权由公共凭据模块提供的凭据客户端完成。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-08
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AliyunSmsConfig {

    /** 阿里云凭据客户端 */
    private final Client credentialsClient;

    // ================================ public 方法 ================================

    /**
     * 配置短信服务客户端 Bean
     *
     * <h3>客户端类型说明
     * <p>短信客户端 com.aliyun.dysmsapi20170525.Client 与凭据客户端 com.aliyun.credentials.Client 简名相同，
     * <p>两者无法同时 import，故短信客户端按官方示例使用全限定名。
     *
     * @return 阿里云短信服务客户端
     */
    @Bean
    public com.aliyun.dysmsapi20170525.Client smsClient() throws Exception {
        // 绑定公共凭据客户端，由其通过默认凭据链完成鉴权
        Config config = new Config().setCredential(credentialsClient);

        // 短信服务的接入端点
        config.endpoint = "dysmsapi.aliyuncs.com";

        com.aliyun.dysmsapi20170525.Client client = new com.aliyun.dysmsapi20170525.Client(config);

        log.info("阿里云短信服务客户端初始化成功");

        return client;
    }
}
