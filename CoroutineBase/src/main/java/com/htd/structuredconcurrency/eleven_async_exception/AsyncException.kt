package com.htd.structuredconcurrency.eleven_async_exception

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-17 0:11
 *
 * Desc: async 对异常的处理
 */

/**
 * 父协程取消
 */
// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     val handler = CoroutineExceptionHandler { thread, exception ->
//         println("cfx handler thread: ${thread.job} exception: $exception")
//     }
//     scope.launch(handler) {
//         val deferred = async {
//             delay(1000)
//             throw RuntimeException("async 协程中主动抛出的异常")
//         }
//         launch {
//             try {
//                 deferred.await()
//             } catch (e: Exception) {
//                 /**
//                  * cfx Exception: kotlinx.coroutines.JobCancellationException: StandaloneCoroutine was cancelled; job=StandaloneCoroutine{Cancelling}@17f44cd4
//                  * 打印的是协程的取消异常
//                  * deferred 没有等到 1s，由于父协程取消而取消了
//                  */
//                 println("cfx Exception: $e")
//             }
//         }
//         delay(100)
//         cancel()
//     }
//     delay(10000)
// }

/**
 * 兄弟协程抛出异常，自己协程的挂起函数抛出取消异常
 */
// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     val handler = CoroutineExceptionHandler { thread, exception ->
//         println("cfx handler thread: ${thread.job} exception: $exception")
//     }
//     scope.launch(handler) {
//         val deferred = async {
//             delay(1000)
//             throw RuntimeException("async 协程中主动抛出的异常")
//         }
//         launch {
//             try {
//                 delay(2000)
//             } catch (e: Exception) {
//                 /**
//                  * cfx Exception: kotlinx.coroutines.JobCancellationException: Parent job is Cancelling; job=StandaloneCoroutine{Cancelling}@4fecfa01
//                  * 打印的是协程的取消异常
//                  * 由于 deferred 协程到达 1s 后抛出 RuntimeException 异常，从而使整个协程树取消
//                  */
//                 println("cfx Exception: $e")
//             }
//         }
//     }
//     delay(10000)
// }

/**
 * 1.deferred.await() 的双重影响
 * 先抛出 deferred 协程本身的异常
 * 再抛出 deferred.await() 的所在协程的取消异常
 */
// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     val handler = CoroutineExceptionHandler { thread, exception ->
//         println("cfx handler thread: ${thread.job} exception: $exception")
//     }
//     scope.launch(handler) {
//         val deferred = async {
//             delay(1000)
//             throw RuntimeException("async 协程中主动抛出的异常")
//         }
//         launch {
//             try {
//                 /**
//                  * deferred.await 不仅作为该协程的挂起函数存在，还作为 deferred 协程的返回对象存在
//                  * 这就会导致在 deferred 协程中抛出的 RuntimeException 异常会作为 deferred.await() 的返回结果
//                  * 同时，由于 deferred 协程抛了 RuntimeException 异常，会导致兄弟协程的挂起函数 deferred.await() 抛出取消异常
//                  * 那么，此处最终捕获的异常究竟是哪个呢？
//                  */
//                 deferred.await()
//             } catch (e: Exception) {
//                 /**
//                  * 由于 deferred.await() 的返回结果是直接触发的，而挂起函数 deferred.await() 抛出取消异常走的是结构化流程
//                  * 先从 deferred 协程抛出到父协程，父协程再到里面的子协程，所以，打印的是 RuntimeException 异常
//                  * cfx Exception: java.lang.RuntimeException: async 协程中主动抛出的异常
//                  * async 的异常先到达了
//                  */
//                 println("cfx Exception: $e")
//             }
//             try {
//                 delay(1000)
//             } catch (e: Exception) {
//                 /**
//                  * cfx delay Exception: kotlinx.coroutines.JobCancellationException: Parent job is Cancelling; job=StandaloneCoroutine{Cancelling}@2ab5a0c9
//                  * 可以看到，协程的取消异常还是会抛出来的，只是会晚一些
//                  */
//                 println("cfx delay Exception: $e")
//             }
//
//         }
//     }
//     delay(10000)
// }

/**
 * 2.当最外层的协程是 async 启动的，使用 CoroutineExceptionHandler 是无效的
 * async 不会把异常抛到线程世界
 * 因为 async 启动的会通过 deferred.await() 抛到另一个协程，不能直接抛到线程，线程不是终点
 * 所以 CoroutineExceptionHandler 只能用在 launch 启动的最外层的协程上
 */
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val handler = CoroutineExceptionHandler { thread, exception ->
        println("cfx handler thread: ${thread.job} exception: $exception")
    }
    scope.async(handler) {
        val deferred = async {
            delay(1000)
            throw RuntimeException("async 协程中主动抛出的异常")
        }
        launch {
            try {
                /**
                 * deferred.await 不仅作为该协程的挂起函数存在，还作为 deferred 协程的返回对象存在
                 * 这就会导致在 deferred 协程中抛出的 RuntimeException 异常会作为 deferred.await() 的返回结果
                 * 同时，由于 deferred 协程抛了 RuntimeException 异常，会导致兄弟协程的挂起函数 deferred.await() 抛出取消异常
                 * 那么，此处最终捕获的异常究竟是哪个呢？
                 */
                deferred.await()
            } catch (e: Exception) {
                /**
                 * 由于 deferred.await() 的返回结果是直接触发的，而挂起函数 deferred.await() 抛出取消异常走的是结构化流程
                 * 先从 deferred 协程抛出到父协程，父协程再到里面的子协程，所以，打印的是 RuntimeException 异常
                 * cfx Exception: java.lang.RuntimeException: async 协程中主动抛出的异常
                 * async 的异常先到达了
                 */
                println("cfx Exception: $e")
            }
            try {
                delay(1000)
            } catch (e: Exception) {
                /**
                 * cfx delay Exception: kotlinx.coroutines.JobCancellationException: Parent job is Cancelling; job=StandaloneCoroutine{Cancelling}@2ab5a0c9
                 * 可以看到，协程的取消异常还是会抛出来的，只是会晚一些
                 */
                println("cfx delay Exception: $e")
            }
        }
    }
    delay(10000)
}