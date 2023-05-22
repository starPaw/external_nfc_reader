package com.workingtimejoblogistic.joblogistic.api


import com.workingtimejoblogistic.joblogistic.model.Worker
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface API {
    //    @GET("api/data")
    @GET("user")
    suspend fun getWorkerByCard(@Query("card") card: Int): Array<Worker>

    @GET("user")
    suspend fun getWorker(): Array<Worker>

    @FormUrlEncoded
    @POST("upd_timestamps")
    suspend fun postTime(
        @Field("base_64_photo") base64Photo: String,
        @Field("card") card: String
    ): Response<Worker>
}