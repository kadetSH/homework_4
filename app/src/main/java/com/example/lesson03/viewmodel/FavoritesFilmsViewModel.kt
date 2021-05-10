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
import com.example.lesson03.fragments.FilmsDescriptionFragment
import com.example.lesson03.R
import com.example.lesson03.fragments.ReminderAddFragment
import com.example.lesson03.jsonMy.FilmsJS
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.FilmDatabase
import com.example.lesson03.room.FilmRepository
import com.example.lesson03.room.RFilm
import io.reactivex.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesFilmsViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val repository: FilmRepository
    private val items = mutableListOf<FilmsJS>()

    private val reposLiveData = MutableLiveData<ArrayList<FilmsItem>>()
    private val repos: LiveData<ArrayList<FilmsItem>>
    get() = reposLiveData
    val repos1: Flowable<LiveData<ArrayList<FilmsItem>>> = Flowable.just(repos)

    private var readLikeBoolMutable = MutableLiveData<Boolean>()
    private val readLikeBool: LiveData<Boolean>
        get() = readLikeBoolMutable
    val readLikeBool1: Flowable<LiveData<Boolean>> = Flowable.just(readLikeBool)

    val readAllLike: Flowable<List<RFilm>>
    private var snackBarString = MutableLiveData<String>()
    private var list = ArrayList<FilmsItem>()
    private var filmP: String = ""
    private var favoriteName: ArrayList<String> = ArrayList()

    init {
        val filmDao = FilmDatabase.getFilmDatabase(application).filmDao()
        repository = FilmRepository(filmDao)
        readAllLike = repository.readAllLike
    }

    fun downloadsFavorites(){
        //Загрузка из Room
        viewModelScope.launch(Dispatchers.IO) {
            downloadsList()
        }
    }

    //Загрузка списка фильмов из рума
    private suspend fun downloadsList() {
        items.clear()
        val searchFilm: List<RFilm> = repository.selectAllFavoritesFilms()
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
    private fun updateList() {
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
        for (i in titleArray.indices) {
            var shortDescription = descriptionArray[i]
            var proverka = ""
            if (titleArray[i] == filmP) {
                proverka = filmP
            }

            var boolFavorite: Boolean
            val like = likeFilmArray[i]
            boolFavorite = like != 0

            val listItem = FilmsItem(
                titleArray[i],
                filmImageArray[i],
                shortDescription,
                proverka,
                boolFavorite,
                idFilmArray[i],
                reminderArray[i],
                reminderDataTime[i]
            )
            list.add(listItem)
        }
        return list
    }

    //Событие при нажатии элемент списка фильмов
    fun filmLikeEvent(filmsItem: FilmsItem, position: Int, note: String, context: Context) {
        if (note == "star") {
            val lik: Int = 0
            updateLike(lik, filmsItem.imageFilm)
            snackBarString.postValue("$lik" + "%" + filmsItem.imageFilm + "%" + filmsItem.nameFilm + "%" + "star")
        } else if (note == "description") {
            openDescriptions(filmsItem, context)
        } else if (note == "dellIcon") {
            dellFilm(filmsItem, position, context)
        } else if (note == "reminder") {
            if (filmsItem.reminder == 0) {
                openReminderAdd(filmsItem, context)
            } else if (filmsItem.reminder == 1) {
                updateReminder(0, filmsItem.imageFilm, "")
                WorkManager.getInstance().cancelAllWorkByTag(filmsItem.imageFilm)
            }
        } else if (note == "reminderDataTime") {
            println("")
        }
    }

    //Обновление лайка фильма: добавление/удаление в/из избранного
    private fun updateLike(lik: Int, imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLike(lik, imagePath)
        }
    }

    //Событие удаления фильма, вызывается диалоговое окно для подтверждения удаления
    private fun dellFilm(filmsItem: FilmsItem, position: Int, context: Context) {
        val bld: AlertDialog.Builder = AlertDialog.Builder(context)
        val lst =
            DialogInterface.OnClickListener { dialog, which ->
                if (which == -2) {
                    dialog.dismiss()
                } else if (which == -1) {
                    dellSelectedFilm(filmsItem.imageFilm)
                }
            }
        bld.setMessage(R.string.alertDialogExit)
        bld.setNegativeButton("Нет", lst)
        bld.setPositiveButton("Да", lst)
        val dialog: AlertDialog = bld.create()
        dialog.show()
    }

    //Удаляем фильм из списка.
    private fun dellSelectedFilm(imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFilm(imagePath)
        }
    }

    //Событие добавления напоминания о просмотре фильма
    private fun openReminderAdd(listItem: FilmsItem, context: Context) {
        (context as AppCompatActivity).supportFragmentManager
            .beginTransaction()
            .replace(R.id.FrameLayoutContainer, ReminderAddFragment.newInstance(listItem))
            .addToBackStack(null)
            .commit()
    }

    //Подробное описание фильма
    private fun openDescriptions(listItem: FilmsItem, context: Context) {
        (context as AppCompatActivity).supportFragmentManager
            .beginTransaction()
            .add(R.id.FrameLayoutContainer, FilmsDescriptionFragment.newInstance(listItem))
            .addToBackStack(null)
            .commit()
    }

    private fun updateReminder(reminder: Int, imagePath: String, reminderDataTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(reminder, imagePath, reminderDataTime)
        }
    }

}