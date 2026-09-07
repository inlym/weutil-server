package com.weutil.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 可分页列表响应数据传输对象
 *
 * <h2>说明
 * <p>用于封装基于游标的分页列表响应数据，包含列表数据和用于查询下一页的游标值。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableListResponse<T> {

    /**
     * 当前页的列表数据
     *
     * @example []
     */
    private List<T> list;

    /**
     * 下一页游标，为空表示已无更多数据
     *
     * @example eyJpZCI6MTAwfQ==
     */
    private String nextCursor;

    /**
     * 是否还有更多数据可加载
     *
     * @example false
     */
    private Boolean hasMore;

    /**
     * 创建完整列表响应
     *
     * <h3>说明
     * <p>当列表数据已全部包含，无需分页时使用此方法快速创建响应对象。
     *
     * @param list 列表数据
     * @param <T>  列表元素类型
     * @return 完整列表响应对象
     */
    public static <T> PageableListResponse<T> all(List<T> list) {
        return PageableListResponse.<T>builder()
            .list(list)
            .nextCursor(null)
            .hasMore(false)
            .build();
    }
}
