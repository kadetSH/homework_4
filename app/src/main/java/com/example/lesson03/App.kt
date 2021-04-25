package com.example.lesson03

import android.app.Application
import com.example.lesson03.net.Api
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


class App : Application() {

    lateinit var api: Api

    override fun onCreate() {
        super.onCreate()
        instance = this

        initRetrofit()
    }

    private fun initRetrofit() {

//        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//            .tlsVersions(TlsVersion.TLS_1_2)  //, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0
//            .cipherSuites(
//                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
//                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
//                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//                CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
//                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
//                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
//            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .writeTimeout(6, TimeUnit.SECONDS)
            .readTimeout(9, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor()
                .apply {
                    if (BuildConfig.DEBUG) {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                })
            .pingInterval(1, TimeUnit.SECONDS)
//            .connectionSpecs(Collections.singletonList(spec))
            .addInterceptor { chain ->
                return@addInterceptor chain.proceed(
                    chain

                        .request()
                        .newBuilder()
                        .build()
                )
            }
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
            "https://api.themoviedb.org/3/" //99.86.242.109  api.themoviedb.org  //100?api_key=576c50d1b23a4e5c26962aa1196de8f3

        lateinit var instance: App
            private set
    }

}