package com.htd.coroutinebase.common.rxjava

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.IllegalStateException
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

/**
 * https://www.jianshu.com/p/c1aa3d0a2613
 */
fun main() {
    // testCreate()
    // testJust()
    // testFrom()
    // testFromCallable()
    // testFromFuture()
    // testFromIterable()
    // testDefer()
    // testTimer()
    // testInterval()
    // testIntervalRange()
    // testRange()
    // testEmptyNeverError()
    testMap()
}

/**
 * 1 创建操作符
 * 1.1 create()
 */
private fun testCreate() {
    /**
     * 1.创建被观察者
     * onNext()	发送该事件时，观察者会回调 onNext() 方法
     * onError() 发送该事件时，观察者会回调 onError() 方法，当发送该事件之后，其他事件将不会继续发送
     * onComplete()	发送该事件时，观察者会回调 onComplete() 方法，当发送该事件之后，其他事件将不会继续发送
     *
     */
    val observable = Observable.create(object : ObservableOnSubscribe<String> {
        override fun subscribe(emitter: ObservableEmitter<String>) {
            println("=========================currentThread name:  ${Thread.currentThread().name}")
            emitter.onNext("one")
            emitter.onNext("two")
            emitter.onNext("three")
            emitter.onComplete()
        }
    })
    /**
     * 2.创建观察者 CustomObserver.getObserver()
     * 3.订阅
     */
    observable.subscribe(CustomObserver.getObserver<String>())
}

/**
 * 1.2 just()
 * 创建一个被观察者，并发送事件，发送的事件不可以超过 10 个以上
 */
private fun testJust() {
    val just = Observable.just("one", "two", "three")
    just.subscribe(CustomObserver.getObserver<String>())
}

/**
 * 1.3 from()
 * 这个方法和 just() 类似，只不过 fromArray 可以传入多于 10 个的变量，并且可以传入一个数组
 */
private fun testFrom() {
    val arr = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")
    val from = Observable.fromArray(*arr)
    from.subscribe(CustomObserver.getObserver<String>())
}

/**
 * 1.4 fromCallable()
 * 这里的 Callable 是 java.util.concurrent 中的 Callable，Callable 和 Runnable 的用法基本一致，
 * 只是它会返回一个结果值，这个结果值就是发给观察者的
 */
private fun testFromCallable() {
    val callable = Observable.fromCallable {
        "荒天帝"
    }
    callable.subscribe(CustomObserver.getObserver<String>())
}

/**
 * 1.5 fromFuture()
 * 参数中的 Future 是 java.util.concurrent 中的 Future，Future 的作用是增加了 cancel() 等方法操作
 * Callable，它可以通过 get() 方法来获取 Callable 返回的值
 * doOnSubscribe() 的作用就是只有订阅时才会发送事件
 */
private fun testFromFuture() {
    val futureTask = FutureTask {
        println("cfx testFromFuture is Running")
        "返回结果"
    }
    val fromFuture = Observable.fromFuture(futureTask).doOnSubscribe {
        futureTask.run()
    }
    fromFuture.subscribe(object : Consumer<String> {
        override fun accept(t: String) {
            println("cfx testFromFuture subscribe accept t: $t")
        }

    })
}

/**
 * 1.6 fromIterable()
 * 直接发送一个 List 集合数据给观察者
 */
private fun testFromIterable() {
    val list = listOf("1", "2", "3")
    Observable.fromIterable(list).subscribe(CustomObserver.getObserver<String>())
}

/**
 * 1.7 defer()
 * 这个方法的作用就是直到被观察者被订阅后才会创建被观察者
 * 因为 defer() 只有观察者订阅的时候才会创建新的被观察者，所以每订阅一次就会打印一次
 * 并且都是打印 i 最新的值
 */
private fun testDefer() {
    var i = 100

    val observable = Observable.defer {
        Observable.just(i)
    }

    i = 200
    var observer = CustomObserver.getObserver<Int>()
    // 开始订阅
    observable.subscribe(observer)
    i = 300
    observable.subscribe(observer)
}

/**
 * 1.8 timer()
 * 当到指定时间后就会发送一个 0L 的值给观察者
 * https://www.jianshu.com/p/c1aa3d0a2613
 */
private fun testTimer() {
    var scheduler = Schedulers.newThread()
    var observable = Observable.timer(2, TimeUnit.SECONDS)
    observable.subscribe(CustomObserver.getObserver<Long>())
}

/**
 * 1.9 interval()
 * 每隔一段时间就会发送一个事件，这个事件是从 0 开始，不断增1的数字
 * 从时间就可以看出每隔 x 秒就会发出一次数字递增 1 的事件
 * 这里说下 interval() 第三个方法的 initialDelay 参数，这个参数的意思就是 onSubscribe 回调之后，
 * 再次回调 onNext 的间隔时间
 */
private fun testInterval() {
    val observable = Observable.interval(2, TimeUnit.SECONDS)
    observable.subscribe(CustomObserver.getObserver<Long>())
}

/**
 * 1.10 intervalRange()
 * 可以指定发送事件的开始值和数量，其他与 interval() 的功能一样
 */
private fun testIntervalRange() {
    val observable = Observable.intervalRange(2, 5, 2, 1, TimeUnit.SECONDS)
    observable.subscribe(CustomObserver.getObserver<Long>())
}

/**
 * 1.11 range()
 * 同时发送一定范围的事件序列
 */
private fun testRange() {
    val observable = Observable.range(2, 5)
    observable.subscribe(CustomObserver.getObserver<Int>()) // 2, 3, 4, 5, 6
}

/**
 * 1.12 rangeLong()
 * 作用与 range() 一样，只是数据类型为 Long
 */

/**
 * 1.13 empty() & never() & error()
 * empty()：直接发送 onComplete() 事件
 * never()：不发送任何事件
 * error()：发送 onError() 事件
 */
private fun testEmptyNeverError() {
    Observable.empty<String>().subscribe(CustomObserver.getObserver<String>())
    Observable.never<String>().subscribe(CustomObserver.getObserver<String>())
    Observable.error<Any>(IllegalStateException()).subscribe(CustomObserver.getObserver<Any>())
}

/**
 * 2 转换操作符
 * 2.1 map()
 * map 可以将被观察者发送的数据类型转变成其他的类型
 * 以下代码将 Integer 类型的数据转换成 String
 */
private fun testMap() {
    Observable.just(1, 2, 3)
        .map {
            "map to $it"
        }
        .subscribe(CustomObserver.getObserver<String>())
}

/**
 * 2.2 flatMap()
 * 这个方法可以将事件序列中的元素进行整合加工，返回一个新的被观察者
 * flatMap() 其实与 map() 类似，但是 flatMap() 返回的是一个 Observerable
 */
private fun testFlatMap() {

}