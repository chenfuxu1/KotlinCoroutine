package com.htd.flow.four_channel_api

import com.htd.coroutinebase.common.api.GithubApi
import com.htd.coroutinebase.common.bean.Contributor
import com.htd.coroutinebase.common.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-24 23:52
 *
 * Desc: Channel 的 API
 */
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val github = RetrofitService.getRetrofit().create(GithubApi::class.java)

    /**
     * public fun <E> Channel(
     *     capacity: Int = RENDEZVOUS,
     *     onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
     *     onUndeliveredElement: ((E) -> Unit)? = null
     * ): Channel<E>
     *
     * channel 缓冲区的大小设置为 8
     * 当 send 7 次前，都不会挂起，到第八次时，队列已满，会把协程挂起
     * 当调用 receive 时，会把队列头的数据取走，这时又会继续 send
     *
     * public const val RENDEZVOUS: Int = 0 不填就是 0
     * public const val CONFLATED: Int = -1 会把队列大小设置为 1，同时缓冲策略设置为 DROP_OLDEST，所以每次都是最新的数据
     * public const val UNLIMITED: Int = Int.MAX_VALUE
     * public const val BUFFERED: Int = -2 默认是 64
     *
     * 1.capacity 默认队列长度是 0
     * 2.onBufferOverflow 默认是队列满了，溢出了，就挂起当前协程
     * SUSPEND --- 溢出了挂起协程 DROP_OLDEST --- 溢出了将队首的元素丢弃 DROP_LATEST --- 溢出了将队尾的元素丢弃
     */
    val channel = Channel<List<Contributor>>(8)
    // 等价写法：val channel2 = Channel<List<Contributor>>(1, BufferOverflow.DROP_OLDEST)
    val channel2 = Channel<List<Contributor>>(CONFLATED)
    scope.launch {
        channel.send(github.coroutineContributors("square", "retrofit"))
        channel.close()
    }

    scope.launch {
        /**
         * 默认情况下，队列长度为 0，没有缓冲区
         * for 循环中，取每个 data 都是挂起式的
         * 可以设置缓冲区 capacity
         */
        for (data  in channel) {
            println("Contributor: $data")
        }
    }
    delay(10000)
}
