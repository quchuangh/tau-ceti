package com.chuang.tauceti.tools.basic.cache;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 缓存接口，描述了一个HCache在本项目中可能涉及的基本操作。
 * HCache的结构为 HCache<KEY, FIELD, VALUE>
 */
public interface HCache {
    /**
     * 删除 缓存
     * @param key 缓存key，key 不 支持正则匹配
     */
    Boolean delete(byte[] key);

	/**
	 * 删除 缓存
	 * @param key key列表，每个key都 不 支持正则匹配
	 */
	Long delete(byte[]... key);

    /**
     * 通过正则key删除 缓存
     * @param keyPattern 支持正则的key
     */
	Long deletePattern(byte[] keyPattern);

	/**
	 * 根据 正则表达式，获取所有key
	 * @param pattern key表达式
	 * @return 匹配的所有key
	 */
	Set<byte[]> getKeys(byte[] pattern);

    /**
     * 获取key（支持正则的数量）
     * redis没有直接获取 key 长度的方法，目前只能先获取全部，然后计算长度
     * @param pattern key正则
     * @return
     */
    int getKeysLen(byte[] pattern);


    /**
     * 根据 正则表达式，获取所有key的所有 field
     * 注意：redis实现中没有通过 pattern 获取 field 的方法，所以这个实现是通过获取keys后，再遍历keys获取field的，可能性能上有点问题。
     * 另外，反序列失败的对象将打印异常并忽略它。
     * @param keyPattern key表达式
     * @return 根据正则匹配的所有key 的 field
     */
	Set<Object> getFields(byte[] keyPattern);
    /**
     * 获取 key中的所有field 的值
     */
    List<?> getValuesByKey(byte[] key);

	/**
	 * 更新 value
	 */
	Boolean updateValue(byte[] key, byte[] field, byte[] value);


	/**
	 * 获取value
	 */
	Optional<Object> getValue(byte[] key, byte[] filed);


	/**
	 * 删除 value
	 */
    Long deleteValue(byte[] key, byte[] filed);

	/**
	 * 获取 key 中的filed 的长度
	 * @param key
	 */
	Long getFieldSize(byte[] key);

    Long deleteField(byte[] key, byte[]... field);
}
