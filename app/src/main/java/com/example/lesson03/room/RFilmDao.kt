package com.example.lesson03.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.gson.annotations.Expose

@Dao
interface RFilmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFilm(film : RFilm)

    @Query("SELECT * FROM films_table WHERE `idFilm` = :idFilm LIMIT 1") //idFilm, name, imagePath, `like`, description
    suspend fun checkFilm(idFilm : Int) :  List<RFilm>

    @Query("SELECT * FROM films_table ORDER BY id ASC")
    fun readAllData() : LiveData<List<RFilm>>

    @Query("SELECT * FROM films_table WHERE `like` = 1 ORDER BY id ASC")
    fun readAllLike() : LiveData<List<RFilm>>

    @Query("DELETE FROM films_table")
    suspend fun deleteAll()

    @Query("DELETE FROM films_table WHERE imagePath = :imagePath")
    suspend fun deleteFilm(imagePath : String)

    @Query("UPDATE films_table SET `like` = :lik WHERE imagePath = :imagePath")
    suspend fun updateLike(lik : Int, imagePath : String)

    @Query("UPDATE films_table SET description = :description, imagePath = :imagePath  WHERE id = :id")
    suspend fun updateSearchFilm(id : Int, imagePath : String, description: String)
}