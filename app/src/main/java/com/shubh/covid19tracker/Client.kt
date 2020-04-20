package com.shubh.covid19tracker

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Client {

    val gson = GsonBuilder()
        .create()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://corona.lmao.ninja/v2/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api = retrofit.create(CoronaService::class.java)
}