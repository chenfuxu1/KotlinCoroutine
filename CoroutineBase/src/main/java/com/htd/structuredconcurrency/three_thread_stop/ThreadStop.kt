package com.htd.structuredconcurrency.three_thread_stop

import kotlinx.coroutines.runBlocking

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-10 23:31
 *
 * Desc: 停止线程
 */
fun main() = runBlocking<Unit> {
    val aa: Thread = Thread {
        println("cfx Thread I`m running")
        Thread.sleep(2000)
        println("cfx Thread I`m done")
    }
    aa.start()
    Thread.sleep(100)
    aa.stop()
}