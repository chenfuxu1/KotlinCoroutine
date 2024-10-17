package com.htd.structuredconcurrency.seven_non_cancellbale

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-13 21:20
 *
 * Desc: 不配合取消
 */
/**
 * 输出结果：
 * cfx parent start...
 * cfx child start...
 * cfx child end...
 *
 * 子协程加了 NonCancellable，父协程取消时，子协程不会取消
 */
// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     val parentJob = scope.launch {
//         val childJob = launch(NonCancellable) {
//             println("cfx child start...")
//             delay(3000)
//             println("cfx child end...")
//         }
//         println("cfx parent start...")
//         delay(3000)
//         println("cfx parent end...")
//     }
//     delay(1500)
//     parentJob.cancel()
//     delay(10000)
// }

/**
 * cfx parent start...
 * cfx child start...
 * cfx child end...
 * 父协程被取消了，子协程没有取消
 * 但是如果直接取消子协程，子协程还是会结束的
 */
// fun main() = runBlocking<Unit> {
//     val scope = CoroutineScope(EmptyCoroutineContext)
//     var childJob: Job? = null
//     val parentJob = scope.launch {
//         /**
//          * launch(Job()) {  } 这种方式启动的协程就不属于 parentJob 的子协程了，parentJob 取消时不会影响到它
//          */
//         launch(Job()) {
//             println("cfx Job start...")
//             delay(3000)
//             println("cfx Job end...")
//         }
//         /**
//          * 原理：
//          * public object NonCancellable : AbstractCoroutineContextElement(Job), Job { }
//          * 1.NonCancellable 就是个单例的 job，所以能切断外部 parentJob 的链条
//          * 2.当外部 parentJob 取消时，childJob 不会取消，但直接取消 childJob，还是能取消的
//          * 3.NonCancellable 也切断自己和内部协程的关系，即 NonCancellable 也不是 childJob 的父协程
//          */
//         childJob = launch(NonCancellable) {
//             println("cfx child start...")
//             delay(3000)
//             println("cfx child end...")
//         }
//         println("cfx childJob?.parent: ${childJob?.parent}") // null, 没有父协程
//         println("cfx parent start...")
//         delay(3000)
//         println("cfx parent end...")
//     }
//     delay(1500)
//     parentJob.cancel()
//     // childJob?.cancel() // 子协程可以被取消
//     // NonCancellable.cancel() // 空实现，不能调用
//     delay(10000)
// }

/**
 * NonCancellable 的使用场景
 * 不想取消协程的场景：
 * 1.取消前的收尾工作
 * 2.不好收尾的业务工作
 * 3.业务和当前协程无关（例如日志）使用 launch 包裹
 */
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    var childJob: Job? = null
    val parentJob = scope.launch {
        /**
         * 原理：
         * public object NonCancellable : AbstractCoroutineContextElement(Job), Job { }
         * 1.NonCancellable 就是个单例的 job，所以能切断外部 parentJob 的链条
         * 2.当外部 parentJob 取消时，childJob 不会取消，但直接取消 childJob，还是能取消的
         * 3.NonCancellable 也切断自己和内部协程的关系，即 NonCancellable 也不是 childJob 的父协程
         */
        childJob = launch(NonCancellable) {
            println("cfx child start...")
            /**
             * 协程退出收尾工作
             */
            if (!isActive) {
                // 正常挂起函数，收尾工作
                // 那么会直接抛出异常了，导致协程结束，收尾工作没有执行完，因此这样是有问题的，可以采用下面的方式
            }

            /**
             * NonCancellable 正常的使用方式
             */
            if (!isActive) {
                /**
                 * withContext 是一个挂起函数，会让出外部协程的线程
                 * 会先执行 withContext(NonCancellable) 内部代码再往下执行
                 */
                withContext(NonCancellable) {
                    // 正常挂起函数，收尾工作
                }
            }

            /**
             * 如果退出是以下业务场景
             */
            if (!isActive) {
                /**
                 * withContext 是一个挂起函数，会让出外部协程的线程
                 * 会先执行 withContext(NonCancellable) 内部代码再往下执行
                 */
                writeInfo3()
            }

            /**
             * 和当前协程业务没有直接关系，使用 launch 包裹，并行执行
             */
            launch(NonCancellable) {
                // 输出日志
            }
            delay(3000)
            println("cfx child end...")
        }
        println("cfx childJob?.parent: ${childJob?.parent}") // null, 没有父协程
        println("cfx parent start...")
        delay(3000)
        println("cfx parent end...")
    }
    delay(1500)
    parentJob.cancel()
    // childJob?.cancel() // 子协程可以被取消
    // NonCancellable.cancel() // 空实现，不能调用
    delay(10000)
}

/**
 * withContext 是一个支持取消的挂起函数
 * 但是只是会在刚开始和结束之前检查取消状态
 * 在开始和结束之间是不会抛出异常的
 */
suspend fun writeInfo() = withContext(Dispatchers.IO) {
    // ==========开始============
    // 1.写文件，不会被打断
    // ==========结束============
}

suspend fun writeInfo2() = withContext(Dispatchers.IO) {
    // ==========开始============
    // 1.写文件，不会被打断 --- 在这期间协程取消了，代码正常执行，不会抛异常
    // 2.数据库读数据（挂起函数 Room）--- 此时会抛异常，结束协程
    // 3.整理数据，写文件 --- 此时该流程就执行不到了
    // ==========结束============
}

/**
 * 解决方案：保证整个 writeInfo3 不会结束
 */
suspend fun writeInfo3() = withContext(Dispatchers.IO + NonCancellable) {
    // ==========开始============
    // 1.写文件，不会被打断 --- 在这期间协程取消了，代码正常执行，不会抛异常
    // 2.数据库读数据（挂起函数 Room）--- 不会取消
    // 3.整理数据，写文件 --- 正常执行
    // ==========结束============
}