package com.weutil.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 分页游标数据
 *
 * <h2>说明
 * <p>用于封装基于游标的分页查询参数，包含主键、主字段时间戳等关键信息。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageCursor {

    /** 主字段的值 */
    private Instant primaryFieldValue;

    /** 主键 ID 的值 */
    private Long primaryKeyId;

    /** 游标值 */
    private String cursor;
}
