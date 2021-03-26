package com.chuang.urras.rowquery.filters;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.chuang.urras.rowquery.RowQuery;
import lombok.Data;

/**
 * Created by ath on 2018/4/29.
 */
@Data
public class TextFilter implements RowQuery.Filter {

    private String field;
    private String option;
    private String value;

    @Override
    public String getField() {
        return field;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.TEXT;
    }

    @Override
    public <T> void handle(QueryWrapper<T> criteria, Class<T> clazz) {
        String _field = StringUtils.camelToUnderline(field);
        if ("equals".equalsIgnoreCase(option)) {
            criteria.eq(_field, value);
        } else if("notEqual".equalsIgnoreCase(option)) {
            criteria.ne(_field, value);
        } else if("startsWith".equalsIgnoreCase(option)) {
            criteria.likeRight(_field, value);
        } else if("endsWith".equalsIgnoreCase(option)) {
            criteria.likeLeft(_field, value);
        } else if("contains".equalsIgnoreCase(option)) {
            criteria.like(_field, value);
        } else if("notContains".equalsIgnoreCase(option)) {
            criteria.notLike(_field, value);
        }
    }

}
