package com.fribuddy.aliyun.pns.config;

import com.aliyun.credentials.Client;
import com.aliyun.teaopenapi.models.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云号码认证服务配置
 *
 * <h2>说明
 * <p>注册阿里云号码认证服务客户端 Bean，鉴权由公共凭据模块提供的凭据客户端完成。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-10
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AliyunPnsConfig {

    /** 阿里云凭据客户端 */
    private final Client credentialsClient;

    // ================================ public 方法 ================================

    /**
     * 配置号码认证服务客户端 Bean
     *
     * <h3>客户端类型说明
     * <p>号码认证客户端 com.aliyun.dypnsapi20170525.Client 与凭据客户端 com.aliyun.credentials.Client 简名相同，
     * <p>两者无法同时 import，故号码认证客户端按官方示例使用全限定名。
     *
     * @return 阿里云号码认证服务客户端
     */
    @Bean
    public com.aliyun.dypnsapi20170525.Client pnsClient() throws Exception {
        // 绑定公共凭据客户端，由其通过默认凭据链完成鉴权
        Config config = new Config().setCredential(credentialsClient);

        // 号码认证服务的接入端点
        config.endpoint = "dypnsapi.aliyuncs.com";

        com.aliyun.dypnsapi20170525.Client client = new com.aliyun.dypnsapi20170525.Client(config);

        log.info("阿里云号码认证服务客户端初始化成功");

        return client;
    }
}
