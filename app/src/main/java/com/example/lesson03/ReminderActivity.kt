package com.example.lesson03

import android.content.res.Resources
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class ReminderActivity : AppCompatActivity() {

    private val url = "https://themoviedb.org/t/p/w500"
    private val key = BuildConfig.apiKey
    private val lang = BuildConfig.langRu

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

        val filmName = this.intent.getStringExtra(Resources.getSystem().getString(R.string.INTENT_label_EXTRA_nameFilm))
        val filmDescription = intent.getStringExtra(Resources.getSystem().getString(R.string.INTENT_label_EXTRA_descriptionFilm))
        val filmImagePath = intent.getStringExtra(Resources.getSystem().getString(R.string.INTENT_label_EXTRA_imagePath))

        filmName?.let { name ->
            filmDescription?.let { description ->
                filmImagePath?.let { imagePath ->
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

    private fun getImagePath(name: String): String = "$url${name}?api_key=$key&language=$lang"

}