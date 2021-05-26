package com.example.lesson03

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.lesson03.jsonMy.Json4KotlinBase
import com.example.lesson03.recyclerMy.FilmsItem
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class MessagingService : FirebaseMessagingService() {

    private val TAG = "FirebaseMessageService"
    private val API_KEY = "2931998c3a80d7806199320f76d65298"
    private val langRu = "ru-Ru"


    override fun onCreate() {
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val titleMessage = remoteMessage.notification?.title.toString()
        if (titleMessage == "фильм название") {
            var idFilm = remoteMessage.notification?.body.toString()
            getFilm(idFilm.toInt())
        }

    }

    private fun getFilm(id: Int) {
        App.instance.api.getFilmsMessage(id, API_KEY, langRu)
            .enqueue(object : Callback<Json4KotlinBase> {
                override fun onFailure(call: Call<Json4KotlinBase>, t: Throwable) {
                    call.cancel()
                }

                override fun onResponse(
                    call: Call<Json4KotlinBase>,
                    response: Response<Json4KotlinBase>,
                ) {

                    if (response.isSuccessful) {

                        val res = response.body()

                        res?.let {
                            it
                            val nameFilm = it.title
                            val imagePath = it.poster_path
                            val descriptionFilm = it.overview
                            val timeReminder = 5000L
                            val idFilm = it.id

                            workManagerReminder(
                                nameFilm,
                                imagePath,
                                descriptionFilm,
                                timeReminder,
                                idFilm
                            )
                        }

                    } else {
                        println("")
                    }

                }
            })
    }

    private fun workManagerReminder(
        nameFilm: String,
        imagePath: String,
        descriptionFilm: String,
        timeReminder: Long,
        idFilm: Int
    ) {
        val myData: Data = Data.Builder()
            .putString("nameFilm", nameFilm)
            .putString("descriptionFilm", descriptionFilm)
            .putString("imagePath", imagePath)
            .putInt("idFilm", idFilm)
            .putString("titleLabel", "Рекомендуем посмотреть")
//            .putAll("filmsItem", FilmsItem)
            .build()

        val myWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .addTag(imagePath)
            .setInitialDelay(timeReminder, TimeUnit.MILLISECONDS)
            .setInputData(myData)
            .build()
        WorkManager.getInstance().enqueue(myWorkRequest)
    }

}