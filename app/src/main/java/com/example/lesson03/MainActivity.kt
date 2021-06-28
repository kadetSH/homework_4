package com.example.lesson03

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.example.lesson03.fragments.FavoritesFragment
import com.example.lesson03.fragments.FilmsDescriptionFragment
import com.example.lesson03.fragments.FilmsFragment
import com.example.lesson03.fragments.ReminderListFragment
import com.example.lesson03.recyclerMy.FilmsItem
import com.google.android.material.navigation.NavigationView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    FilmsFragment.OnFilmDescriptionClickListener {

    object Crashlytics {
        fun log(e: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Без этого лога не отправляется отчет об ошибках
        Crashlytics.log(IllegalArgumentException())

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        //Слушаем нажатие в выпадающем меню
        id_navigation.setNavigationItemSelectedListener(this)

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragments: MutableList<androidx.fragment.app.Fragment> = fragmentManager.fragments

        val filmsItem =
            this.intent.getSerializableExtra(resources.getString(R.string.INTENT_label_filmsItem))

        if (filmsItem is FilmsItem) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.FrameLayoutContainer,
                    FilmsDescriptionFragment.newInstance(filmsItem)
                )
                .addToBackStack(null)
                .commit()
        } else {
            if (fragments.size == 0) {
                openListFilms(null)
            }
        }
        this.intent.putExtra(resources.getString(R.string.INTENT_label_filmsItem), 0)
    }

    private fun openListFilms(filmsItem: FilmsItem?) {
        title = resources.getString(R.string.title)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.FrameLayoutContainer, FilmsFragment.newInstance(filmsItem))
            .commit()
    }

    private fun openListFavorites() {
        title = resources.getString(R.string.title_favorite)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.FrameLayoutContainer, FavoritesFragment())
            .commit()
    }

    private fun openListReminder() {
        title = resources.getString(R.string.menu_watch_later)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.FrameLayoutContainer, ReminderListFragment())
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_films -> openListFilms(null)
            R.id.id_favorites -> openListFavorites()
            R.id.id_watch_leater -> openListReminder()
            R.id.id_invite -> clickInvite()
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun clickInvite() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "textMessage")
            type = "text/plain"
        }
        baseContext.startActivity(sendIntent)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            exitAlertDialog(this, this)
        }
    }

    private fun exitAlertDialog(context: Context, activity: Activity) {
        val builderAlertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        val clickCancel = DialogInterface.OnClickListener { dialog,
                                                            which ->
            dialog.dismiss()
        }

        val clickExit = DialogInterface.OnClickListener { dialog,
                                                          which ->
            activity.finish()
        }
        builderAlertDialog.setMessage(resources.getString(R.string.Exit))
        builderAlertDialog.setTitle(resources.getString(R.string.Hello))
        builderAlertDialog.setNegativeButton(resources.getString(R.string.labelNo), clickCancel)
        builderAlertDialog.setPositiveButton(resources.getString(R.string.labelYes), clickExit)
        val dialog: AlertDialog = builderAlertDialog.create()
        dialog.show()
    }

    override fun onFilmDescriptionClick(filmsItem: FilmsItem, note: String) {
        if (note == resources.getString(R.string.NOTE_DESCRIPTION)) {
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.FrameLayoutContainer,
                    FilmsDescriptionFragment.newInstance(filmsItem)
                )
                .addToBackStack(null)
                .commit()
        }
    }
}