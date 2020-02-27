package com.ptktop.recycleviewloadmoreandroid.data.network.api

import com.ptktop.recycleviewloadmoreandroid.data.network.model.CoinResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinApi {

    @GET("/v1/public/coins")
    fun coinRanking(@Query("limit") limit: Int): Observable<CoinResponse>

    @GET("/v1/public/coins")
    fun coinRankingSearch(
        @Query("prefix") prefix: String,
        @Query("symbols") symbols: String,
        @Query("slugs") slugs: String,
        @Query("limit") limit: Int
    ): Observable<CoinResponse>

}