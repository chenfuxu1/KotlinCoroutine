package com.htd.flow.six_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-26 10:58
 *
 * Desc: Flow --- 协程版的 sequence，提供支持挂起函数的数据流
 *       Sequence --- 提供边生产边消费的数据序列
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    /**
     * sequence 是惰性的，懒加载
     * sequence 里面是没有元素的，它只是装了一套生产逻辑，遍历时才会执行代码块，进行生产
     * 用一条才会生产一条
     *
     * public fun <T> sequence(@BuilderInference block: suspend SequenceScope<T>.() -> Unit): Sequence<T> = Sequence { iterator(block) }
     * sequence 内部只能调用 SequenceScope 内部定义的挂起函数
     * 所以，sequence 是不支持协程的，不能使用挂起函数，不提供挂起函数环境
     * 如果想用，可以使用 flow
     */
    val nums = sequence {
        /**
         * 生产一个元素
         */
        yield(1)
        yield(2)
        // yield(getNetworkData())
    }
    for (num in nums) {
        println("sequence 生产：$num" )
    }

    /**
     * list 在返回前就已经把所有的数据装进去了
     */
    val list = buildList {
        add(1)
        add(2)
    }
    for (num in list) {
        println("buildList: $num")
    }

    val flows = flow {
        /**
         * 生产一个元素
         */
        emit(1)
        emit(getNetworkData())
    }
    // 遍历
    scope.launch {
        flows.collect {
            println(it)
        }
    }

    val flows2 = flow {
        /**
         * 生产一个元素
         */
        emit(1)
        emit(getNetworkData())
    }.map { "num: $it" }
    // 遍历
    scope.launch {
        flows2.collect {
            println(it)
        }
    }

    delay(10000)
}

private suspend fun getNetworkData(): Int {
    return 1
}