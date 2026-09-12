package com.fribuddy.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.util.ErrorHandler;

/**
 * 定时任务调度配置类
 *
 * <h2>配置说明
 * <p>启用 @Scheduled 注解支持，配置基于虚拟线程的定时任务调度器。
 * <p>Java 25 虚拟线程（JEP 444 正式特性）使每次定时任务执行使用轻量级虚拟线程，
 * 无需像平台线程池那样调节线程池大小，避免资源浪费或不足。
 *
 * <h2>调度器选择
 * <p>使用 SimpleAsyncTaskScheduler，单个平台调度线程负责触发 + 每次任务执行创建新虚拟线程。
 * <p>与 SimpleAsyncTaskExecutor（异步方法场景）的设计理念一致：不设线程池上限，由 JVM 自动调度。
 * <p>调度器通过 @Bean 暴露，Spring 自动检测并用于 @Scheduled 方法，替换默认的单线程调度器。
 *
 * <h2>fixedDelay 注意事项
 * <p>SimpleAsyncTaskScheduler 的 fixed-delay 任务在单线程上串行执行，无法利用虚拟线程。
 * <p>需要固定延迟执行的场景，推荐改用 fixed-rate 或 cron 触发器。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@Configuration
@EnableScheduling
public class SchedulingConfig {

    // ================================ public 方法 ================================

    /**
     * 定时任务调度器 Bean
     *
     * <h3>线程策略
     * <p>使用 SimpleAsyncTaskScheduler 并显式启用虚拟线程。
     * <p>Spring Framework 7.x 在 Java 21+ 环境下默认启用虚拟线程，此处显式声明以增强可读性和确定性。
     *
     * <h3>自动发现
     * <p>Spring 的 ScheduledAnnotationBeanPostProcessor 自动检测容器中的 TaskScheduler Bean，替换默认的单线程调度器。
     *
     * @return 基于虚拟线程的定时任务调度器实例
     */
    @Bean
    public TaskScheduler taskScheduler() {
        // 创建 SimpleAsyncTaskScheduler 并显式启用虚拟线程
        SimpleAsyncTaskScheduler scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setVirtualThreads(true);

        // 配置定时任务未捕获异常处理器
        scheduler.setErrorHandler(schedulingErrorHandler());

        return scheduler;
    }

    // ================================ private 方法 ================================

    /**
     * 定时任务未捕获异常处理器
     *
     * <h3>处理策略
     * <p>记录异常的类名和消息，不中断调度循环。
     * <p>与 AsyncConfig 的 AsyncUncaughtExceptionHandler 策略一致。
     *
     * @return 全局定时任务异常处理器实例
     */
    private ErrorHandler schedulingErrorHandler() {
        return ex ->
            log.error(
                "定时任务执行异常，异常类型：{}，异常信息：{}",
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex
            );
    }
}
