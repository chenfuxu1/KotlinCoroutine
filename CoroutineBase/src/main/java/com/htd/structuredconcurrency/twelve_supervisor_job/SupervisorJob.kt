package com.htd.structuredconcurrency.twelve_supervisor_job

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-17 23:49
 *
 * Desc:
 */
// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     val job = Job()
//     /**
//      * scope �ĸ�Э���� Job()
//      */
//     scope.launch(job) {
//         throw RuntimeException("Э���׳��쳣")
//     }
//     delay(100)
//     /**
//      * cfx job job.isActive: false
//      * ���Կ�������Э��������Э�̵��쳣Ҳ��ȡ��
//      */
//     println("cfx job job.isActive: ${job.isActive}")
//     delay(10000)
// }

// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     val supervisorJob = SupervisorJob()
//     /**
//      * scope �ĸ�Э���� SupervisorJob()
//      */
//     scope.launch(supervisorJob) {
//         throw RuntimeException("Э���׳��쳣")
//     }
//     delay(100)
//     /**
//      * cfx supervisorJob supervisorJob.isActive: true
//      * ���Կ�������Э��û��ȡ�������ǻ�Ծ״̬
//      */
//     println("cfx supervisorJob supervisorJob.isActive: ${supervisorJob.isActive}")
//     delay(10000)
// }

// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     /**
//      * ���ָ�ʽ���� scope.launch ȡ��ʱ����ʹ�ڲ�Э�̶�ȡ��
//      * ���� launch(SupervisorJob(coroutineContext.job)) �ڲ����쳣ʱ�����ᵽ�ⲿ�� scope.launch Э��ȡ��
//      */
//     scope.launch {
//         /**
//          * ����д launch ������Э�̵� ��Э���� SupervisorJob
//          * ͬʱ SupervisorJob ���� scope.launch ����Э�̵���Э�̣����ӹ�ϵû�жϿ�
//          */
//         launch(SupervisorJob(coroutineContext.job)) {
//             throw RuntimeException("Э���׳��쳣")
//         }
//
//     }
//     delay(10000)
//
//     /**
//      * д�� 2
//      * 1.ͨ�� scope2 ����������Э�̣��� scope2 ȡ��ʱ���ڲ�Э�̻�ȡ��
//      * 2.scope2 �ڲ��׳��쳣ʱ��scope2 Э�̲���ȡ��
//      */
//     val scope2 = CoroutineScope(SupervisorJob())
// }

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    /**
     * ���ָ�ʽ���� scope.launch ȡ��ʱ����ʹ�ڲ�Э�̶�ȡ��
     * ���� launch(SupervisorJob(coroutineContext.job)) �ڲ����쳣ʱ�����ᵽ�ⲿ�� scope.launch Э��ȡ��
     */
    scope.launch {
        val handler = CoroutineExceptionHandler { thread, exception ->
            println("cfx handler exception: $exception")
        }
        /**
         * SupervisorJob �ڲ�����Э���׳����쳣�ᵽ�� SupervisorJob ���׵��߳�ȥ
         * ǰ��˵ handler ��Ҫ���ø� launch ������������Э�̲�����Ч�������� SupervisorJob ��Ȼ���������
         */
        launch(SupervisorJob(coroutineContext.job) + handler) {
            launch {
                launch {
                    throw RuntimeException("Э���׳��쳣")
                }
            }
        }
    }
    delay(10000)
}