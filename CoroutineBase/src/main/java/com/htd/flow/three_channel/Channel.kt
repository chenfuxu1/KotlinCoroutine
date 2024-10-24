package com.htd.flow.three_channel

import com.htd.coroutinebase.common.api.GithubApi
import com.htd.coroutinebase.common.bean.Contributor
import com.htd.coroutinebase.common.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-24 0:13
 *
 * Desc:
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val github = RetrofitService.getRetrofit().create(GithubApi::class.java)

    /**
     * 用于不同协程之间传递数据的通道
     * 任何协程都可以使用它的 send 方法发送数据，任何协程都可以使用 receive 方法接收数据
     * Channel 就是一个挂起式的队列
     * 当元素满了再去 send 数据，或者当数据空了取 receive 数据的时候，会把写成挂起，不会阻塞协程
     */
    val channel = Channel<List<Contributor>>()
    scope.launch {
        channel.send(github.coroutineContributors("square", "retrofit"))
    }

    scope.launch {
        val data = channel.receive()
        println("数据：$data")
    }

    val receiver = scope.produce {
        val data = github.coroutineContributors("square", "okhttp")
        // 将请求的结果发送出去给别的协程，可以发送多条
        send(data)
    }
    launch {
        // 在另一个协程取数据
        println("cfx receiver: ${receiver.receive()}")

    }

    delay(10000)
}