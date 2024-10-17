package com.htd.structuredconcurrency.five_cancel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-11 23:32
 *
 * Desc: 协程的取消
 *
 * runBlocking 会创建一个阻塞当前线程的协程
 * 这里 runBlocking 是包裹着 main 函数的
 * 在 java 中，main 函数执行完，那么程序就会退出（所有的用户线程执行完毕）
 * java 中的线程分为用户线程和守护线程
 *
 * 协程创建的线程都是守护线程
 * 因此：runBlocking 代码执行完毕后，会检查没有子协程了，那么 runBlocking 这个协程也就结束了，接下来 main 函数结束，主线程结束，程序结束
 */
fun main() = runBlocking<Unit> {
    thread {

    }.apply {
        isDaemon // 区分是否为守护线程，默认为 false
    }

    // thread {
    //     Thread.sleep(10000)
    // }
    println(coroutineContext[ContinuationInterceptor])
    val scope = CoroutineScope(EmptyCoroutineContext) // Default

    /**
     * 直接 launch 启动，属于 runBlocking 的子协程
     * 如果是 scope.launch {  } 启动的，不属于 runBlocking 的子协程，不会等待，会直接执行
     */
    // val job = scope.launch {
    //     var count = 0
    //     println(coroutineContext[ContinuationInterceptor])
    //
    //     while (true) {
    //         println("cfx count: ${count++}")
    //         delay(500)
    //     }
    // }

    println("===================")
    /**
     * 这里直接 launch 启动是 runBlocking 的子协程，默认的 ContinuationInterceptor 是 BlockingEventLoop@2a17b7b6
     * 会阻塞线程，不会执行下面的 println("cfx job launch 1")
     */
    // val job = launch {
    //     var count = 0
    //     println(coroutineContext[ContinuationInterceptor])
    //
    //     while (true) {
    //         println("cfx count: ${count++}")
    //         delay(500)
    //     }
    // }
    // println("cfx job launch 1")

    println("===================")
    /**
     * 设置为 Dispatchers.Default，这样协程在后台，不会阻塞下面的代码 println("cfx job launch 2") 运行
     * job.isActive 检查协程是否在活跃了
     */
    // val job = launch(Dispatchers.Default) {
    //     var count = 0
    //     println(coroutineContext[ContinuationInterceptor])
    //     while (true) {
    //         if (!isActive) { // 检查协程是否在活跃了
    //             return@launch
    //         }
    //         println("cfx count: ${count++}")
    //         delay(500)
    //     }
    // }
    //
    // delay(3000)
    // println("cfx job launch 2")
    // job.cancel()

    println("===================")
    val job = launch(Dispatchers.Default) {
        println("cfx coroutine I`m running")
        println(coroutineContext[ContinuationInterceptor])
        var count = 0
        while (true) {
            /**
             * ensureActive() 结束协程函数，会直接返回异常
             */
            // ensureActive()
            // coroutineContext.ensureActive()
            // coroutineContext.job.ensureActive()
            // if (!isActive) {
            //     // return@launch
            //     /**
            //      * 协程的取消是抛异常，协程会接住该异常，并进行取消
            //      * 因为 return 只能将当前代码返回，而协程是结构化的
            //      * 抛异常不仅可以结束当前协程，还能结束其子协程
            //      */
            //     throw CancellationException()
            // }
            count++
            if (count % 100_000_000 == 0) {
                println(count)
            }
            if (count % 1_000_000_000 == 0) {
                break
            }
            try {
                delay(500)
            } catch (e: CancellationException) {
                println("cfx CancellationException")
                /**
                 * 不需要手动捕获 CancellationException，这样会导致 CancellationException 被吞掉无法结束协程
                 * 如果真的需要捕获 CancellationException，还需要手动抛出去
                 * 协程中所有的挂起函数都会抛 CancellationException，除了 suspendCoroutine，不支持取消
                 */
                throw e
            }
        }
        println("cfx coroutine I`m done")
    }
    delay(1000)
    println("cfx job launch 3")
    job.cancel()
}