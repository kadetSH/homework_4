package com.example.lesson03.viewmodel

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.lesson03.App
import com.example.lesson03.fragments.FilmsDescriptionFragment
import com.example.lesson03.R
import com.example.lesson03.fragments.ReminderAddFragment
import com.example.lesson03.jsonMy.FilmsJS
import com.example.lesson03.jsonMy.Themoviedb
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.FilmDatabase
import com.example.lesson03.room.FilmRepository
import com.example.lesson03.room.RFilm
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RepoListFilmsViewModel(application: Application) : AndroidViewModel(application) {


    val readAllData: LiveData<List<RFilm>>
    val readAllReminder: LiveData<List<RFilm>>

    private var readLikeBoolMutable = MutableLiveData<Boolean>()
    val readLikeBool: LiveData<Boolean>
        get() = readLikeBoolMutable

    private var readFilmsBoolMutable = MutableLiveData<Boolean>()
    val readFilmsBool: LiveData<Boolean>
        get() = readFilmsBoolMutable

    private var readReminderBoolMutable = MutableLiveData<Boolean>()
    val readReminderBool: LiveData<Boolean>
        get() = readReminderBoolMutable

    var snackbarString = MutableLiveData<String>()
    var addListFilm = false
    var favoritesPage: Boolean = false


    private val repository: FilmRepository

    init {
        val filmDao = FilmDatabase.getFilmDatabase(application).filmDao()
        repository = FilmRepository(filmDao)
//        delAll()
        readAllData = repository.readAllData
        readAllReminder = repository.readAllReminder
    }


    private val reposLiveData = MutableLiveData<ArrayList<FilmsItem>>()
    val animBool = MutableLiveData<Boolean>()
    private val items = mutableListOf<FilmsJS>()
    var list = ArrayList<FilmsItem>()
    var filmP: String = ""
    val repos: LiveData<ArrayList<FilmsItem>>
        get() = reposLiveData

    private val fabMutableLiveData = MutableLiveData<Boolean>()
    val fabLiveData: LiveData<Boolean>
        get() = fabMutableLiveData


            private var page = 1
    val API_KEY = "2931998c3a80d7806199320f76d65298"
    val langRu = "ru-Ru"
    var firstStart: Boolean = false

    fun delAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delAll()
        }
    }

    //При первом старте вью модели сразу грузится список фильмов
    fun firstStart() {
        if (!firstStart) {
            fabMutableLiveData.postValue(false)
            firstStart = true
            viewModelScope.launch {
                downloadsList()
            }
        } else {
            //Загрузка из Room
            viewModelScope.launch(Dispatchers.IO) {
                selectListIsRoom(20 * (page - 1))
            }
        }
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

    //Загрузка фильмов из сети и проверка их в руме.
    //Если такой фильм есть в руме то обновляется его инфа
    fun downloadsList() {

        App.instance.api.getFilms(page, API_KEY, langRu)
            .enqueue(object : Callback<Themoviedb> {
                override fun onFailure(call: Call<Themoviedb>, t: Throwable) {

                    fabMutableLiveData.postValue(true)
                    //Загрузка из Room
                    viewModelScope.launch(Dispatchers.IO) {
                        selectListIsRoom(20 * page)
                    }

                    call.cancel()
                    animBool.value = false
                    snackbarString.postValue("-1" + "%image%name%note")
                }

                override fun onResponse(
                    call: Call<Themoviedb>,
                    response: Response<Themoviedb>,
                ) {
                    items.clear()
                    if (response.isSuccessful) {
                        viewModelScope.launch(Dispatchers.IO) {
                            response.body()
                                ?.getResults()?.forEach {

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
                                                    it!!.getBackdropPath().toString(),
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
//                            updateList()
                            //Загрузка из Room
                            selectListIsRoom(20 * page)

                            page += 1
                            addListFilm = false
                            viewModelScope.launch(Dispatchers.Main) {
                                animBool.value = false
                                fabMutableLiveData.postValue(false)
                            }

                        }

                    } else {
                        println("")
                    }
                }
            })
    }

    //Открытие описание фильма
    private fun openDescriptions(spisokItem: FilmsItem, context: Context) {
        (context as AppCompatActivity).supportFragmentManager
            .beginTransaction()
            .add(R.id.FrameLayoutContainer, FilmsDescriptionFragment.newInstance(spisokItem))
            .addToBackStack(null)
            .commit()
    }

    //Загрузка списка фильмов из рума
    suspend fun selectListIsRoom(countLimit: Int) {
        val searchFilm: List<RFilm> = repository.selectAllFilms(countLimit)
        if (searchFilm.isNotEmpty()) {
            items.clear()
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

    //Обновления напоминания
    fun updateReminder(reminder: Int, imagePath: String, reminderDataTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(reminder, imagePath, reminderDataTime)
        }
    }

    //Добавляет фильм в Room
    fun addFilm(film: RFilm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFilm(film)
        }
    }

    //Обновление фильма в руме
    fun updateSearchFilm(id: Int, imagePath: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSearchFilm(id, imagePath, description)
        }
    }

    //Обновляет список фильмов и вставляет в MutableLiveData
    fun updateList() {
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

    //При создании фрагмента создается скрол листенер, который возвращает номер позиции
    //Если до конца списка 6 элементов то грузятся новые
    fun addList(pos: Int, countAdapter: Int) {
        if (countAdapter.minus(pos) == 6) {
            if (addListFilm == false) {
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

    //Удаляем фильм из списка.
    fun dellSelectedFilm(imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delleteFilm(imagePath)
        }
    }

    //Событие добавления напоминания о просмотре фильма
    private fun openReminderAdd(spisokItem: FilmsItem, context: Context) {
        (context as AppCompatActivity).supportFragmentManager
            .beginTransaction()
            .replace(R.id.FrameLayoutContainer, ReminderAddFragment.newInstance(spisokItem))
            .addToBackStack(null)
            .commit()
    }

    //Загрузка новых фильмов
    fun openFilmLis() {
        animBool.value = true
        viewModelScope.launch(Dispatchers.IO) {
            downloadsList()
        }
    }


}