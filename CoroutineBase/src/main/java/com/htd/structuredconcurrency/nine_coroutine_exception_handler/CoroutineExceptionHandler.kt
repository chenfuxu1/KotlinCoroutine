package com.htd.structuredconcurrency.nine_coroutine_exception_handler

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-16 0:24
 *
 * Desc: CoroutineExceptionHandler
 */
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)

    // 1.线程里，这样直接抛，外界能接收到
    // thread {
    //     throw RuntimeException()
    // }

    // 2.线程里，加了 try / catch 外界感知不到
    // thread {
    //     try {
    //         throw RuntimeException()
    //     } catch (e: Exception) {
    //     }
    // }

    // 3.协程里，这样直接抛，外界能接收到
    // scope.launch {
    //     throw RuntimeException()
    // }

    // 4.协程里，加了 try / catch 外界感知不到
    // scope.launch {
    //     try {
    //         throw RuntimeException()
    //     } catch (e: Exception) {
    //     }
    // }

    /**
     * 5.try / catch 里面的新线程的异常，catch 不住
     * 因为二者不在一个线程，没法监听 catch
     */
    // try {
    //     val thread = object: Thread() {
    //         override fun run() {
    //             throw RuntimeException()
    //         }
    //     }
    //     thread.start()
    // } catch (e: Exception) {
    // }

    /**
     * 6.try / catch 里面的新协程的异常，catch 不住
     * 因为二者不在一个线程，没法监听 catch
     */
    // try {
    //     scope.launch {
    //         throw RuntimeException()
    //     }
    // } catch (e: Exception) {
    // }

    /**
     * 可以捕获到 scope 内部的所有异常
     * handler 只有设置到最外层的协程才有效
     */
    val handler = CoroutineExceptionHandler { coroutineContext, exception ->
        println("cfx handler exception: $exception")
    }
    scope.launch(handler) {
        try {
            launch {
                throw RuntimeException("主动抛出异常")
            }
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
    }
    delay(10000)
}