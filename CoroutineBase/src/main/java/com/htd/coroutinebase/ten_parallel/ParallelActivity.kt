package com.htd.coroutinebase.ten_parallel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.lifecycleScope
import com.htd.coroutinebase.R
import com.htd.coroutinebase.common.api.GithubApi
import com.htd.coroutinebase.common.bean.Contributor
import com.htd.coroutinebase.common.retrofit.RetrofitService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 并行协程
 * 需求：同时进行两路网络请求，将请求结果合并展示
 */
class ParallelActivity : AppCompatActivity() {
    private lateinit var mContainer: LinearLayoutCompat
    private val mHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parallel)
        initView()
        // coroutineSerialStyle()
        // coroutineAsyncStyle()
        javaParallel()
    }

    /**
     * 串行
     */
    private fun coroutineSerialStyle() = lifecycleScope.launch {
        val api = RetrofitService.getRetrofit().create(GithubApi::class.java)
        // 第一路请求结束后，才进行的第二路网络请求，串行执行的
        val contributors1 = api.coroutineContributors("square", "retrofit").filter {
            it.contributions > 40
        }
        val contributors2 = api.coroutineContributors("square", "okhttp").filter {
            it.contributions > 40
        }
        // 展示两路请求的结果
        showContributors(contributors1 + contributors2)

    }

    /**
     * 并行 async
     */
    private fun coroutineAsyncStyle() {
        val api = RetrofitService.getRetrofit().create(GithubApi::class.java)
        /**
         * 写法 1
         */
        // val contributors1 = lifecycleScope.async {
        //     api.coroutineContributors("square", "retrofit").filter {
        //         it.contributions > 40
        //     }
        // }
        // lifecycleScope.launch {
        //     val contributors2 = api.coroutineContributors("square", "okhttp").filter {
        //         it.contributions > 40
        //     }
        //     // 两路请求都结束后，展示两路请求的结果
        //     showContributors(contributors1.await() + contributors2)
        // }

        /**
         * 写法 2
         */
        // val contributors1 = lifecycleScope.async {
        //     api.coroutineContributors("square", "retrofit").filter {
        //         it.contributions > 40
        //     }
        // }
        // val contributors2 = lifecycleScope.async {
        //     api.coroutineContributors("square", "okhttp").filter {
        //         it.contributions > 40
        //     }
        // }
        // lifecycleScope.launch {
        //     // 两路请求都结束后，展示两路请求的结果
        //     showContributors(contributors1.await() + contributors2.await())
        // }

        /**
         * 写法 3
         */
        // lifecycleScope.launch {
        //     val contributors1 = async {
        //         api.coroutineContributors("square", "retrofit").filter {
        //             it.contributions > 40
        //         }
        //     }
        //     val contributors2 = async {
        //         api.coroutineContributors("square", "okhttp").filter {
        //             it.contributions > 40
        //         }
        //     }
        //     // 两路请求都结束后，展示两路请求的结果
        //     showContributors(contributors1.await() + contributors2.await())
        // }

        /**
         * 写法 4
         */
        lifecycleScope.launch {
            // 包了一层 coroutineScope 可以处理结构化并发异常的
            coroutineScope {
                val contributors1 = async {
                    api.coroutineContributors("square", "retrofit").filter {
                        it.contributions > 40
                    }
                }
                val contributors2 = async {
                    api.coroutineContributors("square", "okhttp").filter {
                        it.contributions > 40
                    }
                }
                // 两路请求都结束后，展示两路请求的结果
                showContributors(contributors1.await() + contributors2.await())
            }

        }

        /**
         * initJob.join()
         * 流程上有依赖，但不依赖协程的结果
         */
        // lifecycleScope.launch {
        //     val initJob = launch {
        //         // 1.初始化本地任务
        //         init()
        //     }
        //     // 2.同时进行网络请求
        //     api.coroutineContributors("square", "retrofit")
        //     // 3.处理数据依赖于本地初始化的数据，所以需要等待
        //     initJob.join() // 在这里等待，不关心协程的返回值，只关心协程是否结束
        //     // 4.处理数据
        //     processData()
        // }
    }

    private fun javaParallel() {
        val api = RetrofitService.getRetrofit().create(GithubApi::class.java)
        val future1 = api.contributorsFuture("square", "retrofit")
        val future2 = api.contributorsFuture("square", "okhttp")
        future1.thenCombine(future2) { contributors1, contributors2 ->
            contributors1 + contributors2
        }.thenAccept { mergedContributors ->
            mergedContributors.filter {
                it.contributions > 40
            }.let {
                mHandler.post {
                    showContributors(it)
                }
            }

        }
    }

    private fun showContributors(contributors: List<Contributor>) {
        println(contributors.size)
        contributors.map { contributor ->
            "${contributor.login} (${contributor.contributions})"
        }.forEach {
            val blankView = View(this)
            val blankViewLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                50
            )
            blankView.layoutParams = blankViewLayoutParams
            mContainer.addView(blankView)
            val textView =
                (LayoutInflater.from(this).inflate(R.layout.custom_text_view, null)) as TextView
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textView.layoutParams = layoutParams
            textView.text = it
            mContainer.addView(textView)
        }
    }

    private fun initView() {
        mContainer = findViewById(R.id.container_layout)
    }
}