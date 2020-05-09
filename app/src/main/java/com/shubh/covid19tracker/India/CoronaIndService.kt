package com.shubh.covid19tracker.India

import retrofit2.Response
import retrofit2.http.GET

interface CoronaIndService {

    @GET("states")
    suspend fun getMyState(): Response<SearchResponse>
}