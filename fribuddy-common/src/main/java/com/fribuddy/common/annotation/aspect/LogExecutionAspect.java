package com.fribuddy.common.annotation.aspect;

import com.fribuddy.common.annotation.LogExecution;
import com.fribuddy.common.util.LogUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * LogExecution 注解的切面实现
 *
 * <h2>切面说明
 * <p>通过 AOP 切面编程技术，为标注了 @LogExecution 注解的方法提供执行日志记录功能。
 * <p>自动记录方法入参、执行时长和返回值，异常由全局异常处理器统一处理。
 * <p>使用动态 Logger，日志来源显示为目标方法所在的业务类，便于调试和日志分析。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
// 日志切面需在事务切面外层，确保日志能完整记录事务内的执行结果
@Order(0)
@Aspect
@Component
public class LogExecutionAspect {

    /**
     * 环绕通知，拦截标注了 @LogExecution 注解的方法
     *
     * @param joinPoint    连接点，包含方法执行的相关信息
     * @param logExecution LogExecution 注解实例
     * @return 方法执行的返回值
     * @throws Throwable 方法执行时可能抛出的异常
     */
    @Around("@annotation(logExecution)")
    public Object logExecution(ProceedingJoinPoint joinPoint, LogExecution logExecution) throws Throwable {
        // 获取目标类的 Logger
        Logger targetLogger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

        // 获取方法签名信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();

        // 获取自定义方法名或使用"类名#方法名"格式
        String displayName = logExecution.customName().isEmpty()
            ? joinPoint.getTarget().getClass().getSimpleName() + "#" + methodName
            : logExecution.customName();

        // 获取日志级别
        String logLevel = logExecution.level().toLowerCase();

        // 记录方法调用前日志（入参）
        logStartExecution(
            targetLogger,
            displayName,
            joinPoint.getArgs(),
            logLevel,
            signature,
            logExecution.logParams()
        );

        // 记录开始时间
        long startTime = System.nanoTime();

        // 执行目标方法
        Object result = joinPoint.proceed();

        // 计算执行时长
        long executionTime = (System.nanoTime() - startTime) / 1_000_000;

        // 记录方法执行完成日志（返回值 + 耗时）
        logEndExecution(
            targetLogger,
            displayName,
            executionTime,
            result,
            logExecution.logResult(),
            logLevel,
            signature.getReturnType()
        );

        return result;
    }

    /**
     * 记录方法调用前的日志（入参）
     *
     * @param logger      目标类的 Logger 实例
     * @param displayName 显示名称，格式为"类名#方法名"或自定义名称
     * @param args        方法入参数组
     * @param logLevel    日志级别
     * @param signature   方法签名，用于获取参数名
     */
    private void logStartExecution(
        Logger logger,
        String displayName,
        Object[] args,
        String logLevel,
        MethodSignature signature,
        boolean logParams
    ) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("[").append(displayName).append("]");

        logMessage.append(" 开始调用");
        if (!logParams) {
            logMessage.append("，已忽略入参展示");
        } else if (args != null && args.length > 0) {
            String paramsStr = formatArguments(args, signature);
            logMessage.append("，入参为：").append(paramsStr);
        } else {
            logMessage.append("，该方法无入参");
        }

        logWithLevel(logger, logLevel, logMessage.toString());
    }

    /**
     * 记录方法执行完成的日志（返回值 + 耗时）
     *
     * @param logger        目标类的 Logger 实例
     * @param displayName   显示名称，格式为"类名#方法名"或自定义名称
     * @param executionTime 执行时长（毫秒）
     * @param result        方法返回值
     * @param logResult     是否记录返回值
     * @param logLevel      日志级别
     */
    private void logEndExecution(
        Logger logger,
        String displayName,
        long executionTime,
        Object result,
        boolean logResult,
        String logLevel,
        Class<?> returnType
    ) {
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("[").append(displayName).append("]");

        logMessage.append(" 完成调用，耗时 ").append(executionTime).append("ms");
        if (!logResult) {
            logMessage.append("，已忽略返回值展示");
        } else if (returnType == void.class || returnType == Void.class) {
            logMessage.append("，该方法无返回值");
        } else {
            logMessage.append("，返回值为：").append(LogUtils.preview(result));
        }

        logWithLevel(logger, logLevel, logMessage.toString());
    }

    /**
     * 根据指定日志级别记录日志
     *
     * @param logger  目标类的 Logger 实例
     * @param level   日志级别
     * @param message 日志消息
     * @param args    参数
     */
    private void logWithLevel(Logger logger, String level, String message, Object... args) {
        switch (level) {
            case "debug" -> logger.debug(message, args);
            case "info" -> logger.info(message, args);
            case "warn" -> logger.warn(message, args);
            case "error" -> logger.error(message, args);
            default -> logger.trace(message, args);
        }
    }

    /**
     * 格式化方法入参数组为字符串
     *
     * <h3>格式说明
     * <p>将入参格式化为 "paramName1=value1, paramName2=value2" 的形式
     * <p>优先使用方法参数的实际名称，如果无法获取则使用 param1, param2 等形式
     *
     * @param args      入参数组
     * @param signature 方法签名，用于获取参数名
     * @return 格式化后的字符串
     */
    private String formatArguments(Object[] args, MethodSignature signature) {
        if (args == null || args.length == 0) {
            return "";
        }

        // 获取方法参数名
        String[] paramNames = signature.getParameterNames();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            // 优先使用实际参数名，如果没有则使用 param1 形式
            String paramName;
            if (paramNames != null && i < paramNames.length && paramNames[i] != null) {
                paramName = paramNames[i];
            } else {
                paramName = "param" + (i + 1);
            }

            sb.append(paramName).append("=").append(LogUtils.preview(args[i]));
        }
        return sb.toString();
    }

}
