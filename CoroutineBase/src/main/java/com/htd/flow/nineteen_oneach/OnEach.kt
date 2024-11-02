package com.htd.flow.nineteen_oneach

import com.htd.utils.Logit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-30 23:42
 *
 * Desc: onEach
 * onEach
 * 1.可以调用多次
 * 2.onEach 只是针对调用 onEach 的 flow 流进行监听处理
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow1 = flowOf(1, 2, 3, 4, 5)
    scope.launch {
        flow1.onEach {
            Logit.d("onEach 1: $it")
        }.onEach {
            Logit.d("onEach 2: $it")
        }.filter {
            it % 2 == 0
        }.onEach {
            Logit.d("onEach 3: $it")
        }.collect {
            Logit.d("collect: $it")
        }
    }
    delay(10000)
}