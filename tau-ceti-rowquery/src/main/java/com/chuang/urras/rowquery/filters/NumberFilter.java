package com.chuang.urras.rowquery.filters;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.chuang.urras.rowquery.RowQuery;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by ath on 2018/4/29.
 */
@Data
public class NumberFilter implements RowQuery.Filter {
    private String field;
    private String option;
    private BigDecimal value;
    private BigDecimal valueTo;
    @Override
    public String getField() {
        return field;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.NUMBER;
    }


    @Override
    public <T> void handle(QueryWrapper<T> criteria, Class<T> clazz) {
        String _field = StringUtils.camelToUnderline(field);
        switch (option) {
            case "equals":
                criteria.eq(_field, value);
                break;
            case "notEqual":
                criteria.ne(_field, value);
                break;
            case "lessThan":
                criteria.lt(_field, value);
                break;
            case "lessThanOrEqual":
                criteria.le(_field, value);
                break;
            case "greaterThan":
                criteria.gt(_field, value);
                break;
            case "greaterThanOrEqual":
                criteria.ge(_field, value);
                break;
            case "inRange":
                criteria.between(_field, value, valueTo);
                break;

        }
    }
}
