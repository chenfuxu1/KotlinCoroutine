package com.htd.coroutinescope.seven_coroutine_context

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-20 17:37
 *
 * Desc: CoroutineContext 的加减和 get()
 */
@OptIn(ExperimentalStdlibApi::class)
fun main() = runBlocking<Unit> {
    /**
     * 1.Dispatchers.IO + Job() 合并为 CombinedContext[Dispatchers.IO, Job]
     * 2.CombinedContext[Dispatchers.IO, Job] 与 CoroutineName("MyName") 合并为 CombinedContext[[Dispatchers.IO, Job], CoroutineName("MyName")]
     */
    val scope = CoroutineScope(Dispatchers.IO + Job() + CoroutineName("MyName"))
    // [JobImpl{Active}@2b552920, CoroutineName(MyName), Dispatchers.IO]
    println("cfx scope: ${scope.coroutineContext}")
    scope.launch {
        /**
         * public interface Job : CoroutineContext.Element {
         *     public companion object Key : CoroutineContext.Key<Job>
         * }
         *
         * public interface CoroutineContext {
         *     public operator fun <E : Element> get(key: Key<E>): E?
         *
         *     public interface Key<E : Element>
         * }
         *
         * 1.这里填的 Job 指的就是 public interface Job 接口的伴生对象 Key，也就是 CoroutineContext.Key<Job>
         * 2.[] 是重写了操作符 get(key: Key<E>): E? 函数，key 的类型是这里是 Key<Job>，所以这里 E 就是接口 Job
         *
         * 所以，这里传入的 Job 实际是 Job 接口的伴生对象 Key， 这个 Key 又指向了 Job 接口，所以返回就是 Job 对象
         */
        val job = coroutineContext[Job]
        // CoroutineDispatcher
        var continuationInterceptor = coroutineContext[ContinuationInterceptor]
        var coroutineDispatcher = coroutineContext[CoroutineDispatcher]
        // cfx 移除 Job 前 coroutineContext：[CoroutineName(MyName), StandaloneCoroutine{Active}@2106956d, Dispatchers.IO]
        println("cfx 移除 Job 前 coroutineContext：${coroutineContext}")
        // cfx 移除 Job 后 coroutineContext：[CoroutineName(MyName), Dispatchers.IO]
        println("cfx 移除 Job 后 coroutineContext：${coroutineContext.minusKey(Job)}")
        // cfx coroutineContext：[CoroutineName(MyName), StandaloneCoroutine{Active}@2106956d, Dispatchers.IO]
        println("cfx coroutineContext：${coroutineContext}")

    }

    val job1 = Job()
    val job2 = Job()
    // 此时 job2 会覆盖 job1
    val scope2 = CoroutineScope(Dispatchers.IO + job1 + CoroutineName("MyName2") + job2)
    println("cfx job1(): $job1 job2(): $job2") // cfx job1(): JobImpl{Active}@4d95d2a2 job2(): JobImpl{Active}@53f65459

    // cfx scope2: [CoroutineName(MyName2), JobImpl{Active}@53f65459, Dispatchers.IO]
    println("cfx scope 2: ${scope2.coroutineContext}")

    delay(10000)
}
