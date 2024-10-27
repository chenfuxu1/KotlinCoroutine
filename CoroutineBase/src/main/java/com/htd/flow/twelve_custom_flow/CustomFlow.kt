package com.htd.flow.twelve_custom_flow

import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-27 17:34
 *
 * Desc:
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow1 = flowOf(1, 2, 3)
    scope.launch {
        // flow1.customOperator().collect {
        //
        // }
        // 上述等价写法
        flow1.customOperator().collect(object : FlowCollector<Int> {
            override suspend fun emit(value: Int) {
                println("Flow1: $value")
            }

        })
        println(RepeatChar.getStr("="))
        flow1.double().collect {
            println("Flow2: $it")
        }
    }
    delay(10000)
}

/**
 * 自定义操作符
 */
fun <T> Flow<T>.customOperator(): Flow<T> = flow {
    // 将上游的数据发送到下游去
    this@customOperator.collect {
        emit(it)
    }
}

/**
 * 每个元素发送两遍
 */
fun <T> Flow<T>.double(): Flow<T> = flow {
    // 将上游的数据发送到下游去
    // this@double.collect {
    //     emit(it)
    //     emit(it)
    // }
    collect {
        emit(it)
        emit(it)
    }
}
