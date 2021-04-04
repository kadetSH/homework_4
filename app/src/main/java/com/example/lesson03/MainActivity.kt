package com.example.lesson03

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.viewmodel.RepoListFilmsViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    FilmsFragment.OnFilmLikeClickListener {

    object Crashlytics {
        fun log(e: Throwable){
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(RepoListFilmsViewModel::class.java)
    }

    private val toolbar by lazy {
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.idToolbar)
    }

    fun count(a: String){
        val test = a.toInt()
        Log.d("This", test.toString())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        throw IllegalArgumentException()
//        try {
//            val test = 1000000000000000000L
//            count(test.toString())  //error
//        }
//        catch (e : Exception){
//            Crashlytics.log(IllegalArgumentException())
//        }


        //Свой токен для теста
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                println(task.exception)
//                return@OnCompleteListener
//            }
//            // Get new FCM registration token
//            val token = task.result
//            // Log and toast
//            val msg = "$token"
//            println(msg)
//        })



        toolbar.setOnClickListener {
            id_drawerLayout.openDrawer(GravityCompat.START)
        }

        id_navigation.setNavigationItemSelectedListener(this)
        viewModel.firstStart()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_films -> clickFilms()
            R.id.id_favorites -> selectFavorites()
            R.id.id_watch_leater -> selectReminders()
            R.id.id_invite -> invite()
        }
        id_drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun invite() {
        viewModel.invite(baseContext)
    }

    override fun onFilmLikeClick(filmsItem: FilmsItem, position: Int, note: String) {

        if (note.equals("star")) {
            var lik: Int
            if (filmsItem.star == false) lik = 1
            else lik = 0
            viewModel.updateLike(lik, filmsItem.imageFilm)
            viewModel.snackbarString.postValue("$lik" + "%" + filmsItem.imageFilm.toString() + "%" + filmsItem.nameFilm.toString() + "%" + "star")
        } else if (note.equals("description")) {
            openDescriptions(filmsItem, position)
        } else if (note.equals("dellIcon")) {
            viewModel.dellFilm(filmsItem, position, this)
        } else if (note.equals("reminder")) {
            if (filmsItem.reminder == 0) {
                openReminderAdd(filmsItem, position)
            }else if (filmsItem.reminder == 1){
                viewModel.updateReminder(0, filmsItem.imageFilm, "")
                WorkManager.getInstance().cancelAllWorkByTag(filmsItem.imageFilm)
            }
        }else if (note.equals("reminderDataTime")){
            println("")
        }
    }

    fun clickFilms() {
        viewModel.selectFilmsList()
//        viewModel.openFilmLis()
    }

    fun selectFavorites() {
        viewModel.selectLikeList()
    }

    fun selectReminders(){
        viewModel.selectReminderList()
    }

    private fun openDescriptions(spisokItem: FilmsItem, position: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, FilmsDescriptionFragment.newInstance(spisokItem))
            .addToBackStack(null)
            .commit()
    }

    private fun openReminderAdd(spisokItem: FilmsItem, position: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, ReminderFragment.newInstance(spisokItem))
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else super.finish()
    }

}