package com.shubh.covid19tracker

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoronaService {

    @GET("countries")
    suspend fun getMyUser(): Response<List<Country>>

    @GET("countries/{}")
    suspend fun getUser(@Path("id") id:String): Response<Country>

    @GET("search/users")
    suspend fun searchUser(@Query("q") name:String): Response<SearchResponse>

}