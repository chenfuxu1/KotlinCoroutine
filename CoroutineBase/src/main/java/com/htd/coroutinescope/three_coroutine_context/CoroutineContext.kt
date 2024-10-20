package com.htd.coroutinescope.three_coroutine_context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-19 20:22
 *
 * Desc: 挂起函数中的 coroutineContext
 */
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    scope.launch {
        showDispatchers()
    }
    delay(10000)
}

private suspend fun showDispatchers() {
    delay(1000)
    /**
     * 在挂起函数中获取 coroutineContext
     * cfx dispatchers: Dispatchers.Default
     *
     * public suspend inline val coroutineContext: CoroutineContext
     *     get() {
     *         throw NotImplementedError("Implemented as intrinsic")
     *     }
     */
    println("cfx dispatchers: ${coroutineContext[ContinuationInterceptor]}")

    /**
     * public suspend inline fun currentCoroutineContext(): CoroutineContext = coroutineContext
     * currentCoroutineContext 和 coroutineContext 是同一个对象，主要避免同名冲突
     */
    println("cfx dispatchers: ${currentCoroutineContext()[ContinuationInterceptor]}")
}

@OptIn(DelicateCoroutinesApi::class)
private fun flowFun() {
    flow<String> {
        // 用在挂起函数的参数里， { } 是一个挂起函数
        coroutineContext
    }

    // 如果在协程中调用
    GlobalScope.launch {
        flow<String> {
            // 这里的 coroutineContext 是协程里面的，和上述的不是一个对象
            coroutineContext
            // 因此，使用 currentCoroutineContext() 获取就是上述挂气函数的对象
            currentCoroutineContext()
        }
    }
}