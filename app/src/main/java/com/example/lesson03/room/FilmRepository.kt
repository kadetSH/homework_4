package com.example.lesson03.room


import androidx.lifecycle.LiveData
import io.reactivex.Flowable

class FilmRepository(private val filmDao : RFilmDao) {

    val readAllLike : LiveData<List<RFilm>> = filmDao.readAllLike()
    val readAllReminder : LiveData<List<RFilm>> = filmDao.readAllReminder()
    val readAllFilms : LiveData<List<RFilm>> = filmDao.readAllData()

    fun checkFilm(idFilm: Int) : List<RFilm> {
        return filmDao.checkFilm(idFilm)
    }

    suspend fun addFilm(film : RFilm){
        filmDao.addFilm(film)
    }

    suspend fun deleteAll(){
        filmDao.deleteAll()
    }

    suspend fun deleteFilm(imagePath : String){
        filmDao.deleteFilm(imagePath)
    }

     fun updateLike(lik: Int, imagePath: String){
        filmDao.updateLike(lik, imagePath)
    }

    suspend fun updateSearchFilm(id: Int, imagePath: String, description: String){
        filmDao.updateSearchFilm(id, imagePath, description)
    }

     fun updateReminder(lik: Int, imagePath: String, reminderDataTime: String){
       if (reminderDataTime != "" && (lik==1)) {
           filmDao.updateReminder(lik, imagePath, reminderDataTime)
       }else if (reminderDataTime == "" && (lik==0)){
           filmDao.updateReminder(lik, imagePath, reminderDataTime)
       }
    }

    fun selectAllFilms(countLimit: Int) : List<RFilm> {
        return filmDao.selectAllFilms(countLimit)
    }

    fun selectFilmItem(idFilm: Int) : List<RFilm> {
        return filmDao.selectFilmItem(idFilm)
    }

}