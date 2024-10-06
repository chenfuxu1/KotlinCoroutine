package com.htd.coroutinebase.nine_structured_concurrency

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
import com.htd.coroutinebase.common.retrofit.RetrofitService
import com.htd.coroutinebase.common.bean.Contributor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 结构化并发
 * 协程的结构化并发是一层对多层的关系
 */
class StructuredConcurrencyActivity : AppCompatActivity() {
    private lateinit var mContainer: LinearLayoutCompat
    private var mDisposable: Disposable? = null
    private var mCoroutineJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_structured_concurrency)
        initView()

        /**
         * 每个协程可以拿到返回的 job 对象
         * 可以通过 job.cancel() 取消协程
         */
        val job = lifecycleScope.launch {
            println("cfx Coroutine start")
            delay(5000)
            println("cfx Coroutine end")
        }
        job.cancel()

        // callbackStyle()
        mCoroutineJob = coroutineStyle()
        mDisposable = rxJavaStyle()
    }

    private fun initView() {
        mContainer = findViewById(R.id.container_layout)
    }

    // 1.回调的方式
    private fun callbackStyle() {
        val retrofit = RetrofitService.getRetrofit().create(GithubApi::class.java)
        // 1.第一次发起请求
        val call = retrofit.contributors("square", "retrofit")
        call.enqueue(object : Callback<List<Contributor>> {
            override fun onResponse(
                call: Call<List<Contributor>>,
                response: Response<List<Contributor>>
            ) {
                println("cfx callbackStyle retrofit onSucceed Thread: ${Thread.currentThread().name}")
                showContributors(response.body()!!)
                // 2.请求成功后第二次发起请求
                val requestOkHttp = retrofit.contributors("square", "okhttp")
                requestOkHttp.enqueue(object : Callback<List<Contributor>> {
                    override fun onResponse(
                        call: Call<List<Contributor>>,
                        response: Response<List<Contributor>>
                    ) {
                        println("cfx callbackStyle requestOkHttp onSucceed Thread: ${Thread.currentThread().name}")
                        showContributors(response.body()!!)
                    }

                    override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
                        println("cfx onFailure t: ${t.printStackTrace()}")
                    }

                })
            }

            override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
                println("cfx onFailure t: ${t.printStackTrace()}")
            }

        })
    }

    /**
     * 2.协程
     * 2.1 lifecycleScope.launch 启动了一个协程，内部 receiver 是 CoroutineScope
     * lifecycleScope 管理着 CoroutineScope
     *
     * 2.2 lifecycleScope.launch 的内部属于 CoroutineScope，在内部再开启的子协程受 CoroutineScope 管理
     * 所以 lifecycleScope 管理 CoroutineScope，CoroutineScope 再管理内部的子协程
     *
     * 2.3 lifecycleScope.cancel 会取消内部的所有协程、子协程、子子协程...
     */
    private fun coroutineStyle() = lifecycleScope.launch {
        // 内部子协程
        launch {

        }
        val api = RetrofitService.getRetrofit().create(GithubApi::class.java)
        val contributors = api.coroutineContributors("square", "retrofit").filter {
             it.contributions > 40
        }
        showContributors(contributors)
    }

    // 3.rxjava
    private fun rxJavaStyle(): Disposable {
        val api = RetrofitService.getRetrofit().create(GithubApi::class.java)
        /**
         * 链式调用
         * 第一行是进行网络请求
         * 然后数据过滤
         * 然后是回到主线程
         * 最后是显示
         */
        return api.contributorsRx("square", "retrofit")
            .map { list ->
                list.filter {
                    it.contributions > 50
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::showContributors)

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

    override fun onDestroy() {
        super.onDestroy()
        // 取消 rxjava 的后续所有流程
        mDisposable?.dispose()
        // 取消协程
        mCoroutineJob?.cancel() // 只取消当前的协程
        lifecycleScope.cancel() // 会取消 lifecycleScope 启动的所有协程
    }
}