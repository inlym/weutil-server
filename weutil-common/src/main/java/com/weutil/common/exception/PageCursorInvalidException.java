package com.weutil.common.exception;

/**
 * 分页游标无效异常
 *
 * <h2>异常说明
 * <p>当通过游标（cursor）进行分页查询时，发现游标无效、未找到或与查询条件不匹配时抛出此异常。
 *
 * <h2>使用场景
 * <pre>{@code
 * @Service
 * @RequiredArgsConstructor
 * public class OrderService {
 *     private final OrderMapper orderMapper;
 *
 *     public List<Order> getOrdersByCursor(String cursor) {
 *         // 解码并验证游标
 *         PageCursor pageCursor = parseAndValidateCursor(cursor);
 *
 *         // 查询游标对应的记录
 *         Order cursorOrder = orderMapper.selectOneByCondition(
 *             ORDER_INFO.ID.eq(pageCursor.getPrimaryKeyId())
 *         );
 *
 *         // 游标对应的记录不存在或与查询条件不匹配
 *         if (cursorOrder == null || !isMatchQueryCondition(cursorOrder)) {
 *             throw new PageCursorInvalidException("分页游标无效");
 *         }
 *
 *         // 执行分页查询
 *         return orderMapper.selectListByCondition(
 *             ORDER_INFO.CREATE_TIME.lt(pageCursor.getPrimaryFieldValue())
 *         );
 *     }
 * }
 * }</pre>
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
public class PageCursorInvalidException extends BaseException {

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public PageCursorInvalidException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public PageCursorInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}