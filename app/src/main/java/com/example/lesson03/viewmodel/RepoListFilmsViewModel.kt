package com.example.lesson03.viewmodel

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.lifecycle.*
import com.example.lesson03.jsonMy.FilmsJS
import com.example.lesson03.jsonMy.Themoviedb
import com.example.lesson03.App
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
    val readAllLike: LiveData<List<RFilm>>
    val readAllReminder: LiveData<List<RFilm>>

    var readLikeBool = MutableLiveData<Boolean>()
    var readFilmsBool = MutableLiveData<Boolean>()
    var readReminderBool = MutableLiveData<Boolean>()
    var snackbarString = MutableLiveData<String>()
    var addListFilm = false

    private val repository: FilmRepository

    init {
        val filmDao = FilmDatabase.getFilmDatabase(application).filmDao()
        repository = FilmRepository(filmDao)
//        delAll()
        readAllData = repository.readAllData
        readAllLike = repository.readAllLike
        readAllReminder = repository.readAllReminder
    }

    private val reposLiveData = MutableLiveData<ArrayList<FilmsItem>>()
    private val reposLiveDataLike = MutableLiveData<ArrayList<FilmsItem>>()
    val animBool = MutableLiveData<Boolean>()
    private val items = mutableListOf<FilmsJS>()
    var list = ArrayList<FilmsItem>()
    var filmP: String = ""
    var favoriteName: ArrayList<String> = ArrayList()
    val repos: LiveData<ArrayList<FilmsItem>>
        get() = reposLiveData

    val reposLike: LiveData<ArrayList<FilmsItem>>
        get() = reposLiveDataLike

    private var page = 1
    val API_KEY = "2931998c3a80d7806199320f76d65298"
    val langRu = "ru-Ru"
    var firstStart: Boolean = false

    //////////////
    fun addFilm(film: RFilm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFilm(film)
        }
    }

    fun updateSearchFilm(id: Int, imagePath: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSearchFilm(id, imagePath, description)
        }
    }

    fun delAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delAll()
        }
    }

    fun updateLike(lik: Int, imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLike(lik, imagePath)
        }
    }

    fun updateReminder(reminder: Int, imagePath: String, reminderDataTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(reminder, imagePath, reminderDataTime)
        }
    }
    /////////////////////


    fun openFilmLis() {
        animBool.value = true
        viewModelScope.launch(Dispatchers.IO) {
            downloadsList()
        }
    }


    fun downloadsList() {

        App.instance.api.getFilms(page, API_KEY, langRu)
            .enqueue(object : Callback<Themoviedb> {
                override fun onFailure(call: Call<Themoviedb>, t: Throwable) {
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

                        response.body()
                            ?.getResults()?.forEach {

                                viewModelScope.launch(Dispatchers.IO) {
                                    var searchFilm: List<RFilm> =
                                        repository.checkFilm(it?.getIdFilm() as Int)

                                    if (searchFilm.isNotEmpty()) {

                                        updateSearchFilm(
                                            searchFilm[0].id,
                                            searchFilm[0].imagePath,
                                            searchFilm[0].description
                                        )

                                    } else {
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
                            }
                        page += 1
                        updateList()
                        addListFilm = false
                        animBool.value = false
                    } else {
                        println("")
                    }


                }
            })


    }

    fun selectLikeList() {
        readLikeBool.postValue(true)
        readFilmsBool.postValue(false)
        readReminderBool.postValue(false)
    }

    fun selectFilmsList() {
        readFilmsBool.postValue(true)
        readLikeBool.postValue(false)
        readReminderBool.postValue(false)
    }

    fun selectReminderList(){
        readFilmsBool.postValue(false)
        readLikeBool.postValue(false)
        readReminderBool.postValue(true)
    }


    fun updateList() {
        var fgfg = items.size
        items.forEach { itF ->

            list.addAll(
                fillArrays(
                    arrayOf(itF.title),
                    arrayOf(itF.image),
                    arrayOf(itF.description),
                    arrayOf(itF.idFilm),
                    arrayOf(itF.reminder),
                    arrayOf(itF.reminderDataTime)
                )
            )
            reposLiveData.postValue(list)
        }
    }

    fun addList(pos: Int) {
        var ff = reposLiveData.value

        ff?.let {
            if (it.size.minus(pos) == 6) {
                if (addListFilm == false) {
                    openFilmLis()
                    addListFilm = true
                }
            }
        }

    }

    fun invite(context: Context) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "textMessage")
            type = "text/plain"
        }
        context.startActivity(sendIntent)
    }

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

    fun dellSelectedFilm(imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delleteFilm(imagePath)
        }
    }

    fun firstStart() {
        if (!firstStart) {
            firstStart = true

            viewModelScope.launch {
                downloadsList()
            }
        }
    }

    fun fillArrays(
        titleArray: Array<String>,
        filmImageArray: Array<String>,
        descriptionArray: Array<String>,
        idFilmArray: Array<Int>,
        reminderArray: Array<Int>,
        reminderDataTime: Array<String>
    ): List<FilmsItem> {
        var list = ArrayList<FilmsItem>()
        for (i in 0..titleArray.size - 1) {
            var shortDescription = descriptionArray[i]
            var proverka = ""
            if (titleArray[i].equals(filmP)) {
                proverka = filmP
            }
            var idxFav = favoriteName.indexOf(titleArray[i])
            var boolFavorite: Boolean

            if (idxFav == -1) {
                boolFavorite = false
            } else {
                boolFavorite = true
            }

            if (shortDescription.length > 120) {
                shortDescription = shortDescription.substring(0, 120) + "..."
            }

            var spisokItem = FilmsItem(
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


}