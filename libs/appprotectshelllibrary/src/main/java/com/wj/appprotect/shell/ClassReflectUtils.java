package com.wj.appprotect.shell;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * created by wenjing.liu at 2022/2/6
 * 反射
 */
public class ClassReflectUtils {

    /**
     * @param object 要从哪个对象中找出属性
     * @param name   属性的名字
     * @return
     */
    public static Field findField(Object object, String name) throws NoSuchFieldException {
        Class<?> cls = object.getClass();
        while (cls != Object.class) {
            try {
                Field field = cls.getDeclaredField(name);
                //设置访问权限
                field.setAccessible(true);
                if (field != null) {
                    return field;
                }
            } catch (NoSuchFieldException e) {
               // e.printStackTrace();
            } finally {
                cls = cls.getSuperclass();
            }
        }

        throw new NoSuchFieldException(object.getClass().getSimpleName() + " not find '" + name + "'");

    }

    /**
     * @param object         要从哪个对象中找出方法
     * @param name           方法的名字
     * @param parameterTypes 方法参数
     * @return
     */
    public static Method findMethod(Object object, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?> cls = object.getClass();
        //LogUtils.logV("obj = "+object.toString());
        while (cls != Object.class) {
           // LogUtils.logV(cls.toString());
            try {
                Method method = cls.getDeclaredMethod(name, parameterTypes);
                //设置访问权限
                method.setAccessible(true);
                if (method != null) {
                    return method;
                }
            } catch (NoSuchMethodException e) {
                //e.printStackTrace();
            }finally {
                cls = cls.getSuperclass();
            }
        }

        throw new NoSuchMethodException(object.getClass().getSimpleName() + " not find '" + name + "'");
    }


}
