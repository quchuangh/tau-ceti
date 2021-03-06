package com.chuang.tauceti.tools.basic.reflect;

import com.chuang.tauceti.support.Result;
import com.chuang.tauceti.support.exception.BusinessException;
import com.chuang.tauceti.support.exception.SystemException;
import com.chuang.tauceti.tools.basic.BasicType;
import com.chuang.tauceti.tools.basic.HexKit;
import com.chuang.tauceti.tools.basic.StringKit;
import com.chuang.tauceti.tools.basic.collection.CollectionKit;
import org.springframework.beans.BeanUtils;

import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * 类型转换器
 * 
 * @author xiaoleilu
 * 
 */
public class ConvertKit {

	private static final SimpleDateFormat DATE_FORMAT_0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat DATE_FORMAT_1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private ConvertKit() {
		// 静态类不可实例化
	}
	/**
	 * 强制转换类型
	 * 
	 * @param clazz 被转换成的类型
	 * @param value 需要转换的对象
	 * @return 转换后的对象
	 */
	public static Object parse(Class<?> clazz, Object value) throws ParseException {
		try {
			if (clazz.isAssignableFrom(String.class)) {
				// ----2016-12-19---zhuangqian----防止beetlSql对空字符串不检测导致无法入库的问题----
				if (StringKit.isBlank(String.valueOf(value)))
					return " ";
				else
					return String.valueOf(value);
			}
			return clazz.cast(value);
		} catch (ClassCastException e) {
			String valueStr = String.valueOf(value);

			Object result = parseBasic(clazz, valueStr);
			if (result != null) {
				return result;
			}

			if (Date.class.isAssignableFrom(clazz)) {
				// 判断标准日期
				// ----2016-11-24---zhuangqian----需要加toDate(),不然beetlsql转换date类型的时候会报错----
				return parseDate(valueStr);
			} else if (clazz == BigInteger.class) {
				// 数学计算数字
				return new BigInteger(valueStr);
			} else if (clazz == BigDecimal.class) {
				// 数学计算数字
				return new BigDecimal(valueStr);
			} else if (clazz == byte[].class) {
				// 流，由于有字符编码问题，在此使用系统默认
				return valueStr.getBytes();
			}
			// 未找到可转换的类型，返回原值
			return (StringKit.isBlank(valueStr)) ? null : value;
		}
	}

	public static Date parseDate(String date) throws ParseException {
		try {
			return DATE_FORMAT_0.parse(date);
		} catch (Exception e) {
			return DATE_FORMAT_1.parse(date);
		}
	}

	/**
	 * 转换基本类型<br>
	 * 将字符串转换为原始类型或包装类型
	 * 
	 * @param clazz 转换到的类，可以是原始类型类，也可以是包装类型类
	 * @param valueStr 被转换的字符串
	 * @return 转换后的对象，如果非基本类型，返回null
	 */
	public static Object parseBasic(Class<?> clazz, String valueStr) {
		if (null == clazz || null == valueStr) {
			return null;
		}

		if (StringKit.isBlank(valueStr)) return null;
		
		BasicType basicType;
		try {
			basicType = BasicType.valueOf(clazz.getSimpleName().toUpperCase());
		} catch (Exception e) {
			// 非基本类型数据
			return null;
		}

		switch (basicType) {
			case BYTE:
				if (clazz == byte.class) {
					return Byte.parseByte(valueStr);
				}
				return Byte.valueOf(valueStr);
			case SHORT:
				if (clazz == short.class) {
					return Short.parseShort(valueStr);
				}
				return Short.valueOf(valueStr);
			case INT:
				return Integer.parseInt(valueStr);
			case INTEGER:
				return Integer.valueOf(valueStr);
			case LONG:
				if (clazz == long.class) {
					return new BigDecimal(valueStr).longValue();
				}
				return Long.valueOf(valueStr);
			case DOUBLE:
				if (clazz == double.class) {
					return new BigDecimal(valueStr).doubleValue();
				}
			case FLOAT:
				if (clazz == float.class) {
					return Float.parseFloat(valueStr);
				}
				return Float.valueOf(valueStr);
			case BOOLEAN:
				if (clazz == boolean.class) {
					return Boolean.parseBoolean(valueStr);
				}
				return Boolean.valueOf(valueStr);
			case CHAR:
			case CHARACTER:
				return valueStr.charAt(0);
			default:
				return null;
		}
	}

