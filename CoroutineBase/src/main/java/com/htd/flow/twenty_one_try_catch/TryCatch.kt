package com.htd.flow.twenty_one_try_catch

import com.htd.coroutinebase.common.api.GithubApi
import com.htd.coroutinebase.common.retrofit.RetrofitService
import com.htd.utils.Logit
import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeoutException
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.random.Random

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-31 23:39
 *
 * Desc: try / catch
 */
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val github = RetrofitService.getRetrofit().create(GithubApi::class.java)
    val flow1 = flowOf(1, 2, 3, 4, 5)
    val flow2 = flow {
        try {
            for (i in 1..5) {
                // 数据库读数据
                // 网络请求

                /**
                 * 所以这里不能把 emit 包裹在异常内部，会导致异常被吞掉
                 * 或者捕获后继续向外抛出
                 */
                emit(i)
            }
        } catch (e: Exception) {
            Logit.d("Flow2 emit: $e")
            throw e // 继续抛，在 flow2.collect 收集的地方也能接收了
        }
    }

    /**
     * 1.下游 collect 大括号里抛出的异常会经过这里的 map 吗？ --- 不会，只会经过 map 内封装的 emit，不会经过 map 的大括号逻辑代码
     * 2.map 里面的代码块如果发生异常，这个异常会走向什么地方？---
     *
     * map 函数：每次在上游数据到来的时候，经过自生 {} 的逻辑处理后，再调用 emit 发送到下游去
     *
     * 如果是 transform 函数，是会经过的，因为它是显示的调用了 emit 函数来发送的，而 map、onEach 等是隐式的封装起来了
     * 关键就是 emit 的显示调用
     */
    val flow3 = flow {
        try {
            for (i in 1..5) {
                // 数据库读数据
                // 网络请求

                /**
                 * 所以这里不能把 emit 包裹在异常内部，会导致异常被吞掉
                 * 或者捕获后继续向外抛出
                 */
                emit(i)
            }
        } catch (e: Exception) {
            Logit.d("Flow2 emit: $e")
            throw e // 继续抛，在 flow2.collect 收集的地方也能接收了
        }
    }.map {
        // 先转换，再 emit
        it
        throw TimeoutException("Timeout")
        /**
         * 如果 map 内部发生异常，先抛到 map 的上游 flow3，然后再到 flow3.collect 所在位置
         */
    }
    scope.launch {
        // 1.flow 内部进行 catch
        // flow1.collect {
        //     val randomInt = Random.nextInt(2)
        //     val list = try {
        //         val result = github.coroutineContributors("square", "retrofit")
        //         if (randomInt == 1) {
        //             throw TimeoutException("Network Error")
        //         }
        //         result
        //     } catch (e: TimeoutException) {
        //         "Network Error"
        //     }.let {
        //         Logit.d(it)
        //     }
        // }

        /**
         * 2.flow 外部 catch，只要有异常，flow 就结束了
         */
        try {
            flow1.collect {
                val randomInt = Random.nextInt(2)
                val result = github.coroutineContributors("square", "retrofit")
                if (randomInt == 1) {
                    throw TimeoutException("Network Error")
                }
                Logit.d(result)
            }
        } catch (e: TimeoutException) {
            Logit.d("Exception: $e")
        }
        Logit.d(RepeatChar.getStr("="))

        /**
         * 3.flow2 使用 emit 发射，并且 emit 加 try / catch
         * 可以发现此处的 try / catch 未捕获住，而是在 emit 的地方捕获了
         */
        try {
            // flow2.collect(object : FlowCollector<Int> {
            //     override suspend fun emit(value: Int) {
            //     }
            // })

            /**
             * collect 后面的大括号就相当于 emit 函数，会在 flow 发送 emit 的时候调用
             * 这里有 5 条数据，所以大括号会执行五次
             *
             * 函数调用顺序：
             * 1.flow2.collect
             * 2.flow2 定义的地方
             * 3.flow2 调用 emit 函数的地方
             * 4.flow2.collect 的大括号，也就是回调 emit 的地方
             *
             * 如果大括号内部发生异常，会反向抛到
             * 1.抛到 flow2 调用 emit 函数的地方 - 先拿到异常
             * 2.抛到 flow2 定义的地方
             * 3.抛到 flow2.collect 所在代码块的地方 - 后拿到异常
             */
            flow2.collect {
                val randomInt = Random.nextInt(2)
                val result = github.coroutineContributors("square", "retrofit")
                if (randomInt == 1) {
                    throw TimeoutException("Network Error")
                }
                Logit.d(result)
            }
        } catch (e: TimeoutException) {
            Logit.d("Exception: $e")
        }
        Logit.d(RepeatChar.getStr("="))

        /**
         * 4.flow3 增加了 map 函数
         * 所以 collect 收集的时候，启动的是 map 的生产过程，而 map 只是一个操作符，会导致 map 的上游去启动生产过程。即 flow3 开始生产
         *
         * 在 flow3.collect 大括号内部发生异常时
         * 1.首先抛到 map 函数内部的 emit 函数（封装起来了，所以直接到第二步了，不会经过 map 大括号的自定义逻辑代码）
         * 2.抛到 map 上游 flow3 的 emit 函数
         */
        try {
            flow3.collect {
                val randomInt = Random.nextInt(2)
                val result = github.coroutineContributors("square", "retrofit")
                if (randomInt == 1) {
                    throw TimeoutException("Network Error")
                }
                Logit.d(result)
            }
        } catch (e: TimeoutException) {
            Logit.d("Exception: $e")
        }
    }
    delay(10000)
}

// 会按照调用的顺序，一直反方向抛到 fun1
private fun fun1() {
    fun2()
}

private fun fun2() {
    fun3()
}

private fun fun3() {
    throw NullPointerException("空指针异常")
}