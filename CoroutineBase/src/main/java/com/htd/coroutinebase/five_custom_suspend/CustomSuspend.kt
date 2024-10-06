package com.htd.coroutinebase.five_custom_suspend

import com.htd.coroutinebase.common.api.GithubApi
import com.htd.coroutinebase.common.retrofit.RetrofitService
import com.htd.coroutinebase.common.bean.Contributor

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-04 11:18
 *
 * Desc:
 */
/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-04 11:18
 *
 * Desc: 自定义挂起函数
 */
suspend fun CustomSuspendFunc(): List<Contributor> {
    return RetrofitService.getRetrofit().create(GithubApi::class.java).coroutineContributors("square", "retrofit")
}