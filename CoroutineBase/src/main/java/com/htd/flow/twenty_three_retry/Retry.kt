package com.htd.flow.twenty_three_retry

import android.util.Log
import com.htd.coroutinebase.common.api.GithubApi
import com.htd.coroutinebase.common.retrofit.RetrofitService
import com.htd.utils.Logit
import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-11-02 14:51
 *
 * Desc: Retry
 */
fun main() = runBlocking {
    val github = RetrofitService.getRetrofit().create(GithubApi::class.java)
    val scope = CoroutineScope(EmptyCoroutineContext)

    /**
     * 1.retry() 检测到发生异常时，会重新调用 flow 的 collect，这时下游是无感知的，会一直输出 1, 2, 1, 2, 1, 2...
     */
    val flow1 = flow {
        for (i in 1 .. 5) {
            // 数据库读数据
            // 网络请求
            if (i == 3) {
                throw RuntimeException("空指针")
            }
            emit(i)
        }
    }.retry()

    /**
     * 2.retry() 会重启上游的整个链条
     */
    val flow2 = flow {
        for (i in 1 .. 5) {
            // 数据库读数据
            // 网络请求
            if (i == 3) {
                throw RuntimeException("空指针")
            }
            emit(i)
        }
    }.map {
        it * 2
    }.retry()

    /**
     * 3.retry(4) 可以重启的次数
     */
    val flow3 = flow {
        for (i in 1 .. 5) {
            // 数据库读数据
            // 网络请求
            if (i == 3) {
                throw RuntimeException("空指针")
            }
            emit(i)
        }
    }.map {
        it * 2
    }.retry(4)

    /**
     * 4.retry(4) {}
     * 参数 2，lambda 表达式 为 true 才执行
     */
    val flow4 = flow {
        for (i in 1 .. 5) {
            // 数据库读数据
            // 网络请求
            if (i == 3) {
                throw RuntimeException("空指针")
            }
            emit(i)
        }
    }.map {
        it * 2
    }.retry(4) {
        it is RuntimeException
    }

    /**
     * 5.retryWhen 相当于retry(4) { } 合在一起
     * cause：异常
     * attempt：重试的次数
     */
    val flow5 = flow {
        for (i in 1 .. 5) {
            // 数据库读数据
            // 网络请求
            if (i == 3) {
                throw RuntimeException("空指针")
            }
            emit(i)
        }
    }.map {
        it * 2
    }.retryWhen { cause, attempt ->
        cause is RuntimeException
    }

    scope.launch {
        // flow1.collect {
        //     Logit.d(it)
        // }
        // Logit.d(RepeatChar.getStr("="))
        //
        // flow2.collect {
        //     Logit.d(it)
        // }
        // Logit.d(RepeatChar.getStr("="))

        try {
            flow3.collect {
                Logit.d(it)
            }
        } catch (e: Exception) {
            Logit.d("Exception: $e")
        }
    }
    delay(10000)
}