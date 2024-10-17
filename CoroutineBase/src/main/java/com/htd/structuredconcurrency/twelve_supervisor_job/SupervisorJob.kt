package com.htd.structuredconcurrency.twelve_supervisor_job

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-17 23:49
 *
 * Desc:
 */
// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     val job = Job()
//     /**
//      * scope 的父协程是 Job()
//      */
//     scope.launch(job) {
//         throw RuntimeException("协程抛出异常")
//     }
//     delay(100)
//     /**
//      * cfx job job.isActive: false
//      * 可以看到，父协程由于子协程的异常也会取消
//      */
//     println("cfx job job.isActive: ${job.isActive}")
//     delay(10000)
// }

// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     val supervisorJob = SupervisorJob()
//     /**
//      * scope 的父协程是 SupervisorJob()
//      */
//     scope.launch(supervisorJob) {
//         throw RuntimeException("协程抛出异常")
//     }
//     delay(100)
//     /**
//      * cfx supervisorJob supervisorJob.isActive: true
//      * 可以看到，父协程没有取消，还是活跃状态
//      */
//     println("cfx supervisorJob supervisorJob.isActive: ${supervisorJob.isActive}")
//     delay(10000)
// }

// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     /**
//      * 这种格式，当 scope.launch 取消时，会使内部协程都取消
//      * 但当 launch(SupervisorJob(coroutineContext.job)) 内部抛异常时，不会到外部的 scope.launch 协程取消
//      */
//     scope.launch {
//         /**
//          * 这样写 launch 启动的协程的 父协程是 SupervisorJob
//          * 同时 SupervisorJob 又是 scope.launch 启动协程的子协程，父子关系没有断开
//          */
//         launch(SupervisorJob(coroutineContext.job)) {
//             throw RuntimeException("协程抛出异常")
//         }
//
//     }
//     delay(10000)
//
//     /**
//      * 写法 2
//      * 1.通过 scope2 启动的所有协程，当 scope2 取消时，内部协程会取消
//      * 2.scope2 内部抛出异常时，scope2 协程不会取消
//      */
//     val scope2 = CoroutineScope(SupervisorJob())
// }

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    /**
     * 这种格式，当 scope.launch 取消时，会使内部协程都取消
     * 但当 launch(SupervisorJob(coroutineContext.job)) 内部抛异常时，不会到外部的 scope.launch 协程取消
     */
    scope.launch {
        val handler = CoroutineExceptionHandler { thread, exception ->
            println("cfx handler exception: $exception")
        }
        /**
         * SupervisorJob 内部的子协程抛出的异常会到达 SupervisorJob 后抛到线程去
         * 前文说 handler 需要设置给 launch 启动的最外层的协程才能生效，但这里 SupervisorJob 显然不是最外层
         */
        launch(SupervisorJob(coroutineContext.job) + handler) {
            launch {
                launch {
                    throw RuntimeException("协程抛出异常")
                }
            }
        }
    }
    delay(10000)
}