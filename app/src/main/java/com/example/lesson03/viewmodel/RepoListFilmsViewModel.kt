package com.example.lesson03.viewmodel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.lesson03.App
import com.example.lesson03.R
import com.example.lesson03.SingleLiveEvent
import com.example.lesson03.jsonMy.FilmsJS
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.FilmDatabase
import com.example.lesson03.room.FilmRepository
import com.example.lesson03.room.RFilm
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import javax.inject.Inject

class RepoListFilmsViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    private var _snackBarString = SingleLiveEvent<String>()
    val snackBarString: LiveData<String> get() = _snackBarString

    private var _filmItemLoad = SingleLiveEvent<FilmsItem>()
    val filmItemLoad: LiveData<FilmsItem> get() = _filmItemLoad

    private var _addReminder = SingleLiveEvent<FilmsItem>()
    val addReminder: LiveData<FilmsItem> get() = _addReminder

    var addListFilm = false
    private val repository: FilmRepository

    init {
        val filmDao = FilmDatabase.getFilmDatabase(application).filmDao()
        repository = FilmRepository(filmDao)
//        deleteAll()
    }

    private val _animBool = MutableLiveData<Boolean>()
    val animalBool: LiveData<Boolean> get() = _animBool

    private val items = mutableListOf<FilmsJS>()
    var list = ArrayList<FilmsItem>()
    var filmP: String = ""

    private val _repos = MutableLiveData<ArrayList<FilmsItem>>()
    val repos: LiveData<ArrayList<FilmsItem>> get() = _repos

    private val _fabVisible = MutableLiveData<Boolean>()
    val fabVisible: LiveData<Boolean> get() = _fabVisible

    private val filmItemMutable = MutableLiveData<FilmsItem>()
    val filmItemLiveData: LiveData<FilmsItem> get() = filmItemMutable

    private var page = 1
    private val apiKey = "2931998c3a80d7806199320f76d65298"
    private val langRu = "ru-Ru"
    var firstStart: Boolean = false

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    //При первом старте вью модели сразу грузится список фильмов
    fun firstStart() {
        if (!firstStart) {
            firstStart = true
            downloadsList()
        } else {
            //Загрузка из Room
            try {
                selectListIsRoom(20 * (page - 1))
            } catch (ex: java.lang.Exception) {
            }
        }
    }

    //Событие при нажатии элемент списка фильмов
    fun filmLikeEvent(filmsItem: FilmsItem, note: String, context: Context) {
        if (note == context.resources.getString(R.string.NOTE_STAR)) {
            val lik: Int = if (!filmsItem.star) 1 else 0
            _snackBarString.postValue(
                "${lik}%${filmsItem.imageFilm}%${filmsItem.nameFilm}%" + context.resources.getString(
                    R.string.UploadWorker_star
                )
            )
            Observable.just(updateLike(lik, filmsItem.imageFilm))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())
                .doOnNext { selectFilmItem(filmsItem.idFilm) }
                .subscribe()
        } else if (note == context.resources.getString(R.string.NOTE_DESCRIPTION)) {
            openDescriptions(filmsItem)
        } else if (note == context.resources.getString(R.string.NOTE_DEL_ITEM)) {
            deleteFilm(filmsItem, context)
        } else if (note == context.resources.getString(R.string.NOTE_REMINDER)) {
            if (filmsItem.reminder == 0) {
                openReminderAdd(filmsItem)
            } else if (filmsItem.reminder == 1) {
                Observable.just(updateReminder(0, filmsItem.imageFilm, ""))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.computation())
                    .doOnNext { selectFilmItem(filmsItem.idFilm) }
                    .doOnNext { WorkManager.getInstance().cancelAllWorkByTag(filmsItem.imageFilm) }
                    .subscribe()
            }
        } else if (note == context.resources.getString(R.string.NOTE_REMINDER_DATA)) {

        }
    }

    //Загрузка фильмов из сети и проверка их в руме.
    //Если такой фильм есть в руме то обновляется его инфа
    @SuppressLint("CheckResult")
    fun downloadsList() {
        _animBool.value = true
        App.instance.api.getFilms(page, apiKey, langRu)
            .subscribeOn(Schedulers.io())
            .doOnError {
                _fabVisible.postValue(true)
            }
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                { result ->
                    result.getResults()?.forEach { itemFilm ->
                        items.clear()
                        val searchFilm: List<RFilm> =
                            repository.checkFilm(itemFilm?.getIdFilm() as Int)
                        if (searchFilm.isNotEmpty()) {
                            updateSearchFilm(
                                searchFilm[0].id,
                                searchFilm[0].imagePath,
                                searchFilm[0].description
                            )
                        } else {
                            try {
                                //Циклом добавляем фильмы в Room
                                addFilm(
                                    RFilm(
                                        0,
                                        itemFilm!!.getIdFilm() as Int,
                                        itemFilm!!.getTitle().toString(),
                                        itemFilm!!.getBackdropPath()
                                            .toString(),
                                        0,
                                        itemFilm!!.getOverview().toString(),
                                        0,
                                        ""
                                    )
                                )
                            } catch (e: Exception) {
                            }
                        }

                        itemFilm?.getTitle()?.let { titleFilms ->
                            FilmsJS(
                                titleFilms as String,
                                itemFilm.getBackdropPath().toString()!!,
                                0,
                                itemFilm.getOverview().toString()!!,
                                itemFilm.getIdFilm() as Int,
                                0,
                                ""
                            )
                        }?.let { itemFilmsJS ->
                            items.add(itemFilmsJS)
                        }
                    }
                    //Загрузка из Room
                    selectListIsRoom(20 * page)
                    page += 1
                    addListFilm = false
                    viewModelScope.launch(Dispatchers.Main) {
                        _animBool.value = false
                        _fabVisible.postValue(false)
                    }
                },
                { error ->
                    //Загрузка из Room
                    selectListIsRoom(20 * page)
                    viewModelScope.launch(Dispatchers.Main) {
                        _animBool.value = false
                    }
                    _snackBarString.postValue("-1%image%name%note")
                    error.printStackTrace()
                },
            )
    }

    private fun openDescriptions(listItem: FilmsItem) {
        _filmItemLoad.postValue(listItem)
    }

    //Загрузка списка фильмов из рума
    private fun selectListIsRoom(countLimit: Int) {
        val listFilmsIsRoom: List<RFilm> = repository.selectAllFilms(countLimit)
        if (listFilmsIsRoom.isNotEmpty()) {
            var index = 0
            items.clear()
            while (index < listFilmsIsRoom.size) {
                val itemFilm = listFilmsIsRoom[index]
                items.add(
                    FilmsJS(
                        itemFilm.name,
                        itemFilm.imagePath,
                        itemFilm.like,
                        itemFilm.description,
                        itemFilm.idFilm,
                        itemFilm.reminder,
                        itemFilm.reminderDataTime
                    )
                )
                index++
            }
            updateList()
        }
    }

    //Обновления напоминания
    fun updateReminder(reminder: Int, imagePath: String, reminderDataTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(reminder, imagePath, reminderDataTime)
        }
    }

    //Добавляет фильм в Room
    private fun addFilm(film: RFilm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFilm(film)
        }
    }

    //Скачать фильм по конкретному id
    private fun selectFilmItem(idFilm: Int) {

        val itemFilm = repository.selectFilmItem(idFilm)
        if (itemFilm.isNotEmpty()) {
            val likeBoolean: Boolean = itemFilm[0].like == 1
            filmItemMutable.postValue(
                FilmsItem(
                    itemFilm[0].name,
                    itemFilm[0].imagePath,
                    itemFilm[0].description,
                    "",
                    likeBoolean,
                    itemFilm[0].idFilm,
                    itemFilm[0].reminder,
                    itemFilm[0].reminderDataTime
                )
            )
        }
    }

    //Обновление фильма в руме
    private fun updateSearchFilm(id: Int, imagePath: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSearchFilm(id, imagePath, description)
        }
    }

    //Обновляет список фильмов и вставляет в MutableLiveData
    private fun updateList() {
        list.clear()
        items.forEach { itemFilm ->
            list.addAll(
                fillArrays(
                    arrayOf(itemFilm.title),
                    arrayOf(itemFilm.image),
                    arrayOf(itemFilm.description),
                    arrayOf(itemFilm.idFilm),
                    arrayOf(itemFilm.reminder),
                    arrayOf(itemFilm.reminderDataTime),
                    arrayOf(itemFilm.like)
                )
            )
        }
        _repos.postValue(list)
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
            val shortDescription = descriptionArray[i]
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

    //При создании фрагмента создается скрол листенер, который возвращает номер позиции
    //Если до конца списка 6 элементов то грузятся новые
    fun addList(pos: Int, countAdapter: Int) {
        if (countAdapter.minus(pos) == 6) {
            if (!addListFilm) {
                openFilmLis()
                addListFilm = true
            }
        }
    }

    //Обновление лайка фильма: добавление/удаление в/из избранного
    fun updateLike(lik: Int, imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLike(lik, imagePath)
        }
    }

    //Событие удаления фильма, вызывается диалоговое окно для подтверждения удаления
    private fun deleteFilm(filmsItem: FilmsItem, context: Context) {
        val bld: AlertDialog.Builder = AlertDialog.Builder(context)
        val lst =
            DialogInterface.OnClickListener { dialog, which ->
                if (which == -2) {
                    dialog.dismiss()
                } else if (which == -1) {
                    dellSelectedFilm(filmsItem.imageFilm)
                    viewModelScope.launch(Dispatchers.IO) {
                        selectFilmItem(filmsItem.idFilm)
                    }
                }
            }
        bld.setMessage(R.string.alertDialogExit)
        bld.setNegativeButton(context.resources.getString(R.string.labelNo), lst)
        bld.setPositiveButton(context.resources.getString(R.string.labelYes), lst)
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
    private fun openReminderAdd(listItem: FilmsItem) {
        _addReminder.postValue(listItem)
    }

    //Загрузка новых фильмов
    fun openFilmLis() {
        downloadsList()
    }

}