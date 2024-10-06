package com.htd.coroutinebase.eight_delay

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-05 10:08
 *
 * Desc: 协程是轻量级的线程
 */
fun main() {
    // testCoroutine()

    // testThread()

    equalJava()

}

/**
 * runBlocking 会提供一个单线程的 ContinuationInterceptor
 * 所以内部启用的 50000 个协程都运行在单线程上
 * 这里也就是 main 函数所在的线程
 */
private fun testCoroutine() {
    runBlocking {
        repeat(50_000) {
            launch {
                delay(5000L)
                print("*")
            }
        }
    }
}

/**
 * 将 launch 换成了 thread 这里会创建 50000 个线程
 * 因此，一个线程肯定比 50000 个线程省资源
 * 但 testCoroutine 的代码写法不等价与 java 中下面的写法
 */
private fun testThread() {
    runBlocking {
        repeat(50_000) {
            thread {
                Thread.sleep(5000L)
                print("*")
            }
        }
    }

    // 协程写法
    // val dispatcher = newFixedThreadPoolContext(50_000, "50K")
    // repeat(50_000) {
    //     launch(dispatcher) {
    //         delay(5000L)
    //         print("*")
    //     }
    // }
}

/**
 * 等价于上面协程的写法
 */
private fun equalJava() {
    // 创建单线程的线程池
    val executor = Executors.newSingleThreadScheduledExecutor()
    repeat(50_000) {
        executor.schedule( {
            print("*")
        },5, TimeUnit.SECONDS)
    }

}