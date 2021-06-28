package com.example.lesson03.net

import com.example.lesson03.jsonMy.Json4KotlinBase
import com.example.lesson03.jsonMy.TheMovieDB
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("discover/movie?&sort_by=popularity.desc")
    fun getFilms(
        @Query("page") page_int: Int,
        @Query("api_key") API_KEY: String,
        @Query("language") lang: String
    ): Observable<TheMovieDB>

    @GET("movie/{id}?")
    fun getFilmsMessage(
        @Path("id") id: Int,
        @Query("api_key") API_KEY: String,
        @Query("language") lang: String
    ): Observable<Json4KotlinBase>

}