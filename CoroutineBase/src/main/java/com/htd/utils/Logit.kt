package com.htd.utils

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-27 11:37
 *
 * Desc:
 */
object Logit {
    fun d(tag: String, msg: String) {
        println("$tag $msg")
    }

    fun d(tag: String, msg: String, throwable: Throwable) {
        println("$tag $msg \n ${throwable.message}")
    }
}