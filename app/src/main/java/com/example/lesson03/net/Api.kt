package com.example.lesson03.net

import com.example.lesson03.jsonMy.Json4Kotlin_Base
import com.example.lesson03.jsonMy.Themoviedb
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("discover/movie?&sort_by=popularity.desc")
    fun getFilms(
        @Query("page") page_int: Int,
        @Query("api_key") API_KEY: String,
        @Query("language") lang: String
    ): Call<Themoviedb>

    @GET("movie/{id}?") //399566
    fun getFilmsMessage(
        @Path("id") id: Int,
        @Query("api_key") API_KEY: String,
        @Query("language") lang: String
    ): Call<Json4Kotlin_Base>

}