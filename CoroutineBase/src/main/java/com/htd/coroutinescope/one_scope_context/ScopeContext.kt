package com.htd.coroutinescope.one_scope_context

import kotlinx.coroutines.*
import kotlin.coroutines.ContinuationInterceptor

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-19 10:57
 *
 * Desc: CoroutineScope �� CoroutineContext
 *
 * CoroutineScope: �ṩЭ�̵���������Ϣ��������Э��
 * CoroutineContext: ����Э�̴�������������Ϣ
 *
 */
fun main() = runBlocking<Unit> {
    /**
     * cfx scope: CoroutineScope(coroutineContext=[JobImpl{Active}@3dd4520b, Dispatchers.IO])
     * CoroutineScope(Dispatchers.IO) ���ֶ������� CoroutineScope��������Ӧ�κε�Э�̣���Ϊ��û������Э�̣�����Ӧ����Э�̵Ĵ����
     * ���ԣ��ֶ������� CoroutineScope �� coroutineContext �������κ�Э�̵������ģ������ڴ�����Э��ʱʹ�õ� coroutineContext
     */
    val scope = CoroutineScope(Dispatchers.IO)
    println("cfx scope: $scope")
    scope.launch {
        // cfx this: StandaloneCoroutine{Active}@406495cb
        println("cfx this: $this")
        /**
         * coroutineContext ���Ǳ��� this��CoroutineScope Э�̵������Ķ�����Ϣ
         */
        coroutineContext[Job]
        this.coroutineContext[Job]
        coroutineContext.job
        coroutineContext[ContinuationInterceptor]
    }

    delay(1000)
}