package com.example.lesson03.jsonMy

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class Result {

    @SerializedName("adult")
    @Expose
    private var adult: Boolean? = null

    @SerializedName("backdrop_path")
    @Expose
    private var backdropPath: String? = null

    @SerializedName("genre_ids")
    @Expose
    private var genreIds: List<Int?>? = null

    @SerializedName("id")
    @Expose
    private var id: Int? = null

    @SerializedName("original_language")
    @Expose
    private var originalLanguage: String? = null

    @SerializedName("original_title")
    @Expose
    private var originalTitle: String? = null

    @SerializedName("overview")
    @Expose
    private var overview: String? = null

    @SerializedName("popularity")
    @Expose
    private var popularity: Double? = null

    @SerializedName("poster_path")
    @Expose
    private var posterPath: String? = null

    @SerializedName("release_date")
    @Expose
    private var releaseDate: String? = null

    @SerializedName("title")
    @Expose
    private var title: String? = null

    @SerializedName("video")
    @Expose
    private var video: Boolean? = null

    @SerializedName("vote_average")
    @Expose
    private var voteAverage: Double? = null

    @SerializedName("vote_count")
    @Expose
    private var voteCount: Int? = null

    fun getAdult(): Boolean? {
        return adult
    }

    fun setAdult(adult: Boolean?) {
        this.adult = adult
    }

    fun withAdult(adult: Boolean?): Result? {
        this.adult = adult
        return this
    }

    fun getBackdropPath(): String? {
        return backdropPath
    }

    fun setBackdropPath(backdropPath: String?) {
        this.backdropPath = backdropPath
    }

    fun withBackdropPath(backdropPath: String?): Result? {
        this.backdropPath = backdropPath
        return this
    }

    fun getGenreIds(): List<Int?>? {
        return genreIds
    }

    fun setGenreIds(genreIds: List<Int?>?) {
        this.genreIds = genreIds
    }

    fun withGenreIds(genreIds: List<Int?>?): Result? {
        this.genreIds = genreIds
        return this
    }

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun withId(id: Int?): Result? {
        this.id = id
        return this
    }

    fun getOriginalLanguage(): String? {
        return originalLanguage
    }

    fun setOriginalLanguage(originalLanguage: String?) {
        this.originalLanguage = originalLanguage
    }

    fun withOriginalLanguage(originalLanguage: String?): Result? {
        this.originalLanguage = originalLanguage
        return this
    }

    fun getOriginalTitle(): String? {
        return originalTitle
    }

    fun setOriginalTitle(originalTitle: String?) {
        this.originalTitle = originalTitle
    }

    fun withOriginalTitle(originalTitle: String?): Result? {
        this.originalTitle = originalTitle
        return this
    }

    fun getOverview(): String? {
        return overview
    }

    fun setOverview(overview: String?) {
        this.overview = overview
    }

    fun withOverview(overview: String?): Result? {
        this.overview = overview
        return this
    }

    fun getPopularity(): Double? {
        return popularity
    }

    fun setPopularity(popularity: Double?) {
        this.popularity = popularity
    }

    fun withPopularity(popularity: Double?): Result? {
        this.popularity = popularity
        return this
    }

    fun getPosterPath(): String? {
        return posterPath
    }

    fun setPosterPath(posterPath: String?) {
        this.posterPath = posterPath
    }

    fun withPosterPath(posterPath: String?): Result? {
        this.posterPath = posterPath
        return this
    }

    fun getReleaseDate(): String? {
        return releaseDate
    }

    fun setReleaseDate(releaseDate: String?) {
        this.releaseDate = releaseDate
    }

    fun withReleaseDate(releaseDate: String?): Result? {
        this.releaseDate = releaseDate
        return this
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun withTitle(title: String?): Result? {
        this.title = title
        return this
    }

    fun getVideo(): Boolean? {
        return video
    }

    fun setVideo(video: Boolean?) {
        this.video = video
    }

    fun withVideo(video: Boolean?): Result? {
        this.video = video
        return this
    }

    fun getVoteAverage(): Double? {
        return voteAverage
    }

    fun setVoteAverage(voteAverage: Double?) {
        this.voteAverage = voteAverage
    }

    fun withVoteAverage(voteAverage: Double?): Result? {
        this.voteAverage = voteAverage
        return this
    }

    fun getVoteCount(): Int? {
        return voteCount
    }

    fun setVoteCount(voteCount: Int?) {
        this.voteCount = voteCount
    }

    fun withVoteCount(voteCount: Int?): Result? {
        this.voteCount = voteCount
        return this
    }

}