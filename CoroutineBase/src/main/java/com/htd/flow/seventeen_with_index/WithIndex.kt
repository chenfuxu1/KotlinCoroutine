package com.htd.flow.seventeen_with_index

import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-28 22:54
 *
 * Desc: withIndex
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow = flowOf(1, 2, 3, 4, 5)
    scope.launch {
        // 可以对 flow 的数据进行编号
        flow.withIndex().collect {
            println("Flow1: ${it.index} - ${it.value}")
        }
        println(RepeatChar.getStr("="))

        /**
         * public fun <T> Flow<T>.withIndex(): Flow<IndexedValue<T>> = flow {
         *     var index = 0
         *     collect { value ->
         *         emit(IndexedValue(checkIndexOverflow(index++), value))
         *     }
         * }
         *
         * public data class IndexedValue<out T>(public val index: Int, public val value: T)
         *
         * IndexedValue 是一个 data class
         * 也可以直接解构
         */
        flow.withIndex().collect { (index, value) ->
            println("Flow2: $index - $value")
        }
        println(RepeatChar.getStr("="))

        /**
         * collectIndexed 的区别是用于最后一步收集数据的，而 withIndex 是在中间过程中操作 flow 的
         */
        flow.collectIndexed { index, value ->
            println("Flow3: $index - $value")
        }
    }
    delay(10000)
}