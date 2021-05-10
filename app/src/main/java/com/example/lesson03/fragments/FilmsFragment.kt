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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_films_list.*
import javax.inject.Inject


class FilmsFragment : DaggerFragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: RepoListFilmsViewModel by viewModels {
        viewModelFactory
    }

    companion object {
        const val TAG = "ProverkaTAG"
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
                viewModel.filmLikeEvent(filmsItem, position, note, it)
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

        //Через RxJava2

        viewModel.repos1
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                result.observe(viewLifecycleOwner, Observer<ArrayList<FilmsItem>> { item ->

                    if (list.size < item.size) {
                        for (x: Int in list.size until item.size) {
                            list.add(item[x])
                            recyclerView?.adapter?.notifyItemChanged(list.size - 1)
                        }
                    }
                })
            }, { error ->

            })

        viewModel.fabLiveData1
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                result.observe(viewLifecycleOwner, Observer<Boolean> {
                    fabIcon?.isVisible = it
                })
            }, { error ->

            })

        viewModel.animBool1
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                result.observe(viewLifecycleOwner, Observer<Boolean> {
                    if (it == true) {
                        animIc?.visibility = View.VISIBLE
                        animIc.startAnimation(starAnim)
                    } else {
                        animIc.clearAnimation()
                        animIc?.visibility = View.INVISIBLE
                    }
                })
            }, { error ->

            })

        viewModel.getSnackBarString().observe(this, Observer{
            val strArr = it.split("%")
            val lik = strArr[0].toInt()
            val imagePath = strArr[1]
            val name = strArr[2]
            val note = strArr[3]
            snackBarShow(lik, imagePath, name, note)
        })

        viewModel.getFragmentManager().observe(this, Observer{
            (context as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.FrameLayoutContainer, it)
                .addToBackStack(null)
                .commit()
        })


//        viewModel.snackBarRX
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ result ->
//                result.observe(viewLifecycleOwner, Observer<String> {
//                    val strArr = it.split("%")
//                    val lik = strArr[0].toInt()
//                    val imagePath = strArr[1]
//                    val name = strArr[2]
//                    val note = strArr[3]
//                    snackBarShow(lik, imagePath, name, note)
//                })
//            }, { error ->
//
//            })

        viewModel.filmItemRX
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                result.observe(viewLifecycleOwner, Observer<FilmsItem> { item ->
                    if ((note == "star") || (note == "reminder")) {
                        list.removeAt(position)
                        list.add(position, item)
                        recyclerView?.adapter?.notifyItemChanged(position)
                    }
                    if (note == "dellIcon") {
                        list.removeAt(position)
                        recyclerView?.adapter?.notifyItemRemoved(position)
                    }
                })
            }, { error ->

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