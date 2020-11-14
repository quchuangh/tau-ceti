package com.chuang.urras.rowquery.filters;

/**
 * Created by ath on 2018/4/28.
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;

/**
 * 查询条件
 */
@Data
public class RowQuery {
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 分页大小 (limit)
     */
    private Integer pageSize;
    /**
     * 排序模型
     */
    private SortModel[] sorts;
    /**
     * 过滤器模型(条件)
     */
    private Filter[] filters;
    /**
     * 行组（暂时未实现）
     */
    private ColumnQry[] rowGroupCols;
    /**
     * 规定只查询的列（暂时未实现）
     */
    private ColumnQry[] valueCols;
    /**
     * 分组（暂时未实现）
     */
    private String[] groupKeys;

    @Data
    public static class SortModel {
        private String field;
        private String sort;
    }

    @Data
    public static class ColumnQry {
        private String id;
        private String displayName;
        private String field;
        private String aggFunc;
    }

    public interface Filter {
        String getField();
        FilterType getFilterType();
        <T> void handle(QueryWrapper<T> criteria, Class<T> clazz);
    }


}
