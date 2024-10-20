package com.htd.coroutinescope.eight_custom_coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-20 22:32
 *
 * Desc: 自定义 CoroutineContext
 */
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val myContext = MyContext()
    scope.launch(myContext) {
        println(coroutineContext[MyContext])
    }

    val logContext = LogContext()
    scope.launch(logContext) {
        val context = coroutineContext[LogContext]
        // cfx current coroutine: [com.htd.coroutinescope.eight_custom_coroutine.LogContext@b53f078, StandaloneCoroutine{Active}@139bcd2b, Dispatchers.Default]
        context?.log()
    }
    delay(10000)
}

class MyContext: AbstractCoroutineContextElement(MyContext) {
    companion object Key : CoroutineContext.Key<MyContext>
}

/**
 * 可以参考 CoroutineName 的实现
 */
class LogContext: AbstractCoroutineContextElement(LogContext) {
    companion object Key : CoroutineContext.Key<LogContext>

    suspend fun log() {
        println("cfx current coroutine: $coroutineContext")
    }
}