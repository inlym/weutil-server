package com.weutil.bootstrap;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用程序启动类
 *
 * <h2>功能说明
 * <p>服务端项目的主启动入口，负责初始化 Spring Boot 应用程序上下文，
 * 配置组件扫描和数据访问层映射。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@SpringBootApplication(scanBasePackages = {"com.weutil.**"})
@MapperScan(basePackages = {"com.weutil"}, annotationClass = Mapper.class)
public class Application {

    /**
     * 应用程序主入口方法
     *
     * <h3>处理逻辑
     * <p>启动 Spring Boot 应用程序，初始化应用上下文和所有 Spring 管理的 Bean。
     *
     * @param args 命令行参数
     */
    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
