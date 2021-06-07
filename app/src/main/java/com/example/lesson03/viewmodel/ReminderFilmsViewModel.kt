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
import com.example.lesson03.R
import com.example.lesson03.SingleLiveEvent
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.FilmDatabase
import com.example.lesson03.room.FilmRepository
import com.example.lesson03.room.RFilm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReminderFilmsViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    val readAllReminder: LiveData<List<RFilm>>
    private val repository: FilmRepository
    private var snackBarString = MutableLiveData<String>()

    private var _filmItemLoad = SingleLiveEvent<FilmsItem>()
    val filmItemLoad: LiveData<FilmsItem> get() = _filmItemLoad

    init {
        val filmDao = FilmDatabase.getFilmDatabase(application).filmDao()
        repository = FilmRepository(filmDao)
        readAllReminder = repository.readAllReminder
    }

    //Событие при нажатии элемент списка фильмов
    fun filmLikeEvent(filmsItem: FilmsItem, position: Int, note: String, context: Context) {
        if (note == context.resources.getString(R.string.NOTE_STAR)) {
            val lik: Int = if (!filmsItem.star) 1 else 0
            updateLike(lik, filmsItem.imageFilm)
            snackBarString.postValue(
                "${lik}%${filmsItem.imageFilm}%${filmsItem.nameFilm}%" + context.resources.getString(
                    R.string.UploadWorker_star
                )
            )
        } else if (note == context.resources.getString(R.string.NOTE_DESCRIPTION)) {
            openDescriptions(filmsItem)
        } else if (note == context.resources.getString(R.string.NOTE_DEL_ITEM)) {
            dellFilm(filmsItem, context)
        } else if (note == context.resources.getString(R.string.NOTE_REMINDER)) {
            if (filmsItem.reminder == 1) {
                updateReminder(0, filmsItem.imageFilm, "")
                WorkManager.getInstance().cancelAllWorkByTag(filmsItem.imageFilm)
            }
        }
    }

    //Обновление лайка фильма: добавление/удаление в/из избранного
    private fun updateLike(lik: Int, imagePath: String) {
        repository.updateLike(lik, imagePath)
    }

    //Подробное описание фильма
    private fun openDescriptions(listItem: FilmsItem) {
        _filmItemLoad.postValue(listItem)
    }

    //Событие удаления фильма, вызывается диалоговое окно для подтверждения удаления
    private fun dellFilm(filmsItem: FilmsItem, context: Context) {
        val bld: AlertDialog.Builder = AlertDialog.Builder(context)
        val lst =
            DialogInterface.OnClickListener { dialog, which ->
                if (which == -2) {
                    dialog.dismiss()
                } else if (which == -1) {
                    dellSelectedFilm(filmsItem.imageFilm)
                }
            }
        bld.setMessage(context.resources.getString(R.string.dialog_dellete_film))
        bld.setNegativeButton(context.resources.getString(R.string.labelNo), lst)
        bld.setPositiveButton(context.resources.getString(R.string.labelYes), lst)
        val dialog: AlertDialog = bld.create()
        dialog.show()
    }

    private fun updateReminder(reminder: Int, imagePath: String, reminderDataTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(reminder, imagePath, reminderDataTime)
        }
    }

    //Удаляем фильм из списка.
    private fun dellSelectedFilm(imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFilm(imagePath)
        }
    }

}