package com.chuang.tauceti.tools.basic;


import com.chuang.tauceti.support.exception.BusinessException;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public class ObjectKit {
    /**
     * 比较source 和 之后的所有字符串，只有有一个匹配就返回true
     * @param source 源字符串
     * @param eq 需要比较的字符串数组
     * @return 如果eq中有一个字符串和source匹配，就返回true，否则返回false
     */
    public static boolean equalsAny(String source, String... eq) {
        for(String s : eq){
            if(s.equals(source)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 比较两个对象是否相等。<br>
     * 相同的条件有两个，满足其一即可：<br>
     * 1. obj1 == null && obj2 == null; 2. obj1.equals(obj2)
     *
     * @param obj1
     *            对象1
     * @param obj2
     *            对象2
     * @return 是否相等
     */
    public static boolean equals(@Nullable Object obj1, @Nullable Object obj2) {
        return Objects.equals(obj1, obj2);
    }


    /**
     * 序列化
     * @param object 对象
     * @return 序列化后的byte数组
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos;
        ByteArrayOutputStream baos;
        try {
            //
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("序列化失败", e);
        }
    }

    /**
     * 将序列化后的数组转成对象
     * @param bytes 字节数组
     * @return 反序列化后的对象
     */
    public static Object unSerialize(byte[] bytes) {

        ByteArrayInputStream bais;
        try {
            //
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            throw new BusinessException("反序列化失败");
        }
    }

    public static boolean isBasicType(Object obj) {
        return obj instanceof Boolean ||
                obj instanceof Byte ||
                obj instanceof Short ||
                obj instanceof Character ||
                obj instanceof Integer ||
                obj instanceof Long ||
                obj instanceof Float ||
                obj instanceof Double;
    }

    public static boolean isBasicTypeOrString(Object obj) {
        return isBasicType(obj) || obj instanceof String;
    }
    public static boolean isNormalType(Object obj) {
        return isBasicTypeOrString(obj) ||
                obj instanceof Enum ||
                obj instanceof Date ||
                obj instanceof LocalDateTime ||
                obj instanceof Number;
    }

}
