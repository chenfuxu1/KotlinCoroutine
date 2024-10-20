package com.htd.coroutinescope.six_coroutine_name

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-20 17:25
 *
 * Desc: CoroutineName
 */
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val name = CoroutineName("荒天帝")
    scope.launch(name) {
        // 协程的 coroutineContext 中获取设置的 coroutineName
        // cfx coroutineContext[CoroutineName]: CoroutineName(荒天帝)
        println("cfx coroutineContext[CoroutineName]: ${coroutineContext[CoroutineName]}")
    }
    // scope2 启动的所有协程都可以获取到 CoroutineName
    val scope2 = CoroutineScope(name)

    // 混合了 CoroutineDispatcher 和 CoroutineName 的综合 CoroutineContext
    val scope3 = CoroutineScope(Dispatchers.IO + name)

    delay(10000)
}