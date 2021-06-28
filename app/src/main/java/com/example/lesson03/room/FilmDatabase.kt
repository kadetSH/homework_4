package com.example.lesson03.room

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RFilm::class], version = 1, exportSchema = false)
abstract class FilmDatabase : RoomDatabase() {

    abstract fun filmDao() : RFilmDao

    companion object{
        @Volatile
        private var INSTANCE : FilmDatabase? = null

        fun getFilmDatabase(application: Application): FilmDatabase{

            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                    application.applicationContext,
                    FilmDatabase::class.java,
                    "film_database4"
                ).build()
                INSTANCE = instance

                return instance
            }
        }
    }

}