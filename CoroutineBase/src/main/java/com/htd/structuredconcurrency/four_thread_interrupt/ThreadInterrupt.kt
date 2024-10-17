package com.htd.structuredconcurrency.four_thread_interrupt

import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-10 23:41
 *
 * Desc: 线程的安全结束 interrupt
 */
fun main() = runBlocking<Unit> {
    val thread = object : Thread() {
        override fun run() {
            println("cfx Thread I`m running")
            try {
                Thread.sleep(300)
            } catch (e: InterruptedException) {
                println("cfx InterruptedException")
                return
            }
            var count = 0
            while (true) {
                if (isInterrupted) {
                    return
                }
                count++
                if (count % 100_000_000 == 0) {
                    println(count)
                }
                if (count % 1_000_000_000 == 0) {
                    break
                }

            }
            println("cfx Thread I`m done")
        }

    }.apply {
        start()
    }
    Thread.sleep(200)
    thread.interrupt()
}