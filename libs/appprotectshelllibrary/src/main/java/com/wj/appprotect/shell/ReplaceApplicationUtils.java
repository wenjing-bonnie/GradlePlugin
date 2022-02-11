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
 * }
 */
public class ReplaceApplicationUtils {


    public static Application replaceApplication(Application application) {

        try {
            //1.实例化原APP的Application的对象.直接通过clazz.newInstance();+ app.attach(context);的方式
            String originalApplicationName = getOriginalApplicationName();
            Class<?> applicationClass = Class.forName(originalApplicationName);
            Object originalApplication = applicationClass.newInstance();
            //得到contextImpl,通过反射contextImpl中获取mpackageInfo(LoadedApk) mMainThread(ActivityThread)
            Context contextImpl = application.getBaseContext();
            //2.调用Application#attach()
            applicationAttach(originalApplication, contextImpl);
            //3.替换所有的application对象
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

            //4.ActivityThread的 Application mInitialApplication;中的壳application
            //在ActivityThread中通过mInitialApplication = context.mPackageInfo.makeApplication(true, null);
            setActivityThreadApplication(originalApplication, mActivityThreadObject);
            //第四步:
            // originalApplication.onCreate();
            applicationOnCreate(originalApplication);
            return (Application) originalApplication;
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
        return application;
    }

    /**
     * 获取原APP的Application的名字
     * TODO 这里需要在进行优化
     * 因为通过gradle将原APP注册的Application替换了该解密壳的Application
     * 所以要将原APP的Application想方法保存下来，暂时先直接赋值字符串。
     * 另外还要判断若APP中没有配置Application，则直接使用"com.android.Application"
     *
     * @return
     */
    private static String getOriginalApplicationName() {
        //先假设原Manifest中配置的是是名字
        String dexApplication = "com.wj.gradle.plugin.GradlePluginApplication";
        //否则设置为"com.android.Application"
        return dexApplication;
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
        // LogUtils.logV(mOuterContextField.get(contextImpl).toString());
    }

    /**
     * 2.ActivityThread的final ArrayList<Application> mAllApplications = new ArrayList<Application>();中的壳application
     *
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
