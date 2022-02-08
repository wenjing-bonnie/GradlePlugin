package com.wj.appprotect.shell;

import android.app.Application;
import android.content.Context;

/**
 * create by wenjing.liu at 2022/2/7
 * 将application替换成原app的application
 * 1.创建原app的application，维护正常的生命周期，并进行回调
 * 2.屏蔽壳的application，在app的activity中调用getApplicationContext返回是原app的application
 * 3.ContentProvider创建时机比较特殊，在满足正常的初始化顺序之后，要屏蔽壳的application
 * <p>
 * AMS进程在通知Zygote进程创建一个新的APP进程，完成创建初始化之后，
 * 加载的功能的时候，就会通过ActivityThread，来初始化application，
 * 依次调用到application的attachBaseContext()和oncreate
 * <p>
 * 一、application的加载流程
 * 1.ActivityThread->systemMain(),在该方法里面调用到ActivityThread的attach()
 * 2.ActivityThread的attach()->context.mPackageInfo.makeApplication(true, null);来实例化Application对象
 * 2.1 context为ContextImpl实例,可通过[application.getBaseContext()]得倒该实例对象
 * 2.2 context.mPackageInfo为LoadedApk实例,存储的是APk在内存中的存储方式，可以得到代码、资料、功能清单等
 * 2.3 context.mPackageInfo.makeApplication即LoadedApk#makeApplication():
 * 最终调用的mActivityThread.mInstrumentation.newApplication(cl, appClass, appContext);创建一个Application实例
 * 在Instrumentation就是创建Activity实例、Application实例等，通过源码发现Instrumentation#newApplication最终就是通过
 * Application app = (Application)clazz.newInstance();
 * app.attach(context);
 * 来实例化Application对象并且调用 app.attach(context);从而回调到自定义Application的attachBaseContext()
 * <p>
 * 二、得倒原Application实例的方式
 * 1.直接通过clazz.newInstance();+ app.attach(context);的方式
 * 2.通过反射调用的mActivityThread.mInstrumentation.newApplication()
 * 2.1[application.getBaseContext()]的得到ContextImpl实例->反射得倒mMainThread对象->反射得到mActivityThread.mInstrumentation实例
 * 2.2反射newApplication()得倒application
 * <p>
 * 三、得倒原Application之后，需要替换application的位置：
 * 1.ContextImpl的   private Context mOuterContext;中的壳application
 * 2.ActivityThread的 final ArrayList<Application> mAllApplications
 * = new ArrayList<Application>();中的壳application
 * 3.LoadedApk的private Application mApplication;中的壳application
 * 上面的三个赋值是在LoadedApk#makeApplication()中进行的，返回该方法的返回值赋值给ActivityThread的mInitialApplication
 * 4.ActivityThread的  Application mInitialApplication;中的壳application
 * <p>
 * 上面的这ContextImpl、ActivityThread、LoadedApk三个对象在每个app中有且仅有一个
 * }
 */
public class ReplaceApplicationUtils {

    public static void replaceApplication(Application application) {
        //

        // 1.ContextImpl的   private Context mOuterContext;中的壳application
        //从Application的attachBaseContext()中的传入参数就是本app的ContextImpl
        Context contextImpl = application.getBaseContext();
        //有了contextImpl,通过反射contextImpl中获取mpackageInfo(LoadedApk) mMainThread(ActivityThread)

        //2.TODO 原app的application先暂时直接赋值字符串
        String originalApplication = "com.wj.appprotect.shell.AppProtectShellApplication";


    }

}
