package com.htd.coroutinebase.twelve_run_blocking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.LinearLayoutCompat
import com.htd.coroutinebase.R
import com.htd.coroutinebase.common.api.GithubApi
import com.htd.coroutinebase.common.retrofit.RetrofitService
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/**
 * runBlocking 可以提供协程环境，包住整个 main 函数
 */
fun main() = runBlocking<Unit> {
    // 这样 main 函数就在协程的环境了
    val githubApi: GithubApi = RetrofitService.getRetrofit().create(GithubApi::class.java)
    githubApi.coroutineContributors("square", "retrofit")
}

/**
 * RunBlocking
 */
class RunBlockingActivity : AppCompatActivity() {
    private lateinit var mContainer: LinearLayoutCompat
    private val mGithubApi: GithubApi = RetrofitService.getRetrofit().create(GithubApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_blocking)
        initView()
        testRunBlocking()
    }

    private fun testRunBlocking() {
        /**
         * runBlocking 将协程代码转换为阻塞式的，方便传统线程 api 配合使用
         * 1.调用时会阻塞当前线程，并在当前线程开始运行，直至代码执行完毕，才放开当前线程
         */
        runBlocking {
            println("cfx runBlocking start")
            mGithubApi.coroutineContributors("square", "retrofit") // 挂起函数变成阻塞式的了
            println("cfx runBlocking end")
        }
        println("cfx runBlocking outer")
    }

    private fun initView() {
        mContainer = findViewById(R.id.container_layout)
    }
}