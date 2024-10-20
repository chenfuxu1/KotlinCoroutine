package com.htd.coroutinescope.three_coroutine_context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-19 20:22
 *
 * Desc: �������е� coroutineContext
 */
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    scope.launch {
        showDispatchers()
    }
    delay(10000)
}

private suspend fun showDispatchers() {
    delay(1000)
    /**
     * �ڹ������л�ȡ coroutineContext
     * cfx dispatchers: Dispatchers.Default
     *
     * public suspend inline val coroutineContext: CoroutineContext
     *     get() {
     *         throw NotImplementedError("Implemented as intrinsic")
     *     }
     */
    println("cfx dispatchers: ${coroutineContext[ContinuationInterceptor]}")

    /**
     * public suspend inline fun currentCoroutineContext(): CoroutineContext = coroutineContext
     * currentCoroutineContext �� coroutineContext ��ͬһ��������Ҫ����ͬ����ͻ
     */
    println("cfx dispatchers: ${currentCoroutineContext()[ContinuationInterceptor]}")
}

@OptIn(DelicateCoroutinesApi::class)
private fun flowFun() {
    flow<String> {
        // ���ڹ������Ĳ���� { } ��һ��������
        coroutineContext
    }

    // �����Э���е���
    GlobalScope.launch {
        flow<String> {
            // ����� coroutineContext ��Э������ģ��������Ĳ���һ������
            coroutineContext
            // ��ˣ�ʹ�� currentCoroutineContext() ��ȡ�����������������Ķ���
            currentCoroutineContext()
        }
    }
}