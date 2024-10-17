package com.htd.structuredconcurrency.ten_structured_exception

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.exitProcess

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-16 23:29
 *
 * Desc: 异常的结构化管理
 */

/**
 * 线程中的捕获未知异常的方法 tUncaughtExceptionHandler
 * 对于未知的异常，主要处理善后工作，优雅的结束
 */
// fun main() = runBlocking<Unit> {
//     /**
//      * 静态方法
//      * 可以给所有线程设置监听
//      */
//     Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
//         println("cfx static method thread: $thread throwable: $throwable")
//         // 善后工作，日志...
//         // 退出
//         exitProcess(0)
//     }
//     val thread = Thread {
//         throw RuntimeException("线程中主动抛出异常")
//     }
//     /**
//      * 可以捕获到 thread 抛出的所有异常
//      */
//     // thread.setUncaughtExceptionHandler { thread, throwable ->
//     //     println("cfx thread: $thread throwable: $throwable")
//     // }
//     thread.start()
//     delay(10000)
// }

/**
 * 协程中捕获未知异常的方法 CoroutineExceptionHandler
 */
fun main() = runBlocking<Unit> {
    /**
     * 如果协程中没有使用 CoroutineExceptionHandler 拦截
     * 还可以在线程中继续拦截
     */
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        println("cfx static method thread: $thread throwable: $throwable")
        // 善后工作，日志...
        // 退出
        exitProcess(0)
    }
    val scope = CoroutineScope(EmptyCoroutineContext)
    /**
     * 可以捕获到 scope 内部的所有异常
     * handler 只有设置到最外层的协程才有效
     * scope 内部任意一个协程中，只要发生了未知异常，就会结束整个 scope 的协程树，并且异常抛到了 handler，统一处理
     */
    val handler = CoroutineExceptionHandler { coroutineContext, exception ->
        println("cfx handler exception: $exception")
        // 善后工作，日志...
        // 退出
        exitProcess(0)
    }
    scope.launch(handler) {
        launch {
            throw RuntimeException("协程主动抛出异常")
        }
        launch {

        }
    }
    delay(10000)
}