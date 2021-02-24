package com.example.lesson03.jsonMy;

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Themoviedb {
    @SerializedName("adult")
    @Expose
    var adult: Boolean? = null

    @SerializedName("backdrop_path")
    @Expose
    var backdropPath: String? = null

    @SerializedName("belongs_to_collection")
    @Expose
    var belongsToCollection: Any? = null

    @SerializedName("budget")
    @Expose
    var budget: Int? = null

    @SerializedName("genres")
    @Expose
    private var genres: List<Genres>? = null

    @SerializedName("homepage")
    @Expose
    var homepage: String? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("imdb_id")
    @Expose
    var imdbId: String? = null

    @SerializedName("original_language")
    @Expose
    var originalLanguage: String? = null

    @SerializedName("original_title")
    @Expose
    var originalTitle: String? = null

    @SerializedName("overview")
    @Expose
    var overview: String? = null

    @SerializedName("popularity")
    @Expose
    var popularity: Double? = null

    @SerializedName("poster_path")
    @Expose
    var posterPath: String? = null

    @SerializedName("production_companies")
    @Expose
    private var productionCompanies: List<Production_companies>? = null

    @SerializedName("production_countries")
    @Expose
    private var productionCountries: List<Production_countries>? = null

    @SerializedName("release_date")
    @Expose
    var releaseDate: String? = null

    @SerializedName("revenue")
    @Expose
    var revenue: Int? = null

    @SerializedName("runtime")
    @Expose
    var runtime: Int? = null

    @SerializedName("spoken_languages")
    @Expose
    private var spokenLanguages: List<Spoken_languages>? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("tagline")
    @Expose
    var tagline: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("video")
    @Expose
    var video: Boolean? = null

    @SerializedName("vote_average")
    @Expose
    var voteAverage: Double? = null

    @SerializedName("vote_count")
    @Expose
    var voteCount: Int? = null

    fun withAdult(adult: Boolean?): Themoviedb {
        this.adult = adult
        return this
    }

    fun withBackdropPath(backdropPath: String?): Themoviedb {
        this.backdropPath = backdropPath
        return this
    }

    fun withBelongsToCollection(belongsToCollection: Any?): Themoviedb {
        this.belongsToCollection = belongsToCollection
        return this
    }

    fun withBudget(budget: Int?): Themoviedb {
        this.budget = budget
        return this
    }

    fun getGenres(): List<Genres>? {
        return genres
    }

    fun setGenres(genres: List<Genres>?) {
        this.genres = genres
    }

    fun withGenres(genres: List<Genres>?): Themoviedb {
        this.genres = genres
        return this
    }

    fun withHomepage(homepage: String?): Themoviedb {
        this.homepage = homepage
        return this
    }

    fun withId(id: Int?): Themoviedb {
        this.id = id
        return this
    }

    fun withImdbId(imdbId: String?): Themoviedb {
        this.imdbId = imdbId
        return this
    }

    fun withOriginalLanguage(originalLanguage: String?): Themoviedb {
        this.originalLanguage = originalLanguage
        return this
    }

    fun withOriginalTitle(originalTitle: String?): Themoviedb {
        this.originalTitle = originalTitle
        return this
    }

    fun withOverview(overview: String?): Themoviedb {
        this.overview = overview
        return this
    }

    fun withPopularity(popularity: Double?): Themoviedb {
        this.popularity = popularity
        return this
    }

    fun withPosterPath(posterPath: String?): Themoviedb {
        this.posterPath = posterPath
        return this
    }

    fun getProductionCompanies(): List<Production_companies>? {
        return productionCompanies
    }

    fun setProductionCompanies(productionCompanies: List<Production_companies>?) {
        this.productionCompanies = productionCompanies
    }

    fun withProductionCompanies(productionCompanies: List<Production_companies>?): Themoviedb {
        this.productionCompanies = productionCompanies
        return this
    }

    fun getProductionCountries(): List<Production_countries>? {
        return productionCountries
    }

    fun setProductionCountries(productionCountries: List<Production_countries>?) {
        this.productionCountries = productionCountries
    }

    fun withProductionCountries(productionCountries: List<Production_countries>?): Themoviedb {
        this.productionCountries = productionCountries
        return this
    }

    fun withReleaseDate(releaseDate: String?): Themoviedb {
        this.releaseDate = releaseDate
        return this
    }

    fun withRevenue(revenue: Int?): Themoviedb {
        this.revenue = revenue
        return this
    }

    fun withRuntime(runtime: Int?): Themoviedb {
        this.runtime = runtime
        return this
    }

    fun getSpokenLanguages(): List<Spoken_languages>? {
        return spokenLanguages
    }

    fun setSpokenLanguages(spokenLanguages: List<Spoken_languages>?) {
        this.spokenLanguages = spokenLanguages
    }

    fun withSpokenLanguages(spokenLanguages: List<Spoken_languages>?): Themoviedb {
        this.spokenLanguages = spokenLanguages
        return this
    }

    fun withStatus(status: String?): Themoviedb {
        this.status = status
        return this
    }

    fun withTagline(tagline: String?): Themoviedb {
        this.tagline = tagline
        return this
    }

    fun withTitle(title: String?): Themoviedb {
        this.title = title
        return this
    }

    fun withVideo(video: Boolean?): Themoviedb {
        this.video = video
        return this
    }

    fun withVoteAverage(voteAverage: Double?): Themoviedb {
        this.voteAverage = voteAverage
        return this
    }

    fun withVoteCount(voteCount: Int?): Themoviedb {
        this.voteCount = voteCount
        return this
    }




}
