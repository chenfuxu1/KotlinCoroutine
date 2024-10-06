package com.htd.coroutinebase.four_with_context

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.LinearLayoutCompat
import com.htd.coroutinebase.R
import kotlinx.coroutines.*

/**
 * withContext 和 launch
 */
class WithContextActivity : AppCompatActivity() {
    private lateinit var mContainer : LinearLayoutCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_context)
        initView()
        // parallelFunc()

        withContextFunc()
    }

    private fun initView() {
        mContainer = findViewById(R.id.container_layout)
    }

    private fun parallelFunc() {
        CoroutineScope(Dispatchers.Main).launch {
            /**
             * 这种写法 launch 启动的协程和 println("cfx Dispatchers.Main 代码") 是并行执行的
             */
            launch(Dispatchers.IO) {
                println("cfx 协程内部启动协程 start Thread：${Thread.currentThread().name}")
                delay(3000)
                println("cfx 协程内部启动协程 end Thread：${Thread.currentThread().name}")

            }
            println("cfx Dispatchers.Main 代码 Thread：${Thread.currentThread().name}")
        }
    }

    private fun withContextFunc() {
        CoroutineScope(Dispatchers.Main).launch {
            /**
             * withContext 是一个挂起函数，会让出外部协程的线程
             * 所以下面代码是串行执行的
             * 即 withContext(Dispatchers.IO) 协程内部执行完，才会执行下面的代码
             */
            withContext(Dispatchers.IO) {
                println("cfx 协程内部启动协程 start Thread：${Thread.currentThread().name}")
                delay(3000)
                println("cfx 协程内部启动协程 end Thread：${Thread.currentThread().name}")

            }
            println("cfx Dispatchers.Main 代码 Thread：${Thread.currentThread().name}")
        }
    }
}