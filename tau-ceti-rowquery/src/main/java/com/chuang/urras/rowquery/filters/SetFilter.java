package com.chuang.urras.rowquery.filters;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.chuang.tauceti.support.exception.BusinessException;
import com.chuang.tauceti.tools.basic.reflect.ConvertKit;
import com.chuang.urras.rowquery.RowQuery;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ath on 2018/4/29.
 */
@Slf4j
@Data
public class SetFilter implements RowQuery.Filter {

    private String field;
    private String option;
    private String[] value;
    @Override
    public String getField() {
        return field;
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.SET;
    }


    @Override
    public <T> void handle(QueryWrapper<T> criteria, Class<T> objClazz) {
        if(value.length == 0) {
            throw new BusinessException("{}至少要选一项进行查询。否则查询无意义。", field);
        }

        List<Object> list = new ArrayList<>();
        try {
            Class<?> fieldType = objClazz.getDeclaredField(field).getType();
            if(fieldType == String.class) {
                list =  Arrays.asList(value);
            } else if(Enum.class.isAssignableFrom(fieldType)) {
                for (String v: value) {
                    Enum<?> e =  Enum.valueOf((Class<Enum>)fieldType, v);
                    list.add(e);
                }
            } else {
                for(String v: value) {
                    list.add(ConvertKit.parseBasic(fieldType, v));
                }
            }

        } catch (NoSuchFieldException e) {
            log.error("set filter error, 这里字符串方式", e);
            list =  Arrays.asList(value);
        }

        String _field = StringUtils.camelToUnderline(field);
        if("in".equalsIgnoreCase(option)) {
            criteria.in(_field, list);
        } else if("notIn".equalsIgnoreCase(option)) {
            criteria.notIn(_field, list);
        }
    }
}
