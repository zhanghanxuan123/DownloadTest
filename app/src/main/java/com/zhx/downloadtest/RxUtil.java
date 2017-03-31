package com.zhx.downloadtest;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Author   :zhx
 * Create at 2017/3/28
 * Description:
 */
public class RxUtil {
    private Subject<Object, Object> mBus;
    private static RxUtil sInstance;
    private RxUtil() {
        mBus = new SerializedSubject<>(PublishSubject.create());
    }
    static RxUtil getInstance() {
        if (sInstance == null) {
            // [1]
            synchronized (RxUtil.class) {
                if (sInstance == null) {
                    //单例模式之双重检测：线程一在此之前线程二到达了位置[1],如果此处不二次判断，那么线程二执行到这里的时候还会重新new
                    sInstance = new RxUtil();
                }
            }
        }
        return sInstance;
    }
    private Subject<Object, Object> getBus() {
        return mBus;
    }
    static void send(Object obj) {
        if (getInstance().getBus().hasObservers()) {
            getInstance().getBus().onNext(obj);
        }
    }
    static Observable<Object> toObservable() {
        return getInstance().getBus();
    }
    static Observable<DownloadProgressEvent> getDownloadEventObservable() {
        return getInstance().toObservable().ofType(DownloadProgressEvent.class).observeOn(AndroidSchedulers.mainThread());
    }
}
