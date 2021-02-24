package com.example.lesson03.recyclerMy

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson03.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.template.view.*

class FilmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



    val colorTrue = ContextCompat.getColor(itemView.context, R.color.starTrue)
    val colorFalse = ContextCompat.getColor(itemView.context, R.color.starFalse)
    var star = itemView.idStar
    var description = itemView.description
    var dellFilmIcon = itemView.dellFilm

    fun bind(item: FilmsItem) {

        val imagePut = getImagePath(item.imageFilm)

        (itemView.nameFilm as TextView).text = item.nameFilm
        (itemView.shortDescription as TextView).text = item.shortDescription
        star.isSelected = item.star

        Picasso.get()
            .load(imagePut)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_error)
            .resize(150, 150)
            .centerCrop()
            .into(itemView.imageFilmId as ImageView)

        if (star.isSelected == true){
            star.setBackgroundColor(colorTrue)

            var starAnim = android.view.animation.AnimationUtils.loadAnimation(star.context, R.anim.scale_star_1)
            star.startAnimation(starAnim)
        }else star.setBackgroundColor(colorFalse)

    }

    fun getImagePath(name : String): String{
        val puth = "https://themoviedb.org/t/p/w200${name}?api_key=2931998c3a80d7806199320f76d65298&language=ru-Ru"
        return puth
    }

}