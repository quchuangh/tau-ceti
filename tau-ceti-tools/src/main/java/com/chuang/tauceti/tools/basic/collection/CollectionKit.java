package com.chuang.tauceti.tools.basic.collection;

import com.chuang.tauceti.tools.basic.ObjectKit;
import com.chuang.tauceti.tools.basic.StringKit;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 集合相关工具类，包括数组
 * 
 * @author xiaoleilu
 * 
 */
public class CollectionKit {
	private static final Random random = new Random();
	private CollectionKit() {
		// 静态类不可实例化
	}

	/**
	 * 遍历ary的每个值，并交给c处理
	 * @param ary 数组
	 * @param c 数组元素处理器
	 */
	public static <T> void foreach(T[] ary, Consumer<T> c) {
		if(null == ary) {
			return;
		}
		for (T anAry : ary) {
			c.accept(anAry);
		}
	}

	/**
	 * 对象组中是否存在 Empty Object
	 *
	 * @param os
	 *            对象组
	 * @return 如果os中有一个对象是null就返回true，否则返回false
	 */
	public static boolean isAnyEmpty(Object... os) {
		for (Object o : os) {
			if (isEmpty(o)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 对象组中是否全是 Empty Object
	 *
	 * @param os 需要验证的对象数组
	 * @return 如果os中所有对象都为null就返回true，否则返回false
	 */
	public static boolean isAllEmpty(Object... os) {
		for (Object o : os) {
			if (!isEmpty(o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 对象是否为空
	 *
	 * @param o
	 *            String,List,Map,Object[],int[],long[]
	 */
	public static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof String) {
			return o.toString().trim().equals("");
		} else if (o instanceof List) {
			return ((List<?>) o).size() == 0;
		} else if (o instanceof Map) {
			return ((Map<?, ?>) o).size() == 0;
		} else if (o instanceof Set) {
			return ((Set<?>) o).size() == 0;
		} else if (o.getClass().isArray()) {
			return ((Object[]) o).length == 0;
		}
		return false;
	}

	public static <T> List<T> nullToEmpty(@Nullable List<T> list) {
		return null == list ? Collections.emptyList() : list;
	}

	public static <T> Set<T> nullToEmpty(@Nullable Set<T> list) {
		return null == list ? Collections.emptySet() : list;
	}

	public static <K, V> Map<K, V> nullToEmpty(@Nullable Map<K, V> map) {
		return null == map ? Collections.emptyMap() : map;
	}

	/**
	 * 将一个集合转成另一个集合，同时连里面的元素也一起转换
	 */
	public static <CT extends Collection<T>, CR extends Collection<R>, T, R> CR map(CT ct, Supplier<CR> crGetter, Function<T, R> map) {
		CR cr = crGetter.get();
		for(T t : ct) {
			R r = map.apply(t);
			cr.add(r);
		}

		return cr;
	}


	@SuppressWarnings("unchecked")
	public static <T> T randomOne(Collection<T> coll) {
		int index = random.nextInt(coll.size());
		return (T) coll.toArray()[index];
	}

	public static <T> T randomOne(Collection<T> coll, Function<T, Integer> weight) {
		int total = coll.stream().map(weight).reduce(Integer::sum).orElse(0);
		if(total == 0) {
			return randomOne(coll);
		}

		int current = random.nextInt(total);
		for(T obj: coll) {
			int w = weight.apply(obj);
			if(current < w) {
				return obj;
			} else {
				current -= w;
			}
		}

		throw new RuntimeException("方法计算错误");
	}

	@SafeVarargs
	public static <T> T randomOne(T... tn) {
		int current = random.nextInt(tn.length);
		return tn[current];
	}





	/**
	 * 将新元素添加到已有数组中<br/>
	 * 添加新元素会生成一个新的数组，不影响原数组
	 *
	 * @param buffer 已有数组
	 * @param newElement 新元素
	 * @return 新数组
	 */
	public static <T, C extends T> T[] append(T[] buffer, C newElement) {
		T[] t = resize(buffer, buffer.length + 1, buffer.getClass().getComponentType());
		t[buffer.length] = newElement;
		return t;
	}


	/**
	 * 生成一个新的重新设置大小的数组
	 *
	 * @param buffer 原数组
	 * @param newSize 新的数组大小
	 * @param componentType 数组元素类型
	 * @return 调整后的新数组
	 */
	public static <T> T[] resize(T[] buffer, int newSize, Class<?> componentType) {
		T[] newArray = newArray(componentType, newSize);
		System.arraycopy(buffer, 0, newArray, 0, Math.min(buffer.length, newSize));
		return newArray;
	}

	/**
	 * 新建一个空数组
	 * @param componentType 元素类型
	 * @param newSize 大小
	 * @return 空数组
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(Class<?> componentType, int newSize) {
		return (T[]) Array.newInstance(componentType, newSize);
	}


	/**
	 * 生成一个新的重新设置大小的数组<br/>
	 * 新数组的类型为原数组的类型
	 *
	 * @param buffer 原数组
	 * @param newSize 新的数组大小
	 * @return 调整后的新数组
	 */
	public static <T> T[] resize(T[] buffer, int newSize) {
		return resize(buffer, newSize, buffer.getClass().getComponentType());
	}

	/**
	 * 将多个数组合并在一起<br>
	 * 忽略null的数组
	 *
	 * @param arrays 数组集合
	 * @return 合并后的数组
	 */
	@SafeVarargs
	public static <T> T[] addAll(T[]... arrays) {
		if (arrays.length == 1) {
			return arrays[0];
		}

		int length = 0;
		for (T[] array : arrays) {
			if(array == null) {
				continue;
			}
			length += array.length;
		}
		T[] result = newArray(arrays.getClass().getComponentType().getComponentType(), length);

		length = 0;
		for (T[] array : arrays) {
			if(array == null) {
				continue;
			}
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}



	/**
	 * 生成一个数字列表<br>
	 * 自动判定正序反序
	 * @param excludedEnd 结束的数字（不包含）
	 * @return 数字列表
	 */
	public static int[] range(int excludedEnd) {
		return range(0, excludedEnd, 1);
	}

	/**
	 * 生成一个数字列表<br>
	 * 自动判定正序反序
	 * @param includedStart 开始的数字（包含）
	 * @param excludedEnd 结束的数字（不包含）
	 * @return 数字列表
	 */
	public static int[] range(int includedStart, int excludedEnd) {
		return range(includedStart, excludedEnd, 1);
	}

	/**
	 * 生成一个数字列表<br>
	 * 自动判定正序反序
	 * @param includedStart 开始的数字（包含）
	 * @param excludedEnd 结束的数字（不包含）
	 * @param step 步进
	 * @return 数字列表
	 */
	public static int[] range(int includedStart, int excludedEnd, int step) {
		if(includedStart > excludedEnd) {
			int tmp = includedStart;
			includedStart = excludedEnd;
			excludedEnd = tmp;
		}

		if(step <=0) {
			step = 1;
		}

		int deviation = excludedEnd - includedStart;
		int length = deviation / step;
		if(deviation % step != 0) {
			length += 1;
		}
		int[] range = new int[length];
		for(int i = 0; i < length; i++) {
			range[i] = includedStart;
			includedStart += step;
		}
		return range;
	}

	/**
	 * 截取数组的部分
	 * @param list 被截取的数组
	 * @param start 开始位置（包含）
	 * @param end 结束位置（不包含）
	 * @return 截取后的数组，当开始位置超过最大时，返回null
	 */
	public static <T> List<T> sub(List<T> list, int start, int end) {
		if(list == null || list.isEmpty()) {
			return null;
		}

		if(start < 0) {
			start = 0;
		}
		if(end < 0) {
			end = 0;
		}

		if(start > end) {
			int tmp = start;
			start = end;
			end = tmp;
		}

		final int size = list.size();
		if(end > size) {
			if(start >= size) {
				return null;
			}
			end = size;
		}

		return list.subList(start, end);
	}

	/**
	 * 截取集合的部分
	 * @param list 被截取的数组
	 * @param start 开始位置（包含）
	 * @param end 结束位置（不包含）
	 * @return 截取后的数组，当开始位置超过最大时，返回null
	 */
	public static <T> List<T> sub(Collection<T> list, int start, int end) {
		if(list == null || list.isEmpty()) {
			return null;
		}

		return sub(new ArrayList<>(list), start, end);
	}

	/**
	 * 数组是否为空
	 * @param array 数组
	 * @return 是否为空
	 */
	public static <T> boolean isEmpty(@Nullable T[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 数组是否为非空
	 * @param array 数组
	 * @return 是否为非空
	 */
	public static <T> boolean isNotEmpty(@Nullable T[] array) {
		return !isEmpty(array);
	}

	/**
	 * 集合是否为空
	 * @param collection 集合
	 * @return 是否为空
	 */
	public static boolean isEmpty(@Nullable Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * 集合是否为非空
	 * @param collection 集合
	 * @return 是否为非空
	 */
	public static boolean isNotEmpty(@Nullable Collection<?> collection) {
		return !isEmpty(collection);
	}

	/**
	 * Map是否为空
	 * @param map 集合
	 * @return 是否为空
	 */
	public static boolean isEmpty(@Nullable Map<?, ?> map) {
		return null == map || map.isEmpty();
	}

	/**
	 * Map是否为非空
	 * @param map 集合
	 * @return 是否为非空
	 */
	public static <T> boolean isNotEmpty(@Nullable Map<?, ?> map) {
		return !isEmpty(map);
	}

	/**
	 * 映射键值（参考Python的zip()函数）<br>
	 * 例如：<br>
	 * 		keys =    [a,b,c,d]<br>
	 *		values = [1,2,3,4]<br>
	 * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
	 * 如果两个数组长度不同，则只对应最短部分
	 * @param keys 键列表
	 * @param values 值列表
	 * @return Map
	 */
	public static <T, K> Map<T, K> zip(T[] keys, K[] values) {
		if(isEmpty(keys) || isEmpty(values)) {
			return null;
		}

		final int size = Math.min(keys.length, values.length);
		final Map<T, K> map = new HashMap<>((int)(size / 0.75));
		for(int i = 0; i < size; i++) {
			map.put(keys[i], values[i]);
		}

		return map;
	}

	/**
	 * 映射键值（参考Python的zip()函数）<br>
	 * 例如：<br>
	 * 		keys =    a,b,c,d<br>
	 *		values = 1,2,3,4<br>
	 *		delimiter = ,
	 * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
	 * 如果两个数组长度不同，则只对应最短部分
	 * @param keys 键列表
	 * @param values 值列表
	 * @return Map
	 */
	public static Map<String, String> zip(String keys, String values, String delimiter) {
		return zip(StringKit.split(keys, delimiter), StringKit.split(values, delimiter));
	}
	
	/**
	 * 映射键值（参考Python的zip()函数）<br>
	 * 例如：<br>
	 * 		keys =    [a,b,c,d]<br>
	 *		values = [1,2,3,4]<br>
	 * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
	 * 如果两个数组长度不同，则只对应最短部分
	 * @param keys 键列表
	 * @param values 值列表
	 * @return Map
	 */
	public static <T, K> Map<T, K> zip(Collection<T> keys, Collection<K> values) {
		if(isEmpty(keys) || isEmpty(values)) {
			return null;
		}
		
		final List<T> keyList = new ArrayList<>(keys);
		final List<K> valueList = new ArrayList<>(values);
		
		final int size = Math.min(keys.size(), values.size());
		final Map<T, K> map = new HashMap<>((int)(size / 0.75));
		for(int i = 0; i < size; i++) {
			map.put(keyList.get(i), valueList.get(i));
		}
		
		return map;
	}
	
	/**
	 * 数组中是否包含元素
	 * @param array 数组
	 * @param value 被检查的元素
	 * @return 是否包含
	 */
	public static <T> boolean contains(T[] array, T value) {
		final Class<?> componentType = array.getClass().getComponentType();
		boolean isPrimitive = false;
		if(null != componentType) {
			isPrimitive = componentType.isPrimitive();
		}
		for (T t : array) {
			if(t == value) {
				return true;
			}else if(!isPrimitive && null != value && value.equals(t)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 将Entry集合转换为HashMap
	 * @param entryCollection entry集合
	 * @return Map
	 */
	public static <T, K> HashMap<T, K> toMap(Collection<Entry<T, K>> entryCollection) {
		HashMap<T,K> map = new HashMap<>();
		for (Entry<T, K> entry : entryCollection) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	/**
	 * 将集合转换为排序后的TreeSet
	 * @param collection 集合
	 * @param comparator 比较器
	 * @return treeSet
	 */
	public static <T> TreeSet<T> toTreeSet(Collection<T> collection, Comparator<T> comparator){
		final TreeSet<T> treeSet = new TreeSet<>(comparator);
        treeSet.addAll(collection);
		return treeSet;
	}
	
	/**
	 * 排序集合
	 * @param collection 集合
	 * @param comparator 比较器
	 * @return treeSet
	 */
	public static <T> List<T> sort(Collection<T> collection, Comparator<T> comparator){
	    List<T> list = new ArrayList<>(collection);
		list.sort(comparator);
		return list;
	}

	public static <K, V> Map<K,V> sortMap(Map<K, V> map, Comparator<K> keyComp) {
		return map.keySet().stream().sorted(keyComp)
                .collect(LinkedHashMap::new,
                        (Map<K,V> m, K k) -> m.put(k, map.get(k)),
                        (m1, m2) -> {});
    }

	//------------------------------------------------------------------- 基本类型的数组转换为包装类型数组
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Integer[] wrap(int... values){
		final int length = values.length;
		Integer[] array = new Integer[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Long[] wrap(long... values){
		final int length = values.length;
		Long[] array = new Long[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Character[] wrap(char... values){
		final int length = values.length;
		Character[] array = new Character[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Byte[] wrap(byte... values){
		final int length = values.length;
		Byte[] array = new Byte[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Short[] wrap(short... values){
		final int length = values.length;
		Short[] array = new Short[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Float[] wrap(float... values){
		final int length = values.length;
		Float[] array = new Float[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Double[] wrap(double... values){
		final int length = values.length;
		Double[] array = new Double[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 将基本类型数组包装为包装类型
	 * @param values 基本类型数组
	 * @return 包装类型数组
	 */
	public static Boolean[] wrap(boolean... values){
		final int length = values.length;
		Boolean[] array = new Boolean[length];
		for(int i = 0; i < length; i++){
			array[i] = values[i];
		}
		return array;
	}
	
	/**
	 * 判定给定对象是否为数组类型
	 * @param obj 对象
	 * @return 是否为数组类型
	 */
	public static boolean isArray(Object obj){
		return obj.getClass().isArray();
	}
	
	/**
	 * 数组或集合转String
	 * 
	 * @param obj 集合或数组对象
	 * @return 数组字符串，与集合转字符串格式相同
	 */
	public static String toString(Object obj) {
		if (isArray(obj)) {
			try {
				return Arrays.deepToString((Object[]) obj);
			} catch (Exception e) {
				final String className = obj.getClass().getComponentType().getName();
				switch (className) {
					case "long":
						return Arrays.toString((Long[]) obj);
					case "int":
						return Arrays.toString((Integer[]) obj);
					case "short":
						return Arrays.toString((Short[]) obj);
					case "char":
						return Arrays.toString((Character[]) obj);
					case "byte":
						return Arrays.toString((Byte[]) obj);
					case "boolean":
						return Arrays.toString((Boolean[]) obj);
					case "float":
						return Arrays.toString((Float[]) obj);
					case "double":
						return Arrays.toString((Double[]) obj);
					default:
						throw e;
				}
			}
		}
		return obj.toString();
	}

	/**
	 * 取第一个集合对第二个集合的 差集。
     * 第一个集合中所有 不在第二个集合 的元素
	 * @param one 第一个集合
	 * @param two 第二个集合
	 * @param getter 新集合获取方法
	 */
	public static <T, C extends Collection<T>> C subtract(Collection<T> one, Collection<T> two, Supplier<C> getter) {
		C newC = getter.get();
		for(T t : one) {
			if(!two.contains(t)) {
				newC.add(t);
			}
		}

		return newC;
	}

	public static <T, C extends Collection<T>> C union(Collection<T> one, Collection<T> two, Supplier<C> getter) {
		Set<T> set = new HashSet<>();
		set.addAll(one);
		set.addAll(two);
		C newC = getter.get();
		newC.addAll(set);
		return newC;
	}

	/**
	 * 计算对象长度，如果是字符串调用其length函数，集合类调用其size函数，数组调用其length属性，其他可遍历对象遍历计算长度
	 *
	 * @param obj
	 *            被计算长度的对象
	 * @return 长度
	 */
	public static int length(@Nullable Object obj) {
		if (obj == null) {
			return 0;
		}
		if (obj instanceof CharSequence) {
			return ((CharSequence) obj).length();
		}
		if (obj instanceof Collection) {
			return ((Collection<?>) obj).size();
		}
		if (obj instanceof Map) {
			return ((Map<?, ?>) obj).size();
		}

		int count;
		if (obj instanceof Iterator) {
			Iterator<?> iter = (Iterator<?>) obj;
			count = 0;
			while (iter.hasNext()) {
				count++;
				iter.next();
			}
			return count;
		}
		if (obj instanceof Enumeration) {
			Enumeration<?> enumeration = (Enumeration<?>) obj;
			count = 0;
			while (enumeration.hasMoreElements()) {
				count++;
				enumeration.nextElement();
			}
			return count;
		}
		if (obj.getClass().isArray()) {
			return Array.getLength(obj);
		}
		return -1;
	}

	/**
	 * 对象中是否包含元素
	 *
	 * @param obj
	 *            对象
	 * @param element
	 *            元素
	 * @return 是否包含
	 */
	public static boolean contains(Object obj, Object element) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof String) {
			if (element == null) {
				return false;
			}
			return ((String) obj).contains(element.toString());
		}
		if (obj instanceof Collection) {
			return ((Collection<?>) obj).contains(element);
		}
		if (obj instanceof Map) {
			return ((Map<?, ?>) obj).containsValue(element);
		}

		if (obj instanceof Iterator) {
			Iterator<?> iter = (Iterator<?>) obj;
			while (iter.hasNext()) {
				Object o = iter.next();
				if (ObjectKit.equals(o, element)) {
					return true;
				}
			}
			return false;
		}
		if (obj instanceof Enumeration) {
			Enumeration<?> enumeration = (Enumeration<?>) obj;
			while (enumeration.hasMoreElements()) {
				Object o = enumeration.nextElement();
				if (ObjectKit.equals(o, element)) {
					return true;
				}
			}
			return false;
		}
		if (obj.getClass().isArray()) {
			int len = Array.getLength(obj);
			for (int i = 0; i < len; i++) {
				Object o = Array.get(obj, i);
				if (ObjectKit.equals(o, element)) {
					return true;
				}
			}
		}
		return false;
	}

}
