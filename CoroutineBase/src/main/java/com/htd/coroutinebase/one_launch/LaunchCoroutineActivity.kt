package com.htd.coroutinebase.one_launch

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.htd.coroutinebase.R
import com.htd.coroutinebase.ui.CustomTextView
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 启动协程
 *
 * public actual object Dispatchers {
 *      // 默认的后台任务调度器，用于计算密集型任务，图片压缩，媒体编解码
 *      // cpu 是多少核心数，该调度器就是多少个线程，线程更多还会降低效率，因为需要 cpu 来回切换
 *      // 这种设计可以提高效率
 *      @JvmStatic
 *      public actual val Default: CoroutineDispatcher = DefaultScheduler
 *
 *      // 用于 IO 密集型任务，例如读写 IO、网络任务，其余的都属于计算密集型任务
 *      // IO 密集型，cpu 是空闲的，例如网络请求时，使用的是网卡工作，读写文件时，是磁盘工作
 *      // 固定的线程数是 64 线程，当 cpu 核心数高于 64 时，那么有多少核心，线程池就有多大
 *      @JvmStatic
 *      public val IO: CoroutineDispatcher = DefaultIoScheduler
 *
 *      // 会把任务扔到主线程执行
 *      @JvmStatic
 *      public actual val Main: MainCoroutineDispatcher get() = MainDispatcherLoader.dispatcher
 *
 *      // 不切线程
 *      @JvmStatic
 *      public actual val Unconfined: CoroutineDispatcher = kotlinx.coroutines.Unconfined
 *
 * }
 */
class LaunchCoroutineActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_coroutine)

        /**
         * 1.启动一个线程
         */
        thread {

        }

        // cfx Main Thread: main
        println("cfx Main Thread: ${Thread.currentThread().name}")
        /**
         * 2.线程池执行
         */
        val executor = Executors.newCachedThreadPool()
        executor.execute {
            // executor Thread: pool-2-thread-1
            println("cfx executor Thread: ${Thread.currentThread().name}")

        }

        /**
         * 3.切到主线程执行
         */
        val handler = Handler(Looper.getMainLooper())
        handler.post {

        }

        val view : CustomTextView = CustomTextView(this)
        view.post {

        }

        /**
         * 4.协程
         *
         * CoroutineScope(EmptyCoroutineContext) 没有设置 Dispatchers
         * 默认就是 Dispatchers.Default
         * CoroutineScope(EmptyCoroutineContext) 等价 CoroutineScope(Dispatchers.Default)
         */
        var context = CoroutineScope(EmptyCoroutineContext)
        context.launch {
            // context Thread: DefaultDispatcher-worker-1
            println("cfx context Thread: ${Thread.currentThread().name}")
        }

        var context2 = CoroutineScope(Dispatchers.IO)
        // 下面两个协程都是 Dispatchers.IO，可以实现复用
        context2.launch {

        }
        context2.launch {

        }

        var context3 = CoroutineScope(Dispatchers.IO)
        // launch 中设置的会覆盖 CoroutineScope 上面的
        context3.launch(Dispatchers.Default) {

        }

        var context4 = CoroutineScope(Dispatchers.Main)
        context.launch {
            // 这里的代码发生在主线程 ui 线程
        }

        /**
         * 可以自定义线程池
         * 需要自己关闭, 自己管理生命周期
         * newSingleThreadContext() 单线程的线程池
         */
        val threadPool = newFixedThreadPoolContext(4, "NewThreadPool")
        threadPool.close()
    }
}