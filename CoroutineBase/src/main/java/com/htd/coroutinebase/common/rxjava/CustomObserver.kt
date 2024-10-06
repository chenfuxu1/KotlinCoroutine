package com.htd.coroutinebase.common.rxjava

import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

object CustomObserver {
    fun <T : Any> getObserver(): Observer<T> {
        val observer = object : Observer<T> {
            override fun onSubscribe(d: Disposable) {
                println("cfx CustomObserver getObserver onSubscribe disposable: $d")
            }

            override fun onNext(t: T) {
                println("cfx CustomObserver getObserver onNext t: $t")
            }

            override fun onError(e: Throwable) {
                println("cfx CustomObserver getObserver onError throwable: $e")
            }

            override fun onComplete() {
                println("cfx CustomObserver getObserver onComplete")
            }
        }
        return observer
    }

    // fun getSubscriber(): Observer<String> {
    //     val subscriber = @RequiresApi(Build.VERSION_CODES.R)
    //     object : Flow.Subscriber<String> {
    //         override fun onSubscribe(s: Flow.Subscription?) {
    //             println("cfx CustomObserver getSubscriber onSubscribe subscription: $s")
    //         }
    //
    //         override fun onNext(t: String?) {
    //             println("cfx CustomObserver getSubscriber onNext t: $t")
    //         }
    //
    //         override fun onError(t: Throwable?) {
    //             println("cfx CustomObserver getSubscriber onError throwable: $t")
    //         }
    //
    //         override fun onComplete() {
    //             println("cfx CustomObserver getSubscriber onComplete")
    //         }
    //     }
    //     return subscriber
    // }
}