package com.example.shoutbox

import retrofit2.Call
import retrofit2.http.*

interface PostApi {
    @GET("messages")
    fun getPosts(): Call<ArrayList<Posts>>

    @FormUrlEncoded
    @POST("message")
    fun addPost(@Field("content") content: String, @Field("login") login:String): Call<DefaultResponse>

    @FormUrlEncoded
    @PUT("message/{id}")
    fun editPost(@Path("id") id:String, @Field("content") content: String, @Field("login") login: String): Call<DefaultResponse>

    @DELETE("message/{id}")
    fun deletePost(@Path("id") id: String): Call<DefaultResponse>
}