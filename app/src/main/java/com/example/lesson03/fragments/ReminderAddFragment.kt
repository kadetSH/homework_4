package com.example.lesson03.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.lesson03.R
import com.example.lesson03.UploadWorker
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.viewmodel.RepoListFilmsViewModel
import dagger.android.support.DaggerFragment
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ReminderAddFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: RepoListFilmsViewModel by viewModels {
        viewModelFactory
    }

    var list: FilmsItem? = null
    var parentFragment: String? = null
    lateinit var showReminderDate: EditText
    lateinit var showReminderTime: EditText
    var yearGlobal: Int = 0
    var monthGlobal: Int = 0
    var dayGlobal: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list = arguments?.getSerializable(resources.getString(R.string.bundleFlag_listItem)) as FilmsItem
        parentFragment = arguments?.getString(resources.getString(R.string.bundleFlag_parent))
        init(view)
    }

    @SuppressLint("SetTextI18n")
    private fun isData(context: Context) {
        val listener =
            DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year: Int, month: Int, day: Int ->
                yearGlobal = year
                monthGlobal = month
                dayGlobal = day
                var dayStart: String = day.toString()
                if (dayStart.length == 1) {
                    dayStart = "0$dayStart"
                }

                var monthStart: String = (month + 1).toString()
                if (monthStart.length == 1) {
                    monthStart = "0$monthStart"
                }

                val yearStart = year.toString()
                showReminderDate.setText("${dayStart}.${monthStart}.${yearStart}")
            }
        val newCalender = Calendar.getInstance()
        val dialog = DatePickerDialog(
            context,
            listener,
            newCalender[Calendar.YEAR],
            newCalender[Calendar.MONTH],
            newCalender[Calendar.DAY_OF_MONTH]
        )
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun isTime(context: Context) {
        val listener =
            TimePickerDialog.OnTimeSetListener { timePicker: TimePicker, hour: Int, minute: Int ->
                var hourSTR = hour.toString()
                if (hourSTR.length == 1) {
                    hourSTR = "0$hourSTR"
                }
                var minuteSTR = minute.toString()
                if (minuteSTR.length == 1) {
                    minuteSTR = "0$minuteSTR"
                }
                showReminderTime.setText("${hourSTR}:${minuteSTR}")
            }
        val newCalender = Calendar.getInstance()
        val startTime = TimePickerDialog(
            context,
            listener,
            newCalender[Calendar.HOUR_OF_DAY],
            newCalender[Calendar.MINUTE],
            true
        )
        startTime.show()
    }


    private fun init(view: View) {
        view.findViewById<Button>(R.id.idSelectData).setOnClickListener {
            this.context?.let { idSelectData -> isData(idSelectData) }
        }
        view.findViewById<Button>(R.id.idSelectTime).setOnClickListener {
            this.context?.let { idSelectTime -> isTime(idSelectTime) }
        }
        view.findViewById<Button>(R.id.idReminderSave).setOnClickListener {
            saveOnClick()
        }
        view.findViewById<Button>(R.id.idReminderCancell).setOnClickListener {
            cancelClick()
        }
        showReminderDate = view.findViewById<EditText>(R.id.reminderDate)
        showReminderTime = view.findViewById<EditText>(R.id.reminderTime)
    }

    private fun cancelClick() {
        if (parentFragment == resources.getString(R.string.bundleFlag_favorites)) {
            (context as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.FrameLayoutContainer, FavoritesFragment())
                .addToBackStack(null)
                .commit()
        } else if (parentFragment == resources.getString(R.string.bundleFlag_repoList)) {
            (context as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.FrameLayoutContainer, FilmsFragment())
                .addToBackStack(null)
                .commit()}
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveOnClick() {
        if (showReminderDate.text.toString() == "") {
            selectToast(resources.getString(R.string.toastNoDateReminder))
            return
        }
        if (showReminderTime.text.toString() == "") {
            selectToast(resources.getString(R.string.toastNoTimeReminder))
            return
        }
        var dayTimeNumberNew: Long = 0
        val formatDate = SimpleDateFormat()
        formatDate.applyPattern("dd.MM.yyyy HH:mm")
        val isDate =
            formatDate.parse(showReminderDate.text.toString() + " " + showReminderTime.text.toString())
        dayTimeNumberNew = isDate.time

        val realTime = Calendar.getInstance().time
        val realTimeNumber = realTime.time
        if (dayTimeNumberNew - realTimeNumber > 0) {

            val dateDateTime = Date(dayTimeNumberNew)
            val formatDateTime = SimpleDateFormat("yyyy.MM.dd HH:mm")
            val strDataTime = formatDateTime.format(dateDateTime)

            //Когда дата и время указаны и больше текущего времени отправляем в room bool выбора и строку картинки
            val reminderTime = dayTimeNumberNew - realTimeNumber
            list?.let { itemFilm ->
                viewModel.updateReminder(1, itemFilm.imageFilm, strDataTime)
                workManagerReminder(
                    itemFilm.nameFilm,
                    itemFilm.imageFilm,
                    itemFilm.shortDescription,
                    reminderTime,
                    itemFilm.idFilm,
                    itemFilm
                )
                cancelClick()
            }
        } else {
            selectToast(resources.getString(R.string.toastWrongTime))
            return
        }
    }

    @SuppressLint("RestrictedApi")
    private fun workManagerReminder(
        nameFilm: String,
        tag: String,
        descriptionFilm: String,
        timeReminder: Long,
        idFilm: Int,
        filmsItem: FilmsItem
    ) {
        val myDataBuilder = Data.Builder()
            .putString(resources.getString(R.string.UploadWorker_nameFilm), nameFilm)
            .putString(resources.getString(R.string.UploadWorker_descriptionFilm), descriptionFilm)
            .putString(resources.getString(R.string.UploadWorker_imagePath), tag)
            .putInt(resources.getString(R.string.UploadWorker_idFilm), idFilm)
            .putString(
                resources.getString(R.string.UploadWorker_titleLabel),
                resources.getString(R.string.toastViewingReminder)
            )
            .putString(resources.getString(R.string.UploadWorker_check), filmsItem.proverka)
            .putBoolean(resources.getString(R.string.UploadWorker_star), filmsItem.star)
            .putInt(resources.getString(R.string.UploadWorker_reminder), filmsItem.reminder)
            .putString(
                resources.getString(R.string.UploadWorker_reminderDataTime),
                filmsItem.reminderDataTime
            )
            .build()

        val myWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .addTag(tag)
            .setInitialDelay(timeReminder, TimeUnit.MILLISECONDS)
            .setInputData(myDataBuilder)
            .build()
        WorkManager.getInstance().enqueue(myWorkRequest)
    }

    private fun selectToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}