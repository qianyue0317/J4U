package com.qy.j4u.global;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.qy.j4u.di.components.DaggerNetComponent;
import com.qy.j4u.di.components.NetComponent;
import com.qy.j4u.global.constants.Constants;
import com.qy.j4u.iot.IOTConnectService;
import com.qy.j4u.lib.JLog;

import org.greenrobot.greendao.database.Database;
import org.litepal.LitePal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import es.dmoral.toasty.Toasty;

/**
 * 项目的Application类
 * Created by abc on 2016/11/2.
 */
@SuppressLint("Registered")
public class ForUApplication extends Application {

    private static ForUApplication sInstance;
    private NetComponent mNetComponent;
    private List<Activity> mActivities;

    static {
        System.loadLibrary("native-lib");
    }

    public static ForUApplication getInstance() {
        return sInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initIOT();
        initVariables();
        initHookActivityThreadHandler();
        sInstance = this;
        initUser();
        initActivityLife();
        initARouter();
        initNetModule();
        startCoreService();
        initJPush();
        initLitePal();
        initToasty();
    }

    private void initIOT() {
        startService(new Intent(this, IOTConnectService.class));
        JLog.i("application","启动iot");
    }

    private void initVariables() {
        mActivities = new ArrayList<>();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_MODERATE) {
            destroyAllData();
        }
    }

    /**
     * 结束掉所有Activity;监听到系统内存不足,此应用即将被回收的时候,调用
     */
    private void destroyAllData() {
        if (mActivities != null) {
            int size = mActivities.size();
            for (int i = 0; i < size; i++) {
                Activity activity = mActivities.get(i);
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
    }

    private void initHookActivityThreadHandler() {
        try {
            @SuppressLint("PrivateApi") Class<?> atClass = Class.forName("android.app" +
                    ".ActivityThread");
            Method currentActivityThread = atClass.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);
            Field mH = atClass.getDeclaredField("mH");
            mH.setAccessible(true);
            Handler handler = (Handler) mH.get(activityThread);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private static class HookHandler extends Handler {
        private Handler mResourceHandler;

        public HookHandler(Handler resourceHander) {
            this.mResourceHandler = resourceHander;
        }

        @Override
        public void handleMessage(Message msg) {
            JLog.i("HookHandler", "mH的消息:" + msg);
            mResourceHandler.handleMessage(msg);
        }
    }

    private void initLitePal() {
        LitePal.initialize(this);
    }

    private void initUser() {
        User.initFromLocal();
    }

    /**
     * 注册activity生命周期监听
     */
    private void initActivityLife() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//                JLog.i("activity_life", "onActivityCreated:" + activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
//                JLog.i("activity_life", "onActivityStarted:" + activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
//                JLog.i("activity_life", "onActivityResumed:" + activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
//                JLog.i("activity_life", "onActivityPaused:" + activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
//                JLog.i("activity_life", "onActivityStopped:" + activity);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//                JLog.i("activity_life", "onActivitySaveInstanceState:" + activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
//                JLog.i("activity_life", "onActivityDestroyed:" + activity);
            }
        });
    }

    public NetComponent getNetComponent() {
        return mNetComponent == null ? DaggerNetComponent.create() : mNetComponent;
    }

    private void initNetModule() {
        mNetComponent = DaggerNetComponent.create();
    }

    private void initToasty() {
        Toasty.Config.getInstance()
                .tintIcon(true)
                .setTextSize(13)
                .allowQueue(true)
                .apply();
    }

    private void initARouter() {
        if (Constants.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }

    private void startCoreService() {
    }


    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}
