package com.htd.flow.eleven_distinct_until_changed

import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-27 17:17
 *
 * Desc: distinctUntilChanged
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow1 = flowOf(1, 2, 2, 3, 3, 3, 1)
    val flow2 = flowOf("zhangsan", "ZhangSan", "LiSi")
    scope.launch {
        /**
         * 判断使用的是 kotlin 的 == 号，比较内容是否相等
         * 如果是相邻的元素是相等的，就过滤掉
         * 1, 2, 3, 1
         */
        flow1.distinctUntilChanged().collect {
            println("flow1: $it")
        }
        println(RepeatChar.getStr("="))

        /**
         * public fun <T> Flow<T>.distinctUntilChanged(areEquivalent: (old: T, new: T) -> Boolean): Flow<T>
         * 自定义相同元素的规则，这里忽略大小写
         * flow2: zhangsan
         * flow2: LiSi
         */
        flow2.distinctUntilChanged { a, b ->
            a.uppercase() == b.uppercase()
        }.collect {
            println("flow2: $it")
        }
        println(RepeatChar.getStr("="))

        /**
         * distinctUntilChangedBy 只会转换 key，进行比较，不会影响到 value
         * flow3: zhangsan
         * flow3: LiSi
         */
        flow2.distinctUntilChangedBy {
            it.uppercase()
        }.collect {
            println("flow3: $it")
        }

    }
    delay(10000)
}