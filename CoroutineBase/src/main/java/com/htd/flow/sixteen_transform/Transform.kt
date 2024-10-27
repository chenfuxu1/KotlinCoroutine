package com.htd.flow.sixteen_transform

import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-27 22:02
 *
 * Desc:
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow1 = flowOf(1, 2, 3, 4, 5)
    val flow2 = flow {
        delay(100)
        emit(1)
        delay(100)
        emit(2)
        delay(100)
        emit(3)
    }
    scope.launch {
        /**
         * public inline fun <T, R> Flow<T>.map(crossinline transform: suspend (value: T) -> R): Flow<R> = transform { value ->
         *     return@transform emit(transform(value))
         * }
         * map 的底层使用的也是 transform 函数
         */
        // flow1.map {  }

        /**
         * 构造一个元素是多少，就发送多少条哈哈哈
         */
        flow1.transform {
            if (it > 0) {
                repeat(it) { _ ->
                    emit("$it - 哈哈哈")
                }
            }
        }.collect {
            println("Flow1: $it")
        }
        println(RepeatChar.getStr("="))

        /**
         * transformWhile 是有返回值的，和 takeWhile 一样
         * 如果是 true，就继续，遇到 false 就直接掐断
         */
        flow1.transformWhile {
            if (it > 0) {
                repeat(it) { _ ->
                    emit("$it - 哈哈哈")
                }
            }
            it < 3
        }.collect {
            println("Flow2: $it")
        }
        println(RepeatChar.getStr("="))

        /**
         * transformLatest 就是 mapLatest 的底层
         * 当 transformLatest 内部没有执行完有新的数据，就终止对旧的数据处理
         */
        flow2.transformLatest {
            delay(50)
            emit("$it - start")
            delay(100)
            emit("$it - end")
        }.collect {
            println("Flow3: $it")
        }
    }
    delay(10000)
}