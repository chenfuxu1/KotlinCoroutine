package com.htd.flow.fourteen_drop_take

import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-27 21:27
 *
 * Desc: drop take 操作符
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow1 = flowOf(1, 2, 3, 4, 5)
    val flow2 = flowOf(1, 2, 3, 4, 5, 1)
    // scope.launch {
    //     /**
    //      * 会将第一个、第二个元素丢弃
    //      */
    //     flow1.drop(2).collect {
    //         println("flow1: $it") // 3, 4, 5
    //     }
    //     println(RepeatChar.getStr("="))
    // }

    scope.launch {
        /**
         * dropWhile
         * 会将第一个、第二个元素丢弃
         * 会从前往后遍历，只要开始不满足条件了，后续所有元素都不检查了
         */
        flow2.dropWhile {
            it < 3
        }.collect {
            println("flow2: $it") // 3, 4, 5, 1
        }
        println(RepeatChar.getStr("="))

        /**
         * take 只取流的前 n 个元素，后续的都不要了
         */
        flow2.take(3).collect {
            println("flow3: $it") // 1, 2, 3
        }
        println(RepeatChar.getStr("="))

        /**
         * takeWhile 只取满足条件的前 n 个元素，只要有一个不满足，后续的元素都不要了
         */
        flow2.takeWhile {
            it < 3
        }.collect {
            println("flow4: $it") // 1, 2
        }
    }
    delay(10000)
}