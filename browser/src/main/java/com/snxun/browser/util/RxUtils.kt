package com.snxun.browser.util

import android.view.View
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Rx帮助类
 * Created by zhouL on 2018/7/3.
 */
object RxUtils {

    /** 在异步线程发起，在主线程订阅 */
    @JvmStatic
    fun <T> ioToMainObservable(): ObservableTransformer<T, T> = ObservableTransformer { upstream ->
        return@ObservableTransformer upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /** 在异步线程发起，在主线程订阅 */
    @JvmStatic
    fun <T> ioToMainFlowable(): FlowableTransformer<T, T> = FlowableTransformer { upstream ->
        return@FlowableTransformer upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /** 在异步线程发起，在主线程订阅 */
    @JvmStatic
    fun <T> ioToMainMaybe(): MaybeTransformer<T, T> = MaybeTransformer { upstream ->
        return@MaybeTransformer upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /** 在异步线程发起，在主线程订阅 */
    @JvmStatic
    fun <T> ioToMainSingle(): SingleTransformer<T, T> = SingleTransformer { upstream ->
        return@SingleTransformer upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /** 在异步线程发起，在主线程订阅 */
    @JvmStatic
    fun ioToMainCompletable(): CompletableTransformer = CompletableTransformer { upstream ->
        return@CompletableTransformer upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    /** [view]防抖点击，在时长[duration]默认1，单位[unit]默认秒内，只回调一次 */
    @JvmStatic
    @JvmOverloads
    fun viewClick(view: View, duration: Long = 1, unit: TimeUnit = TimeUnit.SECONDS): Observable<View> =
            Observable.create<View> { emitter ->
                view.setOnClickListener { v ->
                    emitter.doNext(v)
                }
            }.throttleFirst(duration, unit)


}