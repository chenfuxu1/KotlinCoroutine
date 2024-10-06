package com.htd.coroutinebase.six_with_context

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.htd.coroutinebase.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 如果协程内部再切的协程是一致的，不会重复切换线程
 */
class WithContextActivity : AppCompatActivity() {
    private lateinit var mContainer : LinearLayoutCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_context)
        initView()

        CoroutineScope(Dispatchers.Main).launch {
            // 1.后台获取数据
            val data = getData()

            // 2.处理数据
            processData(data)

            // 3.更新界面
        }
    }

    private fun initView() {
        mContainer = findViewById(R.id.container_layout)
    }

    private suspend fun getData(): String {
        return withContext(Dispatchers.IO) {
            println("cfx getData 获取数据")
            "data"
        }
    }

    private suspend fun processData(data: String) {
        return withContext(Dispatchers.IO) {
            println("cfx getData 处理数据: $data")
        }
    }

}