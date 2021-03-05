package com.example.lesson03.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


@Database(entities = [RFilm::class], version = 1, exportSchema = false)
abstract class FilmDatabase : RoomDatabase() {

    abstract fun filmDao() : RFilmDao

    companion object{
        @Volatile
        private var INSTANCE : FilmDatabase? = null

        fun getFilmDatabase(context: Context): FilmDatabase{
            val tempInstance = INSTANCE
            val applicationScope = CoroutineScope(SupervisorJob())
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FilmDatabase::class.java,
                    "film_database2"
                ).build()
                INSTANCE = instance

                return instance
            }
        }


    }

}