package com.htd.flow.nine_collect

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-26 23:08
 *
 * Desc: Collect
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)

    // val flow1 = flow {
    //     launch(Dispatchers.IO) {
    //         delay(1000)
    //         emit(2)
    //     }
    //     delay(500)
    //     emit(1)
    // }
    // /**
    //  * 假设业务逻辑是更新界面
    //  */
    // scope.launch(Dispatchers.Main) {
    //     // flow1.collect {
    //     //     println("flow 更新界面 : $it")
    //     // }
    //
    //     /**
    //      * 如果 flow 可以切协程发送数据
    //      * 等价写法
    //      * 那么将会出现问题
    //      */
    //     // launch(Dispatchers.IO) {
    //     //     delay(1000)
    //     //     println("flow 更新界面: $emit(2)")
    //     // }
    //     // delay(500)
    //     // println("flow 更新界面: $emit(1)")
    // }

    val flow2 = flow {
        emit(1)
    }

    /**
     * public fun <T> Flow<T>.launchIn(scope: CoroutineScope): Job = scope.launch {
     *     collect() // tail-call
     * }
     * launchIn 把协程的启动和 Flow 的 collect 合在一起了
     */
    val flow3 = flow2.launchIn(scope) // 使用 scope 启动一个协程，调用 flow2.collect
    // flow3 的等价写法
    scope.launch {
        flow2.collect {}
    }

    flow2.onEach {
        println(it)
    }.launchIn(scope)
    // 等价写法
    scope.launch {
        flow2.collect {
            println(it)
        }
    }

    scope.launch {
        // 可以给 collect 的数据编上序号 index，第一个是 0
        flow2.collectIndexed { index, value ->

        }

        // mapLatest(action).buffer(0).collect()
        flow2.collectLatest {

        }
    }
    delay(10000)
}