package com.example.lesson03

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.viewmodel.RepoListFilmsViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    FilmsFragment.OnFilmLikeClickListener {

    object Crashlytics {
        fun log(e: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(RepoListFilmsViewModel::class.java)
    }

    private val toolbar by lazy {
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.idToolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Без этого лога не отправляется отчет об ошибках
        Crashlytics.log(IllegalArgumentException())

        toolbar.setOnClickListener {
            id_drawerLayout.openDrawer(GravityCompat.START)
        }

        id_navigation.setNavigationItemSelectedListener(this)
        viewModel.firstStart()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_films -> clickFilms()
            R.id.id_favorites -> clickFavorites()
            R.id.id_watch_leater -> selectReminders()
            R.id.id_invite -> clickInvite()
        }
        id_drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun clickInvite() {
        viewModel.invite(baseContext)
    }

    override fun onFilmLikeClick(filmsItem: FilmsItem, position: Int, note: String) {
        viewModel.FilmLikeEvent(filmsItem, position, note, this)
    }

    private fun clickFilms() {
        viewModel.selectFilmsList()
    }

    private fun clickFavorites() {
        viewModel.selectLikeList()
    }

    private fun selectReminders() {
        viewModel.selectReminderList()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            viewModel.exitAlertDialog(this, this)
        }
    }
}