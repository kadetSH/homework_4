package com.example.lesson03.viewmodel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.lesson03.App
import com.example.lesson03.R
import com.example.lesson03.SingleLiveEvent
import com.example.lesson03.fragments.FilmsDescriptionFragment
import com.example.lesson03.fragments.ReminderAddFragment
import com.example.lesson03.jsonMy.FilmsJS
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.FilmDatabase
import com.example.lesson03.room.FilmRepository
import com.example.lesson03.room.RFilm
import dagger.android.support.DaggerFragment
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import javax.inject.Inject


class RepoListFilmsViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    private val readAllData: Flowable<List<RFilm>>
    private val readAllReminder: Flowable<List<RFilm>>
    private val readAllLike: Flowable<List<RFilm>>
    private var snackBarString = SingleLiveEvent<String>()
//    val snackBarRX: Flowable<LiveData<String>> = Flowable.just(snackBarString)
    fun getSnackBarString(): SingleLiveEvent<String> {
        return snackBarString
    }

    private var fragmentManager = SingleLiveEvent<Fragment>()
    fun getFragmentManager(): SingleLiveEvent<Fragment> {

        return fragmentManager
    }

    var addListFilm = false
    private val repository: FilmRepository

    init {

        val filmDao = FilmDatabase.getFilmDatabase(application).filmDao()
        repository = FilmRepository(filmDao)
//        deleteAll()
        readAllLike = repository.readAllLike
        readAllData = repository.readAllData
        readAllReminder = repository.readAllReminder
    }

    private val animBool = MutableLiveData<Boolean>()
    val animBool1: Flowable<MutableLiveData<Boolean>> = Flowable.just(animBool)
    private val items = mutableListOf<FilmsJS>()
    var list = ArrayList<FilmsItem>()
    var filmP: String = ""

    private val reposLiveData = MutableLiveData<ArrayList<FilmsItem>>()
    private val repos: LiveData<ArrayList<FilmsItem>>
        get() = reposLiveData
    val repos1: Flowable<LiveData<ArrayList<FilmsItem>>> = Flowable.just(repos)

    private val fabMutableLiveData = MutableLiveData<Boolean>()
    private val fabLiveData: LiveData<Boolean>
        get() = fabMutableLiveData
    val fabLiveData1: Flowable<LiveData<Boolean>> = Flowable.just(fabLiveData)

    private val filmItemMutable = MutableLiveData<FilmsItem>()
    private val filmItemLiveData: LiveData<FilmsItem>
        get() = filmItemMutable
    val filmItemRX: Flowable<LiveData<FilmsItem>> = Flowable.just(filmItemLiveData)


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
    fun filmLikeEvent(filmsItem: FilmsItem, position: Int, note: String, context: Context) {
        if (note == "star") {
            val lik: Int = if (!filmsItem.star) 1 else 0
            snackBarString.postValue("$lik" + "%" + filmsItem.imageFilm + "%" + filmsItem.nameFilm + "%" + "star")

            Observable.just(updateLike(lik, filmsItem.imageFilm))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())
                .doOnNext { selectFilmItem(filmsItem.idFilm) }
                .subscribe()

        } else if (note == "description") {
            openDescriptions(filmsItem, context)
        } else if (note == "dellIcon") {
            deleteFilm(filmsItem, context)
        } else if (note == "reminder") {

            if (filmsItem.reminder == 0) {
                openReminderAdd(filmsItem, context)
            } else if (filmsItem.reminder == 1) {
                Observable.just(updateReminder(0, filmsItem.imageFilm, ""))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.computation())
                    .doOnNext { selectFilmItem(filmsItem.idFilm) }
                    .doOnNext { WorkManager.getInstance().cancelAllWorkByTag(filmsItem.imageFilm) }
                    .subscribe()
            }
        } else if (note == "reminderDataTime") {
            println("")
        }
    }

    //Загрузка фильмов из сети и проверка их в руме.
    //Если такой фильм есть в руме то обновляется его инфа
    @SuppressLint("CheckResult")
    fun downloadsList() {
        animBool.value = true

        App.instance.api.getFilms(page, apiKey, langRu)
            .subscribeOn(Schedulers.io())
            .doOnError {
                fabMutableLiveData.postValue(true)
            }
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                { result ->

                    result.getResults()?.forEach {
                        items.clear()
                        val searchFilm: List<RFilm> =
                            repository.checkFilm(it?.getIdFilm() as Int)
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
                                        it!!.getIdFilm() as Int,
                                        it!!.getTitle().toString(),
                                        it!!.getBackdropPath()
                                            .toString(),
                                        0,
                                        it!!.getOverview().toString(),
                                        0,
                                        ""
                                    )
                                )
                                /////////////////////
                            } catch (e: Exception) {

                            }

                        }

                        it?.getTitle()?.let { it1 ->
                            FilmsJS(
                                it1 as String,
                                it.getBackdropPath().toString()!!,
                                0,
                                it.getOverview().toString()!!,
                                it.getIdFilm() as Int,
                                0,
                                ""
                            )
                        }?.let { it2 ->
                            items.add(it2)
                        }
                    }
                    //
                    //Загрузка из Room
                    selectListIsRoom(20 * page)

                    page += 1
                    addListFilm = false
                    viewModelScope.launch(Dispatchers.Main) {
                        animBool.value = false
                        fabMutableLiveData.postValue(false)
                    }

                },
                { error ->
                    //Загрузка из Room
                    selectListIsRoom(20 * page)
                    viewModelScope.launch(Dispatchers.Main) {
                        animBool.value = false
                    }
                    snackBarString.postValue("-1" + "%image%name%note")
                    error.printStackTrace()
                },
            )
    }

    //Открытие описание фильма
    private fun openDescriptions(listItem: FilmsItem, context: Context) {

        var df: Fragment = FilmsDescriptionFragment.newInstance(listItem)
        fragmentManager.postValue(df)
//        (context as AppCompatActivity).supportFragmentManager
//            .beginTransaction()
//            .add(R.id.FrameLayoutContainer, FilmsDescriptionFragment.newInstance(listItem))
//            .addToBackStack(null)
//            .commit()
    }

    //Загрузка списка фильмов из рума
    @SuppressLint("CheckResult")
    private fun selectListIsRoom(countLimit: Int) {
        //С RxJava
        repository.selectAllFilms(countLimit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                items.clear()
                result.forEach {
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
            }, { error ->
                error.printStackTrace()
            })
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

        var itemFilm = repository.selectFilmItem(idFilm)
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
        println("")
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
        }
        reposLiveData.postValue(list)
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
            if (titleArray[i].equals(filmP)) {
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

    //Загрузка новых фильмов
    fun openFilmLis() {
        downloadsList()
    }


}