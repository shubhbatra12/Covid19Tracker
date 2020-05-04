package com.shubh.covid19tracker.India

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClientStates {

    val gson = GsonBuilder()
        .create()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://covid19-india-adhikansh.herokuapp.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api = retrofit.create(
        CoronaIndService::class.java)
}