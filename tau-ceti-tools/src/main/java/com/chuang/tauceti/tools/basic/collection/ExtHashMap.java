package com.chuang.tauceti.tools.basic.collection;

import com.chuang.tauceti.tools.basic.reflect.BeanKit;
import com.chuang.tauceti.tools.basic.reflect.ConvertKit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

/**
 * 扩充了HashMap中的方法
 * 
 * @author loolly, chill
 * 
 */
public class ExtHashMap extends HashMap<String, Object> {

	/**
	 * 创建Paras
	 * @return ExtHashMap
	 */
	public static ExtHashMap create() {
		return new ExtHashMap();
	}

	private ExtHashMap(){
		
	}

	
	/**
	 * 将PO对象转为Maps
	 * @param bean Bean对象
	 * @return Vo
	 */
	public static <T> ExtHashMap parse(T bean) {
		return create().parseBean(bean);
	}

	/**
	 * 将map对象转为Maps
	 *
	 * @param map
	 *            值对象
	 * @return Vo
	 */
	public static <T> ExtHashMap parse(Map<String, Object> map) {
		return create().parseMap(map);
	}

	
	/**
	 * 转换为Bean对象
	 * @param bean Bean
	 * @return Bean
	 */
	public <T> T toBean(T bean) {
		BeanKit.fillBeanWithMap(this, bean);
		return bean;
	}
	
	/**
	 * 填充Value Object对象
	 * @param clazz Value Object（或者POJO）的类
	 * @return vo
	 */
	public <T> Optional<T> toBean(Class<T> clazz) {
		return ConvertKit.mapToBean(this, clazz);
	}
	
	/**
	 * 填充Value Object对象，忽略大小写
	 * @param clazz Value Object（或者POJO）的类
	 * @return vo
	 */
	public <T> Optional<T> toBeanIgnoreCase(Class<T> clazz) {
		return ConvertKit.mapToBeanIgnoreCase(this, clazz);
	}
	
	/**
	 * 将值对象转换为Maps<br>
	 * 类名会被当作表名，小写第一个字母
	 * @param bean 值对象
	 * @return 自己
	 */
	public <T> ExtHashMap parseBean(T bean) {
		this.putAll(ConvertKit.beanToMap(bean));
		return this;
	}
	
	/**
	 * 将值对象转换为Maps<br>
	 * 类名会被当作表名，小写第一个字母
	 *
	 * @param map
	 *            值对象
	 * @return 自己
	 */
	public <T> ExtHashMap parseMap(Map<String, Object> map) {
		this.putAll(map);
		return this;
	}

	/**
	 * 与给定实体对比并去除相同的部分<br>
	 * 此方法用于在更新操作时避免所有字段被更新，跳过不需要更新的字段
	 * version from 2.0.0
	 * @param withoutNames 不需要去除的字段名
	 */
	public <T extends ExtHashMap> void removeEqual(T map, String... withoutNames) {
		HashSet<String> withoutSet = new HashSet<>();
        Collections.addAll(withoutSet, withoutNames);
		
		for(Entry<String, Object> entry : map.entrySet()) {
			if(withoutSet.contains(entry.getKey())) {
				continue;
			}
			
			final Object value = this.get(entry.getKey());
			if(null != value && value.equals(entry.getValue())) {
				this.remove(entry.getKey());
			}
		}
	}

	//-------------------------------------------------------------------- 特定类型值
	/**
	 * 设置列
	 * @param attr 属性
	 * @param value 值
	 * @return 本身
	 */
	public ExtHashMap set(String attr, Object value) {
		return this.put(attr, value);
	}
	
	/**
	 * 设置列
	 * @param attr 属性
	 * @param value 值
	 * @return 本身
	 */
	@Override
	public ExtHashMap put(String attr, Object value) {
		super.put(attr, value);
		return this;
	}
	
	
	/**
	 * 设置列，当键或值为null时忽略
	 * @param attr 属性
	 * @param value 值
	 * @return 本身
	 */
	public ExtHashMap setIgnoreNull(String attr, Object value) {
		if(null != attr && null != value) {
			set(attr, value);
		}
		return this;
	}
	
	/**
	 * 获得特定类型值
	 * @param attr 字段名
	 * @param defaultValue 默认值
	 * @return 字段值
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String attr, T defaultValue) {
		final Object result = get(attr);
		return (T)(result != null ? result : defaultValue);
	}
	
	/**
	 * @param attr 字段名
	 * @return 字段值
	 */
	public String getStr(String attr) {
		return ConvertKit.toStr(get(attr), "");
	}
	
	/**
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Integer getInt(String attr) {
		return ConvertKit.toInt(get(attr), 0);
	}
	
	/**
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Long getLong(String attr) {
		return ConvertKit.toLong(get(attr), null);
	}
	
	/**
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Float getFloat(String attr) {
		return ConvertKit.toFloat(get(attr), null);
	}
	
	/**
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Boolean getBool(String attr) {
		return ConvertKit.toBool(get(attr), null);
	}
	
	/**
	 * @param attr 字段名
	 * @return 字段值
	 */
	public byte[] getBytes(String attr) {
		return get(attr, null);
	}
	
	/**
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Date getDate(String attr) {
		return get(attr, null);
	}
	
	/**
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Time getTime(String attr) {
		return get(attr, null);
	}
	
	/**
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Timestamp getTimestamp(String attr) {
		return get(attr, null);
	}
	
	/**
	 * @param attr 字段名
	 * @return 字段值
	 */
	public Number getNumber(String attr) {
		return get(attr, null);
	}
	
	/**
	 * @param attr 字段名
	 * @return 字段值
	 */
	public BigDecimal getBigDecimal(String attr) {
		return get(attr, null);
	}
	
	/**
	 * @param attr 字段名
	 * @return 字段值
	 */
	public BigInteger getBigInteger(String attr) {
		return get(attr, null);
	}
	
	//-------------------------------------------------------------------- 特定类型值
	
	@Override
	public ExtHashMap clone() {
		return (ExtHashMap) super.clone();
	}
	
	//-------------------------------------------------------------------- 特定类型值
}
