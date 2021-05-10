package com.example.lesson03

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.*
import com.example.lesson03.room.FilmDatabase
import com.example.lesson03.room.FilmRepository

class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "channel"
    }

    private val repository: FilmRepository

    init {
        val filmDao = FilmDatabase.getFilmDatabase(Application()).filmDao()
        repository = FilmRepository(filmDao)
    }

    override suspend fun doWork(): Result {

        val nameFilm: String? = inputData.getString("nameFilm")
        val descriptionFilm: String? = inputData.getString("descriptionFilm")
        val imagePath: String? = inputData.getString("imagePath")
        val idFilm: Int? = inputData.getInt("idFilm", 0)
        val titleLabel: String? = inputData.getString("titleLabel")

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


        val am: AlarmManager? = null

        val intent = Intent(applicationContext, ReminderActivity::class.java)
        intent.putExtra("EXTRAnameFilm", nameFilm)
        intent.putExtra("EXTRA_descriptionFilm", descriptionFilm)
        intent.putExtra("EXTRA_imagePath", imagePath)


        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(applicationContext, idFilm, intent, 0)

        am?.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000L, pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel name"
            val description = "Channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            val notificationManager =
                getSystemService(applicationContext, NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_video)
            .setContentTitle(titleLabel)
            .setContentText(nameFilm)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.baseline_description_24, "Click me", pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(2, builder.build())
    }

    private suspend fun cancellReminder(imagePath: String) {
        repository.updateReminder(0, imagePath, "")
        WorkManager.getInstance().cancelAllWorkByTag(imagePath)
    }


}