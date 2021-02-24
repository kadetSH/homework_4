package com.example.lesson03.net

import android.app.Application
import com.example.lesson03.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    lateinit var api: Api

    override fun onCreate() {
        super.onCreate()
        instance = this

        initRetrofit()
    }

    private fun initRetrofit() {

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                return@addInterceptor chain.proceed(
                    chain
                        .request()
                        .newBuilder()
                        .build()
                )
            }
            .addInterceptor(HttpLoggingInterceptor()
                .apply {
                    if (BuildConfig.DEBUG) {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(Api::class.java)
    }

    companion object {
        const val BASE_URL =
            "https://api.themoviedb.org/3/discover/"  //100?api_key=576c50d1b23a4e5c26962aa1196de8f3

        lateinit var instance: App
            private set
    }

}