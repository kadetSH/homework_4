package com.example.lesson03

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso


class ReminderActivity : AppCompatActivity() {

    private val nameFilmID by lazy {
        findViewById<TextView>(R.id.nameFilmID)
    }

    private val descriptionFilmID by lazy {
        findViewById<TextView>(R.id.descriptionFilmID)
    }

    private val imageFilmID by lazy {
        findViewById<ImageView>(R.id.imageFilmID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        val fName = this.intent.getStringExtra("EXTRAnameFilm")
        val fDescription = intent.getStringExtra("EXTRA_descriptionFilm")
        val fImagePath = intent.getStringExtra("EXTRA_imagePath")

        fName?.let { name ->
            fDescription?.let { description ->
                fImagePath?.let { imagePath ->
                    nameFilmID.text = name
                    descriptionFilmID.text = description
                    loadImage(imagePath)
                }
            }
        }
    }

    private fun loadImage(imagePath: String) {
        Picasso.get()
            .load(getImagePath(imagePath))
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_error)
            .resize(700, 900)
            .centerCrop()
            .into(imageFilmID)
    }

    private fun getImagePath(name: String): String {
        val puth =
            "https://themoviedb.org/t/p/w500${name}?api_key=2931998c3a80d7806199320f76d65298&language=ru-Ru"
        return puth
    }
}