package com.example.lesson03

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.RFilm
import com.example.lesson03.viewmodel.RepoListFilmsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    FilmsFragment.OnFilmLikeClickListener { //,

    private val viewModel by lazy {
        ViewModelProvider(this).get(RepoListFilmsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        id_navigation.setNavigationItemSelectedListener(this)
        viewModel.firstStart()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_films -> clickFilms()
            R.id.id_favorites -> selectFavorites()
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
        }else if (note.equals("dellIcon")) {
            viewModel.dellFilm(filmsItem, position, this)
        }
    }

    fun clickFilms() {
        viewModel.selectFilmsList()
//        viewModel.openFilmLis()
    }

    fun selectFavorites() {
        viewModel.selectLikeList()
    }

    private fun openDescriptions(spisokItem: FilmsItem, position: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, FilmsDescriptionFragment.newInstance(spisokItem))
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else super.finish()
    }

}