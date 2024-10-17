package com.htd.coroutinebase.seven_de_magic

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
import kotlinx.coroutines.launch

/**
 * 为什么挂起函数不会阻塞线程？
 * 其本质还是嵌套回调
 * 当执行完后台任务后，通过 handler post 到主线程
 * 所以不会阻塞线程
 */
class DeMagicActivity : AppCompatActivity() {
    private lateinit var mContainer: LinearLayoutCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_de_magic)
        initView()

        coroutineStyle()
    }

    private fun initView() {
        mContainer = findViewById(R.id.container_layout)
    }

    /*
    反编译以后的结果
    @Nullable
         public final Object invokeSuspend(@NotNull Object $result) {
            Object var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            Object var10000;
            Thread var10001;
            StringBuilder var7;
            switch(this.label) {
            case 0:
               ResultKt.throwOnFailure($result);
               GithubApi retrofit = (GithubApi)RetrofitService.INSTANCE.getRetrofit().create(GithubApi.class);
               var7 = (new StringBuilder()).append("cfx coroutineStyle Thread: ");
               var10001 = Thread.currentThread();
               Intrinsics.checkNotNullExpressionValue(var10001, "Thread.currentThread()");
               String var3 = var7.append(var10001.getName()).toString();
               System.out.println(var3);
               this.label = 1;
               var10000 = retrofit.coroutineContributors("square", "retrofit", this);
               if (var10000 == var5) {
                  return var5;
               }
               break;
            case 1:
               ResultKt.throwOnFailure($result);
               var10000 = $result;
               break;
            default:
               throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            List contributors = (List)var10000;
            var7 = (new StringBuilder()).append("cfx coroutineStyle Thread: ");
            var10001 = Thread.currentThread();
            Intrinsics.checkNotNullExpressionValue(var10001, "Thread.currentThread()");
            String var4 = var7.append(var10001.getName()).toString();
            System.out.println(var4);
            DeMagicActivity.this.showContributors(contributors);
            return Unit.INSTANCE;
         }
     */
    private fun coroutineStyle() = lifecycleScope.launch {
        val retrofit = RetrofitService.getRetrofit().create(GithubApi::class.java)
        // println("cfx coroutineStyle Thread: ${Thread.currentThread().name}")
        val contributors = retrofit.coroutineContributors("square", "retrofit")
        // println("cfx coroutineStyle Thread: ${Thread.currentThread().name}")
        showContributors(contributors)

        /**
         * 编译器会在编译时改写协程代码
         * 上述代码类似于
         * lifecycleScope.launch {
         *     switchToBackground {
         *         val contributors = retrofit.coroutineContributors("square", "retrofit")
         *         switchToMain {
         *             showContributors(contributors)
         *         }
         *     }
         * }
         * 所以本质还是通过 handler post 消息到主线程
         * 因此不会阻塞 ui 线程刷新
         */
    }

    private fun showContributors(contributors: List<Contributor>) {
        println(contributors.size)
        contributors.let {

        }

        contributors.subList(0, 9).map { contributor ->
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