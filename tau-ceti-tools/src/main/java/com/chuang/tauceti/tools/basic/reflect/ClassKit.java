package com.chuang.tauceti.tools.basic.reflect;

import com.chuang.tauceti.tools.basic.BasicType;
import com.chuang.tauceti.tools.basic.StringKit;
import com.chuang.tauceti.tools.basic.collection.CollectionKit;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 类工具类
 * 1、扫描指定包下的所有类<br>
 * 参考 http://www.oschina.net/code/snippet_234657_22722
 * @author seaside_hi, xiaoleilu, chill
 *
 */
public class ClassKit {
	
	private ClassKit() {
		// 静态类不可实例化
	}

	private static String webRootPath;
	private static String rootClassPath;


	public static String getRootClassPath() {
		if (rootClassPath == null) {
			try {
				String path = Objects.requireNonNull(ClassKit.class.getClassLoader().getResource("")).toURI().getPath();
				rootClassPath = new File(path).getAbsolutePath();
			}
			catch (Exception e) {
				String path = Objects.requireNonNull(ClassKit.class.getClassLoader().getResource("")).getPath();
				rootClassPath = new File(path).getAbsolutePath();
			}
		}
		return rootClassPath;
	}

	public static String getPackagePath(Object object) {
		Package p = object.getClass().getPackage();
		return p != null ? p.getName().replaceAll("\\.", "/") : "";
	}

	public static File getFileFromJar(String file) {
		throw new RuntimeException("Not finish. Do not use this method.");
	}

	public static String getWebRootPath() {
		if (webRootPath == null)
			webRootPath = detectWebRootPath();
		return webRootPath;
	}

