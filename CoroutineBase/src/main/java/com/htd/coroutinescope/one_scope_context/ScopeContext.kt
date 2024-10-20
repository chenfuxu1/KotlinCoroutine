package com.htd.coroutinescope.one_scope_context

import kotlinx.coroutines.*
import kotlin.coroutines.ContinuationInterceptor

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-19 10:57
 *
 * Desc: CoroutineScope 与 CoroutineContext
 *
 * CoroutineScope: 提供协程的上下文信息和启动子协程
 * CoroutineContext: 保存协程代码块的上下文信息
 *
 */
fun main() = runBlocking<Unit> {
    /**
     * cfx scope: CoroutineScope(coroutineContext=[JobImpl{Active}@3dd4520b, Dispatchers.IO])
     * CoroutineScope(Dispatchers.IO) 是手动创建的 CoroutineScope，并不对应任何的协程，因为还没有启动协程，不对应启动协程的代码块
     * 所以，手动创建的 CoroutineScope 的 coroutineContext 不属于任何协程的上下文，仅用于创建新协程时使用的 coroutineContext
     */
    val scope = CoroutineScope(Dispatchers.IO)
    println("cfx scope: $scope")
    scope.launch {
        // cfx this: StandaloneCoroutine{Active}@406495cb
        println("cfx this: $this")
        /**
         * coroutineContext 就是保存 this：CoroutineScope 协程的上下文对象信息
         */
        coroutineContext[Job]
        this.coroutineContext[Job]
        coroutineContext.job
        coroutineContext[ContinuationInterceptor]
    }

    delay(1000)
}