package com.example.lesson03.net

import com.example.lesson03.jsonMy.FilmModel
import com.example.lesson03.jsonMy.Themoviedb
import com.example.lesson03.jsonMy.Themoviedb2
import com.example.lesson03.recyclerMy.FilmsItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

//    @GET("100?")  //api_key=576c50d1b23a4e5c26962aa1196de8f3
//    fun getFilms(@Query("api_key")api_key : String): Call<List<Themoviedb>>
//https://api.themoviedb.org/3/discover/
// movie?&sort_by=popularity.desc&api_key=2931998c3a80d7806199320f76d65298&page=1&language=ru-Ru
    @GET("movie?&sort_by=popularity.desc&api_key=2931998c3a80d7806199320f76d65298&language=ru-Ru")
    fun getFilms(@Query("page") page_int: Int): Call<Themoviedb2>

}