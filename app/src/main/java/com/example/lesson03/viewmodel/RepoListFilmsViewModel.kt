package com.example.lesson03.viewmodel

import android.app.AlertDialog
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import androidx.lifecycle.*
import com.example.lesson03.FilmsDescriptionFragment
import com.example.lesson03.R

import com.example.lesson03.jsonMy.FilmsJS
import com.example.lesson03.jsonMy.Themoviedb2
import com.example.lesson03.net.App
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.FilmDatabase
import com.example.lesson03.room.FilmRepository
import com.example.lesson03.room.RFilm
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.coroutineContext


class RepoListFilmsViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<RFilm>>
    val readAllLike: LiveData<List<RFilm>>
    var readLikeBool = MutableLiveData<Boolean>()
    var readFilmsBool = MutableLiveData<Boolean>()
    var snackbarString = MutableLiveData<String>()

    private val repository: FilmRepository

    init {
        val filmDao = FilmDatabase.getFilmDatabase(application).filmDao()
        repository = FilmRepository(filmDao)
        delAll()
        readAllData = repository.readAllData
        readAllLike = repository.readAllLike
    }

    val reposLiveData = MutableLiveData<ArrayList<FilmsItem>>()
    val reposLiveDataLike = MutableLiveData<ArrayList<FilmsItem>>()
    val animBool = MutableLiveData<Boolean>()
    val items = mutableListOf<FilmsJS>()
    var list = ArrayList<FilmsItem>()
    var filmP: String = ""
    var favoriteName: ArrayList<String> = ArrayList()
    val repos: LiveData<ArrayList<FilmsItem>>
        get() = reposLiveData

    val reposLike: LiveData<ArrayList<FilmsItem>>
        get() = reposLiveDataLike

    private var page = 1
    var firstStart: Boolean = false

    //////////////
    fun addFilm(film: RFilm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFilm(film)
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
    /////////////////////


    fun openFilmLis() {
        animBool.value = true
        downloadsList()
    }


    fun downloadsList() {
        App.instance.api.getFilms(page)
            .enqueue(object : Callback<Themoviedb2> {
                override fun onFailure(call: Call<Themoviedb2>, t: Throwable) {
                    call.cancel()
                    animBool.value = false
                    snackbarString.postValue("-1" + "%image%name%note")
                }

                override fun onResponse(
                    call: Call<Themoviedb2>,
                    response: Response<Themoviedb2>,
                ) {
                    items.clear()
                    if (response.isSuccessful) {

                        response.body()
                            ?.getResults()?.forEach {

                                addFilm(RFilm(0,
                                    it!!.getTitle().toString(),
                                    it!!.getBackdropPath().toString(),
                                    0,
                                    it!!.getOverview().toString()))

                                it?.getTitle()?.let { it1 ->
                                    FilmsJS(
                                        it1,
                                        it.getBackdropPath()!!,
                                        0,
                                        it.getOverview()!!
                                    )
                                }?.let { it2 -> items.add(it2) }
                            }
                        page += 1
                        updateList()
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
    }

    fun selectFilmsList() {
        readFilmsBool.postValue(true)
        readLikeBool.postValue(false)
    }


    fun updateList() {
        var fgfg = items.size
        items.forEach { itF ->

            list.addAll(
                fillArrays(
                    arrayOf(itF.title),
                    arrayOf(itF.image),
                    arrayOf(itF.description)
                )
            )
            reposLiveData.postValue(list)
        }
    }

    fun addList(pos: Int) {
        var ff = reposLiveData.value

        ff?.let {
            if (it.size.minus(pos) == 6) {
                println("")
                openFilmLis()
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

    fun dellFilm(filmsItem: FilmsItem, position: Int, context: Context){
        val bld: AlertDialog.Builder = AlertDialog.Builder(context)
        val lst =
            DialogInterface.OnClickListener { dialog, which ->
                if (which == -2){
                    dialog.dismiss()
                }else if (which == -1){
                    dellSelectedFilm(filmsItem.imageFilm)
                }
            }
        bld.setMessage("Удалить фильм из списка?")
        bld.setNegativeButton("Нет", lst)
        bld.setPositiveButton("Да", lst)
        val dialog: AlertDialog = bld.create()
        dialog.show()
    }

    fun dellSelectedFilm(imagePath: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delleteFilm(imagePath)
        }
    }

    fun firstStart() {
        if (!firstStart) {
            firstStart = true
            downloadsList()
        }
    }

    fun fillArrays(
        titleArray: Array<String>,
        filmImageArray: Array<String>,
        descriptionArray: Array<String>,
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
                titleArray[i], filmImageArray[i], shortDescription, proverka, boolFavorite
            )
            list.add(spisokItem)
        }
        return list
    }



}

