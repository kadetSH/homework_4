package com.example.lesson03.recyclerMy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson03.R

class FilmsAdapter(private val layoutInflater: LayoutInflater,
                   private val items: ArrayList<FilmsItem>,
                   private val listener: ((newsItem: FilmsItem, position: Int, note : String) -> Unit)?) : RecyclerView.Adapter<FilmsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmsViewHolder {
        return FilmsViewHolder(layoutInflater.inflate(R.layout.template, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FilmsViewHolder, position: Int) {
        holder.bind(items[position])

        holder.star.setOnClickListener {
            listener?.invoke(items[position], position, "star")
        }

        holder.description.setOnClickListener {
            listener?.invoke(items[position], position, "description")
        }

        holder.dellFilmIcon.setOnClickListener {
            listener?.invoke(items[position], position, "dellIcon")
        }

        holder.reminder.setOnClickListener {
            listener?.invoke(items[position], position, "reminder")
        }

        holder.reminderDataTime.setOnClickListener {
            listener?.invoke(items[position], position, "reminderDataTime")
        }
    }

    fun setItems(rep : ArrayList<FilmsItem>) {
        items.clear()
        items.addAll(rep)
        notifyDataSetChanged()
    }

}