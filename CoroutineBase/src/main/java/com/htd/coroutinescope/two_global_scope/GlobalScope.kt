package com.htd.coroutinescope.two_global_scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-19 17:06
 *
 * Desc: GlobalScope ����û���������ڵ�Э��
 * ����û�к͸� job �󶨣����Բ�����������
 */
@OptIn(DelicateCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    CoroutineScope(EmptyCoroutineContext)
    /**
     * job �ǿյ�
     * ��ô GlobalScope ��������Э�̾�û�и�Э����
     */
    println(GlobalScope.coroutineContext[Job]) // null
    val job = GlobalScope.launch {
        /**
         * @DelicateCoroutinesApi
         * public object GlobalScope : CoroutineScope {
         *     override val coroutineContext: CoroutineContext
         *         get() = EmptyCoroutineContext
         * }
         *
         * public operator fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>): E?
         * ���ص� job �����ǿɿյ�
         */
        coroutineContext[Job]
        delay(1000)
        println("cfx coroutineContext[Job]: ${coroutineContext[Job]}")
    }
    println("cfx job.parent: ${job.parent}") // null, ��Э��Ϊ��

    /**
     * �ȼ��������д��
     * ��Ϊ GlobalScope û�� job
     * ���� GlobalScope ������Э��û�� parent�������� GlobalScope.cancel ���� GlobalScope ����������Э�̷����쳣ʱ
     * ���������Э�̵Ľ���
     */
    GlobalScope.launch {

    }
    CoroutineScope(EmptyCoroutineContext).launch {

    }
    delay(10000)
}