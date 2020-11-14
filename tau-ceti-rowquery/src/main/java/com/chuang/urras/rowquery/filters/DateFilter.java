package com.chuang.urras.rowquery.filters;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Data;

/**
 * Created by ath on 2018/4/29.
 */
@Data
public class DateFilter implements RowQuery.Filter {
    private String field;
    private String option;
    private String dateTo;
    private String dateFrom;
    @Override
    public String getField() {
        return field;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.DATE;
    }


    @Override
    public <T> void  handle(QueryWrapper<T> criteria, Class<T> clazz) {
        String _field = StringUtils.camelToUnderline(field);
        switch (option) {
            case "equals":
                criteria.eq(_field, dateFrom);
                break;
            case "greaterThan":
                criteria.gt(_field, dateFrom);
                break;
            case "greaterThanOrEqual":
                criteria.ge(_field, dateFrom);
                break;
            case "lessThan":
                criteria.lt(_field, dateFrom);
                break;
            case "lessThanOrEqual":
                criteria.le(_field, dateFrom);
                break;
            case "notEqual":
                criteria.ne(_field, dateFrom);
                break;
            case "inRange":
                criteria.between(_field, dateFrom, dateTo);
                break;
        }
    }
}
