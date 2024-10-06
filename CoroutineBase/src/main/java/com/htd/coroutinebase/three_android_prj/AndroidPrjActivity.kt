package com.htd.coroutinebase.three_android_prj

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.lifecycleScope
import com.htd.coroutinebase.R
import com.htd.coroutinebase.common.api.GithubApi
import com.htd.coroutinebase.common.retrofit.RetrofitService
import com.htd.coroutinebase.common.bean.Contributor
import kotlinx.coroutines.launch

/**
 * 安卓项目里面的协程
 */
class AndroidPrjActivity : AppCompatActivity() {
    private lateinit var mAndroidLayout: LinearLayoutCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_android_prj)
        initView()
        coroutineStyle()

        /**
         * ktx
         * lifecycleScope 和生命周期绑定
         * 当 Activity 的 onDestroy 时会自动取消协程
         * lifecycleScope 内置的协程是主线程
         *
         * Dispatchers.Main.immediate 和 Dispatchers.Main 的区别：
         * Dispatchers.Main 会直接将使用 handler post 到主线程执行
         * Dispatchers.Main.immediate 会检查当前线程是不是主线程，如果是，立即执行协程内部代码
         * 如果不是，才 post 到主线程执行
         * public val Lifecycle.coroutineScope: LifecycleCoroutineScope
         *      get() {
         *          while (true) {
         *              val existing = mInternalScopeRef.get() as LifecycleCoroutineScopeImpl?
         *              if (existing != null) {
         *                  return existing
         *              }
         *              // 主线程
         *              val newScope = LifecycleCoroutineScopeImpl(this, SupervisorJob() + Dispatchers.Main.immediate)
         *              if (mInternalScopeRef.compareAndSet(null, newScope)) {
         *                  newScope.register()
         *                  return newScope
         *              }
         *          }
         *      }
         */
        lifecycleScope.launch {
            // 发生在主线程: cfx Thread: main
            println("cfx Thread: ${Thread.currentThread().name}" )
        }
    }

    private fun initView() {
        mAndroidLayout = findViewById(R.id.android_layout)
    }


    private fun coroutineStyle() {
        // 主线程的协程
        lifecycleScope.launch {
            val retrofit = RetrofitService.getRetrofit().create(GithubApi::class.java)
            println("cfx coroutineStyle Thread: ${Thread.currentThread().name}")
            val contributors = retrofit.coroutineContributors("square", "retrofit")
            println("cfx coroutineStyle Thread: ${Thread.currentThread().name}")
            showContributors(contributors)

        }
    }

    private fun showContributors(contributors: List<Contributor>) {
        println(contributors.size)

        contributors.subList(0, 9).map { contributor ->
            "${contributor.login} (${contributor.contributions})"
        }.forEach {
            val blankView = View(this)
            val blankViewLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                50
            )
            blankView.layoutParams = blankViewLayoutParams
            mAndroidLayout.addView(blankView)
            val textView =
                (LayoutInflater.from(this).inflate(R.layout.custom_text_view, null)) as TextView
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textView.layoutParams = layoutParams
            textView.text = it
            mAndroidLayout.addView(textView)
        }
    }
}