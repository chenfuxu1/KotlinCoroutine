package com.htd.coroutinescope.four_coroutine_scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-19 20:41
 *
 * Desc: CoroutineScope 和 SupervisorScope
 */
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    scope.launch {
        val startTime = System.currentTimeMillis()
        /**
         * 和 launch 的相同之处
         * 1.都沿用了 scope.launch 的 coroutineContext
         * 2.都启动了新的子协程
         *
         * 区别：
         * 1.coroutineScope { } 是没有参数的，launch 可以传参，定制父 jib，coroutineScope 相当于 launch 的什么参数都不填
         * 2.coroutineScope 是挂起函数，是串行的，会等待 coroutineScope 执行完毕才会往下走，而 launch 不是，launch 是并行的
         * 3.coroutineScope 内部如果再有子协程，也会等待里面的子协程，子子协程执行完毕后，才会往下执行
         * 4.coroutineScope 的表达式是有返回值的，lambda 表达式最后一行，launch 没有返回值
         */
        coroutineScope {
            println("cfx coroutineContext.job.parent: ${coroutineContext.job.parent}")
            delay(2000)
            launch {
                delay(1000)
                println("cfx duration with grand child scope: ${System.currentTimeMillis() - startTime}")
            }
            println("cfx duration with coroutineScope: ${System.currentTimeMillis() - startTime}")

        }
        val name = coroutineScope {
            val deferred1 = async {
                "张三"
            }
            val deferred2 = async {
                "李四"
            }
            deferred1.await() + deferred2.await()
        }
        println("cfx name: $name")

        /**
         * 在 coroutineScope 内部的子协程发生了异常，可以在 coroutineScope 捕获到，而且不会影响到其他协程的取消
         * 原因：coroutineScope 是挂起函数，是串行的
         */
        val name2 = try {
            coroutineScope {
                val deferred1 = async {
                    "张三"
                }
                val deferred2 = async {
                    throw RuntimeException("手动抛出异常")
                }
                deferred1.await() + deferred2.await()
            }
        } catch (e: Exception) {
            println("cfx Exception: $e")
        }
        println("cfx name2: $name2")
        launch {
            println("cfx launch: ${coroutineContext.job.parent}")
            delay(1000)
            println("cfx duration with launch: ${System.currentTimeMillis() - startTime}")

        }
        println("cfx duration with scope.launch: ${System.currentTimeMillis() - startTime}")

        /**
         * supervisorScope 和 coroutineScope 大体功能相似
         * supervisorScope 提供了一个类似于 SupervisorJob 的 job
         */
        supervisorScope {

        }

    }

    delay(10000)
}

/**
 * coroutineScope 的常用场景
 * 1.用来在挂起函数中提供一个 CoroutineScope 的上下文
 * 2.场景2：将一块独立的业务封装起来，可以捕获异常并进行处理修复，不影响其他逻辑
 */
private suspend fun test() = coroutineScope {
    // 启动协程
    launch {
        // 业务代码
    }
}