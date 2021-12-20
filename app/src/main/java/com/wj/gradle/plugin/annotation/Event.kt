package com.wj.gradle.plugin.annotation

/**
 * Created by wenjing.liu on 2021/12/20 in J1.
 *
 * 自定义注解：和一般的类声明类似，只是在class面前增加annotation修饰符
 * //@Target：定义该注解可以用于哪些目标对象(如CLASS、FIELD、FUNCTION等)
 * //@Rentention：保留期（如SOURCE、BINARY、RUNTIME）
 * //@Repeatable：标记的注解可以多次应用于相同的声明或类型
 * //@MustBeDocumented：修饰的注解将被文档提取工具提到到API文档中
 *
 * @author wenjing.liu
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
 annotation class Event(val eventId: String, val label: String) {

}