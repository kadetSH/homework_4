package com.example.lesson03.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson03.R
import com.example.lesson03.recyclerMy.Decor
import com.example.lesson03.recyclerMy.FilmsAdapter
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.viewmodel.ReminderFilmsViewModel

class ReminderListFragment: Fragment() {

    companion object {
        const val TAG = "ProverkaTAG"
    }

    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(ReminderFilmsViewModel::class.java)
    }
    private var recyclerView: RecyclerView? = null
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ReminderFragment - onCreate $this")
        retainInstance = true
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
        Log.d(FavoritesFragment.TAGfavorites, "фавориты - onViewCreated")
        viewModel.downloadsReminders()
        initRecycler(view)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.repos.observe(viewLifecycleOwner, Observer<ArrayList<FilmsItem>> {
            adapter.setItems(it)
        })
    }

    private fun initRecycler(view: View) {
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view?.findViewById(R.id.id_recyclerViewReminder)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.addItemDecoration(Decor(22))
        recyclerView?.adapter = adapter
    }

    interface OnFilmLikeClickListener {
        fun onFilmLikeClick(filmsItem: FilmsItem, position: Int, note: String)
    }

}