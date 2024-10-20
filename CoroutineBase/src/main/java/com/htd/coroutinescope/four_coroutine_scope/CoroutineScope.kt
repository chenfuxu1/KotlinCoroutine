package com.htd.coroutinescope.four_coroutine_scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-19 20:41
 *
 * Desc: CoroutineScope �� SupervisorScope
 */
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    scope.launch {
        val startTime = System.currentTimeMillis()
        /**
         * �� launch ����֮ͬ��
         * 1.�������� scope.launch �� coroutineContext
         * 2.���������µ���Э��
         *
         * ����
         * 1.coroutineScope { } ��û�в����ģ�launch ���Դ��Σ����Ƹ� jib��coroutineScope �൱�� launch ��ʲô����������
         * 2.coroutineScope �ǹ��������Ǵ��еģ���ȴ� coroutineScope ִ����ϲŻ������ߣ��� launch ���ǣ�launch �ǲ��е�
         * 3.coroutineScope �ڲ����������Э�̣�Ҳ��ȴ��������Э�̣�����Э��ִ����Ϻ󣬲Ż�����ִ��
         * 4.coroutineScope �ı��ʽ���з���ֵ�ģ�lambda ���ʽ���һ�У�launch û�з���ֵ
         */
        coroutineScope {
            println("cfx coroutineContext.job.parent: ${coroutineContext.job.parent}")
            delay(2000)
            launch {
                delay(1000)
                println("cfx duration with grand child scope: ${System.currentTimeMillis() - startTime}")
            }
            println("cfx duration with coroutineScope: ${System.currentTimeMillis() - startTime}")

        }
        val name = coroutineScope {
            val deferred1 = async {
                "����"
            }
            val deferred2 = async {
                "����"
            }
            deferred1.await() + deferred2.await()
        }
        println("cfx name: $name")

        /**
         * �� coroutineScope �ڲ�����Э�̷������쳣�������� coroutineScope ���񵽣����Ҳ���Ӱ�쵽����Э�̵�ȡ��
         * ԭ��coroutineScope �ǹ��������Ǵ��е�
         */
        val name2 = try {
            coroutineScope {
                val deferred1 = async {
                    "����"
                }
                val deferred2 = async {
                    throw RuntimeException("�ֶ��׳��쳣")
                }
                deferred1.await() + deferred2.await()
            }
        } catch (e: Exception) {
            println("cfx Exception: $e")
        }
        println("cfx name2: $name2")
        launch {
            println("cfx launch: ${coroutineContext.job.parent}")
            delay(1000)
            println("cfx duration with launch: ${System.currentTimeMillis() - startTime}")

        }
        println("cfx duration with scope.launch: ${System.currentTimeMillis() - startTime}")

        /**
         * supervisorScope �� coroutineScope ���幦������
         * supervisorScope �ṩ��һ�������� SupervisorJob �� job
         */
        supervisorScope {

        }

    }

    delay(10000)
}

/**
 * coroutineScope �ĳ��ó���
 * 1.�����ڹ��������ṩһ�� CoroutineScope ��������
 * 2.����2����һ�������ҵ���װ���������Բ����쳣�����д����޸�����Ӱ�������߼�
 */
private suspend fun test() = coroutineScope {
    // ����Э��
    launch {
        // ҵ�����
    }
}