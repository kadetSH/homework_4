package com.example.lesson03.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
import com.example.lesson03.viewmodel.RepoListFilmsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_films_list.*
import javax.inject.Inject

class FilmsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel: RepoListFilmsViewModel by viewModels {
        viewModelFactory
    }

    companion object {
        const val TAG = "ProverkaTAG"
        fun newInstance(filmsItem: FilmsItem?): FilmsFragment {
            val args = Bundle()
            args.putSerializable("item", filmsItem)
            val fragment = FilmsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    var list = ArrayList<FilmsItem>()
    private lateinit var starAnim: Animation
    private var position: Int = -1
    private var idFilm: Int = -1
    private var note: String = ""

    private val fabIcon by lazy {
        requireActivity().findViewById(R.id.fab) as FloatingActionButton
    }

    private var recyclerView: RecyclerView? = null
    private val adapter by lazy {
        FilmsAdapter(
            LayoutInflater.from(requireContext()),
            list
        ) { filmsItem: FilmsItem, position: Int, note: String ->
            (activity as? OnFilmLikeClickListener)?.onFilmLikeClick(filmsItem, position, note)
            context?.let {
                this.position = position
                this.note = note
                idFilm = filmsItem.idFilm
                viewModel.filmLikeEvent(filmsItem, note, it)
            }
        }
    }

    private val animIc by lazy {
        load_anim
    }

    //SnackBar
    var snackbar: Snackbar? = null
    lateinit var listenerSnackBar: View.OnClickListener

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate $this")
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        starAnim =
            android.view.animation.AnimationUtils.loadAnimation(this.context, R.anim.scale_star)
        return inflater.inflate(R.layout.fragment_films_list, container, false)
        Log.d(TAG, "onCreateView")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        initRecycler()
        observeViewModel()
        viewModel.firstStart()
        fabIcon.setOnClickListener {
            viewModel.downloadsList()
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    @SuppressLint("CheckResult")
    private fun observeViewModel() {

        viewModel.repos.observe(viewLifecycleOwner, Observer<ArrayList<FilmsItem>> { item ->
            if (list.size < item.size) {
                for (x: Int in list.size until item.size) {
                    list.add(item[x])
                    recyclerView?.adapter?.notifyItemChanged(list.size - 1)
                }
            }
        })

        viewModel.fabVisible.observe(viewLifecycleOwner, Observer<Boolean> { visibleBool ->
            this.fabIcon.isVisible = visibleBool
        })

        viewModel.animalBool.observe(viewLifecycleOwner, Observer<Boolean> { animalBool ->
            if (animalBool == true) {
                animIc?.visibility = View.VISIBLE
                animIc.startAnimation(starAnim)
            } else {
                animIc.clearAnimation()
                animIc?.visibility = View.INVISIBLE
            }
        })

        viewModel.snackBarString.observe(viewLifecycleOwner, Observer { snackStr ->
            val strArr = snackStr.split("%")
            val lik = strArr[0].toInt()
            val imagePath = strArr[1]
            val name = strArr[2]
            val note = strArr[3]
            snackBarShow(lik, imagePath, name, note)
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

        viewModel.addReminder.observe(viewLifecycleOwner, Observer { addReminder ->
            val reminderAddFragment: Fragment = ReminderAddFragment()
            val argument = Bundle()
            argument.putSerializable(
                context?.resources?.getString(R.string.bundleFlag_listItem),
                addReminder
            )
            argument.putString(
                context?.resources?.getString(R.string.bundleFlag_parent),
                context?.resources?.getString(R.string.bundleFlag_repoList)
            )
            reminderAddFragment.arguments = argument
            (context as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.FrameLayoutContainer, reminderAddFragment)
                .addToBackStack(null)
                .commit()
        })

        viewModel.filmItemLiveData.observe(viewLifecycleOwner, Observer { item ->
            if ((note == resources.getString(R.string.NOTE_STAR)) || (note == resources.getString(R.string.NOTE_REMINDER))) {
                list.removeAt(position)
                list.add(position, item)
                recyclerView?.adapter?.notifyItemChanged(position)
            }
            if (note == resources.getString(R.string.NOTE_DEL_ITEM)) {
                list.removeAt(position)
                recyclerView?.adapter?.notifyItemRemoved(position)
            }
        })

    }

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view?.findViewById(R.id.id_recyclerView)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.addItemDecoration(Decor(22))
        recyclerView?.adapter = adapter
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                viewModel.addList(layoutManager.findLastVisibleItemPosition(), adapter.itemCount)
            }
        })
    }

    interface OnFilmLikeClickListener {
        fun onFilmLikeClick(filmsItem: FilmsItem, position: Int, note: String)
    }

    //snackBar ----------------------
    private fun snackBarShow(lik: Int, imagePath: String, name: String, note: String) {
        listenerSnackBar = View.OnClickListener {
            when (lik) {
                1 -> {
                    viewModel.updateLike(0, imagePath)
                }
                0 -> {
                    viewModel.updateLike(1, imagePath)
                }
                -1 -> {
                    viewModel.openFilmLis()
                }
            }
        }

        if (lik == 1) snackbar =
            Snackbar.make(
                load_anim,
                "$name${resources.getString(R.string.addFavorites)}",
                Snackbar.LENGTH_SHORT
            )
        if (lik == 0) snackbar =
            Snackbar.make(
                load_anim,
                "$name${resources.getString(R.string.dellFavorites)}",
                Snackbar.LENGTH_SHORT
            )
        if (lik == -1) snackbar =
            Snackbar.make(load_anim, resources.getString(R.string.noConnect), Snackbar.LENGTH_SHORT)
        if (lik == 1 or 0) {
            snackbar?.setAction(resources.getString(R.string.cancellAction), listenerSnackBar)
        }
        if (lik == -1) {
            snackbar?.setAction(resources.getString(R.string.repeatAction), listenerSnackBar)
        }

        snackbar?.show()
        if (lik == 1 or 0) {
            fab?.postDelayed({
                snackbar?.dismiss()
            }, 3000)
        } else {
            fab?.postDelayed({
                snackbar?.dismiss()
            }, 40000)
        }
    }

}