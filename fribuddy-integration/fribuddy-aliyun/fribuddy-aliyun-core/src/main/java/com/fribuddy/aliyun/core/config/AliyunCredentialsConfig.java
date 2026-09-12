package com.fribuddy.aliyun.core.config;

import com.aliyun.credentials.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云凭据配置
 *
 * <h2>说明
 * <p>注册阿里云凭据客户端 Bean，供各阿里云服务模块注入使用。
 * <p>凭据通过 SDK 默认凭据链获取，本地开发环境读取 ~/.aliyun/config.json 中配置的 AK。
 * <p>默认凭据链查找顺序与 config.json 配置方式参见官方文档：<a href="https://help.aliyun.com/zh/sdk/developer-reference/v2-manage-access-credentials">管理访问凭据</a>
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-08
 */
@Slf4j
@Configuration
public class AliyunCredentialsConfig {

    // ================================ public 方法 ================================

    /**
     * 配置凭据客户端 Bean
     *
     * <h3>凭据获取
     * <p>无参构造走默认凭据链，依次查找环境变量、~/.aliyun/config.json 配置文件、实例 RAM 角色等。
     * <p>以单例 Bean 复用凭据客户端，启用 SDK 内置的凭据缓存与自动刷新。
     *
     * @return 阿里云凭据客户端
     */
    @Bean
    public Client credentialsClient() {
        Client client = new Client();

        log.info("阿里云凭据客户端初始化成功");

        return client;
    }
}
