package com.example.lesson03.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson03.R
import com.example.lesson03.recyclerMy.Decor
import com.example.lesson03.recyclerMy.FilmsAdapter
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.viewmodel.FavoritesFilmsViewModel
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FavoritesFragment : DaggerFragment() {

    companion object {
        const val TAGfavorites = "ProverkaTAG"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: FavoritesFilmsViewModel by viewModels {
        viewModelFactory
    }

    private var recyclerView: RecyclerView? = null
    var filmsBool: Boolean = true
    private var favoritesBool: Boolean = false
    var reminderBool: Boolean = false
    var list = ArrayList<FilmsItem>()
    private val adapter by lazy {
        FilmsAdapter(
            LayoutInflater.from(requireContext()),
            list
        ) { filmsItem: FilmsItem, position: Int, note: String ->
            (activity as? OnFilmLikeClickListener)?.onFilmLikeClick(filmsItem, position, note)
            context?.let {
                viewModel.filmLikeEvent(filmsItem, position, note, it)
            }
        }
    }
    var listLike = ArrayList<FilmsItem>()

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAGfavorites, "фавориты - onCreate $this")
        retainInstance = true

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAGfavorites, "фавориты - onCreateView")
        return inflater.inflate(R.layout.fragment_favorites_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAGfavorites, "фавориты - onViewCreated")
        viewModel.downloadsFavorites()
        initRecycler(view)
        observeViewModel()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAGfavorites, "фавориты - onDetach")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAGfavorites, "фавориты - onDestroy")
    }

    @SuppressLint("CheckResult")
    private fun observeViewModel() {

        //RxJava
        viewModel.repos1
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                result.observe(viewLifecycleOwner, Observer<ArrayList<FilmsItem>> {
                    adapter.setItems(it)
                })
            }, { error ->

            })

        viewModel.readAllLike
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                listLike.clear()
                result.forEach {
                    val like: Boolean = it.like != 0
                    listLike.add(
                        FilmsItem(
                            it.name,
                            it.imagePath,
                            it.description,
                            "",
                            like,
                            it.idFilm,
                            it.reminder,
                            it.reminderDataTime
                        )
                    )
                }
                adapter.setItems(listLike)
            }, { error ->

            })

        viewModel.readLikeBool1
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                result.observe(viewLifecycleOwner, Observer<Boolean> {
                    filmsBool = false
                    reminderBool = false
                    if (it) {
                        adapter.setItems(listLike)
                        favoritesBool = true
                    }
                })
            }, { error ->

            })

    }

    private fun initRecycler(view: View) {
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view?.findViewById(R.id.id_recyclerViewFavorites)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.addItemDecoration(Decor(22))
        recyclerView?.adapter = adapter
    }

    interface OnFilmLikeClickListener {
        fun onFilmLikeClick(filmsItem: FilmsItem, position: Int, note: String)
    }
}