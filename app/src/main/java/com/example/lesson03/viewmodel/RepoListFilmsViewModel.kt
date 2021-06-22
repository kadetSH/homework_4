package com.example.lesson03.viewmodel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.lesson03.App
import com.example.lesson03.BuildConfig
import com.example.lesson03.R
import com.example.lesson03.SingleLiveEvent
import com.example.lesson03.jsonMy.FilmsJS
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.FilmDatabase
import com.example.lesson03.room.FilmRepository
import com.example.lesson03.room.RFilm
import com.example.lesson03.snacbar.SnacbarData
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.io.IOException
import javax.inject.Inject

class RepoListFilmsViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    companion object {
        private var page = 1
    }

    private var _snackBarString = SingleLiveEvent<SnacbarData>()
    val snackBarString: LiveData<SnacbarData> get() = _snackBarString

    private var _addReminder = SingleLiveEvent<FilmsItem>()
    val addReminder: LiveData<FilmsItem> get() = _addReminder

    private val beforeEndOfList: Int = 6
    private var addListFilm = false
    private val repository: FilmRepository

    init {
        val filmDao = FilmDatabase.getFilmDatabase(application).filmDao()
        repository = FilmRepository(filmDao)
    }

    private val _animBool = MutableLiveData<Boolean>()
    val animalBool: LiveData<Boolean> get() = _animBool

    private val items = mutableListOf<FilmsJS>()
    var listFilmsItem = ArrayList<FilmsItem>()
    var filmCheck: String = ""

    private val _repos = MutableLiveData<ArrayList<FilmsItem>>()
    val repos: LiveData<ArrayList<FilmsItem>> get() = _repos

    private val _fabVisible = MutableLiveData<Boolean>()
    val fabVisible: LiveData<Boolean> get() = _fabVisible

    private val filmItemMutable = MutableLiveData<FilmsItem>()
    val filmItemLiveData: LiveData<FilmsItem> get() = filmItemMutable

    private val apiKey = BuildConfig.apiKey
    private val langRu = BuildConfig.langRu
    var firstStart: Boolean = false

    //При первом старте вью модели сразу грузится список фильмов
    fun onViewCreated() {
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
            val selectFavorites: Int =
                if (!filmsItem.star) BuildConfig.ACTION_TO_ACCEPT else BuildConfig.ACTION_CANCEL
            _snackBarString.postValue(
                SnacbarData(
                    selectFavorites,
                    filmsItem.imageFilm,
                    filmsItem.nameFilm,
                    context.resources.getString(R.string.UploadWorker_star)
                )
            )
            Observable.just(updateLike(selectFavorites, filmsItem.imageFilm))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())
                .doOnNext { selectFilmItem(filmsItem.idFilm) }
                .subscribe()
        } else if (note == context.resources.getString(R.string.NOTE_DESCRIPTION)) {

        } else if (note == context.resources.getString(R.string.NOTE_DEL_ITEM)) {
            deleteFilm(filmsItem, context)
        } else if (note == context.resources.getString(R.string.NOTE_REMINDER)) {
            if (filmsItem.reminder == BuildConfig.ACTION_CANCEL) {
                openReminderAdd(filmsItem)
            } else if (filmsItem.reminder == BuildConfig.ACTION_TO_ACCEPT) {
                Observable.just(updateReminder(BuildConfig.ACTION_CANCEL, filmsItem.imageFilm, ""))
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
                    _snackBarString.postValue(
                        SnacbarData(
                            -1,
                            "image",
                            "name",
                            "note"
                        )
                    )
                    error.printStackTrace()
                }
            )
    }

    //Загрузка списка фильмов из рума
    @SuppressLint("CheckResult")
    private fun selectListIsRoom(countLimit: Int) {

        try {
            Observable.just(repository.selectAllFilms(countLimit))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())  //AndroidSchedulers.mainThread()
                .subscribe({ result ->
                    if (result.isNotEmpty()) {
                        var index = 0
                        items.clear()
                        while (index < result.size) {
                            val itemFilm = result[index]
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
                }, { error ->
                })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //Обновления напоминания
    fun updateReminder(reminder: Int, imagePath: String, reminderDataTime: String) {
        Completable.fromCallable {
            repository.updateReminder(
                reminder,
                imagePath,
                reminderDataTime
            )
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    //Добавляет фильм в Room
    private fun addFilm(film: RFilm) {
        Completable.fromCallable { repository.addFilm(film) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    //Скачать фильм по конкретному id
    @SuppressLint("CheckResult")
    private fun selectFilmItem(idFilm: Int) {

        try {
            Observable.just(repository.selectFilmItem(idFilm))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())  //AndroidSchedulers.mainThread()
                .subscribe({ itemFilm ->
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
                }, { error ->
                })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //Обновление фильма в руме
    private fun updateSearchFilm(id: Int, imagePath: String, description: String) {
        Completable.fromCallable { repository.updateSearchFilm(id, imagePath, description) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    //Обновляет список фильмов и вставляет в MutableLiveData
    private fun updateList() {
        listFilmsItem.clear()
        items.forEach { itemFilm ->
            listFilmsItem.addAll(
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
        _repos.postValue(listFilmsItem)
    }

    //Создает массив списка фильмов
    private fun fillArrays(
        titleArray: Array<String>,
        filmImageArray: Array<String>,
        descriptionArray: Array<String>,
        idFilmArray: Array<Int>,
        reminderArray: Array<Int>,
        reminderDataTime: Array<String>,
        likeFilmArray: Array<Int>
    ): List<FilmsItem> {
        val list = ArrayList<FilmsItem>()
        for (i in titleArray.indices) {
            val shortDescription = descriptionArray[i]
            var proverka = ""
            if (titleArray[i] == filmCheck) {
                proverka = filmCheck
            }
            var boolFavorite: Boolean
            val selectFavorites = likeFilmArray[i]
            boolFavorite = selectFavorites != 0

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
        if (countAdapter.minus(pos) == beforeEndOfList) {
            if (!addListFilm) {
                openFilmLis()
                addListFilm = true
            }
        }
    }

    //Обновление лайка фильма: добавление/удаление в/из избранного
    fun updateLike(selectFavorites: Int, imagePath: String) {
        Completable.fromCallable { repository.updateLike(selectFavorites, imagePath) }
            .subscribeOn(Schedulers.io())
            .subscribe()
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
                    viewModelScope.launch(Dispatchers.IO) {
                        selectFilmItem(filmsItem.idFilm)
                    }
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
        Completable.fromCallable { repository.deleteFilm(imagePath) }
            .subscribeOn(Schedulers.io())
            .subscribe()
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