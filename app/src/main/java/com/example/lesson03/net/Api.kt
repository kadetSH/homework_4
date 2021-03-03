package com.example.lesson03.net

import com.example.lesson03.jsonMy.Themoviedb
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("movie?&sort_by=popularity.desc")
     fun getFilms(
        @Query("page") page_int: Int,
        @Query("api_key") API_KEY: String,
        @Query("language") lang: String
    ): Call<Themoviedb>

}