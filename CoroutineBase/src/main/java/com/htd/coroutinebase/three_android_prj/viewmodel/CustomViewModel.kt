package com.htd.coroutinebase.three_android_prj.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-04 10:38
 *
 * Desc:
 */
/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-04 10:38
 *
 * Desc:
 */
class CustomViewModel: ViewModel() {
    /**
     * public val ViewModel.viewModelScope: CoroutineScope
     *     get() {
     *         val scope: CoroutineScope? = this.getTag(JOB_KEY)
     *         if (scope != null) {
     *             return scope
     *         }
     *         // 和 lifecycleScope 一样，也是在主线程
     *         return setTagIfAbsent(JOB_KEY,
     *         CloseableCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
     *         )
     *     }
     */
    fun test() {
        viewModelScope.launch {
            // 启动协程
        }
    }
}