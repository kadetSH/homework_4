package com.example.lesson03.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.lesson03.BuildConfig
import com.example.lesson03.R
import com.example.lesson03.recyclerMy.FilmsItem
import com.squareup.picasso.Picasso
import java.io.Serializable

class FilmsDescriptionFragment : Fragment() {

    companion object {
        fun newInstance(list: Serializable?): FilmsDescriptionFragment {
            val args = Bundle()
            args.putSerializable("list", list)
            val fragment = FilmsDescriptionFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val url = "https://themoviedb.org/t/p/"
    private val apiKey = BuildConfig.apiKey
    private val lang = BuildConfig.langRu
    var list: FilmsItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list = arguments?.getSerializable("list") as FilmsItem
        val toolbar = view.findViewById<Toolbar>(R.id.toolbarM)
        toolbar.title = list?.nameFilm
        val listFilm = resources.getStringArray(R.array.film)
        val imageId = view.findViewById<ImageView>(R.id.app_bar_image)
        val descriptionId = view.findViewById<TextView>(R.id.description)
        list?.let { itemFilm ->
            val imagePut = getImagePath(itemFilm.imageFilm)
            Picasso.get()
                .load(imagePut)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_error)
                .resize(300, 400)
                .centerCrop()
                .into(imageId)
            val position = listFilm.indexOf(itemFilm.nameFilm)
            if (position > -1) {
                descriptionId.text = resources.getStringArray(R.array.film_description)[position]
            } else {
                descriptionId.text = itemFilm.shortDescription
            }
        }
    }

    private fun getImagePath(name: String): String = "${url}w500${name}?api_key=$apiKey&language=$lang"
}