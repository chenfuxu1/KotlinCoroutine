package com.htd.flow.twenty_four_on_start_on_completion

import com.htd.coroutinebase.common.api.GithubApi
import com.htd.coroutinebase.common.retrofit.RetrofitService
import com.htd.utils.Logit
import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-11-02 15:05
 *
 * Desc: OnStart、onCompletion
 */
fun main() = runBlocking {
    val github = RetrofitService.getRetrofit().create(GithubApi::class.java)
    val scope = CoroutineScope(EmptyCoroutineContext)

    /**
     * onStart 监听流的开始
     * 1.onStart 在流开始之前调用
     * 2.多个 onStart 调用的顺序是从下往上
     * 3.try / catch 只能捕获下游的异常，对于 onStart 的异常捕获不到
     * 4.onStart 中的异常可以使用 catch 操作符捕获
     *
     * onCompletion 监听流的结束，可以监听到异常的结束，并且不会拦截异常
     *
     * onEmpty 一条数据没发，正常结束时会调用
     */
    val flow1 = flow {
        try {
            for (i in 1 .. 5) {
                // 数据库读数据
                // 网络请求
                emit(i)
            }
        } catch (e: Exception) {
            Logit.d("Exception: $e")
        }
    }.onStart {
        Logit.d("OnStart 1....")
        throw NullPointerException("空指针")
    }.onStart {
        Logit.d("OnStart 2....")
    }.onCompletion {
        Logit.d("onCompletion .... $it")
    }.onEmpty {
        Logit.d("onEmpty")
    }.catch {
        Logit.d("catch: $it")
    }


    scope.launch {
        flow1.collect {
            Logit.d(it)
        }
        Logit.d(RepeatChar.getStr("="))
    }
    delay(10000)
}
