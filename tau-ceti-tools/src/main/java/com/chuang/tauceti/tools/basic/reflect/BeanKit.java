package com.chuang.tauceti.tools.basic.reflect;

import com.chuang.tauceti.support.Result;
import com.chuang.tauceti.support.exception.BusinessException;
import com.chuang.tauceti.support.exception.SystemException;
import com.chuang.tauceti.tools.basic.ObjectKit;
import com.chuang.tauceti.tools.basic.StringKit;
import org.springframework.beans.BeanUtils;

import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Bean工具类
 *
 * @author Looly
 */
@SuppressWarnings("unused")
public class BeanKit {

    /**
     * 判断是否为Bean对象
     *
     * @param clazz 待测试类
     * @return 是否为Bean对象
     */
    public static boolean isBean(Class<?> clazz) {
        if (ClassKit.isNormalClass(clazz)) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getParameterTypes().length == 1 && method.getName().startsWith("set")) {
                    //检测包含标准的setXXX方法即视为标准的JavaBean
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Field> getFields(Class<?> clazz, boolean publicOnly, Predicate<Field> test) {
        Field[] fields = publicOnly ? clazz.getFields() : clazz.getDeclaredFields();
        return Arrays.stream(fields).filter(test).collect(Collectors.toList());
    }

    public static PropertyEditor findEditor(Class<?> type) {
        return BeanUtils.findEditorByConvention(type);
    }

    /**
     * 获得Bean字段描述数组
     *
     * @param clazz Bean类
     * @return 字段描述数组
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz)  {
        return BeanUtils.getPropertyDescriptors(clazz);
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz, Predicate<PropertyDescriptor> test) {
        PropertyDescriptor[]  pds = BeanUtils.getPropertyDescriptors(clazz);
        List<PropertyDescriptor> list = new ArrayList<>();
        for(PropertyDescriptor pd : pds) {
            if(test.test(pd)) {
                list.add(pd);
            }
        }
        return list.toArray(new PropertyDescriptor[0]);
    }

    /**
     * 获得字段名和字段描述Map
     *
     * @param clazz Bean类
     * @return 字段名和字段描述Map
     */
    public static Map<String, PropertyDescriptor> getFieldNamePropertyDescriptorMap(Class<?> clazz)  {
        final PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
        Map<String, PropertyDescriptor> map = new HashMap<>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            map.put(propertyDescriptor.getName(), propertyDescriptor);
        }
        return map;
    }


