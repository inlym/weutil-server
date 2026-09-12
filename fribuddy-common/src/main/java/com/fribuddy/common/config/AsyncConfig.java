package com.fribuddy.common.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 异步方法配置类
 *
 * <h2>配置说明
 * <p>启用 @Async 注解支持，配置基于虚拟线程的异步任务执行器。
 * <p>Java 25 虚拟线程（JEP 444 正式特性）使每个异步任务使用轻量级虚拟线程执行，
 * 无需像平台线程池那样调节核心线程数和最大线程数，避免资源浪费或不足。
 *
 * <h2>虚拟线程优势
 * <p>每个异步任务使用独立的虚拟线程，由 JVM 自动调度到少量平台线程上执行。
 * <p>虚拟线程创建和切换成本极低，支持百万级并发任务。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    // ================================ public 方法 ================================

    /**
     * 异步任务执行器
     *
     * <h3>线程策略
     * <p>使用 SimpleAsyncTaskExecutor 并显式启用虚拟线程。
     * <p>Spring Framework 7.x 在 Java 21+ 环境下默认启用虚拟线程，此处显式声明以增强可读性和确定性。
     *
     * <h3>执行器选择
     * <p>SimpleAsyncTaskExecutor 不为每个任务创建平台线程，而是创建虚拟线程，
     * 避免了 ThreadPoolTaskExecutor 需要手动调优线程池参数的弊端。
     *
     * <h3>上下文传递
     * <p>通过任务装饰器将主线程的 MDC（含 traceId）和 LocaleContext 复制到异步线程，
     * 保证异步任务的日志链路追踪和国际化上下文不丢失。
     *
     * @return 基于虚拟线程的异步任务执行器实例
     */
    @Override
    public Executor getAsyncExecutor() {
        // 创建 SimpleAsyncTaskExecutor 并显式启用虚拟线程
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setVirtualThreads(true);

        // 配置任务装饰器，在任务提交时捕获主线程的 MDC 和 LocaleContext
        executor.setTaskDecorator(runnable -> {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
            return () -> {
                // 异步线程上下文管理：使用 try-finally 确保上下文清理
                try {
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    if (localeContext != null) {
                        LocaleContextHolder.setLocaleContext(localeContext, true);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                    LocaleContextHolder.resetLocaleContext();
                }
            };
        });

        return executor;
    }

    /**
     * 异步方法未捕获异常处理器
     *
     * <h3>处理策略
     * <p>记录异常的方法名、参数数量和异常信息，不中断应用运行。
     * <p>业务模块如需自定义异常处理，应在 @Async 方法内部自行捕获。
     *
     * @return 全局异步异常处理器实例
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) ->
            log.error(
                "异步方法执行异常，方法：{}，参数数量：{}，异常类型：{}，异常信息：{}",
                method.getName(),
                params.length,
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex
            );
    }
}
