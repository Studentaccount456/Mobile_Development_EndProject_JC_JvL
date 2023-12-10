package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mobile_dev_endproject_jc_jvl.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ReservationActivity : AppCompatActivity() {

    private lateinit var establishmentTextView: TextView
    private lateinit var courtNameTextView: TextView
    private var startingTime_Play: String = ""
    private var endingTime_Play: String = ""
    private lateinit var createMatchCheckBox: CheckBox
    private lateinit var orderFieldButton: Button
    private lateinit var returnButton: Button
    private lateinit var datePickerButton: Button
    private lateinit var dateTextView: TextView
    private lateinit var selectedDate: Date
    private var yearReservation: Int = 0
    private var monthReservation: Int = 0
    private var dayReservation: Int = 0


    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservation_screen)

        establishmentTextView = findViewById(R.id.establishmentTextView)
        courtNameTextView = findViewById(R.id.courtNameTextView)
        datePickerButton = findViewById(R.id.datePickerButton)
        dateTextView = findViewById(R.id.dateTextView)

        // Set up the initial date
        val calendar1 = Calendar.getInstance()
        calendar1.add(Calendar.DAY_OF_MONTH, 1)
        selectedDate = calendar1.time
        updateDateTextView()

        // Set up the DatePickerDialog
        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Get the layout components (Time)
        val timeGrid: LinearLayout = findViewById(R.id.timeGrid)
        val reservedTimeText: TextView = findViewById(R.id.reservedTimeText)
        // Time
        val scrollView: ScrollView = findViewById(R.id.yourScrollViewId)

        createMatchCheckBox = findViewById(R.id.createMatchCheckBox)
        orderFieldButton = findViewById(R.id.orderFieldButton)
        returnButton = findViewById(R.id.returnButton)

        // Retrieve data from the intent and set the text views accordingly
        val establishment = intent.getStringExtra("sentThroughClubEstablishment")
        val courtName = intent.getStringExtra("sentThroughCourtName")
        establishmentTextView.text = establishment
        courtNameTextView.text = courtName

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
                    startingTime_Play = selectedTime;
                    endingTime_Play = reservedEndTime
                    reservedTimeText.text = "Time Reserved: $selectedTime to $reservedEndTime"
                }

                rowLayout.addView(timeSquare)
            }

            timeGrid.addView(rowLayout)
        }


        // Set click listener for the "Order Field" button
        orderFieldButton.setOnClickListener {
            // Handle order field logic
            if (startingTime_Play == "" || endingTime_Play == "") {
                // Show a warning to the user
                // You can display a toast, dialog, or any other suitable UI element to notify the user
                // For example, using a Toast:
                findViewById<View>(R.id.redBorder).visibility = View.VISIBLE
                Toast.makeText(this, "Please select a time slot before ordering the field", Toast.LENGTH_SHORT).show()
            } else {
                findViewById<View>(R.id.redBorder).visibility = View.GONE
                orderField()
            }
        }

        // Set click listener for the "Return" button
        returnButton.setOnClickListener {
            // Handle return logic
            finish()
        }
    }

    private fun orderField() {

        val sanitizedClubEstablishment = intent.getStringExtra("SanitizedClubEstablishment")
        val sanitizedCourtName = intent.getStringExtra("SanitizedCourtName")
        val courtName = intent.getStringExtra("sentThroughCourtName")
        val sanitizedClubName = intent.getStringExtra("SanitizedClubName")


        val yearForFirestore = yearReservation
        val monthForFirestore = monthReservation
        val dayForFirestore = dayReservation

        val startTimeForFireStoreInsert = startingTime_Play
        val endTimeForFireStoreInsert = endingTime_Play

        val dateReservation = formatDate(yearForFirestore, monthForFirestore, dayForFirestore)
        val timeslot = "$startTimeForFireStoreInsert - $endTimeForFireStoreInsert"
        val sanitizedStartTimeMoment = startTimeForFireStoreInsert.replace(":", "")
        val sanitizedEndTimeMoment = endTimeForFireStoreInsert.replace(":", "")
        val sanitizedTimeslot = "$sanitizedStartTimeMoment$sanitizedEndTimeMoment"


        // Create a unique MatchId based on your specified format
        // (Courtname_year_month_day_hour-begin_hour-end)
        val matchId = "$courtName$dateReservation$sanitizedTimeslot"

        // Sample data with default values
        val reservationData = hashMapOf(
            "DateReservation" to dateReservation,
            "DetailsReservation" to hashMapOf(
                "MatchId" to matchId,
                "Timeslot" to timeslot,
                "Participators" to hashMapOf(
                    "UserName_One" to "Default",
                    "UserAvatar_One" to "Default",
                    "UserId_One" to "Default",
                    "UserName_Two" to "Default",
                    "UserAvatar_Two" to "Default",
                    "UserId_Two" to "Default",
                    "UserName_Three" to "Default",
                    "UserAvatar_Three" to "Default",
                    "UserId_Three" to "Default",
                    "UserName_Four" to "Default",
                    "UserAvatar_Four" to "Default",
                    "UserId_Four" to "Default"
                )
            )
        )

        Log.d("ReservationActivity", "Data: $dateReservation, $matchId, $timeslot")
        Log.d("ReservationActivity", "Data: $sanitizedClubName, $sanitizedClubEstablishment, $sanitizedCourtName")
        // Update Firestore with the reservation data
        if (sanitizedClubName != null) {
                    if (sanitizedClubEstablishment != null) {
                        if (sanitizedCourtName != null) {
                            firestore.collection("TheClubDetails")
                                .document(sanitizedClubName)
                                .collection("TheClubEstablishments")
                                .document(sanitizedClubEstablishment)
                                .collection("TheClubCourts")
                                .document(sanitizedCourtName)
                                .update("CourtReservations", reservationData)
                                .addOnSuccessListener {
                                    // Handle success
                                    // Optionally, show a success message or navigate to another screen
                                    Log.d("ReservationActivity", "Yeey the add was succesfull")
                                }
                                .addOnFailureListener { e ->
                                    // Handle failure
                                    // Optionally, show an error message
                                }
                        }
                    }
        }
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(calendar.time)
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
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedDate = selectedCalendar.time
                yearReservation = year
                monthReservation = month
                dayReservation = dayOfMonth
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
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate)
        dateTextView.text = "Date picked: $formattedDate"
    }
}

