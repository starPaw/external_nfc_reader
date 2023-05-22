package com.workingtimejoblogistic.joblogistic.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
//    val okHttpClient = OkHttpClient.Builder()
////        .hostnameVerifier { _, _ -> true }
//        .build()
    val BASE_URL = "https://x4ot-ncyw-z9th.n7.xano.io/api:JjCJI81e/"
    // retrofit instance object class
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
            .build()
    }

    val api: API by lazy {
        retrofit.create(API::class.java)
    }
}