package com.htd.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Objects

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-27 11:37
 *
 * Desc:
 */
object Logit {
    @SuppressLint("SimpleDateFormat")
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    private val calendar: Calendar = Calendar.getInstance()

    fun d(msg: Any) {
        val formatStr = simpleDateFormat.format(calendar.time)
        println("$formatStr\t${msg}")
    }

    fun d(tag: String, msg: Any) {
        val formatStr = simpleDateFormat.format(calendar.time)
        println("$formatStr\t$tag\t$msg")
    }

    fun d(tag: String, msg: Any, throwable: Throwable) {
        val formatStr = simpleDateFormat.format(calendar.time)
        println("$formatStr\t$tag\t$msg \n ${throwable.message}")
    }
}