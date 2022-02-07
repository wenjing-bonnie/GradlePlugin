package com.wj.appprotect.shell;

/**
 * create by wenjing.liu at 2022/2/7
 * 将application替换成原app的application
 * 1.创建原app的application，维护正常的生命周期，并进行回调
 * 2.屏蔽壳的application，在app的activity中调用getApplicationContext返回是原app的application
 * 3.ContentProvider创建时机比较特殊，在满足正常的初始化顺序之后，要屏蔽壳的application
 *
 *
 */
public class ReplaceApplicationUtils {

    /**
     * AMS进程在通知Zygote进程创建一个新的APP进程，完成创建初始化之后，
     * 加载的功能的时候，就会通过ActivityThread，来初始化application，
     * 依次调用到application的attachBaseContext()和oncreate
     */
    public static void replaceApplication(){

    }

}
