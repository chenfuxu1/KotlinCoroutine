package com.htd.flow.fifteen_map

import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-27 21:42
 *
 * Desc: map 转换映射
 */
@OptIn(ExperimentalCoroutinesApi::class)
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
         * 输出 2，3，null, 5, 6
         */
        flow1.map {
            if (it == 3) null else it + 1
        }.collect {
            println("Flow1: $it")
        }
        println(RepeatChar.getStr("="))

        /**
         * mapNotNull 和 map 的区别是会把 null 元素过滤掉
         * 等价于 map + filterNotNull
         */
        flow1.mapNotNull {
            if (it == 3) null else it + 1
        }.collect {
            println("Flow2: $it")
        }
        println(RepeatChar.getStr("="))

        flow1.map {
            if (it == 3) null else it + 1
        }.filterNotNull().collect {
            println("Flow3: $it")
        }
        println(RepeatChar.getStr("="))

        /**
         * mapLatest 有新数据就停止旧数据的转换
         * 如果 mapLatest 内部的数据没有处理完，新数据已经来了，那么旧数据就会丢弃, 不会处理了
         * 所以第一条、第二条都丢弃了
         * 最终输出 4
         */
        flow2.mapLatest {
            delay(120)
            it + 1
        }.collect {
            println("Flow4: $it") // 4
        }
    }
    delay(10000)
}