	/**
	 * 转换为字符串<br>
	 * 如果给定的值为null，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static String toStr(Object value, String defaultValue) {
		if (null == value) {
			return defaultValue;
		}
		if (value instanceof String) {
			return (String) value;
		} else if (CollectionKit.isArray(value)) {
			return CollectionKit.toString(value);
		}
		return value.toString();
	}

	/**
	 * 转换为字符串<br>
	 * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static String toStr(Object value) {
		return toStr(value, null);
	}

	public static <V> V toBean(Object entity, Supplier<V> objNew) {
		if(null == entity) {
			return objNew.get();
		}
		V vo = objNew.get();
		BeanUtils.copyProperties(entity, vo);
		return vo;
	}

	public static <V> List<V> toBeans(Collection<?> list, Supplier<V> objNew) {
		List<V> vList = new ArrayList<>();
		for(Object obj: list) {
			vList.add(toBean(obj, objNew));
		}

		return vList;
	}

	public static <C extends Collection<V>, V> C convert(Collection<?> list, Supplier<C> collectionNew, Supplier<V> objNew) {
		C collection = collectionNew.get();
		for(Object obj: list) {
			collection.add(toBean(obj, objNew));
		}

		return collection;
	}

	/**
	 * ServletRequest 参数转Bean
	 *
	 * @param request   ServletRequest
	 * @param beanClass Bean Class
	 * @return Bean
	 */
	public static <T> Optional<T> requestParamToBean(javax.servlet.ServletRequest request, Class<T> beanClass) {
		return ClassKit.newInstance(beanClass).map(bean -> BeanKit.fillBeanWithRequestParam(request, bean));
	}

	/**
	 * Map转换为Bean对象
	 *
	 * @param map       Map
	 * @param beanClass Bean Class
	 * @return Bean
	 */
	public static <T> Optional<T> mapToBean(Map<?, ?> map, Class<T> beanClass) {
		return ClassKit.newInstance(beanClass).map(t -> BeanKit.fillBeanWithMap(map, t));
	}

	/**
	 * Map转换为Bean对象<br>
	 * 忽略大小写
	 *
	 * @param map       Map
	 * @param beanClass Bean Class
	 * @return Bean
	 */
	public static <T> Optional<T> mapToBeanIgnoreCase(Map<?, ?> map, Class<T> beanClass) {
		return ClassKit.newInstance(beanClass).map(t -> BeanKit.fillBeanWithMapIgnoreCase(map, t));
	}

	/**
	 * 转换为字符<br>
	 * 如果给定的值为null，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Character toChar(Object value, Character defaultValue) {
		if (null == value) {
			return defaultValue;
		}
		if (value instanceof Character) {
			return (Character) value;
		}

		final String valueStr = toStr(value, null);
		return StringKit.isEmpty(valueStr) ? defaultValue : valueStr.charAt(0);
	}

	/**
	 * ServletRequest 参数转Bean
	 *
	 * @param beanClass     Bean Class
	 * @param valueProvider 值提供者
	 * @return Bean
	 */
	public static <T> Optional<T> toBean(Class<T> beanClass, @Nullable Function<String, Object> valueProvider) {
		return ClassKit.newInstance(beanClass).map(t -> BeanKit.fillBean(t, valueProvider));
	}
	/**
	 * 对象转Map
	 *
	 * @param bean bean对象
	 * @return Map
	 */
	public static <T> Map<String, Object> beanToMap(T bean) {
		return beanToMap(bean, false);
	}

