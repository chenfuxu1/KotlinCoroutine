package com.htd.coroutinebase.common.api

import com.htd.coroutinebase.common.bean.Contributor
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.CompletableFuture


/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-02 20:34
 *
 * Desc:
 */
/**
 * Project: CoroutineBase
 * Create By: Chen.F.X
 * DateTime: 2024-10-02 20:34
 *
 * Desc:
 */
interface GithubApi {
    @GET("/repos/{owner}/{repo}/contributors")
    fun contributors(
        @Path("owner") owner: String, // square
        @Path("repo") repo: String // retrofit
    ): Call<List<Contributor>>

    @GET("/repos/{owner}/{repo}/contributors")
    suspend fun coroutineContributors(
        @Path("owner") owner: String, // square
        @Path("repo") repo: String // retrofit
    ): List<Contributor>

    @GET("/repos/{owner}/{repo}/contributors")
    fun contributorsRx(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Single<List<Contributor>>

    @GET("/repos/{owner}/{repo}/contributors")
    fun contributorsFuture(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): CompletableFuture<List<Contributor>>
}