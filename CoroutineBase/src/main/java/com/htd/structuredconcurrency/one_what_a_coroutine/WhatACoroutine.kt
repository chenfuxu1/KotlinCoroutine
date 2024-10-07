package com.htd.structuredconcurrency.one_what_a_coroutine

import kotlinx.coroutines.*
import okhttp3.Dispatcher
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-06 23:15
 *
 * Desc: 协程是什么
 */
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(Dispatchers.IO)
    // 创建的协程，但不启动协程
    // val job = scope.launch(start = CoroutineStart.LAZY) {
    //
    // }
    // scope.async {
    //
    // }
    // job.start()

    var innerJob: Job? = null
    var innerCoroutineScope: CoroutineScope? = null
    val outerJob = scope.launch(Dispatchers.Default) {
        val outerContinuationInterceptor = scope.coroutineContext[ContinuationInterceptor]
        val continuationInterceptor = coroutineContext[ContinuationInterceptor]
        println("cfx outerContinuationInterceptor: $outerContinuationInterceptor") // Dispatchers.IO
        println("cfx continuationInterceptor: $continuationInterceptor") // Dispatchers.Default

        innerJob = coroutineContext[Job]
        innerCoroutineScope = this
    }
    println("cfx outerJob: $outerJob") // 和 innerJob 是同一个对象
    println("cfx innerJob: $innerJob")
    println("cfx outerJob === innerJob: ${outerJob === innerJob}")
    // job 和 CoroutineScope 是同一个对象
    println("cfx outerJob === innerCoroutineScope: ${outerJob === innerCoroutineScope}") // true
}