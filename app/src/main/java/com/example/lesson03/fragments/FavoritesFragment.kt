package com.example.lesson03.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson03.R
import com.example.lesson03.recyclerMy.Decor
import com.example.lesson03.recyclerMy.FilmsAdapter
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.RFilm
import com.example.lesson03.viewmodel.FavoritesFilmsViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class FavoritesFragment : DaggerFragment() {

    companion object {
        const val TAG_favorites = "checkTAG"
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
        Log.d(TAG_favorites, "фавориты - onCreate $this")
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG_favorites, "фавориты - onCreateView")
        return inflater.inflate(R.layout.fragment_favorites_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG_favorites, "фавориты - onAttach")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG_favorites, "фавориты - onResume")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG_favorites, "фавориты - onViewCreated")
        initRecycler(view)
        observeViewModel()
    }

    @SuppressLint("CheckResult")
    private fun observeViewModel() {

        viewModel.repos.observe(viewLifecycleOwner, Observer<ArrayList<FilmsItem>> { arrayFilmsItem ->
            adapter.setItems(arrayFilmsItem)
        })

        viewModel.readAllLike.observe(viewLifecycleOwner, Observer<List<RFilm>> { result ->
            list.clear()
            result.forEach { itemRoom ->
                val like: Boolean = itemRoom.like != 0
                list.add(
                    FilmsItem(
                        itemRoom.name,
                        itemRoom.imagePath,
                        itemRoom.description,
                        "",
                        like,
                        itemRoom.idFilm,
                        itemRoom.reminder,
                        itemRoom.reminderDataTime
                    )
                )
            }
            adapter.notifyDataSetChanged()
        })

        viewModel.readLikeBool.observe(viewLifecycleOwner, Observer<Boolean> { likeBool ->
            filmsBool = false
            reminderBool = false
            if (likeBool) {
                adapter.setItems(listLike)
                favoritesBool = true
            }
        })

        viewModel.addReminder.observe(viewLifecycleOwner, Observer { addReminder ->
            val reminderAddFragment: Fragment = ReminderAddFragment()
            val argument = Bundle()
            argument.putSerializable(
                context?.resources?.getString(R.string.bundleFlag_listItem),
                addReminder
            )
            argument.putString(
                context?.resources?.getString(R.string.bundleFlag_parent),
                context?.resources?.getString(R.string.bundleFlag_favorites)
            )
            reminderAddFragment.arguments = argument
            (context as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.FrameLayoutContainer, reminderAddFragment)
                .addToBackStack(null)
                .commit()
        })

        viewModel.filmItemLoad.observe(viewLifecycleOwner, Observer { descriptionItem ->
            (context as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.FrameLayoutContainer,
                    FilmsDescriptionFragment.newInstance(descriptionItem)
                )
                .addToBackStack(null)
                .commit()
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