	private static String detectWebRootPath() {
		try {
			String path = ClassKit.class.getResource("/").toURI().getPath();
			return new File(path).getParentFile().getParentFile().getCanonicalPath();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获得ClassPath
	 *
	 * @return ClassPath
	 */
	public static String getClassPath() {
		return getClassPathURL().getPath();
	}

	/**
	 * 获得ClassPath URL
	 *
	 * @return ClassPath URL
	 */
	public static URL getClassPathURL() {
		return getURL(StringKit.EMPTY);
	}

	/**
	 * 获得资源的URL
	 *
	 * @param resource 资源（相对Classpath的路径）
	 * @return 资源URL
	 */
	public static URL getURL(String resource) {
		return ClassKit.getClassLoader().getResource(resource);
	}


	/**
	 * 是否为标准的类<br>
	 * 这个类必须：<br>
	 * 1、非接口 2、非抽象类 3、非Enum枚举 4、非数组 5、非注解 6、非原始类型（int, long等）
	 * 
	 * @param clazz 类
	 * @return 是否为标准类
	 */
	public static boolean isNormalClass(Class<?> clazz) {
		return !clazz.isInterface() &&
                !isAbstract(clazz) &&
                !clazz.isEnum() &&
                !clazz.isArray() &&
                !clazz.isAnnotation() &&
                !clazz.isSynthetic() &&
                !clazz.isPrimitive();
	}
	
	/**
	 * 是否为抽象类
	 * 
	 * @param clazz 类
	 * @return 是否为抽象类
	 */
	public static boolean isAbstract(Class<?> clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}
	
	/**
	 * 实例化对象
	 * 
	 * @param clazz 类名
	 * @return 对象
	 */
    @SuppressWarnings("unchecked")
	public static <T> Optional<T> newInstance(String clazz) {
		try {
			 return Optional.of((T) Class.forName(clazz).newInstance());
		} catch (Exception e) {
			throw new RuntimeException(StringKit.format("Instance class [{}] error!", clazz), e);
		}
	}

	/**
	 * 实例化对象
	 * 
	 * @param clazz 类
	 * @return 对象
	 */
	public static <T> Optional<T> newInstance(Class<T> clazz) {
		try {
			return Optional.of(clazz.newInstance());
		} catch (Exception e) {
			throw new RuntimeException(StringKit.format("Instance class [{}] error!", clazz), e);
		}
	}

	/**
	 * 实例化对象
	 * 
	 * @param clazz 类
	 * @return 对象
	 */
	public static <T> Optional<T> newInstance(Class<T> clazz, Object... params) {
		if (CollectionKit.isEmpty(params)) {
			return newInstance(clazz);
		}
		try {
			return Optional.of(clazz.getDeclaredConstructor(getClasses(params)).newInstance(params));
		} catch (Exception e) {
			throw new RuntimeException(StringKit.format("Instance class [{}] error!", clazz), e);
		}
	}
	
	/**
	 * 获得对象数组的类数组
	 * @param objects 对象数组
	 * @return 类数组
	 */
	public static Class<?>[] getClasses(Object... objects){
		Class<?>[] classes = new Class<?>[objects.length];
		for (int i = 0; i < objects.length; i++) {
			classes[i] = objects[i].getClass();
		}
		return classes;
	}
	
	/**
	 * 检查目标类是否可以从原类转化<br>
	 * 转化包括：<br>
	 * 1、原类是对象，目标类型是原类型实现的接口<br>
	 * 2、目标类型是原类型的父类<br>
	 * 3、两者是原始类型或者包装类型（相互转换）
	 * 
	 * @param targetType 目标类型
	 * @param sourceType 原类型
	 * @return 是否可转化
	 */
	public static boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
		// 对象类型
		if (targetType.isAssignableFrom(sourceType)) {
			return true;
		}

		// 基本类型
		if (targetType.isPrimitive()) {
			// 原始类型
			Class<?> resolvedPrimitive = BasicType.wrapperPrimitiveMap.get(sourceType);
            return targetType.equals(resolvedPrimitive);
		} else {
			// 包装类型
			Class<?> resolvedWrapper = BasicType.primitiveWrapperMap.get(sourceType);
            return resolvedWrapper != null && targetType.isAssignableFrom(resolvedWrapper);
		}
    }
	
	/**
	 * 设置方法为可访问
	 * 
	 * @param method 方法
	 * @return 方法
	 */
	public static Method setAccessible(Method method) {
		if (ClassKit.isNotPublic(method)) {
			method.setAccessible(true);
		}
		return method;
	}
	
	/**
	 * 指定类是否为非public
	 * 
	 * @param clazz 类
	 * @return 是否为非public
	 */
	public static boolean isNotPublic(Class<?> clazz) {
		return !isPublic(clazz);
	}

	/**
	 * 指定方法是否为非public
	 * 
	 * @param method 方法
	 * @return 是否为非public
	 */
	public static boolean isNotPublic(Method method) {
		return !isPublic(method);
	}
	
	/**
	 * 指定类是否为Public
	 * 
	 * @param clazz 类
	 * @return 是否为public
	 */
	public static boolean isPublic(Class<?> clazz) {
		return Modifier.isPublic(clazz.getModifiers());
	}
	
	/**
	 * 指定方法是否为Public
	 * 
	 * @param method 方法
	 * @return 是否为public
	 */
	public static boolean isPublic(Method method) {
		return isPublic(method.getDeclaringClass());
	}

	/**
	 * @return 当前线程的class loader
	 */
	public static ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * 获得class loader<br>
	 * 若当前线程class loader不存在，取当前类的class loader
	 * @return 类加载器
	 */
	public static ClassLoader getClassLoader() {
		ClassLoader classLoader = getContextClassLoader();
		if(classLoader == null) {
			classLoader = ClassKit.class.getClassLoader();
		}
		return classLoader;
	}

	public static List<Field> findFieldList(Class<?> clazz, boolean and, Class<? extends Annotation>... ann) {
		List<Field> list = new ArrayList<>();
		Class<?> superClass = clazz.getSuperclass();
		if(superClass != Object.class) {
			list.addAll(findFieldList(superClass, and, ann));
		}

		Field[] fds = clazz.getDeclaredFields();
		for(Field f : fds) {
			if(hasAnnotation(f, and, ann)) {
				list.add(f);
			}
		}
		return list;
	}
	/**
	 * 检查是Class 是否存在指定的超类、接口、或注解
	 * <code>
	 *     String[] args = new String[] {};
	 *     checkHasAnnOrSuperClass(args, java.concurrent.reflect.Array.class);
	 * </code>
	 * 结果为 false, 检查是否为数组，请调用 {@link com.chuang.tauceti.tools.basic.ObjectKit#isArray(Object)}
	 * @param clazz
	 * @param c
	 * @return
	 */
	public static boolean checkHasAnnOrSuperClass(Class<?> clazz, Class<?> c) {
		if(c.isAnnotation()) {
			return clazz.getAnnotation((Class<Annotation>)c) != null;
		} else {
			return c.isAssignableFrom(clazz);
		}
	}
	/**
	 * 是否包含多个指定的超类或者注解
	 */
	public static boolean hasSuperClassWithAnnotation(Class<?> clazz, boolean and, Class<?>... classes) {
		for(Class<?> c : classes) {
			if(and && (!checkHasAnnOrSuperClass(clazz, c))) {
				return false;
			}

			if(!and && (checkHasAnnOrSuperClass(clazz, c))) {
				return true;
			}
		}

		return and;
	}

	/**
	 * 验证是否包含指定的注解
	 */
	public static boolean hasAnnotation(AnnotatedElement ae, boolean and, Class<? extends Annotation>... annotations) {

		for(Class<? extends Annotation> ann : annotations) {
			if(and && null == ae.getAnnotation(ann)) {
				return false;
			}

			if(!and && null != ae.getAnnotation(ann)) {
				return true;
			}
		}

		return and;
	}
}
