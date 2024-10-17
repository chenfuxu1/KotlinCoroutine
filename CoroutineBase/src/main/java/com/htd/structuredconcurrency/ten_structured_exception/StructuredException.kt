package com.htd.structuredconcurrency.ten_structured_exception

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.exitProcess

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-16 23:29
 *
 * Desc: �쳣�Ľṹ������
 */

/**
 * �߳��еĲ���δ֪�쳣�ķ��� tUncaughtExceptionHandler
 * ����δ֪���쳣����Ҫ�����ƺ��������ŵĽ���
 */
// fun main() = runBlocking<Unit> {
//     /**
//      * ��̬����
//      * ���Ը������߳����ü���
//      */
//     Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
//         println("cfx static method thread: $thread throwable: $throwable")
//         // �ƺ�������־...
//         // �˳�
//         exitProcess(0)
//     }
//     val thread = Thread {
//         throw RuntimeException("�߳��������׳��쳣")
//     }
//     /**
//      * ���Բ��� thread �׳��������쳣
//      */
//     // thread.setUncaughtExceptionHandler { thread, throwable ->
//     //     println("cfx thread: $thread throwable: $throwable")
//     // }
//     thread.start()
//     delay(10000)
// }

/**
 * Э���в���δ֪�쳣�ķ��� CoroutineExceptionHandler
 */
fun main() = runBlocking<Unit> {
    /**
     * ���Э����û��ʹ�� CoroutineExceptionHandler ����
     * ���������߳��м�������
     */
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        println("cfx static method thread: $thread throwable: $throwable")
        // �ƺ�������־...
        // �˳�
        exitProcess(0)
    }
    val scope = CoroutineScope(EmptyCoroutineContext)
    /**
     * ���Բ��� scope �ڲ��������쳣
     * handler ֻ�����õ�������Э�̲���Ч
     * scope �ڲ�����һ��Э���У�ֻҪ������δ֪�쳣���ͻ�������� scope ��Э�����������쳣�׵��� handler��ͳһ����
     */
    val handler = CoroutineExceptionHandler { coroutineContext, exception ->
        println("cfx handler exception: $exception")
        // �ƺ�������־...
        // �˳�
        exitProcess(0)
    }
    scope.launch(handler) {
        launch {
            throw RuntimeException("Э�������׳��쳣")
        }
        launch {

        }
    }
    delay(10000)
}