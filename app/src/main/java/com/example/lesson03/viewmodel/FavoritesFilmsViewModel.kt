package com.example.lesson03.viewmodel

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.lesson03.BuildConfig
import com.example.lesson03.R
import com.example.lesson03.SingleLiveEvent
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.FilmDatabase
import com.example.lesson03.room.FilmRepository
import com.example.lesson03.room.RFilm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesFilmsViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    private val repository: FilmRepository

    private val _repos = MutableLiveData<ArrayList<FilmsItem>>()
    val repos: LiveData<ArrayList<FilmsItem>> get() = _repos

    private var _readLikeBool = MutableLiveData<Boolean>()
    val readLikeBool: LiveData<Boolean> get() = _readLikeBool

    val readAllLike: LiveData<List<RFilm>>
    private var snackBarString = MutableLiveData<String>()

    private var _addReminder = SingleLiveEvent<FilmsItem>()
    val addReminder: LiveData<FilmsItem> get() = _addReminder

    private var _filmItemLoad = SingleLiveEvent<FilmsItem>()
    val filmItemLoad: LiveData<FilmsItem> get() = _filmItemLoad

    init {
        val filmDao = FilmDatabase.getFilmDatabase(application).filmDao()
        repository = FilmRepository(filmDao)
        readAllLike = repository.readAllLike
    }

    //Событие при нажатии элемент списка фильмов
    fun filmLikeEvent(filmsItem: FilmsItem, position: Int, note: String, context: Context) {
        if (note == context.resources.getString(R.string.NOTE_STAR)) {
            val selectFavorites: Int = BuildConfig.ACTION_CANCEL
            updateLike(selectFavorites, filmsItem.imageFilm)
            snackBarString.postValue(
                "${selectFavorites}%${filmsItem.imageFilm}%${filmsItem.nameFilm}%" + context.resources.getString(
                    R.string.UploadWorker_star
                )
            )
        } else if (note == context.resources.getString(R.string.NOTE_DESCRIPTION)) {
            openDescriptions(filmsItem)
        } else if (note == context.resources.getString(R.string.NOTE_DEL_ITEM)) {
            deleteFilm(filmsItem, context)
        } else if (note == context.resources.getString(R.string.NOTE_REMINDER)) {
            if (filmsItem.reminder == BuildConfig.ACTION_CANCEL) {
                openReminderAdd(filmsItem)
            } else if (filmsItem.reminder == BuildConfig.ACTION_TO_ACCEPT) {
                updateReminder(BuildConfig.ACTION_CANCEL, filmsItem.imageFilm, "")
                WorkManager.getInstance().cancelAllWorkByTag(filmsItem.imageFilm)
            }
        } else if (note == context.resources.getString(R.string.NOTE_REMINDER_DATA)) {
        }
    }

    //Обновление лайка фильма: добавление/удаление в/из избранного
    private fun updateLike(selectFavorites: Int, imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLike(selectFavorites, imagePath)
        }
    }

    //Событие удаления фильма, вызывается диалоговое окно для подтверждения удаления
    private fun deleteFilm(filmsItem: FilmsItem, context: Context) {
        val builderAlertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        val list =
            DialogInterface.OnClickListener { dialog, which ->
                if (which == -2) {
                    dialog.dismiss()
                } else if (which == -1) {
                    deleteSelectedFilm(filmsItem.imageFilm)
                }
            }
        builderAlertDialog.setMessage(R.string.alertDialogExit)
        builderAlertDialog.setNegativeButton(context.resources.getString(R.string.labelNo), list)
        builderAlertDialog.setPositiveButton(context.resources.getString(R.string.labelYes), list)
        val dialog: AlertDialog = builderAlertDialog.create()
        dialog.show()
    }

    //Удаляем фильм из списка.
    private fun deleteSelectedFilm(imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFilm(imagePath)
        }
    }

    //Событие добавления напоминания о просмотре фильма
    private fun openReminderAdd(listItem: FilmsItem) {
        _addReminder.postValue(listItem)
    }

    //Подробное описание фильма
    private fun openDescriptions(listItem: FilmsItem) {
        _filmItemLoad.postValue(listItem)
    }

    private fun updateReminder(reminder: Int, imagePath: String, reminderDataTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(reminder, imagePath, reminderDataTime)
        }
    }

}