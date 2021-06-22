package com.example.lesson03.recyclerMy

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson03.BuildConfig
import com.example.lesson03.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.template.view.*

class FilmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val url = "https://themoviedb.org/t/p/w200"
    private val key = BuildConfig.apiKey
    private val lang = BuildConfig.langRu

    private val colorTrue = ContextCompat.getColor(itemView.context, R.color.starTrue)
    private val colorFalse = ContextCompat.getColor(itemView.context, R.color.starFalse)
    var star: ImageView = itemView.idStar
    var description: Button = itemView.description
    var deleteFilmIcon: ImageView = itemView.dellFilm
    var reminder: ImageView = itemView.idReminder
    val reminderDataTime: TextView = itemView.idReminderDataTime

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

        if (star.isSelected){
            star.setBackgroundColor(colorTrue)
            val starAnim = android.view.animation.AnimationUtils.loadAnimation(star.context, R.anim.scale_star_1)
            star.startAnimation(starAnim)
        }else star.setBackgroundColor(colorFalse)

        if (item.reminder == 1){
            reminder.setBackgroundColor(colorTrue)
            reminderDataTime.isVisible = true
            reminderDataTime.text = item.reminderDataTime
        }else {
            reminder.setBackgroundColor(colorFalse)
            reminderDataTime.isVisible = false
        }
    }

    private fun getImagePath(name : String): String = "${url}${name}?api_key=${key}&language=${lang}"

}