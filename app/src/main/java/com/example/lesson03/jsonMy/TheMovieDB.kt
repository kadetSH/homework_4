package com.example.lesson03.jsonMy

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TheMovieDB {

    @SerializedName("results")
    @Expose
    private var results: List<Result?>? = null

       fun getResults(): List<Result?>? {
        return results
    }

}