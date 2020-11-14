package com.chuang.urras.rowquery;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuang.tauceti.tools.basic.collection.CollectionKit;
import com.chuang.urras.rowquery.filters.RowQuery;

public class RowQueryService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IRowQueryService<T> {


    @Override
    @SuppressWarnings("unchecked")
    public Class<T> currentModelClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(getClass(), 1);
    }

    @Override
    public IPage<T> pageByRowQuery(RowQuery rowQuery) {
        QueryWrapper<T> query = new QueryWrapper<>();
        //order by
        CollectionKit.foreach(rowQuery.getSorts(), sort -> {
            if("asc".equalsIgnoreCase(sort.getSort())) {
                query.orderByAsc(StringUtils.camelToUnderline(sort.getField()));
            } else {
                query.orderByDesc(StringUtils.camelToUnderline(sort.getField()));
            }
        });

        // 将所有filter 转化为条件
        CollectionKit.foreach(rowQuery.getFilters(), filter -> filter.handle(query, currentModelClass()));

        return baseMapper.selectPage(new Page<>(rowQuery.getPageNum(), rowQuery.getPageSize()), query);
    }
}
