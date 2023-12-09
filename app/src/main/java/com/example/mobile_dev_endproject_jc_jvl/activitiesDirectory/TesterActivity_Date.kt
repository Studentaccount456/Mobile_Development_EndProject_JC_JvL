package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile_dev_endproject_jc_jvl.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
class TesterActivity_Date : AppCompatActivity() {

    private lateinit var datePickerButton: Button
    private lateinit var dateTextView: TextView
    private lateinit var selectedDate: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.testactivitydate_screen)

        datePickerButton = findViewById(R.id.datePickerButton)
        dateTextView = findViewById(R.id.dateTextView)

        // Set up the initial date
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        selectedDate = calendar.time
        updateDateTextView()

        // Set up the DatePickerDialog
        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedDate = selectedCalendar.time
                updateDateTextView()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set the minimum and maximum date
        calendar.add(Calendar.DAY_OF_MONTH, 30) // 31 days in the future
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun updateDateTextView() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate)
        dateTextView.text = "Date picked: $formattedDate"
    }
}