    /**
     * 获得Bean类属性描述
     *
     * @param clazz     Bean类
     * @param fieldName 字段名
     * @return PropertyDescriptor
     *
     */
    public static Optional<PropertyDescriptor> getPropertyDescriptor(Class<?> clazz, final String fieldName) {
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (ObjectKit.equals(fieldName, propertyDescriptor.getName())) {
                return Optional.of(propertyDescriptor);
            }
        }
        return Optional.empty();
    }

    public static void setProperty(Object obj, String fieldName, Object value) {
        getPropertyDescriptor(obj.getClass(), fieldName).ifPresent(property -> {
            try {
                property.getWriteMethod().invoke(obj, ConvertKit.parse(property.getPropertyType(), value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



    /**
     * 使用Map填充Bean对象
     *
     * @param map  Map
     * @param bean Bean
     * @return Bean
     */
    public static <T> T fillBeanWithMap(final Map<?, ?> map, T bean) {
        return fillBean(bean, map::get);
    }

    /**
     * 使用Map填充Bean对象，可配置将下划线转换为驼峰
     *
     * @param map           Map
     * @param bean          Bean
     * @param isToCamelCase 是否将下划线模式转换为驼峰模式
     * @return Bean
     */
    public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, boolean isToCamelCase) {
        if (isToCamelCase) {
            final Map<Object, Object> map2 = new HashMap<>();
            for (Entry<?, ?> entry : map.entrySet()) {
                final Object key = entry.getKey();
                if (key instanceof String) {
                    final String keyStr = (String) key;
                    map2.put(StringKit.toCamelCase(keyStr), entry.getValue());
                } else {
                    map2.put(key, entry.getValue());
                }
            }
            return fillBeanWithMap(map2, bean);
        }

        return fillBeanWithMap(map, bean);
    }

    /**
     * 使用Map填充Bean对象，忽略大小写
     *
     * @param map  Map
     * @param bean Bean
     * @return Bean
     */
    public static <T> T fillBeanWithMapIgnoreCase(Map<?, ?> map, T bean) {
        final Map<Object, Object> map2 = new HashMap<>();
        for (Entry<?, ?> entry : map.entrySet()) {
            final Object key = entry.getKey();
            if (key instanceof String) {
                final String keyStr = (String) key;
                map2.put(keyStr.toLowerCase(), entry.getValue());
            } else {
                map2.put(key, entry.getValue());
            }
        }

        return fillBean(bean, name -> map2.get(name.toLowerCase()));
    }



    /**
     * ServletRequest 参数转Bean
     *
     * @param request ServletRequest
     * @param bean    Bean
     * @return Bean
     */
    public static <T> T fillBeanWithRequestParam(final javax.servlet.ServletRequest request, T bean) {
        final String beanName = StringKit.lowerFirst(bean.getClass().getSimpleName());
        return fillBean(bean, (String name) -> {
            String value = request.getParameter(name);
            if (StringKit.isEmpty(value)) {
                // 使用类名前缀尝试查找值
                value = request.getParameter(beanName + StringKit.DOT + name);
                if (StringKit.isEmpty(value)) {
                    // 此处取得的值为空时跳过，包括null和""
                    value = null;
                }
            }
            return value;

        });
    }



    /**
     * 填充Bean
     *
     * @param bean          Bean
     * @param valueProvider 值提供者
     * @return Bean
     */
    public static <T> T fillBean(T bean, @Nullable Function<String, Object> valueProvider) {
        if (null == valueProvider) {
            return bean;
        }

        Class<?> beanClass = bean.getClass();
        try {
            PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(beanClass);
            String propertyName;
            Object value;
            for (PropertyDescriptor property : propertyDescriptors) {
                propertyName = property.getName();
                value = valueProvider.apply(propertyName);
                if (null == value) {
                    continue;
                }
                try {
                    property.getWriteMethod().invoke(bean, ConvertKit.parse(property.getPropertyType(), value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new BusinessException(Result.FAIL_CODE, "", e);
        }
        return bean;
    }



    /**
     * 复制Bean对象属性
     *
     * @param source 源Bean对象
     * @param target 目标Bean对象
     */
    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, CopyOptions.create());
    }

    @SuppressWarnings("unchecked")
    public static <T> T copyOne(Object source) {
        try {
            T t = (T) source.getClass().newInstance();
            copyProperties(source, t, CopyOptions.create());
            return t;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BusinessException(Result.FAIL_CODE, "复制对象失败");
        }
    }
    /**
     * 复制Bean对象属性<br>
     * 限制类用于限制拷贝的属性，例如一个类我只想复制其父类的一些属性，就可以将editable设置为父类
     *
     * @param source           源Bean对象
     * @param target           目标Bean对象
     * @param ignoreProperties 不拷贝的的属性列表
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        copyProperties(source, target, CopyOptions.create().setIgnoreProperties(ignoreProperties));
    }

    /**
     * 复制Bean对象属性<br>
     * 限制类用于限制拷贝的属性，例如一个类我只想复制其父类的一些属性，就可以将editable设置为父类
     *
     * @param source      源Bean对象
     * @param target      目标Bean对象
     * @param copyOptions 拷贝选项，见 {@link CopyOptions}
     */
    public static void copyProperties(Object source, Object target, @Nullable CopyOptions copyOptions) {
        if (null == copyOptions) {
            copyOptions = new CopyOptions();
        }

        Class<?> actualEditable = target.getClass();
        if (copyOptions.editable != null) {
            //检查限制类是否为target的父类或接口
            if (!copyOptions.editable.isInstance(target)) {
                throw new IllegalArgumentException(StringKit.format("Target class [{}] not assignable to Editable class [{}]", target.getClass().getName(), copyOptions.editable.getName()));
            }
            actualEditable = copyOptions.editable;
        }
        PropertyDescriptor[] targetPds;
        Map<String, PropertyDescriptor> sourcePdMap;
        try {
            sourcePdMap = getFieldNamePropertyDescriptorMap(source.getClass());
            targetPds = getPropertyDescriptors(actualEditable);
        } catch (RuntimeException e) {
            throw new SystemException("", e);
        }

        HashSet<String> ignoreSet = copyOptions.ignoreProperties != null ? new HashSet<>(Arrays.asList(copyOptions.ignoreProperties)) : null;
        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreSet == null || !ignoreSet.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = sourcePdMap.get(targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    // 源对象字段的getter方法返回值必须可转换为目标对象setter方法的第一个参数
                    if (readMethod != null && ClassKit.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            Object value = ClassKit.setAccessible(readMethod).invoke(source);
                            if (null != value || !copyOptions.isIgnoreNullValue) {
                                ClassKit.setAccessible(writeMethod).invoke(target, value);
                            }
                        } catch (Throwable ex) {
                            throw new SystemException(MessageFormat.format("Copy property [{0}] to [{1}] error: {2}", sourcePd.getName(), targetPd.getName(), ex.getMessage()), ex);
                        }
                    }
                }
            }
        }
    }



    /**
     * 属性拷贝选项<br>
     * 包括：<br>
     * 1、限制的类或接口，必须为目标对象的实现接口或父类，用于限制拷贝的属性，例如一个类我只想复制其父类的一些属性，就可以将editable设置为父类<br>
     * 2、是否忽略空值，当源对象的值为null时，true: 忽略而不注入此值，false: 注入null<br>
     * 3、忽略的属性列表，设置一个属性列表，不拷贝这些属性值<br>
     *
     * @author Looly
     */
    public static class CopyOptions {
        /**
         * 限制的类或接口，必须为目标对象的实现接口或父类，用于限制拷贝的属性，例如一个类我只想复制其父类的一些属性，就可以将editable设置为父类
         */
        private Class<?> editable;
        /**
         * 是否忽略空值，当源对象的值为null时，true: 忽略而不注入此值，false: 注入null
         */
        private boolean isIgnoreNullValue;
        /**
         * 忽略的属性列表，设置一个属性列表，不拷贝这些属性值
         */
        private String[] ignoreProperties;

        /**
         * 创建拷贝选项
         *
         * @return 拷贝选项
         */
        public static CopyOptions create() {
            return new CopyOptions();
        }

        /**
         * 创建拷贝选项
         *
         * @param editable          限制的类或接口，必须为目标对象的实现接口或父类，用于限制拷贝的属性
         * @param isIgnoreNullValue 是否忽略空值，当源对象的值为null时，true: 忽略而不注入此值，false: 注入null
         * @param ignoreProperties  忽略的属性列表，设置一个属性列表，不拷贝这些属性值
         * @return 拷贝选项
         */
        public static CopyOptions create(Class<?> editable, boolean isIgnoreNullValue, String... ignoreProperties) {
            return new CopyOptions(editable, isIgnoreNullValue, ignoreProperties);
        }

        /**
         * 构造拷贝选项
         */
        public CopyOptions() {
        }

        /**
         * 构造拷贝选项
         *
         * @param editable          限制的类或接口，必须为目标对象的实现接口或父类，用于限制拷贝的属性
         * @param isIgnoreNullValue 是否忽略空值，当源对象的值为null时，true: 忽略而不注入此值，false: 注入null
         * @param ignoreProperties  忽略的属性列表，设置一个属性列表，不拷贝这些属性值
         */
        public CopyOptions(Class<?> editable, boolean isIgnoreNullValue, String... ignoreProperties) {
            this.editable = editable;
            this.isIgnoreNullValue = isIgnoreNullValue;
            this.ignoreProperties = ignoreProperties;
        }

        /**
         * 设置限制的类或接口，必须为目标对象的实现接口或父类，用于限制拷贝的属性
         *
         * @param editable 限制的类或接口
         * @return CopyOptions
         */
        public CopyOptions setEditable(Class<?> editable) {
            this.editable = editable;
            return this;
        }

        /**
         * 设置是否忽略空值，当源对象的值为null时，true: 忽略而不注入此值，false: 注入null
         *
         * @param isIgnoreNullVal 是否忽略空值，当源对象的值为null时，true: 忽略而不注入此值，false: 注入null
         * @return CopyOptions
         */
        public CopyOptions setIgnoreNullValue(boolean isIgnoreNullVal) {
            this.isIgnoreNullValue = isIgnoreNullVal;
            return this;
        }

        /**
         * 设置忽略的属性列表，设置一个属性列表，不拷贝这些属性值
         *
         * @param ignoreProperties 忽略的属性列表，设置一个属性列表，不拷贝这些属性值
         * @return CopyOptions
         */
        public CopyOptions setIgnoreProperties(String... ignoreProperties) {
            this.ignoreProperties = ignoreProperties;
            return this;
        }
    }
}
