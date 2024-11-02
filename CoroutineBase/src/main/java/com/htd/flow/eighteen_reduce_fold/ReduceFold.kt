package com.htd.flow.eighteen_reduce_fold

import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-28 23:52
 *
 * Desc: Reduce、Fold
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow1 = flowOf(1, 2, 3, 4, 5)
    val list = listOf(1, 2, 3, 4, 5)
    /**
     * reduce
     * 计算所有的结果之和
     * 第一轮会把第一个元素赋值给 acc，第二个元素赋值给 i
     * 第二路把第一轮的结果给 acc，第三个元素赋值给 i
     */
    list.reduce { acc, i ->
        println("$acc - $i")
        acc + i
    }.let {
        println("list1 result: $it")
    }
    println(RepeatChar.getStr("="))

    /**
     * runningReduce
     * runningReduce 这里会返回一个新的集合对象
     * 集合元素：
     * 第一个元素就是原有集合的第一个元素
     * 第一轮计算：将第一个元素 1 赋值给 acc，第二个元素赋值给 i，计算结果赋值给新集合的第二个元素
     * 第二轮计算：将第一轮计算结果 3 赋值给 acc，第三个元素 3 赋值给 i，计算结果赋值给新集合的第三个元素
     */
    list.runningReduce { acc, i ->
        println("$acc - $i")
        acc + i
    }.let {
        println("list2 runningReduce result: $it")
    }
    println(RepeatChar.getStr("="))

    /**
     * fold
     * public inline fun <T, R> Iterable<T>.fold(initial: R, operation: (acc: R, T) -> R): R
     * 1.和 reduce 的区别是，可以提供一个初始值
     * 2.初始值的类型和返回值类型一致，初始值类型决定了返回值类型
     * 第一轮计算：acc 是初始值 100，i 为 1
     * 第二轮计算：acc 是 100 + 1，i 为 2
     * 第三轮计算：acc 是 101 + 2，i 为 3
     * 初始值可以定义其他类型
     */
    list.fold(100) { acc, i ->
        println("$acc - $i")
        acc + i
    }.let {
        println("list3 fold result: $it")
    }
    println(RepeatChar.getStr("="))
    list.fold("张三") { acc, i ->
        println("$acc - $i")
        acc + i
    }.let {
        println("list4 fold result: $it")
    }
    println(RepeatChar.getStr("="))

    /**
     * runningFold
     * 和 runningReduce 的区别是有初始值
     * 1.新的集合的元素类型和初始值类型一致
     * 2.新的集合比初始集合多一个元素
     * [张三, 张三1, 张三12, 张三123, 张三1234, 张三12345]
     * 第一轮计算：acc 为 张三，i 为 1，新集合第一个元素为 张三
     * 第二轮计算：acc 为 张三1，i 为 2， 新集合第二个元素为 张三1
     */
    list.runningFold("张三") { acc, i ->
        println("$acc - $i")
        acc + i
    }.let {
        println("list5 runningFold result: $it")
    }
    println(RepeatChar.getStr("="))

    scope.launch {
        /**
         * 1.flow 的 reduce 返回的是 flow 里面的元素最终结果
         * 2.reduce 会启动 flow 的收集 collect 过程
         */
        val result = flow1.reduce { accumulator, value ->
            accumulator + value
        }
        println("Flow1 reduce result: $result")
        println(RepeatChar.getStr("="))

        /**
         * runningReduce
         * 和 list 的 runningReduce 一样，会返回多个 flow 流，不会主动收集，所以不是挂起函数，是普通函数
         * 第一个流的结果是 flow1 的第一个元素
         * 第二个流的结果是 accumulator + flow1 的第二个元素
         */
        flow1.runningReduce { accumulator, value ->
            println("$accumulator - $value")
            accumulator + value
        }.collect {
            println("Flow2 runningReduce result: $it")
        }
        println(RepeatChar.getStr("="))

        /**
         * Flow3 runningFold result: 李四
         * 李四 - 1
         * Flow3 runningFold result: 李四1
         * 李四1 - 2
         * Flow3 runningFold result: 李四12
         * 李四12 - 3
         * Flow3 runningFold result: 李四123
         * 李四123 - 4
         * Flow3 runningFold result: 李四1234
         * 李四1234 - 5
         * Flow3 runningFold result: 李四12345
         */
        flow1.runningFold("李四") { accumulator, value ->
            println("$accumulator - $value")
            accumulator + value
        }.collect {
            println("Flow3 runningFold result: $it")
        }
        println(RepeatChar.getStr("="))

        /**
         * scan 和 runningFold 没有区别，只是函数名不一样
         */
        flow1.scan("李四") { accumulator, value ->
            println("$accumulator - $value")
            accumulator + value
        }.collect {
            println("Flow3 scan result: $it")
        }
    }
    delay(10000)
}