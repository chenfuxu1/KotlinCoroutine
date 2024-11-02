package com.htd.flow.twenty_two_catch

import com.htd.coroutinebase.common.api.GithubApi
import com.htd.coroutinebase.common.retrofit.RetrofitService
import com.htd.utils.Logit
import com.htd.utils.RepeatChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeoutException
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.random.Random

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-11-02 14:10
 *
 * Desc: Catch
 */
fun main() = runBlocking {
    val github = RetrofitService.getRetrofit().create(GithubApi::class.java)
    val scope = CoroutineScope(EmptyCoroutineContext)

    /**
     * 1.Catch 操作符只捕获上游的异常，不捕获来自下游的异常
     * 2.不会捕获 CancellationException
     */
    val flow1 = flow {
        for (i in 1 .. 5) {
            // 数据库读数据
            // 网络请求
            // throw RuntimeException("空指针")
            emit(i)
        }
    }.catch {
        Logit.d("Catch: $it")
    }

    val flow2 = flow {
        for (i in 1 .. 5) {
            // 数据库读数据
            // 网络请求
            // throw RuntimeException("空指针")
            emit(i)
        }
    }.catch {
        Logit.d("Catch1: $it")
        throw RuntimeException("空指针")
    }.catch {
        // 第二个 catch 只会捕获两个 catch 之间发生的异常
        Logit.d("Catch2: $it")
    }

    /**
     * catch 的用法：上游数据发送异常了，进行接管
     * 能用 try / catch, 就用，否则就用 catch
     * 例如别人封装后的 flow，无法在其内部 try / catch, 可以用 catch 操作符进行接管
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
    }.catch {
        Logit.d("Catch3: $it")
        // 处理异常后，继续向下发送
        emit(3)
        emit(4)
        emit(5)
    }
    scope.launch {
        try {
            flow1.collect {
                val randomInt = Random.nextInt(2)
                val result = github.coroutineContributors("square", "retrofit")
                if (randomInt == 1) {
                    throw TimeoutException("Network Error")
                }
                Logit.d(result)
            }
        } catch (e: TimeoutException) {
            Logit.d("Exception: $e")
        }
        Logit.d(RepeatChar.getStr("="))

        flow3.collect {
            Logit.d(it)
        }
    }
    delay(10000)
}