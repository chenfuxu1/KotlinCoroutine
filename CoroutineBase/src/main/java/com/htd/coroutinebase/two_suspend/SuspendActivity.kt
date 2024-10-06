package com.htd.coroutinebase.two_suspend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.htd.coroutinebase.R
import com.htd.coroutinebase.common.api.GithubApi
import com.htd.coroutinebase.common.retrofit.RetrofitService
import com.htd.coroutinebase.common.bean.Contributor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 挂起函数 Suspend
 */
class SuspendActivity : AppCompatActivity() {
    private lateinit var mSuspendLayout: LinearLayoutCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suspend)
        initView()
        // callbackStyle()
        coroutineStyle()
    }

    /**
     * 协程的方式
     * 区别：
     * 1.没有回调
     * 2.挂起函数只有在协程中或另一个挂起函数中才有意义，逻辑上不成立
     */
    private fun coroutineStyle() {
        // 主线程的协程
        CoroutineScope(Dispatchers.Main).launch {
            val retrofit = RetrofitService.getRetrofit().create(GithubApi::class.java)
            println("cfx coroutineStyle Thread: ${Thread.currentThread().name}")
            /**
             * 请求数据，发生在后台线程
             * 执行完成后，会自动切到当前线程
             * retrofit.coroutineContributors 是一个挂起函数
             * 执行挂起函数时，会让出当前作用域的线程（主线程），即主线程的协程被挂起暂停了，这个协程的线程就可以去执行其他事情了
             * 但后台网络请求任务的线程还是正常执行的（挂起函数正常执行）
             */
            val contributors = retrofit.coroutineContributors("square", "retrofit")
            /**
             * 挂起函数执行完，自动回到主线程
             * 展示界面，回到主线程
             * 因为这个协程是在主线程执行
             */
            println("cfx coroutineStyle Thread: ${Thread.currentThread().name}")
            showContributors(contributors)

        }
    }

    /**
     * 常规回调的展示方式
     * 先切到后台进行网络请求任务
     * 然后再切到 ui 线程更新界面
     */
    private fun callbackStyle() {
        val retrofit = RetrofitService.getRetrofit().create(GithubApi::class.java)
        val call = retrofit.contributors("square", "retrofit")
        call.enqueue(object : Callback<List<Contributor>> {
            override fun onResponse(
                call: Call<List<Contributor>>,
                response: Response<List<Contributor>>
            ) {
                println("cfx callbackStyle onSucceed Thread: ${Thread.currentThread().name}")
                showContributors(response.body()!!)
            }

            override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
                println("cfx onFailure t: ${t.printStackTrace()}")
            }

        })
    }

    private fun showContributors(contributors: List<Contributor>) {
        println(contributors.size)
        val list = contributors.subList(0, 9)
        list.forEach {
            val textView = (LayoutInflater.from(this).inflate(R.layout.custom_text_view, null)) as TextView
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textView.layoutParams = layoutParams
            textView.text = it.login + " (${it.contributions})"
            mSuspendLayout.addView(textView)
            val blankView = View(this)
            val blankViewLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                50
            )
            blankView.layoutParams = blankViewLayoutParams
            mSuspendLayout.addView(blankView)
        }
    }

    private fun initView() {
        mSuspendLayout = findViewById(R.id.suspend_layout)
    }


}