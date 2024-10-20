package com.htd.coroutinescope.two_global_scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-19 17:06
 *
 * Desc: GlobalScope 启动没有生命周期的协程
 * 由于没有和父 job 绑定，所以不会连带结束
 */
@OptIn(DelicateCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    CoroutineScope(EmptyCoroutineContext)
    /**
     * job 是空的
     * 那么 GlobalScope 启动的子协程就没有父协程了
     */
    println(GlobalScope.coroutineContext[Job]) // null
    val job = GlobalScope.launch {
        /**
         * @DelicateCoroutinesApi
         * public object GlobalScope : CoroutineScope {
         *     override val coroutineContext: CoroutineContext
         *         get() = EmptyCoroutineContext
         * }
         *
         * public operator fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>): E?
         * 返回的 job 对象是可空的
         */
        coroutineContext[Job]
        delay(1000)
        println("cfx coroutineContext[Job]: ${coroutineContext[Job]}")
    }
    println("cfx job.parent: ${job.parent}") // null, 父协程为空

    /**
     * 等价与下面的写法
     * 因为 GlobalScope 没有 job
     * 所以 GlobalScope 启动的协程没有 parent，这样当 GlobalScope.cancel 或者 GlobalScope 启动的其他协程发生异常时
     * 不会引起该协程的结束
     */
    GlobalScope.launch {

    }
    CoroutineScope(EmptyCoroutineContext).launch {

    }
    delay(10000)
}