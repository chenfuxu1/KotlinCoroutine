package com.htd.structuredconcurrency.two_parent_children

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-07 22:41
 *
 * Desc: 父子协程
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
        innerJob = launch {
            delay(100)
        }
    }
    val children = job.children
    val parent = innerJob?.parent
    println("cfx children count: ${children.count()}")
    println("cfx innerJob === children.first(): ${innerJob === children.first()}") // true
    println("cfx job === parent: ${job === parent}") // true
}