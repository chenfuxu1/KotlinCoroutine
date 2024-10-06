package com.htd.coroutinebase.common.retrofit

import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-02 20:41
 *
 * Desc:
 */
object RetrofitService {
    private const val API_URL = "https://api.github.com"

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    public fun getRetrofit(): Retrofit {
        return retrofit
    }
}