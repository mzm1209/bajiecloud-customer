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
            field.setAccessible(true);
            return field.get(targetObj);
        } catch (NoSuchFieldException e) {
            // 字段不存在异常
            throw new RuntimeException("目标对象不存在字段：" + fieldName, e);
        } catch (IllegalAccessException e) {
            // 字段访问权限异常
            throw new RuntimeException("获取字段" + fieldName + "值失败，权限不足", e);
        }
    }

   /**
    * 根据字段名给对象的属性赋值
    * */
    public static void setFieldValue(Object targetObj, String fieldName, Object value) {
        Class<?> clazz = targetObj.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(targetObj, value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("响应对象不存在字段：" + fieldName, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("给字段" + fieldName + "赋值失败，权限不足", e);
        }
    }
}
