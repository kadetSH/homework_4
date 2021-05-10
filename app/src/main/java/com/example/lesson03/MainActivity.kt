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
import com.example.lesson03.fragments.FilmsFragment
import com.example.lesson03.fragments.ReminderListFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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
//        drawer_layout.openDrawer(GravityCompat.START)



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

        val fm: FragmentManager = supportFragmentManager
        val fragments: MutableList<androidx.fragment.app.Fragment> = fm.fragments

        if (fragments.size == 0){
            openListFilms()
        }

    }

    private fun openListFilms() {
        title = resources.getString(R.string.title)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.FrameLayoutContainer, FilmsFragment())
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
            R.id.id_films -> openListFilms()
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
        val bld: AlertDialog.Builder = AlertDialog.Builder(context)
        val clickCancell = DialogInterface.OnClickListener { dialog,
                                                             which ->
            dialog.dismiss()
        }

        val clickExit = DialogInterface.OnClickListener { dialog,
                                                          which ->
            activity.finish()
        }
        bld.setMessage(resources.getString(R.string.Exit))
        bld.setTitle(resources.getString(R.string.Hello))
        bld.setNegativeButton("Нет", clickCancell)
        bld.setPositiveButton("Да", clickExit)
        val dialog: AlertDialog = bld.create()
        dialog.show()
    }
}