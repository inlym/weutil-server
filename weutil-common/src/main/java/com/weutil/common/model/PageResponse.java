package com.weutil.common.model;

import com.mybatisflex.core.paginate.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果响应数据
 *
 * <h2>主要用途
 * <p>包装分页结果
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 2026-09-07
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    /** 当前页数据 */
    private List<T> list;

    /** 当前页码（从1开始） */
    private Long currentPage;

    /** 总页数（即最大页码） */
    private Long totalPage;

    /** 每页数量 */
    private Long pageSize;

    /** 数据总条数 */
    private Long totalCount;

    /**
     * 包装 MyBatis-Flex 的分页结果
     *
     * @param page 分页结果
     * @return 分页结果响应数据
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setList(page.getRecords());
        response.setCurrentPage(page.getPageNumber());
        response.setPageSize(page.getPageSize());
        response.setTotalPage(page.getTotalPage());
        response.setTotalCount(page.getTotalRow());

        return response;
    }
}
