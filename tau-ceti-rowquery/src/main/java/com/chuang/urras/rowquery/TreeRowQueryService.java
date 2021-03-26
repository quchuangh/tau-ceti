package com.chuang.urras.rowquery;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public abstract class TreeRowQueryService<M extends BaseMapper<T>, T> extends RowQueryService<M, T> {

    @Override
    protected QueryWrapper<T> toQueryWrapper(RowQuery rowQuery) {
        QueryWrapper<T> wrapper = super.toQueryWrapper(rowQuery);
        treePath(wrapper, rowQuery.getTreePath());
        return wrapper;
    }

    protected abstract void treePath(QueryWrapper<T> wrapper, String[] treePath);
}
