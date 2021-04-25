package com.example.lesson03.room

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope

class FilmRepository(private val filmDao : RFilmDao) {

    val readAllData : LiveData<List<RFilm>> = filmDao.readAllData()
    val readAllLike : LiveData<List<RFilm>> = filmDao.readAllLike()
    val readAllReminder : LiveData<List<RFilm>> = filmDao.readAllReminder()

    suspend fun checkFilm(idFilm: Int) : List<RFilm> {
        return filmDao.checkFilm(idFilm)
    }

    suspend fun addFilm(film : RFilm){
        filmDao.addFilm(film)
    }

    suspend fun delAll(){
        filmDao.deleteAll()
    }

    suspend fun delleteFilm(imagePath : String){
        filmDao.deleteFilm(imagePath)
    }

    suspend fun updateLike(lik: Int, imagePath: String){
        filmDao.updateLike(lik, imagePath)
    }

    suspend fun updateSearchFilm(id: Int, imagePath: String, description: String){
        filmDao.updateSearchFilm(id, imagePath, description)
    }

    suspend fun updateReminder(lik: Int, imagePath: String, reminderDataTime: String){
       if (!reminderDataTime.equals("") && (lik==1)) {
           filmDao.updateReminder(lik, imagePath, reminderDataTime)
       }else if (reminderDataTime.equals("") && (lik==0)){
           filmDao.updateReminder(lik, imagePath, reminderDataTime)
       }
    }

    fun selectAllFilms(countLimit: Int) : List<RFilm> {
        return filmDao.selectAllFilms(countLimit)
    }

    fun selectAllFavoritesFilms() : List<RFilm> {
        return filmDao.selectAllFavorites()
    }

    fun selectAllRemindersFilms() : List<RFilm> {
        return filmDao.selectAllReminders()
    }


}