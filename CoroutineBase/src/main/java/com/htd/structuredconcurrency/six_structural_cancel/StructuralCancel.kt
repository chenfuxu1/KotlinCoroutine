package com.htd.structuredconcurrency.six_structural_cancel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-13 17:42
 *
 * Desc: 协程的结构化取消
 * 父协程的取消，会导致子协程，子子协程都会取消
 * 父协程的取消，子协程可以拒绝吗？
 */
// fun main() = runBlocking<Unit> {
//     val start = System.currentTimeMillis()
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     val parentJob = scope.launch {
//         val childJob = launch {
//             println("cfx child job started")
//             delay(3000)
//             println("cfx child job end")
//         }
//     }
//     delay(1000)
//     /**
//      * 协程的取消
//      * 1.当一个 job 调用 cancel 时，首先会将自己变为非活跃状态，即 isActive 为 false
//      * 2.作为一个父协程，会调用所有子协程 job 的 cancel 方法
//      * 3.会把这些子协程变为非活跃状态
//      * 4.每个协程检测到 isActive 为 false 都会抛出 CancellationException，互相协程间不影响，时间上也没依赖关系，各抛各的
//      * （如果一个协程在运行到非挂起函数时，那么暂时就不会抛异常，或者协程内部代码没有挂起函数，那就不会取消，会执行完）
//      */
//     /**
//      * 协程取消三个阶段：
//      * 1.job.cancel, isActive 为 false（开发者不能修改）
//      * 2.调用子协程的 cancel 方法（开发者不能修改）
//      * 3.检测到 isActive 为 false 都会抛出 CancellationException (开发者可以修改，不抛出异常，但这样代码结果可能会超出预期，是有风险的，不建议)
//      * 所以父协程的取消，子协程不可以拒绝，只能在第三阶段修改一点点
//      */
//     parentJob.cancel()
//     delay(10000)
// }

fun main() = runBlocking<Unit> {
    val start = System.currentTimeMillis()
    val scope = CoroutineScope(EmptyCoroutineContext)
    val parentJob = scope.launch {
        val childJob = launch {
            println("cfx child job started")
            // 会拖住父协程，父协程也不会结束
            // try {
            //     delay(3000)
            // } catch (e: CancellationException) {
            //     // 这样子协程就没有取消
            //     println("cfx e: $e time: ${System.currentTimeMillis() - start}")
            // }

            Thread.sleep(3000) // 或者强行阻塞 3s

            println("cfx child job end time: ${System.currentTimeMillis() - start}")
        }
    }
    delay(1000)
    parentJob.cancel()
    val cancel = System.currentTimeMillis()
    parentJob.join()
    println("cfx cancel job time: ${System.currentTimeMillis() - cancel}")
    delay(10000)
}