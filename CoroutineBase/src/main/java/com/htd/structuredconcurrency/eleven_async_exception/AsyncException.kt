package com.htd.structuredconcurrency.eleven_async_exception

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-17 0:11
 *
 * Desc: async ���쳣�Ĵ���
 */

/**
 * ��Э��ȡ��
 */
// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     val handler = CoroutineExceptionHandler { thread, exception ->
//         println("cfx handler thread: ${thread.job} exception: $exception")
//     }
//     scope.launch(handler) {
//         val deferred = async {
//             delay(1000)
//             throw RuntimeException("async Э���������׳����쳣")
//         }
//         launch {
//             try {
//                 deferred.await()
//             } catch (e: Exception) {
//                 /**
//                  * cfx Exception: kotlinx.coroutines.JobCancellationException: StandaloneCoroutine was cancelled; job=StandaloneCoroutine{Cancelling}@17f44cd4
//                  * ��ӡ����Э�̵�ȡ���쳣
//                  * deferred û�еȵ� 1s�����ڸ�Э��ȡ����ȡ����
//                  */
//                 println("cfx Exception: $e")
//             }
//         }
//         delay(100)
//         cancel()
//     }
//     delay(10000)
// }

/**
 * �ֵ�Э���׳��쳣���Լ�Э�̵Ĺ������׳�ȡ���쳣
 */
// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     val handler = CoroutineExceptionHandler { thread, exception ->
//         println("cfx handler thread: ${thread.job} exception: $exception")
//     }
//     scope.launch(handler) {
//         val deferred = async {
//             delay(1000)
//             throw RuntimeException("async Э���������׳����쳣")
//         }
//         launch {
//             try {
//                 delay(2000)
//             } catch (e: Exception) {
//                 /**
//                  * cfx Exception: kotlinx.coroutines.JobCancellationException: Parent job is Cancelling; job=StandaloneCoroutine{Cancelling}@4fecfa01
//                  * ��ӡ����Э�̵�ȡ���쳣
//                  * ���� deferred Э�̵��� 1s ���׳� RuntimeException �쳣���Ӷ�ʹ����Э����ȡ��
//                  */
//                 println("cfx Exception: $e")
//             }
//         }
//     }
//     delay(10000)
// }

/**
 * 1.deferred.await() ��˫��Ӱ��
 * ���׳� deferred Э�̱�����쳣
 * ���׳� deferred.await() ������Э�̵�ȡ���쳣
 */
// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     val handler = CoroutineExceptionHandler { thread, exception ->
//         println("cfx handler thread: ${thread.job} exception: $exception")
//     }
//     scope.launch(handler) {
//         val deferred = async {
//             delay(1000)
//             throw RuntimeException("async Э���������׳����쳣")
//         }
//         launch {
//             try {
//                 /**
//                  * deferred.await ������Ϊ��Э�̵Ĺ��������ڣ�����Ϊ deferred Э�̵ķ��ض������
//                  * ��ͻᵼ���� deferred Э�����׳��� RuntimeException �쳣����Ϊ deferred.await() �ķ��ؽ��
//                  * ͬʱ������ deferred Э������ RuntimeException �쳣���ᵼ���ֵ�Э�̵Ĺ����� deferred.await() �׳�ȡ���쳣
//                  * ��ô���˴����ղ�����쳣�������ĸ��أ�
//                  */
//                 deferred.await()
//             } catch (e: Exception) {
//                 /**
//                  * ���� deferred.await() �ķ��ؽ����ֱ�Ӵ����ģ��������� deferred.await() �׳�ȡ���쳣�ߵ��ǽṹ������
//                  * �ȴ� deferred Э���׳�����Э�̣���Э���ٵ��������Э�̣����ԣ���ӡ���� RuntimeException �쳣
//                  * cfx Exception: java.lang.RuntimeException: async Э���������׳����쳣
//                  * async ���쳣�ȵ�����
//                  */
//                 println("cfx Exception: $e")
//             }
//             try {
//                 delay(1000)
//             } catch (e: Exception) {
//                 /**
//                  * cfx delay Exception: kotlinx.coroutines.JobCancellationException: Parent job is Cancelling; job=StandaloneCoroutine{Cancelling}@2ab5a0c9
//                  * ���Կ�����Э�̵�ȡ���쳣���ǻ��׳����ģ�ֻ�ǻ���һЩ
//                  */
//                 println("cfx delay Exception: $e")
//             }
//
//         }
//     }
//     delay(10000)
// }

/**
 * 2.��������Э���� async �����ģ�ʹ�� CoroutineExceptionHandler ����Ч��
 * async ������쳣�׵��߳�����
 * ��Ϊ async �����Ļ�ͨ�� deferred.await() �׵���һ��Э�̣�����ֱ���׵��̣߳��̲߳����յ�
 * ���� CoroutineExceptionHandler ֻ������ launch ������������Э����
 */
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val handler = CoroutineExceptionHandler { thread, exception ->
        println("cfx handler thread: ${thread.job} exception: $exception")
    }
    scope.async(handler) {
        val deferred = async {
            delay(1000)
            throw RuntimeException("async Э���������׳����쳣")
        }
        launch {
            try {
                /**
                 * deferred.await ������Ϊ��Э�̵Ĺ��������ڣ�����Ϊ deferred Э�̵ķ��ض������
                 * ��ͻᵼ���� deferred Э�����׳��� RuntimeException �쳣����Ϊ deferred.await() �ķ��ؽ��
                 * ͬʱ������ deferred Э������ RuntimeException �쳣���ᵼ���ֵ�Э�̵Ĺ����� deferred.await() �׳�ȡ���쳣
                 * ��ô���˴����ղ�����쳣�������ĸ��أ�
                 */
                deferred.await()
            } catch (e: Exception) {
                /**
                 * ���� deferred.await() �ķ��ؽ����ֱ�Ӵ����ģ��������� deferred.await() �׳�ȡ���쳣�ߵ��ǽṹ������
                 * �ȴ� deferred Э���׳�����Э�̣���Э���ٵ��������Э�̣����ԣ���ӡ���� RuntimeException �쳣
                 * cfx Exception: java.lang.RuntimeException: async Э���������׳����쳣
                 * async ���쳣�ȵ�����
                 */
                println("cfx Exception: $e")
            }
            try {
                delay(1000)
            } catch (e: Exception) {
                /**
                 * cfx delay Exception: kotlinx.coroutines.JobCancellationException: Parent job is Cancelling; job=StandaloneCoroutine{Cancelling}@2ab5a0c9
                 * ���Կ�����Э�̵�ȡ���쳣���ǻ��׳����ģ�ֻ�ǻ���һЩ
                 */
                println("cfx delay Exception: $e")
            }
        }
    }
    delay(10000)
}