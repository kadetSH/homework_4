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

    companion object {

        fun newInstance(list: FilmsItem): ReminderAddFragment {
            val args = Bundle()
            args.putSerializable("spisokReminder", list)
            val fragment = ReminderAddFragment()
            fragment.arguments = args
            return fragment
        }
    }

    var list: FilmsItem? = null
    lateinit var showReminderDate: EditText
    lateinit var showReminderTime: EditText
    var yearGlobal: Int = 0
    var monthGlobal: Int = 0
    var dayGlobal: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list = arguments?.getSerializable("spisokReminder") as FilmsItem
        init(view)
    }

    private fun isData(context: Context) {
        val listener =
            DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year: Int, month: Int, day: Int ->
                yearGlobal = year
                monthGlobal = month
                dayGlobal = day
                var dayN: String = day.toString()
                if (dayN.length == 1) {
                    dayN = "0$dayN"
                }

                var monthN: String = (month + 1).toString()
                if (monthN.length == 1) {
                    monthN = "0$monthN"
                }

                val yearN = year.toString()
                showReminderDate.setText("${dayN}.${monthN}.${yearN}")
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
                showReminderTime.setText("${hourSTR}:${minuteSTR}") //("${hour}:${minute + 1}")
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
            this.context?.let { it1 -> isData(it1) }
        }
        view.findViewById<Button>(R.id.idSelectTime).setOnClickListener {
            this.context?.let { it1 -> isTime(it1) }
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

        (context as AppCompatActivity).supportFragmentManager
            .beginTransaction()
            .add(R.id.FrameLayoutContainer, FilmsFragment())
            .addToBackStack(null)
            .commit()

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
        val formatDate1 = SimpleDateFormat()
        formatDate1.applyPattern("dd.MM.yyyy HH:mm")
        val isDate1 =
            formatDate1.parse(showReminderDate.text.toString() + " " + showReminderTime.text.toString())
        dayTimeNumberNew = isDate1.time

        val realTime = Calendar.getInstance().time
        val realTimeNumber = realTime.time
        if (dayTimeNumberNew - realTimeNumber > 0) {

            val dateDateTime = Date(dayTimeNumberNew)
            val formatDateTime = SimpleDateFormat("yyyy.MM.dd HH:mm")
            val strDataTime = formatDateTime.format(dateDateTime)

            //Когда дата и время указаны и больше текущего времени отправляем в room bool выбора и строку картинки
            val reminderTime = dayTimeNumberNew - realTimeNumber
            list?.let {
                viewModel.updateReminder(1, it.imageFilm, strDataTime)
                workManagerReminder(
                    it.nameFilm,
                    it.imageFilm,
                    it.shortDescription,
                    reminderTime,
                    it.idFilm
                )
                cancelClick()
            }
        } else {
            selectToast(resources.getString(R.string.toastWrongTime))
            return
        }
    }

    private fun workManagerReminder(
        nameFilm: String,
        tag: String,
        descriptionFilm: String,
        timeReminder: Long,
        idFilm: Int
    ) {
        val myData: Data = Data.Builder()
            .putString("nameFilm", nameFilm)
            .putString("descriptionFilm", descriptionFilm)
            .putString("imagePath", tag)
            .putInt("idFilm", idFilm)
            .putString("titleLabel", resources.getString(R.string.toastViewingReminder))
            .build()

        val myWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .addTag(tag)
            .setInitialDelay(timeReminder, TimeUnit.MILLISECONDS)
            .setInputData(myData)
            .build()
        WorkManager.getInstance().enqueue(myWorkRequest)
    }

    private fun selectToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}