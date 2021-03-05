package com.example.lesson03.room

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope

class FilmRepository(private val filmDao : RFilmDao) {

    val readAllData : LiveData<List<RFilm>> = filmDao.readAllData()
    val readAllLike : LiveData<List<RFilm>> = filmDao.readAllLike()

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

}