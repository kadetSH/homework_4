package com.example.lesson03.room

import androidx.lifecycle.LiveData

class FilmRepository(private val filmDao : RFilmDao) {

    val readAllData : LiveData<List<RFilm>> = filmDao.readAllData()
    val readAllLike : LiveData<List<RFilm>> = filmDao.readAllLike()

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

}