package com.weutil.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法执行日志注解
 *
 * <h2>注解说明
 * <p>用于标记需要记录执行日志的方法，通过 AOP 切面自动记录方法的执行过程，
 * 包括方法入参、执行时长和返回值等信息。
 *
 * <h2>日志特性
 * <p>使用动态 Logger 实例，日志来源显示为目标方法所在的业务类，而非切面类，
 * 便于调试和日志分析。日志中显示的类名将被正确识别为实际执行业务方法的类。
 *
 * <h2>异常处理
 * <p>方法执行异常时，由全局异常处理器统一记录异常日志，此切面不再接管异常处理。
 *
 * <h2>注意事项
 * <p><strong>AOP 无法拦截同一类中的内部方法调用</strong>。如果标记的方法是在同一个类中被其他方法调用，
 * 则注解不会生效。请确保标记的方法是通过外部调用触发的，或者将方法移到单独的服务类中。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecution {

    /**
     * 是否记录入参
     *
     * @return true 表示记录方法入参，false 表示不记录
     */
    boolean logParams() default true;

    /**
     * 是否记录返回值
     *
     * @return true 表示记录方法返回值，false 表示不记录
     */
    boolean logResult() default true;

    /**
     * 自定义方法名称
     * <p>当设置此值时，日志中将使用此名称而非实际方法名
     *
     * @return 自定义的方法名称，默认为空字符串表示使用实际方法名
     */
    String customName() default "";

    /**
     * 日志级别
     *
     * @return 日志级别字符串：trace、debug、info、warn、error
     */
    String level() default "trace";
}