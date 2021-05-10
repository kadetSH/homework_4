package com.example.lesson03.room


import io.reactivex.Flowable

class FilmRepository(private val filmDao : RFilmDao) {

    val readAllData : Flowable<List<RFilm>> = filmDao.readAllData()
    val readAllLike : Flowable<List<RFilm>> = filmDao.readAllLike()
    val readAllReminder : Flowable<List<RFilm>> = filmDao.readAllReminder()

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

    fun selectAllFilms(countLimit: Int) : Flowable<List<RFilm>> {
        return filmDao.selectAllFilms(countLimit)
    }

    fun selectAllFavoritesFilms() : List<RFilm> {
        return filmDao.selectAllFavorites()
    }

    fun selectAllRemindersFilms() : List<RFilm> {
        return filmDao.selectAllReminders()
    }

    fun selectFilmItem(idFilm: Int) : List<RFilm> {
        return filmDao.selectFilmItem(idFilm)
    }

}