package com.weutil.common.util;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * MDC 任务包装工具类
 *
 * <h2>功能说明
 * <p>用于在异步任务执行时传递 MDC（Mapped Diagnostic Context）上下文
 * <p>解决多线程环境下 MDC 上下文丢失的问题，确保日志追踪信息完整
 *
 * <h2>使用场景
 * <ul>
 *   <li>线程池执行器提交任务时</li>
 *   <li>异步回调处理时</li>
 *   <li>任何涉及线程切换的异步场景</li>
 * </ul>
 *
 * <h2>使用示例
 * <pre>
 * // 提交 Runnable 任务
 * executor.execute(MdcUtils.wrapWithMdc(() -> doSomething()));
 *
 * // 提交 Callable 任务
 * Future&lt;String&gt; future = executor.submit(MdcUtils.wrapWithMdc(() -> computeResult()));
 * </pre>
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public final class MdcUtils {

    // 私有构造函数，防止实例化
    private MdcUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 包装任务以传递 MDC 上下文到执行线程
     *
     * <h3>处理逻辑
     * <p>捕获当前线程的 MDC 上下文副本
     * <p>在任务执行前将 MDC 上下文设置到执行线程
     * <p>任务执行完成后清理 MDC 上下文，防止上下文污染
     *
     * <h3>线程安全
     * <p>每个任务都会获取独立的 MDC 上下文副本，互不影响
     * <p>使用 try-finally 确保上下文清理，防止内存泄漏
     *
     * @param task 需要在 MDC 上下文中执行的任务
     * @return 包装了 MDC 上下文的任务
     */
    public static Runnable wrapWithMdc(Runnable task) {
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                task.run();
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     * 包装 Callable 任务以传递 MDC 上下文到执行线程
     *
     * <h3>处理逻辑
     * <p>捕获当前线程的 MDC 上下文副本
     * <p>在任务执行前将 MDC 上下文设置到执行线程
     * <p>任务执行完成后清理 MDC 上下文，防止上下文污染
     *
     * <h3>线程安全
     * <p>每个任务都会获取独立的 MDC 上下文副本，互不影响
     * <p>使用 try-finally 确保上下文清理，防止内存泄漏
     *
     * @param task 需要在 MDC 上下文中执行的 Callable 任务
     * @param <T>  返回值类型
     * @return 包装了 MDC 上下文的 Callable
     */
    public static <T> Callable<T> wrapWithMdc(Callable<T> task) {
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                return task.call();
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     * 包装 Consumer 以传递 MDC 上下文到执行线程
     *
     * <h3>处理逻辑
     * <p>捕获当前线程的 MDC 上下文副本
     * <p>在 Consumer 执行前将 MDC 上下文设置到执行线程
     * <p>Consumer 执行完成后清理 MDC 上下文，防止上下文污染
     *
     * <h3>线程安全
     * <p>每个 Consumer 都会获取独立的 MDC 上下文副本，互不影响
     * <p>使用 try-finally 确保上下文清理，防止内存泄漏
     *
     * @param consumer 需要在 MDC 上下文中执行的 Consumer
     * @param <T>      Consumer 接收的参数类型
     * @return 包装了 MDC 上下文的 Consumer
     */
    public static <T> Consumer<T> wrapConsumerWithMdc(Consumer<T> consumer) {
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return t -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                consumer.accept(t);
            } finally {
                MDC.clear();
            }
        };
    }
}
