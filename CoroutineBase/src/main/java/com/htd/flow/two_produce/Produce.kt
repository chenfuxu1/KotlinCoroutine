package com.htd.flow.two_produce

import com.htd.coroutinebase.common.api.GithubApi
import com.htd.coroutinebase.common.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-21 23:26
 *
 * Desc: Channel
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val github = RetrofitService.getRetrofit().create(GithubApi::class.java)

    /**
     * public fun <E> CoroutineScope.produce(
     *     context: CoroutineContext = EmptyCoroutineContext,
     *     capacity: Int = 0,
     *     @BuilderInference block: suspend ProducerScope<E>.() -> Unit
     * ): ReceiveChannel<E> =
     *     produce(context, capacity, BufferOverflow.SUSPEND, CoroutineStart.DEFAULT, onCompletion = null, block = block)
     *
     * public interface ProducerScope<in E> : CoroutineScope, SendChannel<E> { }
     *
     * ProduceScope 可以给别的协程发送消息的协程
     */
    val receiver = scope.produce {
        while (isActive) {
            val data = github.coroutineContributors("square", "okhttp")
            // 将请求的结果发送出去给别的协程，可以发送多条
            send(data)
        }
    }
    launch {
        while (isActive) {
            delay(1000)
            // 在另一个协程取数据
            println("cfx receiver: ${receiver.receive()}")
        }
    }
    delay(10000)
}