	/**
	 * 对象转Map
	 *
	 * @param bean bean对象
	 * @return Map
	 */
	public static <T> List<Map<String, Object>> listToMapList(List<T> bean) {
		ArrayList<Map<String, Object>> maps = new ArrayList<>();
		for (T t : bean) {
			maps.add(beanToMap(bean, false));
		}
		return maps;
	}

	/**
	 * 对象转Map
	 *
	 * @param bean              bean对象
	 * @param isToUnderlineCase 是否转换为下划线模式
	 * @return Map
	 */
	public static <T> Map<String, Object> beanToMap(T bean, boolean isToUnderlineCase) {

		Map<String, Object> map = new HashMap<>();
		try {
			final PropertyDescriptor[] propertyDescriptors = BeanKit.getPropertyDescriptors(bean.getClass());
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				// 过滤class属性
				if (!key.equals("class")) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(bean);
					if (null != value) {
						map.put(isToUnderlineCase ? StringKit.toUnderlineCase(key) : key, value);
					}
				}
			}
		} catch (Exception e) {
			throw new SystemException("数据转换异常", e);
		}
		return map;
	}

	/**
	 * 转换为字符<br>
	 * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Character toChar(Object value) {
		return toChar(value, null);
	}

	/**
	 * 转换为byte<br>
	 * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Byte toByte(Object value, Byte defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Byte) {
			return (Byte) value;
		}
		if (value instanceof Number) {
			return ((Number) value).byteValue();
		}
		final String valueStr = toStr(value, null);
		if (StringKit.isBlank(valueStr)) {
			return defaultValue;
		}
		try {
			return Byte.parseByte(valueStr);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为byte<br>
	 * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Byte toByte(Object value) {
		return toByte(value, null);
	}

	/**
	 * 转换为Short<br>
	 * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Short toShort(Object value, Short defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Short) {
			return (Short) value;
		}
		if (value instanceof Number) {
			return ((Number) value).shortValue();
		}
		final String valueStr = toStr(value, null);
		if (StringKit.isBlank(valueStr)) {
			return defaultValue;
		}
		try {
			return Short.parseShort(valueStr.trim());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为Short<br>
	 * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Short toShort(Object value) {
		return toShort(value, null);
	}

	/**
	 * 转换为Number<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Number toNumber(Object value, Number defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Number) {
			return (Number) value;
		}
		final String valueStr = toStr(value, null);
		if (StringKit.isBlank(valueStr)) {
			return defaultValue;
		}
		try {
			return NumberFormat.getInstance().parse(valueStr);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为Number<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Number toNumber(Object value) {
		return toNumber(value, null);
	}

	/**
	 * 转换为int<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Integer toInt(@Nullable Object value, Integer defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Integer) {
			return (Integer) value;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		final String valueStr = toStr(value, null);
		if (StringKit.isBlank(valueStr)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(valueStr.trim());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为int<br>
	 * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Integer toInt(Object value) {
		return toInt(value, null);
	}

	/**
	 * 转换为Integer数组<br>
	 * 
	 * @param isIgnoreConvertError 是否忽略转换错误，忽略则给值null
	 * @param values 被转换的值
	 * @return 结果
	 */
	public static Integer[] toIntArray(boolean isIgnoreConvertError, Object... values) {
		if (CollectionKit.isEmpty(values)) {
			return new Integer[] {};
		}
		final Integer[] ints = new Integer[values.length];
		for (int i = 0; i < values.length; i++) {
			final Integer v = toInt(values[i], null);
			if (null == v && !isIgnoreConvertError) {
				throw new BusinessException("Convert [{}] to Integer error!", values[i]);
			}
			ints[i] = v;
		}
		return ints;
	}
	
	
	/**
	 * 转换为Integer数组<br>
	 * 
	 * @param str 被split转换的值
	 * @return 结果
	 */
	public static Integer[] toIntArray(String str) {
		return toIntArray(",", str);
	}
	
	/**
	 * 转换为Integer数组<br>
	 * 
	 * @param split 分隔符
	 * @param str 被转换的值
	 * @return 结果
	 */
	public static Integer[] toIntArray(String split, String str) {
		if (StringKit.isEmpty(str)) {
			return new Integer[] {};
		}
		String[] arr = str.split(split);
		final Integer[] ints = new Integer[arr.length];
		for (int i = 0; i < arr.length; i++) {
			final Integer v = toInt(arr[i], 0);
			ints[i] = v;
		}
		return ints;
	}
	
	/**
	 * 转换为String数组<br>
	 * 
	 * @param str 被转换split 的值
	 * @return 结果
	 */
	public static String[] toStrArray(String str) {
		return toStrArray("", str);
	}
	
	/**
	 * 转换为String数组<br>
	 * 
	 * @param split 分隔符
	 * @param str 被转换的值
	 * @return 结果
	 */
	public static String[] toStrArray(String split, String str) {
		return str.split(split);
	}

	/**
	 * 转换为long<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Long toLong(Object value, Long defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Long) {
			return (Long) value;
		}
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		final String valueStr = toStr(value, null);
		if (StringKit.isBlank(valueStr)) {
			return defaultValue;
		}
		try {
			// 支持科学计数法
			return new BigDecimal(valueStr.trim()).longValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为long<br>
	 * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Long toLong(Object value) {
		return toLong(value, null);
	}

	/**
	 * 转换为Long数组<br>
	 * 
	 * @param isIgnoreConvertError 是否忽略转换错误，忽略则给值null
	 * @param values 被转换的值
	 * @return 结果
	 */
	public static Long[] toLongArray(boolean isIgnoreConvertError, Object... values) {
		if (CollectionKit.isEmpty(values)) {
			return new Long[] {};
		}
		final Long[] longs = new Long[values.length];
		for (int i = 0; i < values.length; i++) {
			final Long v = toLong(values[i], null);
			if (null == v && !isIgnoreConvertError) {
				throw new BusinessException("Convert [{}] to Long error!", values[i]);
			}
			longs[i] = v;
		}
		return longs;
	}

	/**
	 * 转换为double<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Double toDouble(Object value, Double defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Double) {
			return (Double) value;
		}
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}
		final String valueStr = toStr(value, null);
		if (StringKit.isBlank(valueStr)) {
			return defaultValue;
		}
		try {
			// 支持科学计数法
			return new BigDecimal(valueStr.trim()).doubleValue();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为double<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Double toDouble(Object value) {
		return toDouble(value, null);
	}

	/**
	 * 转换为Double数组<br>
	 * 
	 * @param isIgnoreConvertError 是否忽略转换错误，忽略则给值null
	 * @param values 被转换的值
	 * @return 结果
	 */
	public static Double[] toDoubleArray(boolean isIgnoreConvertError, Object... values) {
		if (CollectionKit.isEmpty(values)) {
			return new Double[] {};
		}
		final Double[] doubles = new Double[values.length];
		for (int i = 0; i < values.length; i++) {
			final Double v = toDouble(values[i], null);
			if (null == v && !isIgnoreConvertError) {
				throw new BusinessException("Convert [{}] to Double error!", values[i]);
			}
			doubles[i] = v;
		}
		return doubles;
	}

	/**
	 * 转换为Float<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Float toFloat(Object value, Float defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Float) {
			return (Float) value;
		}
		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}
		final String valueStr = toStr(value, null);
		if (StringKit.isBlank(valueStr)) {
			return defaultValue;
		}
		try {
			return Float.parseFloat(valueStr.trim());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为Float<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Float toFloat(Object value) {
		return toFloat(value, null);
	}

	/**
	 * 转换为Float数组<br>
	 * 
	 * @param isIgnoreConvertError 是否忽略转换错误，忽略则给值null
	 * @param values 被转换的值
	 * @return 结果
	 */
	public static Float[] toFloatArray(boolean isIgnoreConvertError, Object... values) {
		if (CollectionKit.isEmpty(values)) {
			return new Float[] {};
		}
		final Float[] floats = new Float[values.length];
		for (int i = 0; i < values.length; i++) {
			final Float v = toFloat(values[i], null);
			if (null == v && !isIgnoreConvertError) {
				throw new SystemException(StringKit.format("Convert [{}] to Float error!", values[i]));
			}
			floats[i] = v;
		}
		return floats;
	}

	/**
	 * 转换为boolean<br>
	 * String支持的值为：true、false、yes、ok、no，1,0 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Boolean toBool(Object value, Boolean defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		String valueStr = toStr(value, null);
		if (StringKit.isBlank(valueStr)) {
			return defaultValue;
		}
		valueStr = valueStr.trim().toLowerCase();
		switch (valueStr) {
			case "true":
			case "ok":
			case "yes":
			case "1":
				return true;
			case "false":
			case "no":
			case "0":
				return false;
			default:
				return defaultValue;
		}
	}

	/**
	 * 转换为boolean<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Boolean toBool(Object value) {
		return toBool(value, null);
	}

	/**
	 * 转换为Boolean数组<br>
	 * 
	 * @param isIgnoreConvertError 是否忽略转换错误，忽略则给值null
	 * @param values 被转换的值
	 * @return 结果
	 */
	public static Boolean[] toBooleanArray(boolean isIgnoreConvertError, Object... values) {
		if (CollectionKit.isEmpty(values)) {
			return new Boolean[] {};
		}
		final Boolean[] booleans = new Boolean[values.length];
		for (int i = 0; i < values.length; i++) {
			final Boolean v = toBool(values[i], null);
			if (null == v && !isIgnoreConvertError) {
				throw new SystemException(StringKit.format("Convert [{}] to Boolean error!", values[i]));
			}
			booleans[i] = v;
		}
		return booleans;
	}

	/**
	 * 转换为Enum对象<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 
	 * @param clazz Enum的Class
	 * @param value 值
	 * @param defaultValue 默认值
	 * @return Enum
	 */
	public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value, E defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (clazz.isAssignableFrom(value.getClass())) {
			@SuppressWarnings("unchecked")
			E myE = (E) value;
			return myE;
		}
		final String valueStr = toStr(value, null);
		if (StringKit.isBlank(valueStr)) {
			return defaultValue;
		}
		try {
			return Enum.valueOf(clazz, valueStr);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为Enum对象<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
	 * 
	 * @param clazz Enum的Class
	 * @param value 值
	 * @return Enum
	 */
	public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value) {
		return toEnum(clazz, value, null);
	}

	/**
	 * 转换为BigInteger<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static BigInteger toBigInteger(Object value, BigInteger defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof BigInteger) {
			return (BigInteger) value;
		}
		if (value instanceof Long) {
			return BigInteger.valueOf((Long) value);
		}
		final String valueStr = toStr(value, null);
		if (StringKit.isBlank(valueStr)) {
			return defaultValue;
		}
		try {
			return new BigInteger(valueStr);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为BigInteger<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static BigInteger toBigInteger(Object value) {
		return toBigInteger(value, null);
	}

	/**
	 * 转换为BigDecimal<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}
		if (value instanceof Long) {
			return new BigDecimal((Long) value);
		}
		if (value instanceof Double) {
			return BigDecimal.valueOf((Double) value);
		}
		if (value instanceof Integer) {
			return new BigDecimal((Integer) value);
		}
		final String valueStr = toStr(value, null);
		if (StringKit.isBlank(valueStr)) {
			return defaultValue;
		}
		try {
			return new BigDecimal(valueStr);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 转换为BigDecimal<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 * 
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static BigDecimal toBigDecimal(Object value) {
		return toBigDecimal(value, null);
	}

	// ----------------------------------------------------------------------- 全角半角转换
	/**
	 * 半角转全角
	 * 
	 * @param input String.
	 * @return 全角字符串.
	 */
	public static String toSBC(String input) {
		return toSBC(input, null);
	}

	/**
	 * 半角转全角
	 * 
	 * @param input String
	 * @param notConvertSet 不替换的字符集合
	 * @return 全角字符串.
	 */
	public static String toSBC(String input, Set<Character> notConvertSet) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (null != notConvertSet && notConvertSet.contains(c[i])) {
				// 跳过不替换的字符
				continue;
			}

			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);

			}
		}
		return new String(c);
	}

	/**
	 * 全角转半角
	 * 
	 * @param input String.
	 * @return 半角字符串
	 */
	public static String toDBC(String input) {
		return toDBC(input, null);
	}

	/**
	 * 替换全角为半角
	 * 
	 * @param text 文本
	 * @param notConvertSet 不替换的字符集合
	 * @return 替换后的字符
	 */
	public static String toDBC(String text, Set<Character> notConvertSet) {
		char[] c = text.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (null != notConvertSet && notConvertSet.contains(c[i])) {
				// 跳过不替换的字符
				continue;
			}

			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
		}

		return new String(c);
	}

	// --------------------------------------------------------------------- hex
	/**
	 * 字符串转换成十六进制字符串
	 * 
	 * @param str 待转换的ASCII字符串
	 * @return 16进制字符串
	 */
	public static String toHex(String str) {
		return HexKit.encodeHexStr(str.getBytes());
	}

	/**
	 * byte数组转16进制串
	 * 
	 * @param bytes 被转换的byte数组
	 * @return 转换后的值
	 */
	public static String toHex(byte[] bytes) {
		return HexKit.encodeHexStr(bytes);
	}

	/**
	 * Hex字符串转换为Byte值
	 * 
	 * @param src Byte字符串，每个Byte之间没有分隔符
	 * @return byte[]
	 */
	public static byte[] hexToBytes(String src) {
		return HexKit.decodeHex(src.toCharArray());
	}

	/**
	 * 十六进制转换字符串
	 * 
	 * @param hexStr Byte字符串(Byte之间无分隔符 如:[616C6B])
	 * @param charset 编码 {@link Charset}
	 * @return 对应的字符串
	 */
	public static String hexStrToStr(String hexStr, Charset charset) {
		return HexKit.decodeHexStr(hexStr, charset);
	}

	/**
	 * String的字符串转换成unicode的String
	 * 
	 * @param strText 全角字符串
	 * @return String 每个unicode之间无分隔符
	 */
	public static String strToUnicode(String strText) {
		char c;
		StringBuilder str = new StringBuilder();
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++) {
			c = strText.charAt(i);
			intAsc = c;
			strHex = Integer.toHexString(intAsc);
			if (intAsc > 128)
				str.append("\\u").append(strHex);
			else // 低位在前面补00
				str.append("\\u00").append(strHex);
		}
		return str.toString();
	}

	/**
	 * unicode的String转换成String的字符串
	 * 
	 * @param hex 16进制值字符串 （一个unicode为2byte）
	 * @return String 全角字符串
	 */
	public static String unicodeToStr(String hex) {
		int t = hex.length() / 6;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < t; i++) {
			String s = hex.substring(i * 6, (i + 1) * 6);
			// 高位需要补上00再转
			String s1 = s.substring(2, 4) + "00";
			// 低位直接转
			String s2 = s.substring(4);
			// 将16进制的string转为int
			int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
			// 将int转换为字符
			char[] chars = Character.toChars(n);
			str.append(new String(chars));
		}
		return str.toString();
	}

	/**
	 * 给定字符串转换字符编码<br/>
	 * 如果参数为空，则返回原字符串，不报错。
	 * 
	 * @param str 被转码的字符串
	 * @param sourceCharset 原字符集
	 * @param destCharset 目标字符集
	 * @return 转换后的字符串
	 */
	public static String convertCharset(String str, String sourceCharset, String destCharset) {
		if (StringKit.hasBlank(str, sourceCharset, destCharset)) {
			return str;
		}

		try {
			return new String(str.getBytes(sourceCharset), destCharset);
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}



	
}
