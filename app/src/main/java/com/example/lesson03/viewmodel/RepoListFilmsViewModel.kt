package com.example.lesson03.viewmodel

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.work.WorkManager
import com.example.lesson03.App
import com.example.lesson03.FilmsDescriptionFragment
import com.example.lesson03.R
import com.example.lesson03.ReminderFragment
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
    val readAllLike: LiveData<List<RFilm>>
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
    var favoritesPage : Boolean = false

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
                        viewModelScope.launch(Dispatchers.Main) {
                        response.body()
                            ?.getResults()?.forEach {

//                                viewModelScope.launch(Dispatchers.IO) {
                                    val searchFilm: List<RFilm> =
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
                            page += 1
                            updateList()
                            addListFilm = false
                            animBool.value = false
                            }

                    } else {
                        println("")
                    }


                }
            })


    }

    fun selectLikeList() {
        favoritesPage = true
        readLikeBoolMutable.postValue(true)
        readFilmsBoolMutable.postValue(false)
        readReminderBoolMutable.postValue(false)
    }

    fun exitAlertDialog(context: Context, activity: Activity){
        val bld: AlertDialog.Builder = AlertDialog.Builder(context)
        val clickCancell = DialogInterface.OnClickListener { dialog,
                                                             which ->
            dialog.dismiss()
        }

        val clickExit = DialogInterface.OnClickListener { dialog,
                                                          which ->
            activity.finish()
        }
        bld.setMessage("Выйти из приложения?")
        bld.setTitle("Привет!")
        bld.setNegativeButton("Нет", clickCancell)
        bld.setPositiveButton("Да", clickExit)
        val dialog: AlertDialog = bld.create()
        dialog.show()
    }

    fun selectFilmsList() {
        favoritesPage = false
        readFilmsBoolMutable.postValue(true)
        readLikeBoolMutable.postValue( false)
        readReminderBoolMutable.postValue(false)
    }

    fun selectReminderList(){
        readFilmsBoolMutable.postValue(false)
        readLikeBoolMutable.postValue( false)
        readReminderBoolMutable.postValue(true)
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
        val ff = reposLiveData.value

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

    fun FilmLikeEvent(filmsItem: FilmsItem, position: Int, note: String, context: Context){
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

    private fun openDescriptions(spisokItem: FilmsItem, context: Context) {
        (context as AppCompatActivity).supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, FilmsDescriptionFragment.newInstance(spisokItem))
            .addToBackStack(null)
            .commit()
    }

    private fun openReminderAdd(spisokItem: FilmsItem, context: Context) {
        (context as AppCompatActivity).supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, ReminderFragment.newInstance(spisokItem))
            .addToBackStack(null)
            .commit()
    }

    private fun fillArrays(
        titleArray: Array<String>,
        filmImageArray: Array<String>,
        descriptionArray: Array<String>,
        idFilmArray: Array<Int>,
        reminderArray: Array<Int>,
        reminderDataTime: Array<String>
    ): List<FilmsItem> {
        val list = ArrayList<FilmsItem>()
        for (i in 0..titleArray.size - 1) {
            var shortDescription = descriptionArray[i]
            var proverka = ""
            if (titleArray[i].equals(filmP)) {
                proverka = filmP
            }
            val idxFav = favoriteName.indexOf(titleArray[i])
            var boolFavorite: Boolean

            if (idxFav == -1) {
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


}