package com.example.lesson03.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson03.BuildConfig
import com.example.lesson03.R
import com.example.lesson03.recyclerMy.Decor
import com.example.lesson03.recyclerMy.FilmsAdapter
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.RFilm
import com.example.lesson03.viewmodel.ReminderFilmsViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ReminderListFragment : DaggerFragment() {

    companion object {
        const val TAG = "CheckTAG"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ReminderFilmsViewModel by viewModels {
        viewModelFactory
    }

    private val recyclerView by lazy {
        requireActivity().findViewById(R.id.id_recyclerViewReminder) as RecyclerView
    }
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "фавориты - onCreateView")
        return inflater.inflate(R.layout.fragment_reminder_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(FavoritesFragment.TAG_favorites, "фавориты - onViewCreated")
        initRecycler()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.readAllReminder.observe(viewLifecycleOwner, Observer<List<RFilm>> { result ->
            list.clear()
            result.forEach { item ->
                val selectFavorites: Boolean = item.like != BuildConfig.ACTION_CANCEL
                list.add(
                    FilmsItem(
                        item.name,
                        item.imagePath,
                        item.description,
                        "",
                        selectFavorites,
                        item.idFilm,
                        item.reminder,
                        item.reminderDataTime
                    )
                )
            }
            adapter.notifyDataSetChanged()
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

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(Decor(22))
        recyclerView.adapter = adapter
    }

    interface OnFilmLikeClickListener {
        fun onFilmLikeClick(filmsItem: FilmsItem, position: Int, note: String)
    }

}