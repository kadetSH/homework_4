package com.example.lesson03.recyclerMy

import android.app.Application
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson03.R
import kotlin.coroutines.coroutineContext

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
            listener?.invoke(items[position], position,  layoutInflater.context.resources.getString(R.string.NOTE_STAR))
        }

        holder.description.setOnClickListener {
            listener?.invoke(items[position], position, layoutInflater.context.resources.getString(R.string.NOTE_DESCRIPTION))
        }

        holder.dellFilmIcon.setOnClickListener {
            listener?.invoke(items[position], position, layoutInflater.context.resources.getString(R.string.NOTE_DEL_ITEM))
        }

        holder.reminder.setOnClickListener {
            listener?.invoke(items[position], position, layoutInflater.context.resources.getString(R.string.NOTE_REMINDER))
        }

        holder.reminderDataTime.setOnClickListener {
            listener?.invoke(items[position], position, layoutInflater.context.resources.getString(R.string.NOTE_REMINDER_DATA))
        }
    }

    fun setItems(rep : ArrayList<FilmsItem>) {
        items.clear()
        items.addAll(rep)
        notifyDataSetChanged()
    }

}