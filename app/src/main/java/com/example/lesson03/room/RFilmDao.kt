package com.example.lesson03.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RFilmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFilm(film : RFilm)

    @Query("SELECT * FROM films_table WHERE `idFilm` = :idFilm LIMIT 1")
     fun checkFilm(idFilm : Int) :  List<RFilm>

    @Query("SELECT * FROM films_table ORDER BY id ASC")
    fun readAllData() : LiveData<List<RFilm>>

    @Query("SELECT * FROM films_table WHERE `like` = 1 ORDER BY id ASC")
    fun readAllLike() : LiveData<List<RFilm>>

    @Query("SELECT * FROM films_table WHERE reminder = 1 ORDER BY id ASC")
    fun readAllReminder() : LiveData<List<RFilm>>

    @Query("DELETE FROM films_table")
    suspend fun deleteAll()

    @Query("DELETE FROM films_table WHERE imagePath = :imagePath")
    suspend fun deleteFilm(imagePath : String)

    @Query("UPDATE films_table SET `like` = :lik WHERE imagePath = :imagePath")
     fun updateLike(lik : Int, imagePath : String)

    @Query("UPDATE films_table SET description = :description, imagePath = :imagePath  WHERE id = :id")
    suspend fun updateSearchFilm(id : Int, imagePath : String, description: String)

    @Query("UPDATE films_table SET reminderDataTime = :reminderDataTime, reminder = :reminder WHERE imagePath = :imagePath")
     fun updateReminder(reminder : Int, imagePath : String, reminderDataTime: String)

    @Query("SELECT * FROM films_table ORDER BY id ASC LIMIT :countLimit")
    fun selectAllFilms(countLimit: Int): List<RFilm>

    @Query("SELECT * FROM films_table WHERE `like` = 1 ORDER BY id ASC")
    fun selectAllFavorites() : List<RFilm>

    @Query("SELECT * FROM films_table WHERE reminder = 1 ORDER BY id ASC")
    fun selectAllReminders() : List<RFilm>

    @Query("SELECT * FROM films_table WHERE `idFilm` = :idFilm LIMIT 1")
    fun selectFilmItem(idFilm : Int) :  List<RFilm>
}