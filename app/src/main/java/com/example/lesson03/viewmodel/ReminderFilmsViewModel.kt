package com.example.lesson03.viewmodel

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.lesson03.R
import com.example.lesson03.fragments.FilmsDescriptionFragment
import com.example.lesson03.fragments.ReminderAddFragment
import com.example.lesson03.jsonMy.FilmsJS
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.FilmDatabase
import com.example.lesson03.room.FilmRepository
import com.example.lesson03.room.RFilm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderFilmsViewModel(application: Application) : AndroidViewModel(application) {

    private val items = mutableListOf<FilmsJS>()
    val readAllReminder: LiveData<List<RFilm>>
    private val repository: FilmRepository
    private var list = ArrayList<FilmsItem>()
    private var filmP: String = ""
    private var snackbarString = MutableLiveData<String>()
    val readAllLike: LiveData<List<RFilm>>
    private val reposLiveData = MutableLiveData<ArrayList<FilmsItem>>()
    val repos: LiveData<ArrayList<FilmsItem>>
        get() = reposLiveData

    init {
        val filmDao = FilmDatabase.getFilmDatabase(application).filmDao()
        repository = FilmRepository(filmDao)
        readAllLike = repository.readAllLike
//        readAllData = repository.readAllData
//        readAllLike = repository.readAllLike
        readAllReminder = repository.readAllReminder
    }

    fun downloadsReminders() {
        //Загрузка из Room
        viewModelScope.launch(Dispatchers.IO) {
            downloadsList()
        }
    }

    //Загрузка списка фильмов из рума
    suspend fun downloadsList() {
        items.clear()
        val searchFilm: List<RFilm> = repository.selectAllRemindersFilms()
        if (searchFilm.isNotEmpty()) {
            searchFilm.forEach {
                items.add(
                    FilmsJS(
                        it.name,
                        it.imagePath,
                        it.like,
                        it.description,
                        it.idFilm,
                        it.reminder,
                        it.reminderDataTime
                    )
                )
            }
            updateList()
        }
    }

    //Обновляет список фильмов и вставляет в MutableLiveData
    fun updateList() {
        list.clear()
        items.forEach { itF ->
            list.addAll(
                fillArrays(
                    arrayOf(itF.title),
                    arrayOf(itF.image),
                    arrayOf(itF.description),
                    arrayOf(itF.idFilm),
                    arrayOf(itF.reminder),
                    arrayOf(itF.reminderDataTime),
                    arrayOf(itF.like)
                )
            )
            reposLiveData.postValue(list)
        }
    }

    //Создает массив списка фильмов
    private fun fillArrays(
        titleArray: Array<String>,
        filmImageArray: Array<String>,
        descriptionArray: Array<String>,
        idFilmArray: Array<Int>,
        reminderArray: Array<Int>,
        reminderDataTime: Array<String>,
        likeFilmArray: Array<Int>,
    ): List<FilmsItem> {
        val list = ArrayList<FilmsItem>()
        for (i in 0..titleArray.size - 1) {
            var shortDescription = descriptionArray[i]
            var proverka = ""
            if (titleArray[i].equals(filmP)) {
                proverka = filmP
            }
            var boolFavorite: Boolean
            val like = likeFilmArray[i]
            if (like == 0) {
                boolFavorite = false
            } else {
                boolFavorite = true
            }

            if (shortDescription.length > 120) {
                shortDescription = shortDescription.substring(0, 120) + "..."
            }

            val spisokItem = FilmsItem(
                titleArray[i],
                filmImageArray[i],
                shortDescription,
                proverka,
                boolFavorite,
                idFilmArray[i],
                reminderArray[i],
                reminderDataTime[i]
            )
            list.add(spisokItem)
        }
        return list
    }

    //Событие при нажатии элемент списка фильмов
    fun filmLikeEvent(filmsItem: FilmsItem, position: Int, note: String, context: Context) {
        if (note.equals("star")) {
            val lik: Int
            if (filmsItem.star == false) lik = 1
            else lik = 0
            updateLike(lik, filmsItem.imageFilm)
            snackbarString.postValue("$lik" + "%" + filmsItem.imageFilm + "%" + filmsItem.nameFilm + "%" + "star")
        } else if (note.equals("description")) {
            openDescriptions(filmsItem, context)
        } else if (note.equals("dellIcon")) {
            dellFilm(filmsItem, position, context)
        } else if (note.equals("reminder")) {
            if (filmsItem.reminder == 0) {
                openReminderAdd(filmsItem, context)
            } else if (filmsItem.reminder == 1) {
                updateReminder(0, filmsItem.imageFilm, "")
                WorkManager.getInstance().cancelAllWorkByTag(filmsItem.imageFilm)
            }
        } else if (note.equals("reminderDataTime")) {
            println("")
        }
    }

    //Обновление лайка фильма: добавление/удаление в/из избранного
    fun updateLike(lik: Int, imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLike(lik, imagePath)
        }
    }

    //Подробное описание фильма
    private fun openDescriptions(spisokItem: FilmsItem, context: Context) {
        (context as AppCompatActivity).supportFragmentManager
            .beginTransaction()
            .add(R.id.FrameLayoutContainer, FilmsDescriptionFragment.newInstance(spisokItem))
            .addToBackStack(null)
            .commit()
    }

    //Событие удаления фильма, вызывается диалоговое окно для подтверждения удаления
    fun dellFilm(filmsItem: FilmsItem, position: Int, context: Context) {
        val bld: AlertDialog.Builder = AlertDialog.Builder(context)
        val lst =
            DialogInterface.OnClickListener { dialog, which ->
                if (which == -2) {
                    dialog.dismiss()
                } else if (which == -1) {
                    dellSelectedFilm(filmsItem.imageFilm)
                }
            }
        bld.setMessage("Удалить фильм из списка?")
        bld.setNegativeButton("Нет", lst)
        bld.setPositiveButton("Да", lst)
        val dialog: AlertDialog = bld.create()
        dialog.show()
    }

    //Событие добавления напоминания о просмотре фильма
    private fun openReminderAdd(spisokItem: FilmsItem, context: Context) {
        (context as AppCompatActivity).supportFragmentManager
            .beginTransaction()
            .replace(R.id.FrameLayoutContainer, ReminderAddFragment.newInstance(spisokItem))
            .addToBackStack(null)
            .commit()
    }

    fun updateReminder(reminder: Int, imagePath: String, reminderDataTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(reminder, imagePath, reminderDataTime)
        }
    }

    //Удаляем фильм из списка.
    fun dellSelectedFilm(imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delleteFilm(imagePath)
        }
    }


}