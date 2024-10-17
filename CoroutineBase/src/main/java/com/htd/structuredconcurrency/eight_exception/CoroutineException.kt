package com.htd.structuredconcurrency.eight_exception

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-15 0:09
 *
 * Desc: 协程的异常
 * 协程的取消：会导致子协程的取消（向内）
 * 协程的异常：会导致父协程、子协程都发生异常（递归，包括父协程的兄弟协程，即整个协程树，向内向外）
 * 协程的取消和异常使用的是同一套流程，但是协程的取消进行了特殊的处理
 *
 */
// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     var childJob: Job? = null
//     val parentJob = scope.launch {
//         childJob = launch {
//             println("cfx child coroutine started")
//             delay(3000)
//             println("cfx child coroutine end")
//         }
//         println("cfx parent coroutine started")
//         delay(1000)
//         println("cfx parent coroutine end")
//         // throw IllegalStateException("cfx wrong state")
//         throw CancellationException("cfx wrong state") // 当换成 CancellationException 时，下面父子协程的状态还是一样的，子协程会被取消，只是没有打印抛异常
//     }
//     delay(500)
//     /**
//      * 抛异常前的父子协程状态
//      * cfx parentJob.isActive: true, childJob.isActive: true
//      * cfx parentJob.isCancelled: false, childJob.isCancelled: false
//      */
//     println("cfx parentJob.isActive: ${parentJob.isActive}, childJob.isActive: ${childJob?.isActive}")
//     println("cfx parentJob.isCancelled: ${parentJob.isCancelled}, childJob.isCancelled: ${childJob?.isCancelled}")
//     delay(600)
//     /**
//      * 抛异常后的父子协程状态
//      * cfx parentJob.isActive: false, childJob.isActive: false
//      * cfx parentJob.isCancelled: true, childJob.isCancelled: true
//      *
//      * 可以看到，子协程 println("cfx parent coroutine end") 并没有打印，是由于父协程异常结束导致子协程也出现了异常
//      */
//     println("cfx parentJob.isActive: ${parentJob.isActive}, childJob.isActive: ${childJob?.isActive}")
//     println("cfx parentJob.isCancelled: ${parentJob.isCancelled}, childJob.isCancelled: ${childJob?.isCancelled}")
//     delay(10000)
// }

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    var childJob: Job? = null
    var grandChildJob: Job? = null
    val parentJob = scope.launch {
        childJob = launch {
            grandChildJob = launch {
                println("cfx grand child coroutine started")
                delay(3000)
                println("cfx grand child coroutine end")
            }
            println("cfx child coroutine started")
            delay(1000)
            println("cfx child coroutine end")
            // throw IllegalStateException("cfx wrong state")
            throw CancellationException("cfx wrong state")
        }
        println("cfx parent coroutine started")
        delay(2000)
        println("cfx parent coroutine end")
    }
    delay(500)
    /**
     * 抛异常前的父子孙协程状态
     * cfx parentJob.isActive: true, childJob.isActive: true, grandChildJob?.isActive: true
     * cfx parentJob.isCancelled: false, childJob.isCancelled: false, grandChildJob?.isCancelled: false
     */
    println("cfx parentJob.isActive: ${parentJob.isActive}, childJob.isActive: ${childJob?.isActive}, grandChildJob?.isActive: ${grandChildJob?.isActive}")
    println("cfx parentJob.isCancelled: ${parentJob.isCancelled}, childJob.isCancelled: ${childJob?.isCancelled}, grandChildJob?.isCancelled: ${grandChildJob?.isCancelled}")
    delay(600)
    /**
     * 抛异常后的父子协程状态
     * 抛异常的结果，你的协程取消 -> 导致父协程取消 -> 导致兄弟协程取消...
     * cfx parentJob.isActive: false, childJob.isActive: false, grandChildJob?.isActive: false
     * cfx parentJob.isCancelled: true, childJob.isCancelled: true, grandChildJob?.isCancelled: true
     *
     * 抛取消的结果
     * cfx parentJob.isActive: true, childJob.isActive: false, grandChildJob?.isActive: false
     * cfx parentJob.isCancelled: false, childJob.isCancelled: true, grandChildJob?.isCancelled: true
     *
     * 可以发现，普通异常是双向结束，而取消是向内结束，因为 parentJob.isCancelled: false 并没有取消，还是正常的执行
     */
    println("cfx parentJob.isActive: ${parentJob.isActive}, childJob.isActive: ${childJob?.isActive}, grandChildJob?.isActive: ${grandChildJob?.isActive}")
    println("cfx parentJob.isCancelled: ${parentJob.isCancelled}, childJob.isCancelled: ${childJob?.isCancelled}, grandChildJob?.isCancelled: ${grandChildJob?.isCancelled}")
    delay(10000)
}

/**
 * 每个协程取消时都会调用父协程的 childCancelled 方法
 * 如果是 CancellationException 直接返回 true 了
 * 普通异常：cancelImpl(cause) 会取消父协程自己
 *
 * public open fun childCancelled(cause: Throwable): Boolean {
 *     if (cause is CancellationException) return true
 *     return cancelImpl(cause) && handlesException
 * }
 */

/**
 * 普通异常 和 取消的区别
 * 1.普通异常是双向的导致协程取消，而 协程的取消是向内的，会导致本身和子协程的取消
 * 2.取消有两种方式：job.cancel 和 抛 CancellationException; 异常只有一种方式，内部抛出
 * 3.协程的取消 job.cancel(CancellationException) 只能传入 CancellationException，而普通异常会暴露给到线程世界中
 */