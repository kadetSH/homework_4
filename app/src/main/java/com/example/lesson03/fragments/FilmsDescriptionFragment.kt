package com.example.lesson03.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.lesson03.R
import com.example.lesson03.recyclerMy.FilmsAdapter
import com.example.lesson03.recyclerMy.FilmsItem
import com.squareup.picasso.Picasso


class FilmsDescriptionFragment : Fragment() {

    companion object {
        fun newInstance(list: FilmsItem): FilmsDescriptionFragment {
            val args = Bundle()
            args.putSerializable("spisok", list)

            val fragment = FilmsDescriptionFragment()
            fragment.arguments = args
            return fragment
        }
    }

    val url = "https://themoviedb.org/t/p/"
    val api_key = "2931998c3a80d7806199320f76d65298"
    val lang = "ru-Ru"
    var list: FilmsItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list = arguments?.getSerializable("spisok") as FilmsItem
        val toolbar = view.findViewById<Toolbar>(R.id.toolbarM)
        toolbar.title = list?.nameFilm
        val spisokFilm = resources.getStringArray(R.array.film)
        val imageId = view.findViewById<ImageView>(R.id.app_bar_image)
        val descriptionId = view.findViewById<TextView>(R.id.description)
        list?.let {
//            imageId.setImageResource(it.imageFilm)  //установить картинку в ImageView
            val imagePut = getImagePath(it.imageFilm)

            Picasso.get()
                .load(imagePut)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_error)
                .resize(300, 400)
                .centerCrop()
                .into(imageId)

            val pos = spisokFilm.indexOf(it.nameFilm)
            if (pos > -1) {
                descriptionId.text = resources.getStringArray(R.array.film_description)[pos]
            } else {
                descriptionId.text = it.shortDescription
            }
        }

    }

    fun getImagePath(name: String): String {
        val puth = "${url}w500${name}?api_key=${api_key}&language=${lang}"
        return puth
    }

}