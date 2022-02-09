package com.wj.appprotect.shell;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

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

        // 1.ContextImpl的   private Context mOuterContext;中的壳application
        //从Application的attachBaseContext()中的传入参数就是本app的ContextImpl
        Context contextImpl = application.getBaseContext();
        //有了contextImpl,通过反射contextImpl中获取mpackageInfo(LoadedApk) mMainThread(ActivityThread)

        //二、得倒原Application实例的方式
        // 2.直接通过clazz.newInstance();+ app.attach(context);的方式
        //TODO 原app的application先暂时直接赋值字符串
        String originalApplicationName = "com.wj.gradle.plugin.GradlePluginApplication";
        try {
            //第一步:得到原生Application的对象
            Class<?> applicationClass = Class.forName(originalApplicationName);
            Object originalApplication = applicationClass.newInstance();
            //第二步:调用Application#attach()
            applicationAttach(originalApplication, contextImpl);
            //第三步:替换所有的application对象
            //1.ContextImpl的  private Context mOuterContext;中的壳application
            //在LoadedApk#makeApplication()中通过appContext.setOuterContext(app);赋值
            setOuterContext(originalApplication, contextImpl);

            //通过从ContextImpl实例 反射获取mpackageInfo(LoadedApk)和 mMainThread(ActivityThread)。
            Field loadedApkField = ClassReflectUtils.findField(contextImpl, "mPackageInfo");
            Field mMainThread = ClassReflectUtils.findField(contextImpl, "mMainThread");
            //2.ActivityThread的final ArrayList<Application> mAllApplications = new ArrayList<Application>();中的壳application
            //在LoadedApk#makeApplication()中通过mActivityThread.mAllApplications.add(app);
            Object mActivityThreadObject = mMainThread.get(contextImpl);
            setActivityThreadAllApplication(originalApplication, mActivityThreadObject, application);

            // 3.LoadedApk的private Application mApplication;中的壳application
            //在LoadedApk#makeApplication()中通过mApplication = app;
            setLoadedApkApplication(originalApplication, loadedApkField, contextImpl);

            //上面的三个赋值是在LoadedApk#makeApplication()中进行的，返回该方法的返回值赋值给ActivityThread的mInitialApplication

            //4.ActivityThread的 Application mInitialApplication;中的壳application
            //在ActivityThread中通过mInitialApplication = context.mPackageInfo.makeApplication(true, null);
            setActivityThreadApplication(originalApplication, mActivityThreadObject);
            //第四步:
            // originalApplication.onCreate();
            applicationOnCreate(originalApplication);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用 Application#attach(),从而回调到到自定义Application的attachBaseContext()
     *
     * @param originalApplication
     * @param contextImpl
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private static void applicationAttach(Object originalApplication, Context contextImpl) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method attachMethod = ClassReflectUtils.findMethod(originalApplication, "attach", Context.class);
        attachMethod.setAccessible(true);
        attachMethod.invoke(originalApplication, contextImpl);
    }

    /**
     * 1.ContextImpl的  private Context mOuterContext;中的壳application
     *
     * @param originalApplication
     * @param contextImpl
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private static void setOuterContext(Object originalApplication, Context contextImpl) throws NoSuchFieldException, IllegalAccessException {
        Field mOuterContextField = ClassReflectUtils.findField(contextImpl, "mOuterContext");
        mOuterContextField.setAccessible(true);
        mOuterContextField.set(contextImpl, originalApplication);
        LogUtils.logV(mOuterContextField.get(contextImpl).toString());
    }

    /**
     * 2.ActivityThread的final ArrayList<Application> mAllApplications = new ArrayList<Application>();中的壳application
     * @param originalApplication
     * @param mActivityThreadObject
     * @param shellApplication
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private static void setActivityThreadAllApplication(Object originalApplication, Object mActivityThreadObject, Application shellApplication) throws NoSuchFieldException, IllegalAccessException {
        Field mAllApplicationsField = ClassReflectUtils.findField(mActivityThreadObject, "mAllApplications");
        ArrayList<Application> mAllApplicationsObject = (ArrayList<Application>) mAllApplicationsField.get(mActivityThreadObject);
        mAllApplicationsObject.remove(shellApplication);
        mAllApplicationsObject.add((Application) originalApplication);
        mAllApplicationsField.set(mActivityThreadObject, mAllApplicationsObject);
    }

    /**
     * 3.LoadedApk的private Application mApplication;中的壳application
     *
     * @param originalApplication
     * @param loadedApkField
     * @param contextImpl
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    private static void setLoadedApkApplication(Object originalApplication, Field loadedApkField, Context contextImpl) throws IllegalAccessException, NoSuchFieldException {
        Object loadedApkObject = loadedApkField.get(contextImpl);
        Field mApplicationField = ClassReflectUtils.findField(loadedApkObject, "mApplication");
        mApplicationField.set(loadedApkObject, originalApplication);
    }

    /**
     * 4.ActivityThread的 Application mInitialApplication;中的壳application
     *
     * @param originalApplication
     * @param mActivityThreadObject
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private static void setActivityThreadApplication(Object originalApplication, Object mActivityThreadObject) throws NoSuchFieldException, IllegalAccessException {
        Field mInitialApplicationField = ClassReflectUtils.findField(mActivityThreadObject, "mInitialApplication");
        mInitialApplicationField.set(mActivityThreadObject, originalApplication);
    }

    /**
     * 调用Application的onCreate
     *
     * @param originalApplication
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private static void applicationOnCreate(Object originalApplication) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method onCreateMethod = ClassReflectUtils.findMethod(originalApplication, "onCreate");
        onCreateMethod.setAccessible(true);
        onCreateMethod.invoke(originalApplication);
    }

}
