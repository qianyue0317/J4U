package com.qy.j4u.base;

import com.qy.j4u.utils.JLog;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

/**
 * presenter接口基类
 * Created by abc on 2016/11/2, modified by qy on 2019/5/3
 * 用动态代理监控view是否为空,并捕获view方法执行过程中未捕获的异常
 */
@SuppressWarnings("all")
public class BasePresenter<V extends BaseView> {

    private final RealViewHandler mH;
    private WeakReference<V> mReference;
    private V mRealView;

    public BasePresenter(V v) {
        mReference = new WeakReference<>(v);
        Class<?> vClass =
                (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        mH = new RealViewHandler(v);
        mRealView = (V) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{vClass},
                mH);
    }

    public V getView() {
        return mRealView;
    }

    public boolean isAttached() {
        return mReference != null && mReference.get() != null;
    }

    /**
     * 在Activity的onDestroy中调用
     */
    void detach() {
        if (mReference != null) {
            mReference.clear();
            mReference = null;
        }
        if (mH != null && mH.mTarget != null) {
            mH.mTarget.clear();
            mH.mTarget = null;
        }
    }


    public class RealViewHandler implements InvocationHandler {
        WeakReference<V> mTarget;

        public RealViewHandler(V v) {
            mTarget = new WeakReference<>(v);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (mTarget != null && mTarget.get() != null) {
                try {
                    /* ***********捕获其他异常(此处是view的实际调用的位置)************ */
                    JLog.i("实际的view方法执行的位置---------");
                    Object invoke = method.invoke(mTarget.get(), args);
                    return invoke;
                } catch (Exception e) {
                    JLog.i("view在执行的时候出了异常=====");
                    e.printStackTrace();
                    return null;
                }
            }
            JLog.i("view是空");
            return null;
        }
    }

    /**
     * 取消一些异步任务
     */
    public void cancel() {

    }

}
