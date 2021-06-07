package com.example.lesson03.room


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.room.Room
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@SmallTest
class RFilmDaoTest {

    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var rFilmDao: RFilmDao
    lateinit var filmDatabase: FilmDatabase
    private val rFilm = RFilm(1, 123, "name", "/sdf3ewr33", 0, "description", 0, "30.05.2021")

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        filmDatabase = Room.inMemoryDatabaseBuilder(
            context,
            FilmDatabase::class.java
        ).allowMainThreadQueries().build()

        rFilmDao = filmDatabase.filmDao()
    }

    @After
    fun teardown() {
        filmDatabase.close()
    }


    @Test
    fun addFilmTest() {

        runBlockingTest {
            rFilmDao.addFilm(rFilm)
            val readAllFilmDao = rFilmDao.readAllData()
            val allItem = readAllFilmDao.getOrAwaitValue()
            assertThat(allItem).contains(rFilm)
        }
    }

    @Test
    fun deleteAllTest() {
        runBlockingTest {
            rFilmDao.addFilm(rFilm)
            rFilmDao.deleteAll()
            val readAllFilmDao = rFilmDao.readAllData()
            val allItem = readAllFilmDao.getOrAwaitValue()
            assertThat(allItem.size).isEqualTo(0)
        }
    }

    @Test
    fun updateLikeTest() {
        runBlockingTest {
            rFilmDao.addFilm(rFilm)
            rFilmDao.updateLike(1, rFilm.imagePath)
            val readAllFilmDao = rFilmDao.readAllData()
            val allItem = readAllFilmDao.getOrAwaitValue()
            assertThat(allItem[0].like).isEqualTo(1)
        }
    }

    @Test
    fun updateSearchFilmTest() {
        runBlockingTest {
            rFilmDao.addFilm(rFilm)
            rFilmDao.updateSearchFilm(1, "newImagePath", "newDescription")
            val readAllFilmDao = rFilmDao.readAllData()
            val allItem = readAllFilmDao.getOrAwaitValue()
            assertThat(allItem[0].imagePath).isEqualTo("newImagePath")
        }
    }


}