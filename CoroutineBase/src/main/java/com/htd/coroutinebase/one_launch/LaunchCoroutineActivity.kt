package com.htd.coroutinebase.one_launch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.htd.coroutinebase.R
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.concurrent.thread

/**
 * 启动协程
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

        /**
         * 2.线程池执行
         */
        val executor = Executors.newCachedThreadPool()
        executor.execute {

        }
    }
}