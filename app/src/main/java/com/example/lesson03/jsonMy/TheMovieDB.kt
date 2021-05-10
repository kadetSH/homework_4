package com.example.lesson03.jsonMy

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TheMovieDB {

    @SerializedName("page")
    @Expose
    private var page: Int? = null

    @SerializedName("results")
    @Expose
    private var results: List<Result?>? = null

    @SerializedName("total_pages")
    @Expose
    private var totalPages: Int? = null

    @SerializedName("total_results")
    @Expose
    private var totalResults: Int? = null

    fun getPage(): Int? {
        return page
    }

    fun setPage(page: Int?) {
        this.page = page
    }

    fun withPage(page: Int?): TheMovieDB? {
        this.page = page
        return this
    }

    fun getResults(): List<Result?>? {
        return results
    }

    fun setResults(results: List<Result?>?) {
        this.results = results
    }

    fun withResults(results: List<Result?>?): TheMovieDB? {
        this.results = results
        return this
    }

    fun getTotalPages(): Int? {
        return totalPages
    }

    fun setTotalPages(totalPages: Int?) {
        this.totalPages = totalPages
    }

    fun withTotalPages(totalPages: Int?): TheMovieDB {
        this.totalPages = totalPages
        return this
    }

    fun getTotalResults(): Int? {
        return totalResults
    }

    fun setTotalResults(totalResults: Int?) {
        this.totalResults = totalResults
    }

    fun withTotalResults(totalResults: Int?): TheMovieDB? {
        this.totalResults = totalResults
        return this
    }
}