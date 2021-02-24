package com.example.lesson03

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.lesson03.recyclerMy.FilmsAdapter
import com.example.lesson03.recyclerMy.FilmsItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.template.view.*


class FilmsDescriptionFragment : Fragment() {

    companion object {

        fun newInstance(list: FilmsItem): FilmsDescriptionFragment {
            val args = Bundle()
            args.putSerializable("spisok", list)

            val fragment = FilmsDescriptionFragment()
            fragment.arguments = args
            return fragment
        }


        var adapter: FilmsAdapter? = null
    }


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
        println("")
        var spisokFilm = resources.getStringArray(R.array.film)
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

            var pos = spisokFilm.indexOf(it.nameFilm)
            if (pos > -1) {
                descriptionId.text = resources.getStringArray(R.array.film_description)[pos]
            } else {
                descriptionId.text = it.shortDescription
            }
        }


    }

    fun getImagePath(name : String): String{
        val puth = "https://themoviedb.org/t/p/w500${name}?api_key=2931998c3a80d7806199320f76d65298&language=ru-Ru"
        return puth
    }

}