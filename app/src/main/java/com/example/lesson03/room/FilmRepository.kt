package com.example.lesson03.room


import androidx.lifecycle.LiveData
import com.example.lesson03.BuildConfig

class FilmRepository(private val filmDao : RFilmDao) {

    val readAllLike : LiveData<List<RFilm>> = filmDao.readAllLike()
    val readAllReminder : LiveData<List<RFilm>> = filmDao.readAllReminder()

    fun checkFilm(idFilm: Int) : List<RFilm> {
        return filmDao.checkFilm(idFilm)
    }

    suspend fun addFilm(film : RFilm){
        filmDao.addFilm(film)
    }

    suspend fun deleteFilm(imagePath : String){
        filmDao.deleteFilm(imagePath)
    }

     fun updateLike(selectFavorites: Int, imagePath: String){
        filmDao.updateLike(selectFavorites, imagePath)
    }

    suspend fun updateSearchFilm(id: Int, imagePath: String, description: String){
        filmDao.updateSearchFilm(id, imagePath, description)
    }

     fun updateReminder(selectFavorites: Int, imagePath: String, reminderDataTime: String){
       if (reminderDataTime != "" && (selectFavorites== BuildConfig.ACTION_TO_ACCEPT)) {
           filmDao.updateReminder(selectFavorites, imagePath, reminderDataTime)
       }else if (reminderDataTime == "" && (selectFavorites==BuildConfig.ACTION_CANCEL)){
           filmDao.updateReminder(selectFavorites, imagePath, reminderDataTime)
       }
    }

    fun selectAllFilms(countLimit: Int) : List<RFilm> {
        return filmDao.selectAllFilms(countLimit)
    }

    fun selectFilmItem(idFilm: Int) : List<RFilm> {
        return filmDao.selectFilmItem(idFilm)
    }

}