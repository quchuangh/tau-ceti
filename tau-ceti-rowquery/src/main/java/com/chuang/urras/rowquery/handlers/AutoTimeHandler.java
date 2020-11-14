package com.chuang.urras.rowquery.handlers;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

public class AutoTimeHandler implements MetaObjectHandler {
    private final ValueGetter<String> operatorGetter;

    public AutoTimeHandler(ValueGetter<String> operatorGetter) {
        this.operatorGetter = operatorGetter;
    }
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.setFieldValByName("createdTime", now, metaObject);
        this.setFieldValByName("updatedTime", now, metaObject);
        this.setFieldValByName("deleted", false, metaObject);


        operatorGetter.get().ifPresent(operator -> {
            if(metaObject.hasGetter("creator") && null == metaObject.getValue("creator")) {
                this.setFieldValByName("creator", operator, metaObject);
            }
            if(metaObject.hasGetter("updater") && null == metaObject.getValue("updater")) {
                this.setFieldValByName("updater", operator, metaObject);
            }
        });
    }


    @Override
    public void updateFill(MetaObject metaObject) {

        LocalDateTime now = LocalDateTime.now();
        this.setFieldValByName("updatedTime", now, metaObject);
        operatorGetter.get().ifPresent(operator -> this.setFieldValByName("updater", operator, metaObject));
    }


}
