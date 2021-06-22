package com.example.lesson03

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.*
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.FilmDatabase
import com.example.lesson03.room.FilmRepository

class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "channel"
    }

    val context: Context = this.applicationContext
    private val repository: FilmRepository

    init {
        val filmDao = FilmDatabase.getFilmDatabase(Application()).filmDao()
        repository = FilmRepository(filmDao)
    }

    override suspend fun doWork(): Result {

        val nameFilm: String? =
            inputData.getString(context.getString(R.string.UploadWorker_nameFilm))
        val descriptionFilm: String? = inputData.getString(
            context.getString(R.string.UploadWorker_descriptionFilm)
        )
        val imagePath: String? =
            inputData.getString(context.getString(R.string.UploadWorker_imagePath))
        val idFilm: Int? =
            inputData.getInt(context.getString(R.string.UploadWorker_idFilm), 0)
        val titleLabel: String? =
            inputData.getString(context.getString(R.string.UploadWorker_titleLabel))
        val check: String? =
            inputData.getString(context.getString(R.string.UploadWorker_check))
        val star: Boolean? =
            inputData.getBoolean(context.getString(R.string.UploadWorker_star), false)
        val reminder: Int? =
            inputData.getInt(context.getString(R.string.UploadWorker_reminder), 0)
        val reminderDataTime: String? = inputData.getString(
            context.getString(R.string.UploadWorker_reminderDataTime)
        )

        val filmItem = nameFilm?.let { _nameFilm ->
            imagePath?.let { _imagePath ->
                descriptionFilm?.let { _descriptionFilm ->
                    check?.let { _check ->
                        star?.let { _star ->
                            idFilm?.let { _idFilm ->
                                reminder?.let { _reminder ->
                                    reminderDataTime?.let { _reminderDataTime ->
                                        FilmsItem(
                                            _nameFilm,
                                            _imagePath,
                                            _descriptionFilm,
                                            _check,
                                            _star,
                                            _idFilm,
                                            _reminder,
                                            _reminderDataTime
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        nameFilm?.let { nameFilm ->
            imagePath?.let { imagePath ->
                idFilm?.let { idFilm ->
                    titleLabel?.let { titleLabel ->
                        filmItem?.let { filmItem ->
                            message(nameFilm, idFilm, titleLabel, filmItem)
                        }
                        cancelReminder(imagePath)
                    }
                }
            }
        }
        return Result.success()
    }

    private fun message(
        nameFilm: String,
        idFilm: Int,
        titleLabel: String,
        filmsItem: FilmsItem
    ) {
        val alarmManager: AlarmManager? = null
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra(
            context.getString(R.string.INTENT_label_filmsItem),
            filmsItem
        )

        val pendingIntent = PendingIntent.getActivity(applicationContext, idFilm, intent, 0)

        alarmManager?.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 1000L,
            pendingIntent
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.UploadWorker_channelName)
            val description =
                context.getString(R.string.UploadWorker_channelDescription)
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
            .addAction(
                R.drawable.baseline_description_24,
                context.getString(R.string.UploadWorker_clickMe),
                pendingIntent
            )
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(2, builder.build())
    }

    private fun cancelReminder(imagePath: String) {
        repository.updateReminder(0, imagePath, "")
        WorkManager.getInstance().cancelAllWorkByTag(imagePath)
    }

}