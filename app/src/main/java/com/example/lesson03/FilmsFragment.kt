package com.example.lesson03

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson03.FilmsDescriptionFragment.Companion.adapter

import com.example.lesson03.recyclerMy.FilmsAdapter
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.RFilm
import com.example.lesson03.viewmodel.RepoListFilmsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.otus.otusaacpart1.data.entity.Repo
import kotlinx.android.synthetic.main.fragment_films_list.*


class FilmsFragment : Fragment() {

    var list = ArrayList<FilmsItem>()
    var listRoom = ArrayList<FilmsItem>()
    var listLike = ArrayList<FilmsItem>()
    private lateinit var starAnim: Animation
    private var firstStart = false

    var filmsBool: Boolean = true
    var favoritesBool: Boolean = false


    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(RepoListFilmsViewModel::class.java)
    }

    private var recyclerView: RecyclerView? = null
    private val adapter by lazy {
        FilmsAdapter(LayoutInflater.from(requireContext()),
            list
        ) { filmsItem: FilmsItem, position: Int, note: String ->
            (activity as? OnFilmLikeClickListener)?.onFilmLikeClick(filmsItem, position, note)
        }
    }

    val animIc by lazy {
        load_anim //.findViewById<RecyclerView>(R.id.load_anim)
    }

    //Snackbar
    var snackbar: Snackbar? = null
    lateinit var listenerSnackbar: View.OnClickListener
    /////


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        starAnim =
            android.view.animation.AnimationUtils.loadAnimation(this.context, R.anim.scale_star)
        return inflater.inflate(R.layout.fragment_films_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initRecycler()
        viewModel.repos.observe(viewLifecycleOwner, Observer<ArrayList<FilmsItem>> {
            adapter.setItems(it)
        })

        viewModel.readAllData.observe(viewLifecycleOwner, Observer<List<RFilm>> {
            if (!firstStart) firstStart = true
            else {
                println("")
                listRoom.clear()
                it.forEach {
                    val like: Boolean
                    if (it.like == 0) like = false
                    else like = true
                    listRoom.add(FilmsItem(it.name, it.imagePath, it.description, "", like))
                }
                if (filmsBool) {
                    adapter.setItems(listRoom)
                    favoritesBool = false
                } else {

                }
            }
        })

        viewModel.readAllLike.observe(viewLifecycleOwner, Observer<List<RFilm>> {
            listLike.clear()
            it.forEach {
                val like: Boolean
                like = it.like != 0
                listLike.add(FilmsItem(it.name, it.imagePath, it.description, "", like))
            }
            if (favoritesBool) {
                adapter.setItems(listLike)
            }
        })

        viewModel.readLikeBool.observe(viewLifecycleOwner, Observer<Boolean> {
            filmsBool = false
            if (it) {
                adapter.setItems(listLike)
                favoritesBool = true
            }
        })

        viewModel.readFilmsBool.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it) {
                adapter.setItems(listRoom)
            }
        })

        viewModel.animBool.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it == true) {
                animIc?.visibility = View.VISIBLE
                animIc.startAnimation(starAnim)
            } else {
                animIc.clearAnimation()
                animIc?.visibility = View.INVISIBLE
            }
        })

        viewModel.snackbarString.observe(viewLifecycleOwner, Observer<String> {
            var strArr = it.split("%")
            val lik = strArr[0].toInt()
            val imagePath = strArr[1]
            val name = strArr[2]
            val note = strArr[3]
            snackbarShow(lik, imagePath, name, note)
        })

    }

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view!!.findViewById(R.id.id_recyclerView)
        recyclerView!!.layoutManager = layoutManager
        recyclerView?.addItemDecoration(Decor(22))
        recyclerView!!.adapter = adapter

        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                var fgfg = layoutManager.findLastVisibleItemPosition()
                viewModel.addList(layoutManager.findLastVisibleItemPosition())
            }
        })
    }

    interface OnFilmLikeClickListener {
        fun onFilmLikeClick(filmsItem: FilmsItem, position: Int, note: String)
    }

    //snackbar ----------------------
    fun snackbarShow(lik: Int, imagePath: String, name: String, note: String) {
        listenerSnackbar = View.OnClickListener {

            if (lik == 1) {
                viewModel.updateLike(0, imagePath)
            } else if (lik == 0) {
                viewModel.updateLike(1, imagePath)
            } else if (lik == -1) {
                viewModel.openFilmLis()
            }
        }

        if (lik == 1) snackbar =
            Snackbar.make(load_anim, "$name - добавили в избранное", Snackbar.LENGTH_INDEFINITE)
        if (lik == 0) snackbar =
            Snackbar.make(load_anim, "$name - удалили из избранного", Snackbar.LENGTH_INDEFINITE)
        if (lik == -1) snackbar =
            Snackbar.make(load_anim, "Нет связи с сервером", Snackbar.LENGTH_INDEFINITE)

        if (lik == 1 or 0) {
            snackbar?.setAction("Отменить", listenerSnackbar)
        }
        if (lik == -1) {
            snackbar?.setAction("Повторить", listenerSnackbar)
        }

        snackbar?.show()
        if (lik == 1 or 0) {
            fab?.postDelayed({
                snackbar?.dismiss()
            }, 3000)
        }else{
            fab?.postDelayed({
                snackbar?.dismiss()
            }, 40000)
        }


    }
    //------------------------


}