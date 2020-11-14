package com.chuang.urras.rowquery.handlers;//package com.chuang.urras.crud.handlers;
//
//import com.chuang.urras.support.enums.GenericEnum;
//import com.chuang.urras.toolskit.basic.util.ClassSearch;
//import org.apache.ibatis.type.BaseTypeHandler;
//import org.apache.ibatis.type.JdbcType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.sql.CallableStatement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * Created by ath on 2017/1/10.
// */
//public class GenericEnumTypeHandler<E extends GenericEnum> extends BaseTypeHandler<E> {
//
//    private static final Logger logger = LoggerFactory.getLogger(GenericEnumTypeHandler.class);
//
//    private Class<E> type;
//    private E [] enums;
//
//    /**
//     * 设置配置文件设置的转换类以及枚举类内容，供其他方法更便捷高效的实现
//     * @param type 配置文件中设置的转换类
//     */
//    public GenericEnumTypeHandler(Class<E> type) {
//        if (type == null)
//            throw new IllegalArgumentException("Type argument cannot be null");
//        this.type = type;
//        this.enums = type.getEnumConstants();
//        if (this.enums == null)
//            throw new IllegalArgumentException(type.getSimpleName()
//                    + " does not represent an enum type.");
//    }
//
//    @Override
//    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
//        //BaseTypeHandler已经帮我们做了parameter的null判断
//        // 忽略 jdbcType ,Enum全部都当做 TINYINT (Byte)
//        try {
//            ps.setObject(i, parameter.getCode(), JdbcType.TINYINT.TYPE_CODE);
//        } catch (SQLException e) {
//            logger.error("转换枚举字段异常。枚举字段所对应的数据库字段类型必须是tinyint类型。请检查是否是数据库字段类型错误导致的问题。", e);
//            throw e;
//        }
//    }
//
//    @Override
//    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
//        // 根据数据库存储类型决定获取类型，本例子中数据库中存放Byte类型
//        byte i = rs.getByte(columnName);
//        if (rs.wasNull()) {
//            return null;
//        } else {
//            // 根据数据库中的value值，定位PersonType子类
//            return locateEnumStatus(i);
//        }
//    }
//
//    @Override
//    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//        // 根据数据库存储类型决定获取类型，本例子中数据库中存放Byte类型
//        byte i = rs.getByte(columnIndex);
//        if (rs.wasNull()) {
//            return null;
//        } else {
//            // 根据数据库中的value值，定位PersonType子类
//            return locateEnumStatus(i);
//        }
//    }
//
//
//    @Override
//    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//        // 根据数据库存储类型决定获取类型，本例子中数据库中存放Byte类型
//        Byte i = cs.getByte(columnIndex);
//        if (cs.wasNull()) {
//            return null;
//        } else {
//            // 根据数据库中的value值，定位PersonType子类
//            return locateEnumStatus(i);
//        }
//    }
//
//    /**
//     * 枚举类型转换，由于构造函数获取了枚举的子类enums，让遍历更加高效快捷
//     * @param value 数据库中存储的自定义value属性
//     * @return value对应的枚举类
//     */
//    private E locateEnumStatus(byte value) {
//
//        for(E e : enums) {
//            if(e.getCode() == value) {
//                return e;
//            }
//        }
//        throw new IllegalArgumentException("未知的枚举类型：" + value + ",请核对" + type.getSimpleName());
//    }
//
//    private Class<E> getEnumType() {
//        return type;
//    }
//
//
//    private static boolean test(Class<?> clazz) {
//        return clazz.isEnum() && GenericEnum.class.isAssignableFrom(clazz);
//    }
//    public static MapTypeHandler[] scan(String... scanConvertGenericEnumToHandlerPackage) {
//
//        Set<Class<GenericEnum>> enumClassSet = new HashSet<>();
//        for (String enumPackage : scanConvertGenericEnumToHandlerPackage) {
////                ResolverUtil<GenericEnum> resolverUtil = new ResolverUtil<>();
//            enumClassSet.addAll(ClassSearch.unSafeFindClass(enumPackage, true, GenericEnumTypeHandler::test));
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("搜索到的 enumClassSet 如下：");
//            enumClassSet.forEach(eClass -> logger.debug(eClass.getName()));
//        }
//
//        List<MapTypeHandler<GenericEnum>> list = enumClassSet.stream()
//                .map(GenericEnumTypeHandler::new)
//                .map(genericEnumTypeHandler -> new MapTypeHandler<>(genericEnumTypeHandler.getEnumType(), genericEnumTypeHandler))
//                .collect(Collectors.toList());
//
//
//        if (logger.isDebugEnabled()) {
//            logger.debug("搜索到的 enumTypeHandlers 如下：");
//            list.forEach(handler -> logger.debug("type -> " + handler.getInClass().getName() + ", handler -> " + handler.getHandler().getClass().getName()));
//        }
//
//        return list.toArray(new MapTypeHandler[]{});
//    }
//}
