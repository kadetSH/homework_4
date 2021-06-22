package com.example.lesson03

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.lesson03.jsonMy.FilmsJS
import com.example.lesson03.jsonMy.Json4KotlinBase
import com.example.lesson03.room.RFilm
import com.example.lesson03.snacbar.SnacbarData
import com.example.lesson03.viewmodel.RepoListFilmsViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class MessagingService : FirebaseMessagingService() {

    private val apiKey = BuildConfig.apiKey
    private val langRu = BuildConfig.langRu

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val titleMessage = remoteMessage.notification?.title.toString()
        if (titleMessage == Resources.getSystem().getString(R.string.MessagingService_title)) {
            val idFilm = remoteMessage.notification?.body.toString()
            getFilm(idFilm.toInt())
        }
    }

    @SuppressLint("CheckResult")
    private fun getFilm(id: Int) {

        App.instance.api.getFilmsMessage(id, apiKey, langRu)
            .subscribeOn(Schedulers.io())
            .doOnError {

            }
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                { result ->
                    result?.let { result ->
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
                },
                { error ->
                }
            )
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