package com.example.lesson03

import android.content.res.Resources
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.lesson03.jsonMy.Json4KotlinBase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class MessagingService : FirebaseMessagingService() {

    private val API_KEY = "2931998c3a80d7806199320f76d65298"
    private val langRu = "ru-Ru"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val titleMessage = remoteMessage.notification?.title.toString()
        if (titleMessage == Resources.getSystem().getString(R.string.MessagingService_title)) {
            val idFilm = remoteMessage.notification?.body.toString()
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

                        res?.let { result ->
                            val nameFilm = result.title
                            val imagePath = result.poster_path
                            val descriptionFilm = result.overview
                            val timeReminder = 5000L
                            val idFilm = result.id

                            workManagerReminder(
                                nameFilm,
                                imagePath,
                                descriptionFilm,
                                timeReminder,
                                idFilm
                            )
                        }
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
            .putString(Resources.getSystem().getString(R.string.UploadWorker_nameFilm), nameFilm)
            .putString(
                Resources.getSystem().getString(R.string.UploadWorker_descriptionFilm),
                descriptionFilm
            )
            .putString(Resources.getSystem().getString(R.string.UploadWorker_imagePath), imagePath)
            .putInt(Resources.getSystem().getString(R.string.UploadWorker_idFilm), idFilm)
            .putString(
                Resources.getSystem().getString(R.string.UploadWorker_titleLabel),
                Resources.getSystem().getString(R.string.UploadWorker_viewRecommendation)
            )
            .build()

        val myWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .addTag(imagePath)
            .setInitialDelay(timeReminder, TimeUnit.MILLISECONDS)
            .setInputData(myData)
            .build()
        WorkManager.getInstance().enqueue(myWorkRequest)
    }

}