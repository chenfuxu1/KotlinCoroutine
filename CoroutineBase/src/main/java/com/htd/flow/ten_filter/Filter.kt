package com.htd.flow.ten_filter

import com.htd.utils.RepeatChar
import com.htd.utils.times
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-27 11:28
 *
 * Desc: filter 操作符
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow = flowOf(1, 2, 3, 4, 5)
    val flowOrNull = flowOf(1, 2, null, 3, 4, null, 5)
    val flowIntAndString = flowOf(1, 2, 3, 4, "张飒", "荒天帝", listOf("A", "B"), listOf(1, 2))
    scope.launch {
        /**
         * 1.filter
         * public inline fun <T> Flow<T>.filter(crossinline predicate: suspend (T) -> Boolean): Flow<T> = transform { value ->
         *     if (predicate(value)) return@transform emit(value)
         * }
         * suspend (T) -> Boolean 会筛选出结果是 true 的元素，形成新的 flow1，不会影响原有 flow
         */
        flow.filter {
            it % 2 == 0
        }.collect {
            println("Flow1 筛选出偶数：$it")
        }
        println(RepeatChar.getStr("="))

        /**
         * 2.filterNot 和 filter 相反，筛选出判断条件为 false 的
         * public inline fun <T> Flow<T>.filterNot(crossinline predicate: suspend (T) -> Boolean): Flow<T> = transform { value ->
         *     if (!predicate(value)) return@transform emit(value)
         * }
         * suspend (T) -> Boolean 会筛选出结果是 false 的元素，形成新的 flow2，不会影响原有 flow
         */
        flow.filterNot {
            it % 2 == 0
        }.collect {
            println("Flow2 筛选出奇数：$it")
        }
        println(RepeatChar.getStr("="))

        /**
         * 含有空值
         */
        flowOrNull.filterNot {
            it?.rem(2) == 0
        }.collect {
            println("Flow3 筛选出奇数 (含空值)：$it")
        }
        println(RepeatChar.getStr("="))

        /**
         * 3.filterNotNull 筛选出非空值
         */
        flowOrNull.filterNotNull().filterNot {
            it % 2 == 0
        }.collect {
            println("Flow4 筛选出奇数 (不含空值)：$it")
        }
        println(RepeatChar.getStr("="))

        /**
         * 4.filterIsInstance<Type> 帅选出 Type 类型的数据流
         */
        flowIntAndString.filterIsInstance<String>().collect {
            println("Flow5 筛选出 String 类型：$it")
        }
        println(RepeatChar.getStr("="))

        flowIntAndString.filterIsInstance(String::class).collect {
            println("Flow5 筛选出 String 类型：$it")
        }

        println(RepeatChar.getStr("="))
        // 如果有泛型，是没有办法识别到具体泛型类型的，例如 List<String>、List<Int>
        flowIntAndString.filterIsInstance<List<String>>().collect {
            /**
             * Flow6 筛选出 String 类型：[A, B]
             * Flow6 筛选出 String 类型：[1, 2]
             */
            println("Flow6 筛选出 List 类型：$it")
        }
        println(RepeatChar.getStr("="))

        // 如果需要过滤 List 内部类型，只能通过 filter
        flowIntAndString.filter {
            it is List<*> && it.firstOrNull().let { item ->
                item is String
            }
        }.collect {
            println("Flow6 筛选出 List<String> 类型：$it")

        }
    }
    delay(10000)
}