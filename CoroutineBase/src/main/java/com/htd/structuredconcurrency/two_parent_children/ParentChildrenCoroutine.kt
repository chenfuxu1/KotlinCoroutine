package com.htd.structuredconcurrency.two_parent_children

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-07 22:41
 *
 * Desc: 父子协程
 * 所有的协程都是并行的
 * 协程的父子关系是要看该协程是由哪个 CoroutineScope 启动的
 * 父协程会等待子协程全部结束后才会结束
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    var innerJob: Job? = null

    /**
     * job.parent 父协程
     * job.children 子协程
     */
    val job = scope.launch {
        innerJob = this.launch {
            delay(100)
        }
    }
    val children = job.children
    val parent = innerJob?.parent
    println("cfx children count: ${children.count()}")
    println("cfx innerJob === children.first(): ${innerJob === children.first()}") // true
    println("cfx job === parent: ${job === parent}") // true
}