package com.example.lesson03

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.lesson03.recyclerMy.FilmsItem
import com.example.lesson03.viewmodel.RepoListFilmsViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import java.util.*
import java.util.concurrent.TimeUnit

class ReminderFragment : Fragment() {

    companion object {

        fun newInstance(list: FilmsItem): ReminderFragment {
            val args = Bundle()
            args.putSerializable("spisokReminder", list)

            val fragment = ReminderFragment()
            fragment.arguments = args
            return fragment
        }

    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(RepoListFilmsViewModel::class.java)
    }
    var list: FilmsItem? = null
    lateinit var showReminderDate: EditText
    lateinit var showReminderTime: EditText

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

    fun isData(context: Context) {
        val listener =
            DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year: Int, month: Int, day: Int ->
                showReminderDate.setText("${day}.${month + 1}.${year}")
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

    fun isTime(context: Context) {
        val listener =
            TimePickerDialog.OnTimeSetListener { timePicker: TimePicker, hour: Int, minute: Int ->
                showReminderTime.setText("${hour}:${minute + 1}")
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


    fun init(view: View) {
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
            cancellClick()
        }

        showReminderDate = view.findViewById<EditText>(R.id.reminderDate)
        showReminderTime = view.findViewById<EditText>(R.id.reminderTime)

    }

    private fun cancellClick() {
        activity?.onBackPressed()
    }

    private fun saveOnClick() {

        if (showReminderDate.text.toString().equals("")) {
            selectToast("Не выбрана дата напоминания")
            return
        }

        if (showReminderTime.text.toString().equals("")) {
            selectToast("Не выбрано время напоминания")
            return
        }

        var dayTimeNumber: Long = 0

        var formatDate = SimpleDateFormat()
        formatDate.applyPattern("dd.MM.yyyy")
        var isDate = formatDate.parse(showReminderDate.text.toString())
        dayTimeNumber = dayTimeNumber + isDate.time

        var formatTime = SimpleDateFormat()
        formatTime.applyPattern("hh:mm")
        var isTime = formatTime.parse(showReminderTime.text.toString())
        dayTimeNumber = dayTimeNumber + isTime.time

        val realTime = Date()
        val realTimeNumber = realTime.time
        if (dayTimeNumber - realTimeNumber > 0) {

            val dateDateTime = Date(dayTimeNumber)
            val formatDateTime = SimpleDateFormat("yyyy.MM.dd HH:mm")
            val strDataTime = formatDateTime.format(dateDateTime)

            //Когда дата и время указаны и больше текущего времени отправляем в room bool выбора и строку картинки
            val reminderTime = dayTimeNumber - realTimeNumber
            list?.let {
                viewModel.updateReminder(1, it.imageFilm, strDataTime)
                activity?.onBackPressed()
                workManagetReminder(it.nameFilm, it.imageFilm, it.shortDescription, reminderTime, it.idFilm)
            }
        } else {
            selectToast("Время напоминания не может быть меньше текущего времени")
            return
        }
    }

    private fun workManagetReminder(nameFilm: String, tag: String, descriptionFilm: String, timeReminder : Long, idFilm: Int){
        var myData : Data = Data.Builder()
            .putString("nameFilm", nameFilm)
            .putString("descriptionFilm", descriptionFilm)
            .putString("imagePath", tag)
            .putInt("idFilm", idFilm)
            .putString("titleLabel", "Напоминание просмотра фильма")
            .build()

        val myWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .addTag(tag)
            .setInitialDelay(8000, TimeUnit.MILLISECONDS) //timeReminder
            .setInputData(myData)
            .build()
        WorkManager.getInstance().enqueue(myWorkRequest)
    }

    private fun selectToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}