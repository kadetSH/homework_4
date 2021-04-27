package com.example.lesson03

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.lesson03.room.FilmDatabase
import com.example.lesson03.room.FilmRepository
import com.example.lesson03.viewmodel.RepoListFilmsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "channel"
    }

    private val repository: FilmRepository

    init {
        val filmDao = FilmDatabase.getFilmDatabase(appContext).filmDao()
        repository = FilmRepository(filmDao)
    }

    override suspend fun doWork(): Result {

        var nameFilm: String? = inputData.getString("nameFilm")
        var descriptionFilm: String? = inputData.getString("descriptionFilm")
        var imagePath: String? = inputData.getString("imagePath")
        var idFilm: Int? = inputData.getInt("idFilm", 0)
        var titleLabel: String? = inputData.getString("titleLabel")

        nameFilm?.let {
            descriptionFilm?.let { it1 ->
                imagePath?.let { it2 ->
                    idFilm?.let { it3 ->
                        titleLabel?.let { it4 ->
                            message(it, it1, it2, it3, it4)
                            cancellReminder(imagePath)
                        }
                    }
                }
            }
        }
        return Result.success()
    }

    private fun message(
        nameFilm: String,
        descriptionFilm: String,
        imagePath: String,
        idFilm: Int,
        titleLabel: String
    ) {


        var am: AlarmManager? = null

//        var myData: Data = Data.Builder()
//            .putString("nameFilm", nameFilm)
//            .putString("descriptionFilm", descriptionFilm)
//            .putString("imagePath", imagePath)
//            .build()

//        val film  = ArrayList<String>()
//        film.add(nameFilm)
//        film.add(descriptionFilm)
//        film.add(imagePath)

        var intent = Intent(applicationContext, ReminderActivity::class.java)
        intent.putExtra("EXTRAnameFilm", nameFilm)
        intent.putExtra("EXTRA_descriptionFilm", descriptionFilm)
        intent.putExtra("EXTRA_imagePath", imagePath)


        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(applicationContext, idFilm, intent, 0)

        am?.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000L, pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel name" // getString(R.string.channel_name)
            val description = "Channel description" //getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager =
                getSystemService(applicationContext, NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_video)
            .setContentTitle(titleLabel)
            .setContentText(nameFilm)
            .setPriority(NotificationCompat.PRIORITY_LOW) // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)

            .addAction(R.drawable.baseline_description_24, "Click me", pendingIntent)
//                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(resources,
//                        R.drawable.sq500x500))
//                        .setSummaryText("Summary"))
            /*.setStyle(NotificationCompat.BigTextStyle().bigText(resources.getString(R.string.lorem_ipsum)))*/
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(2, builder.build())
    }

    suspend fun cancellReminder(imagePath: String) {
        repository.updateReminder(0, imagePath, "")
        WorkManager.getInstance().cancelAllWorkByTag(imagePath)
    }


}