package com.wj.gradle.plugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * create by wenjing.liu at 2022/2/23
 * WjRouter的代码注解
 *
 * @Target:所修饰对象的范围 {@link ElementType#TYPE}:描述类、接口或者enum的声明
 * {@link ElementType#CONSTRUCTOR}:描述构造器
 * {@link ElementType#FIELD}
 * {@link ElementType#LOCAL_VARIABLE}
 * {@link ElementType#METHOD}
 * {@link ElementType#PACKAGE}
 * {@link ElementType#PARAMETER}
 * @Retention:作用时机及生成文件的保留时机 {@link RetentionPolicy#CLASS} 默认。注解被保留在.class，但在运行期间不识别该注解。
 * 用于生成辅助代码，该代码生成之后，该注解任务结束。例如ARouter
 * {@link RetentionPolicy#SOURCE}保留在source文件，编译过程中可见，编译后被丢弃。
 * 用于检查性操作。如@Override
 * {@link RetentionPolicy#RUNTIME}保留在.class，在运行时也可见。如@Deprected
 * @Inherited 是否可被继承，默认为false
 * @Doucment 是否保存在Javadoc文档中
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface WjRouter {
    /**
     * 跳转到该页面的key
     *
     * @return
     */
    String key();
}
