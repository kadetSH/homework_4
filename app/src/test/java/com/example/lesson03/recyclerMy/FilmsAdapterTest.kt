package com.example.lesson03.recyclerMy


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.test.filters.SmallTest
import com.example.lesson03.R
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner


@Suppress("ControlFlowWithEmptyBody")
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@SmallTest
class FilmsAdapterTest {


    private var items = ArrayList<FilmsItem>()
    var layoutInflater: LayoutInflater? = null
    private var adapterFilms: FilmsAdapter? = null

    @Before
    fun setup() {
        layoutInflater = mock(LayoutInflater::class.java)
        items.add(
            FilmsItem(
                "nameFilm",
                "imagePath",
                "shortDescription",
                "check",
                false,
                1,
                0,
                "00:00"
            )
        )

        adapterFilms = FilmsAdapter(
            layoutInflater!!,
            items
        ) { newsItem: FilmsItem, position: Int, note: String -> Unit }
    }

    @After
    fun tearDown() {
        layoutInflater = null
    }

    @Test
    fun itemCount_afterSetItems() {
        val itemsMock = ArrayList<FilmsItem>()
        itemsMock.add(mock(FilmsItem::class.java))
        itemsMock.add(mock(FilmsItem::class.java))
        adapterFilms?.setItems(itemsMock)
        assertThat(adapterFilms?.itemCount).isEqualTo(2)
    }

    @Test
    fun checkItemCount() {
        Assert.assertNotNull(layoutInflater)
        Assert.assertEquals(adapterFilms?.itemCount, 1)
    }

    @Test
    fun checkObserver() {
        val itemsMock = ArrayList<FilmsItem>()
        itemsMock.add(mock(FilmsItem::class.java))
        val observer = mock(AdapterDataObserver::class.java)
        adapterFilms?.registerAdapterDataObserver(observer)
        adapterFilms?.setItems(itemsMock)
        verify(observer, timeout(100)).onChanged()
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun qw() {
//        val parent = mock(ViewGroup::class.java)
//        val parent2 = mockk<ViewGroup>()
//        val itemView = mock(View::class.java)
//
//        val imageView = mock(ImageView::class.java)
////        `when`(itemView.findViewById(R.id.imageFilmId) as ImageView ).thenReturn(imageView)
//
//        val itemView2 = mockk<View>()
//        every { itemView2.findViewById(R.id.imageFilmId) as ImageView} returns imageView
//
//        val descriptionTextView = mockk<TextView>()
//        every { itemView2.findViewById(R.id.shortDescription) as TextView} returns descriptionTextView
//
//        val titleTextView = mockk<TextView>()
//        every { itemView2.findViewById(R.id.nameFilm) as TextView} returns titleTextView
//
//        val imageFilm = mockk<ImageView>()
//        every { itemView2.findViewById(R.id.imageFilmId) as ImageView} returns imageFilm
//
//        val star = mockk<ImageView>()
//        every { itemView2.findViewById(R.id.idStar) as ImageView} returns star
//
//        val reminder = mockk<ImageView>()
//        every { itemView2.findViewById(R.id.idReminder) as ImageView} returns reminder
//
//        val reminderText = mockk<TextView>()
//        every { itemView2.findViewById(R.id.idReminderDataTime) as TextView} returns reminderText
//
//        val descriptionButt = mockk<Button>()
//        every { itemView2.findViewById(R.id.description) as Button} returns descriptionButt
//
//        val dellFilm = mockk<ImageView>()
//        every { itemView2.findViewById(R.id.dellFilm) as ImageView} returns dellFilm
//
//        val layoutInflater2 = mockk<LayoutInflater>()
//        val presenter = spyk(FilmsAdapter(layoutInflater2,
//            items
//        ) { newsItem: FilmsItem, position: Int, note: String -> Unit })
//
//        every { layoutInflater2.inflate(R.layout.template, parent2, false) } returns itemView2
//
//
//        val viewHolder = mockk<FilmsViewHolder>()
//        every { presenter.onCreateViewHolder(parent2, 0) } returns viewHolder
//
//        println(itemView2.id)

//        assertThat(viewHolder.itemView).isSameInstanceAs(itemView2)

    }

    @Test
    fun df() {
//        var itemsList = ArrayList<FilmsItem>()
//        val itemsMock = ArrayList<FilmsItem>()
//        itemsMock.add(mock(FilmsItem::class.java))
//        itemsMock.add(mock(FilmsItem::class.java))

//        println(itemsMock.size)
//        adapterFilms?.setItems(itemsMock)
//
//        println(adapterFilms?.itemCount)
//
//        for (position: Int in 0 until itemsMock.size ){
//            val filmsViewHolder = mock(FilmsViewHolder::class.java)
//            println(1)
//            adapterFilms?.onBindViewHolder(filmsViewHolder, position)
//            verify(filmsViewHolder).bind(itemsMock[position])
//        }

    }

//    class FinalClass(inflater: LayoutInflater, items: ArrayList<FilmsItem>) {
//        private val inflater = inflater
//        private val items = items
//        fun finalMethod(): FilmsAdapter {
//            println(items.size)
//            return FilmsAdapter(
//                inflater,
//                items
//            ) { filmsItem: FilmsItem, position: Int, note: String -> }
//        }
//    }

}