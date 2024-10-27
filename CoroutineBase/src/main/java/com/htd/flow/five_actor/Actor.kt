package com.htd.flow.five_actor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-26 10:31
 *
 * Desc: Actor
 */
@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    // val channel = Channel<Int>()
    // // 发送数据
    // scope.launch {
    //     for (num in 1 .. 100) {
    //         channel.send(num)
    //         delay(1000)
    //     }
    // }
    // // 接收数据
    // scope.launch {
    //     for (num in channel) {
    //         println("Number: $num")
    //     }
    // }

    /**
     * produce 是将 Channel 的创建和发送的函数融合在一起
     */
    // val receiver = scope.produce {
    //     for (num in 1 .. 100) {
    //         send(num)
    //         delay(1000)
    //     }
    // }
    // scope.launch {
    //     for (num in receiver) {
    //         println("receive: $num")
    //     }
    // }

    /**
     * produce 的反义词
     * actor 函数，是将 Channel 的创建和接收的函数融合在一起
     */
    val sender = scope.actor<Int> {
        for (num in this) {
            println("receive: $num")
        }
    }
    scope.launch {
        for (num in 1 .. 100) {
            sender.send(num)
            delay(1000)
        }
    }
    delay(10000)
}