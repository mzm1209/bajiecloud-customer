package com.bajiezu.cloud.customer.utils;

import java.lang.reflect.Field;

public class ReflectUtils {

    /**
     * 根据字段名获取对象的属性值
     * @param targetObj 目标对象
     * @param fieldName 字段名（与实体类属性名一致，如realName、mobile）
     * @return 字段对应的值，无对应字段返回null
     */
    public static Object getFieldValue(Object targetObj, String fieldName) {
        Class<?> clazz = targetObj.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            // 2. 设置字段可访问（突破私有字段访问限制）
            field.setAccessible(true);
            // 3. 获取字段对应的值并返回
            return field.get(targetObj);
        } catch (NoSuchFieldException e) {
            // 字段不存在异常
            throw new RuntimeException("目标对象不存在字段：" + fieldName, e);
        } catch (IllegalAccessException e) {
            // 字段访问权限异常
            throw new RuntimeException("获取字段" + fieldName + "值失败，权限不足", e);
        }
    }
}
