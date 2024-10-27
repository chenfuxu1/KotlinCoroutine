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
     *     onUndeliveredElement: ((E) -> Unit)? = null // 可以设置回调，对未接收的资源进行关闭，防止泄露
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
        /**
         * 发送端关闭：
         * 在 close 之后，isClosedForSend 参数会置为 true
         * 这个时候如果继续 send，会抛出 ClosedSendChannelException 异常
         * 但是对于已经处于挂起状态的，或者在缓冲池中的数据是不受影响是，还可以正常接收
         *
         * 当所有的数据都接收完了，isClosedForReceive 参数会置为 true
         * 这个时候如果继续 receive，会抛出 ClosedReceiveChannelException 异常
         *
         * 接收端关闭：
         * channel,cancel()
         * 会直接将 isClosedForSend、isClosedForReceive 都置为 true，所以会抛 CancellationException
         * 缓冲区的数据、send 的挂起的数据全都没用了
         */
        channel.close()
        /**
         * 后续 send、receive 抛出的都是自定义的异常
         */
        channel.close(IllegalStateException("自定义异常"))

        /**
         * trySend tryReceive 是非挂起函数，立即执行, 不等待，不阻塞线程，瞬时返回，没有数据返回的就是失败的结果
         */
        channel.trySend(github.coroutineContributors("square", "retrofit"))
        channel.tryReceive()
    }

    scope.launch {
        /**
         * 默认情况下，队列长度为 0，没有缓冲区
         * for 循环中，取每个 data 都是挂起式的
         * 可以设置缓冲区 capacity
         *
         * 对于 for 循环中，当 close 之后
         * 也会把剩下的几个已经发送但未被接收的元素接收之后，结束 for 循环
         * 然后再往下执行代码
         */
        for (data  in channel) {
            println("Contributor: $data")
        }
    }
    delay(10000)
}
