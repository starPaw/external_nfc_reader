package com.workingtimejoblogistic.joblogistic.api

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface API {
//    @GET("api/data")
    @GET("user")
    suspend fun getWorker():Array<Worker>

    @FormUrlEncoded
    @POST("upd_timestamps")
    suspend fun postTime(
        @Field("base_64_photo") base64Photo: String,
        @Field("card") card: String
    ): Response<Worker>
}


//https://x4ot-ncyw-z9th.n7.xano.io/api:JjCJI81e/user
//https://x4ot-ncyw-z9th.n7.xano.io/api:JjCJI81e/upd_timestamps