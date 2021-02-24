package com.example.lesson03.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "films_table")
data class RFilm(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val name : String,
    val imagePath : String,
    val like : Int,
    val description : String
)