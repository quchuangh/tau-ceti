package com.chuang.urras.rowquery;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chuang.tauceti.tools.basic.collection.CollectionKit;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface IRowQueryService<T> extends IService<T> {

    /**
     * @see #findById(Serializable)
     */
    @Deprecated
    default T getById(Serializable id) {
        return getBaseMapper().selectById(id);
    }

    /**
     * @see #findOne(Wrapper)
     */
    @Deprecated
    default T getOne(Wrapper<T> queryWrapper) {
        return getOne(queryWrapper, true);
    }

    /**
     * @see #findOne(Wrapper, boolean)
     */
    @Deprecated
    T getOne(Wrapper<T> queryWrapper, boolean throwEx);

    /**
     * @see #findObj(Wrapper, Function)
     */
    @Deprecated
    <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper);

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    default Optional<T> findById(Serializable id) {
        return Optional.ofNullable(getBaseMapper().selectById(id));
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     */
    default Optional<T> findOne(Wrapper<T> queryWrapper) {
        return findOne(queryWrapper, true);
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     * @param throwEx      有多个 result 是否抛出异常
     */
    default Optional<T> findOne(Wrapper<T> queryWrapper, boolean throwEx) {
        return Optional.ofNullable(getOne(queryWrapper, throwEx));
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     * @param mapper       转换函数
     */
    default <V> Optional<V> findObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return Optional.ofNullable(getObj(queryWrapper, mapper));
    }

    Class<T> currentModelClass();

    /**
     * 查找出所有已存在的数据
     * @param map 字段
     * @param values 需要检测的值
     */
    default <R> Set<R> contains(SFunction<T, R> map, R... values) {
        return contains(map, Arrays.stream(values).collect(Collectors.toSet()));
    }

    default <R> Set<R> contains(SFunction<T, R> map, Set<R> values) {
        return lambdaQuery()
                .select(map)
                .in(map, values)
                .list()
                .stream()
                .map(map)
                .collect(Collectors.toSet());
    }

    /**
     * 查找出所有不存在的数据
     * @param map 字段
     * @param values 需要检测的值
     */
    default <R> Set<R> notContains(SFunction<T, R> map, R... values) {
        return notContains(map, Arrays.stream(values).collect(Collectors.toSet()));
    }

    /**
     * 查找出所有不存在的数据
     * @param map 字段
     * @param values 需要检测的值
     */
    default <R> Set<R> notContains(SFunction<T, R> map, Set<R> values) {
        Set<R> contains = contains(map, values);
        return CollectionKit.subtract(values, contains, HashSet::new);
    }

    /**
     * 根据condition参数的字段作为条件进行删除，为null的字段不参与条件
     * @param condition 条件对象
     * @return 是否删除
     */
    default boolean remove(T condition) {
        return remove(new QueryWrapper<>(condition));
    }

    /**
     * 根据condition参数的字段作为条件进行查询，为null的字段不参与条件
     * @param condition 条件对象
     * @return 查询结果
     */
    default List<T> list(T condition) {
        return list(new QueryWrapper<>(condition));
    }

    /**
     * 根据rowQuery对象分页查询
     * @param rowQuery rowquery
     * @return 分页数据
     */
    IPage<T> pageByRowQuery(RowQuery rowQuery);

}
