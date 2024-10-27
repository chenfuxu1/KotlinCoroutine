package com.htd.flow.seven_flow_use_case

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-26 16:13
 *
 * Desc: Flow 的使用
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val numsFlow = flow {
        emit(1)
        delay(100)
        emit(2)
    }
    // 都会接收到数据
    scope.launch {
        numsFlow.collect {
            println("A 收集的数据: $it")
        }
    }
    // 都会接收到数据
    scope.launch {
        delay(50)
        numsFlow.collect {
            println("B 收集的数据: $it")
        }
    }
    // ================================
    /**
     * 上述的代码等价写法：
     * 将 flow 代码块拿到 collect 处进行执行
     * 本质：
     * flow 处提供数据的生产逻辑
     * 每个 collect 处都会执行一遍 flow 内部的生产逻辑
     *
     * Channel hot：和 receive 无关，只有 send 都会发送
     * Flow code：在 collect 才会生产，每次 collect 都独立生产，互不干扰
     */
    // scope.launch {
    //     println("A 收集的数据: $emit(1)")
    //     delay(100)
    //     println("A 收集的数据: $emit(2)")
    // }
    // scope.launch {
    //     delay(50)
    //     println("B 收集的数据: $emit(1)")
    //     delay(100)
    //     println("B 收集的数据: $emit(2)")
    // }

    scope.launch {
        showWeather(weatherFlow)

        weatherFlow.collect {

        }
        /**
         * 会卡住下面的代码
         * 因此可以将 collect 放在单独的协程里面
         */
        println("==================")
    }
    delay(10000)
}

private suspend fun getWeather() = withContext(Dispatchers.IO) {
    "Sunny"
}

/**
 * 一分钟生产一条天气数据
 * 持续提供数据的形式
 */
val weatherFlow = flow {
    while (true) {
        emit(getWeather())
        delay(60000)
    }
}

// 显示到界面
private suspend fun showWeather(flow: Flow<String>) {
    flow.collect {
        // 显示界面
        println("显示界面：$it")
    }
}