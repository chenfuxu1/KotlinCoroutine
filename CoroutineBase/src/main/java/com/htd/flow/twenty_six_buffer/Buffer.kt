package com.htd.flow.twenty_six_buffer

import com.htd.utils.Logit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-11-02 18:21
 *
 * Desc: Buffer
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val startTime = System.currentTimeMillis()

    /**
     * buffer 大小会合并
     * 1.右边的 buffer 没设置缓冲溢出策略，默认的 suspend，会左右相加为 3，缓冲策略会延用左边 buffer 的缓冲策略
     * 2.如果右边的设置不是 suspend，那右边的 buffer 会完全覆盖左边的 buffer
     */
    val flow1 = flow {
        for (i in 1 .. 5) {
            emit(i)
            Logit.d("Emitted: $i - ${System.currentTimeMillis() - startTime} ms")
        }
    }.buffer(1).flowOn(Dispatchers.IO).buffer(2)

    /**
     * 便捷函数 conflate() 缓冲最新的数据
     * 等价于 buffer(CONFLATED)
     */
    val flow2 = flow {
        for (i in 1 .. 5) {
            emit(i)
            Logit.d("Emitted: $i - ${System.currentTimeMillis() - startTime} ms")
        }
    }.conflate()

    scope.launch {
        flow1.collect {
            delay(1000)
            Logit.d("Collect Data: $it")
        }
    }
    delay(10000)
}