package com.example.lesson03.recyclerMy

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson03.R
import com.example.lesson03.viewmodel.RepoListFilmsViewModel
import com.otus.otusaacpart1.data.entity.Repo
import kotlinx.coroutines.withContext

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
    }

    fun setItems(rep : ArrayList<FilmsItem>) {
        items.clear()
        items.addAll(rep)
        notifyDataSetChanged()
    }

}