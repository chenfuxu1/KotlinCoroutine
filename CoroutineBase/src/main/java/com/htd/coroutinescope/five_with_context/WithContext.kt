package com.htd.coroutinescope.five_with_context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-20 11:10
 *
 * Desc: withContext 启动串行的协程
 * 相当于串行的 launch、async
 * 相当于可以定制 CoroutineContext 的 coroutineScope { }
 *
 * withContext：用来临时切换 CoroutineContext
 */
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    scope.launch {
        /**
         * withContext 和 coroutineScope 区别是可以填参数
         *
         * return suspendCoroutineUninterceptedOrReturn sc@ { uCont ->
         *     // 1.创建一个新的 context
         *     val oldContext = uCont.context
         *     val newContext = oldContext.newCoroutineContext(context)
         *     // 2.验证协程是否取消了
         *     newContext.ensureActive()
         *     // 3.新旧 context 一样，例如没填 context，context 没改变
         *     if (newContext === oldContext) {
         *         // 创建 ScopeCoroutine
         *         val coroutine = ScopeCoroutine(newContext, uCont)
         *         // 和 coroutineScope 逻辑一样
         *         return@sc coroutine.startUndispatchedOrReturn(coroutine, block)
         *     }
         *
         *     // 4.如果 context 改变了，但是线程池 ContinuationInterceptor 没有改变
         *     if (newContext[ContinuationInterceptor] == oldContext[ContinuationInterceptor]) {
         *         // 启动一个新协程
         *         val coroutine = UndispatchedCoroutine(newContext, uCont)
         *         // There are changes in the context, so this thread needs to be updated
         *         withCoroutineContext(coroutine.context, null) {
         *             return@sc coroutine.startUndispatchedOrReturn(coroutine, block)
         *         }
         *     }
         *     // 5.线程也改变了
         *     val coroutine = DispatchedCoroutine(newContext, uCont)
         *     block.startCoroutineCancellable(coroutine, coroutine)
         *     coroutine.getResult()
         * }
         */
        withContext(coroutineContext) {

        }
        /**
         * coroutineScope 和 launch、async 的区别之一是不能填 coroutineContext 参数
         * 相当于没填参数的 withContext，等价于 withContext(EmptyCoroutineContext) 或 withContext(coroutineContext)
         *
         * public suspend fun <R> coroutineScope(block: suspend CoroutineScope.() -> R): R {
         *     contract {
         *         callsInPlace(block, InvocationKind.EXACTLY_ONCE)
         *     }
         *     return suspendCoroutineUninterceptedOrReturn { uCont ->
         *         val coroutine = ScopeCoroutine(uCont.context, uCont)
         *         coroutine.startUndispatchedOrReturn(coroutine, block)
         *     }
         * }
         */
        coroutineScope {

        }
    }
    delay(10000)

}