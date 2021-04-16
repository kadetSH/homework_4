package com.example.lesson03

import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson03.recyclerMy.Decor
import com.example.lesson03.recyclerMy.FilmsAdapter
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.room.RFilm
import com.example.lesson03.viewmodel.RepoListFilmsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_films_list.*
import kotlin.properties.Delegates

class FilmsFragment : Fragment() {

    var list = ArrayList<FilmsItem>()
    var listRoom = ArrayList<FilmsItem>()
    var listLike = ArrayList<FilmsItem>()
    var listReminder = ArrayList<FilmsItem>()
    private lateinit var starAnim: Animation
    private var firstStart = false

    private var favoritesBoolTest by Delegates.notNull<Int>()

    var filmsBool: Boolean = true
    private var favoritesBool: Boolean = false
    var reminderBool: Boolean = false


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
        observeViewModel()
    }

    private fun observeViewModel(){
        viewModel.repos.observe(viewLifecycleOwner, Observer<ArrayList<FilmsItem>> {
            adapter.setItems(it)
        })

        viewModel.readAllData.observe(viewLifecycleOwner, Observer<List<RFilm>> {
            if (!firstStart) {
                firstStart = true
            }
            else {
                listRoom.clear()
                it.forEach {
                    val like: Boolean
                    if (it.like == 0) like = false
                    else like = true
                    listRoom.add(FilmsItem(it.name, it.imagePath, it.description, "", like, it.idFilm, it.reminder, it.reminderDataTime))
                }
                if (filmsBool) {
                    adapter.setItems(listRoom)
                    favoritesBool = false
                }
            }
        })

        viewModel.readAllLike.observe(viewLifecycleOwner, Observer<List<RFilm>> {
            listLike.clear()
            it.forEach {
                val like: Boolean
                like = it.like != 0
                listLike.add(FilmsItem(it.name, it.imagePath, it.description, "", like, it.idFilm, it.reminder, it.reminderDataTime))
            }

            if (viewModel.favoritesPage) {  //favoritesBool
                adapter.setItems(listLike)
            }
        })

        viewModel.readAllReminder.observe(viewLifecycleOwner, Observer<List<RFilm>>{
            listReminder.clear()
                it.forEach {
                    val like: Boolean
                    like = it.like != 0
                    listReminder.add(FilmsItem(it.name, it.imagePath, it.description, "", like, it.idFilm, it.reminder, it.reminderDataTime))
                }
            if (reminderBool){
                adapter.setItems(listReminder)
            }
        })

        viewModel.readLikeBool.observe(viewLifecycleOwner, Observer<Boolean> {
            filmsBool = false
            reminderBool = false
            if (it) {
                adapter.setItems(listLike)
                favoritesBool = true
            }
        })

        viewModel.readReminderBool.observe(viewLifecycleOwner, Observer<Boolean> {
            filmsBool = false
            favoritesBool = false
            if (it) {
                adapter.setItems(listReminder)
                reminderBool = true
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
            val strArr = it.split("%")
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