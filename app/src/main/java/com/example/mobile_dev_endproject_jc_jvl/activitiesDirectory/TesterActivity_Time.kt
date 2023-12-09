package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mobile_dev_endproject_jc_jvl.R
import java.text.SimpleDateFormat
import java.util.*

class TesterActivity_Time : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_screen)

        // Get the layout components
        val timeGrid: LinearLayout = findViewById(R.id.timeGrid)
        val reservedTimeText: TextView = findViewById(R.id.reservedTimeText)

        // Set up the time grid
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)

        for (i in 0 until 6) { // 6 rows for 8:00 to 19:30
            val rowLayout = LinearLayout(this)
            rowLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.orientation = LinearLayout.HORIZONTAL

            for (j in 0 until 4) {
                val startTime = sdf.format(calendar.time)
                calendar.add(Calendar.MINUTE, 30)

                val timeSquare = TextView(this)
                val params = LinearLayout.LayoutParams(
                    50, // width in dp
                    140, // height in dp
                    1f
                )
                params.setMargins(24, 24, 24, 24)
                timeSquare.layoutParams = params
                timeSquare.text = startTime
                timeSquare.tag = "$startTime"
                timeSquare.gravity = Gravity.CENTER
                timeSquare.setBackgroundResource(R.drawable.selector_time_square)
                timeSquare.setTextColor(ContextCompat.getColorStateList(this, R.color.text_color)) // Add this line
                timeSquare.setOnClickListener { view ->
                    // Clear the selection of all other time squares
                    clearSelection(timeGrid)

                    // Toggle the selection of the current time square
                    view.isSelected = !view.isSelected
                    val selectedTime = view.tag as String
                    val reservedEndTime = calculateReservedEndTime(selectedTime)
                    reservedTimeText.text = "Time Reserved: $selectedTime to $reservedEndTime"
                }

                rowLayout.addView(timeSquare)
            }

            timeGrid.addView(rowLayout)
        }
    }

    // Function to clear the selection of all time squares in the grid
    private fun clearSelection(parentLayout: LinearLayout) {
        for (i in 0 until parentLayout.childCount) {
            val rowLayout = parentLayout.getChildAt(i) as LinearLayout
            for (j in 0 until rowLayout.childCount) {
                val timeSquare = rowLayout.getChildAt(j) as TextView
                timeSquare.isSelected = false
            }
        }
    }

    private fun calculateReservedEndTime(selectedTime: String): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val startTime = sdf.parse(selectedTime)
        calendar.time = startTime
        calendar.add(Calendar.MINUTE, 90)

        return sdf.format(calendar.time)
    }
}