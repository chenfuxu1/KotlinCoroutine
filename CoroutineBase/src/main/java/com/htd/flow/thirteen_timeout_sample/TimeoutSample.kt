package com.htd.flow.thirteen_timeout_sample

import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-27 18:18
 *
 * Desc: timeout、sample
 */
@OptIn(FlowPreview::class)
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    // val flow1 = flowOf(1, 2, 3)
    // scope.launch {
    //     try {
    //         flow1.timeout(5.seconds).collect {
    //             // 正常收集
    //         }
    //     } catch (e: TimeoutCancellationException) {
    //         // 超时
    //     }
    // }

    // val flow2 = flow {
    //     delay(500)
    //     emit(1)
    //     delay(800)
    //     emit(2)
    //     delay(2000)
    //     emit(3)
    // }
    // scope.launch {
    //     // 第三条元素超时了，无法打印
    //     flow2.timeout(1.seconds).collect {
    //         println("Flow2: $it") // 1, 2
    //     }
    //     println(RepeatChar.getStr("="))
    // }

    val flow3 = flow {
        delay(23)
        delay(500)
        emit(1)
        delay(800)
        emit(2)
        delay(900)
        emit(3)
    }
    scope.launch {
        /**
         * 使用时间间隔进行采样
         * 固定周期采样，给定一个时间周期，保留周期内最后发出的值，其他的值将被丢弃
         * sample 是一个时间窗口，每隔 1s 去取一下数据
         * 500ms - 1  1300ms - 2  2200ms - 3
         * 第一次掐表是第 1s，将元素 1 放到下游
         * 第二次掐表是第 2s，将元素 2 放到下游
         * 没有第三次采样了，应为程序在 2.2s 就执行完了
         *
         * 如果一个周期内发送了多个数据，之前的数据会被过滤掉，只会保留最新的
         */
        // flow3.sample(1.seconds).collect {
        //     println("Flow3: $it") // 1, 2
        // }

        /**
         * 去抖动
         * 遇到新数据先不着急往下游发送，等待一个时间窗口超时了才往下发送
         * 并且随时根据新来的数据重置这个窗口
         */
        flow3.debounce(1.seconds).collect {
            println("Flow4: $it") // 1, 2
        }

    }

    delay(10000)
}

private fun <T> Flow<T>.throttle(timeWindow: Duration): Flow<T> = flow {
    var lastTime = 0L
    collect {
        // 超时了，才往下发送数据
        if (System.currentTimeMillis() - lastTime > timeWindow.inWholeSeconds) {
            emit(it)
            lastTime = System.currentTimeMillis()
        }
    }
}