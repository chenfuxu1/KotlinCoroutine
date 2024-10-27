package com.htd.flow.eight_flow_build

import com.htd.coroutinebase.common.api.GithubApi
import com.htd.coroutinebase.common.bean.Contributor
import com.htd.coroutinebase.common.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CancellationException
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-26 17:34
 *
 * Desc: Flow build
 */
fun main() = runBlocking {
    val github = RetrofitService.getRetrofit().create(GithubApi::class.java)
    val flow1 = flowOf(1, 2, 3)
    // 把 list 中的元素依次发送的 flow
    val flow2 = listOf(4, 5, 6).asFlow()
    val flow3 = setOf(7, 8, 9).asFlow()
    val flow4 = sequenceOf(10, 11, 12).asFlow()
    val channel = Channel<Int>()
    /**
     * 由 channel 提供数 据的 flow
     */
    val flow5 = channel.consumeAsFlow()
    val flow6 = channel.receiveAsFlow()
    val scope = CoroutineScope(EmptyCoroutineContext)
    scope.launch {
        flow1.collect {
            println("Flow1: $it")
        }
    }

    /**
     * channel 发送的数据在不同协程中 collect，数据会被瓜分，不是独立的
     * 所以各自会漏掉一半的事件
     */
    // scope.launch {
    //     flow6.collect {
    //         println("Flow6 - 1: $it")
    //     }
    // }
    // scope.launch {
    //     flow6.collect {
    //         println("Flow6 - 2: $it")
    //     }
    // }
    // channel.send(1)
    // channel.send(2)
    // channel.send(3)
    // channel.send(4)

    /**
     * receiveAsFlow 和 consumeAsFlow 区别
     * consumeAsFlow 只能调用一次 collect，第二次就会抛异常
     * 因为通过 channel 提供的数据，通常也只能 collect 一次
     */
    // scope.launch {
    //     flow5.collect {
    //         println("Flow5 - 1: $it")
    //     }
    // }
    // /**
    //  * Caused by: java.lang.IllegalStateException: ReceiveChannel.consumeAsFlow can be collected just once
    //  * at kotlinx.coroutines.flow.ChannelAsFlow.markConsumed
    //  */
    // scope.launch {
    //     flow5.collect {
    //         println("Flow5 - 2: $it")
    //     }
    // }
    // channel.send(1)
    // channel.send(2)
    // channel.send(3)
    // channel.send(4)

    /**
     * channelFlow
     * channelFlow 创建出来的 flow 直到 collect 才会创建 channel
     * 所以可以多次 collect，多次 collect 的数据是独立生产流程的，是 code
     */
    val flow7 = channelFlow {
        // 可以使用挂起函数
        send(1)
        send(2)
        send(3)
        send(4)
    }
    scope.launch {
        flow7.collect {
            println("Flow7 - 1: $it")
        }
    }
    scope.launch {
        flow7.collect {
            println("Flow7 - 2: $it")
        }
    }

    /**
     * 等价写法
     * flow {
     *     emit(1)
     * }
     *
     * channelFlow 可以在内部子协程发送数据（可以跨协程）
     * flow 内部启动不了子协程，会抛异常，因为 flow 没有子协程
     */
    val flow8 = channelFlow {
        send(1)
        launch {
            delay(100)
            send(2)
        }
    }
    scope.launch {
        flow8.collect {
            println("Flow8 : $it")
        }
    }

    val flow9 = flow {
        emit(1)
        // 启动不了子协程
        // launch {
        //     emit(2)
        // }
    }
    scope.launch {
        flow9.collect {
            println("Flow9 : $it")
        }
    }

    // channelFlow 内部是 produce 函数，是一个协程
    val flow10 = channelFlow {
        github.contributors("square", "retrofit").enqueue(object: Callback<List<Contributor>> {
            override fun onResponse(
                call: Call<List<Contributor>>,
                response: Response<List<Contributor>>
            ) {
                trySend(response.body()!!)
                // 数据发送完毕，关闭 channel
                close()
            }

            override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
                cancel(CancellationException(t.message))
            }

        })
        awaitClose() // 会将协程挂起，不让协程结束，否则协程结束了，数据就没法发送了
    }
    scope.launch {
        flow10.collect {
            println("Flow10 : $it")
        }
    }

    /**
     * callbackFlow 内部就是一个 channelFlow
     * 区别就是：callbackFlow 需要强制 awaitClose，否则会抛异常
     */
    val flow11 = callbackFlow {
        github.contributors("square", "retrofit").enqueue(object: Callback<List<Contributor>> {
            override fun onResponse(
                call: Call<List<Contributor>>,
                response: Response<List<Contributor>>
            ) {
                trySend(response.body()!!)
                // 数据发送完毕，关闭 channel
                close()
            }

            override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
                cancel(CancellationException(t.message))
            }

        })
        awaitClose() // 会将协程挂起，不让协程结束，否则协程结束了，数据就没法发送了
    }
    scope.launch {
        flow11.collect {
            println("Flow11 : $it")
        }
    }

    /**
     * 可以提供单次的回调函数，本身就是挂起函数，如果需要数据流，连续的回调，那就需要 callbackFlow
     */
    // suspendCancellableCoroutine {
    //
    // }

    delay(10000)
}