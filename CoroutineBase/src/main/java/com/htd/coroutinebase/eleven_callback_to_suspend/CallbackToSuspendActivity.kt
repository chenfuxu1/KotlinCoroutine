package com.htd.coroutinebase.eleven_callback_to_suspend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 连接线程世界：和回调型 API 协作
 */
class CallbackToSuspendActivity : AppCompatActivity() {
    private lateinit var mContainer: LinearLayoutCompat
    private val mGithub: GithubApi = RetrofitService.getRetrofit().create(GithubApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_callback_to_suspend)
        initView()
        callbackStyle()
        testCancel()
    }

    private fun testCancel() {
        // 挂起函数可以配合协程的取消行为
        val job = lifecycleScope.launch {
            println("cfx coroutine cancel: 1")
            delay(500)
            // Thread.sleep(500) 如果换成 sleep 就不会打断了，依然会执行下去
            println("cfx coroutine cancel: 2")
        }
        lifecycleScope.launch {
            delay(200)
            job.cancel() // 此时 cancel: 2 不会打印，job 直接终止了
        }
    }

    private fun initView() {
        mContainer = findViewById(R.id.container_layout)
    }

    private fun callbackStyle() {
        lifecycleScope.launch {
            val contributors = callbackToSuspend().filter {
                it.contributions > 40
            }
            showContributors(contributors)
        }
    }

    /**
     * 通过 suspendCoroutine 将回调函数装进挂起函数中
     * suspendCoroutine 不配合协程的取消
     * suspendCancellableCoroutine 支持取消
     */
    private suspend fun callbackToSuspend() = suspendCoroutine<List<Contributor>> {
        val call = mGithub.contributors("square", "retrofit")
        call.enqueue(object : Callback<List<Contributor>> {
            override fun onResponse(
                call: Call<List<Contributor>>,
                response: Response<List<Contributor>>
            ) {
                println("cfx callbackStyle retrofit onSucceed Thread: ${Thread.currentThread().name}")
                // showContributors(response.body()!!)
                it.resume(response.body()!!)
            }

            override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
                println("cfx onFailure t: ${t.printStackTrace()}")
                // 让 suspendCoroutine 立即结束，并抛出 t 异常
                it.resumeWithException(t)
            }

        })
    }

    private suspend fun callbackToCancellableSuspend() =
        suspendCancellableCoroutine<List<Contributor>> {
            it.invokeOnCancellation {
                // 可以监听异常退出回调
            }
            val call = mGithub.contributors("square", "retrofit")
            call.enqueue(object : Callback<List<Contributor>> {
                override fun onResponse(
                    call: Call<List<Contributor>>,
                    response: Response<List<Contributor>>
                ) {
                    println("cfx callbackStyle retrofit onSucceed Thread: ${Thread.currentThread().name}")
                    // showContributors(response.body()!!)
                    it.resume(response.body()!!)
                }

                override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
                    println("cfx onFailure t: ${t.printStackTrace()}")
                    // 让 suspendCoroutine 立即结束，并抛出 t 异常
                    it.resumeWithException(t)
                }

            })
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
}

