package com.example.lesson03.jsonMy

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class Result(

    @SerializedName("adult")
    @Expose
    private var adult: Boolean? = null,

    @SerializedName("backdrop_path")
    @Expose
    private var backdropPath: String? = null,

    @SerializedName("genre_ids")
    @Expose
    private var genreIds: List<Int?>? = null,

    @SerializedName("id")
    @Expose
    private var id: Int? = null,

    @SerializedName("original_language")
    @Expose
    private var originalLanguage: String? = null,

    @SerializedName("original_title")
    @Expose
    private var originalTitle: String? = null,

    @SerializedName("overview")
    @Expose
    private var overview: String? = null,

    @SerializedName("popularity")
    @Expose
    private var popularity: Double? = null,

    @SerializedName("poster_path")
    @Expose
    private var posterPath: String? = null,

    @SerializedName("release_date")
    @Expose
    private var releaseDate: String? = null,

    @SerializedName("title")
    @Expose
    private var title: String? = null,

    @SerializedName("video")
    @Expose
    private var video: Boolean? = null,

    @SerializedName("vote_average")
    @Expose
    private var voteAverage: Double? = null,

    @SerializedName("vote_count")
    @Expose
    private var voteCount: Int? = null

) {
    fun getTitle(): Any {
        return title!!
    }

    fun getBackdropPath(): Any {
        return backdropPath!!
    }

    fun getOverview(): Any {
        return overview!!
    }
}

